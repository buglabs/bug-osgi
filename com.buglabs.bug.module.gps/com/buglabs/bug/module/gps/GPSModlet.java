/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package com.buglabs.bug.module.gps;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.List;
import java.util.Timer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.Unit;
import org.osgi.util.position.Position;

import com.buglabs.bug.bmi.pub.AbstractBUGModlet;
import com.buglabs.bug.bmi.sysfs.BMIDevice;
import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.bug.dragonfly.module.IModuleLEDController;
import com.buglabs.bug.dragonfly.module.IModuleProperty;
import com.buglabs.bug.dragonfly.module.ModuleProperty;
import com.buglabs.bug.jni.common.CharDeviceUtils;
import com.buglabs.bug.jni.common.FCNTL_H;
import com.buglabs.bug.jni.gps.GPS;
import com.buglabs.bug.jni.gps.GPSControl;
import com.buglabs.bug.module.gps.pub.IGPSModuleControl;
import com.buglabs.bug.module.gps.pub.INMEARawFeed;
import com.buglabs.bug.module.gps.pub.INMEASentenceProvider;
import com.buglabs.bug.module.gps.pub.INMEASentenceSubscriber;
import com.buglabs.bug.module.gps.pub.IPositionProvider;
import com.buglabs.bug.module.gps.pub.IPositionSubscriber;
import com.buglabs.bug.module.gps.pub.LatLon;
import com.buglabs.nmea.DegreesMinutesSeconds;
import com.buglabs.nmea2.RMC;
import com.buglabs.services.ws.IWSResponse;
import com.buglabs.services.ws.PublicWSDefinition;
import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.services.ws.PublicWSProvider2;
import com.buglabs.services.ws.WSResponse;
import com.buglabs.util.osgi.FilterUtil;
import com.buglabs.util.xml.XmlNode;

/**
 * The Modlet exports the hardware-level services to the OSGi runtime.
 * 
 * @author kgilmer
 * 
 */
public class GPSModlet extends AbstractBUGModlet implements IGPSModuleControl, PublicWSProvider2, IPositionProvider, IModuleLEDController {
	private ServiceRegistration moduleReg;

	protected static final String PROPERTY_IOX = "IOX";
	protected static final String PROPERTY_GPS_FIX = "GPS Fix";
	protected static final String PROPERTY_ANTENNA = "Antenna";
	protected static final String PROPERTY_ANTENNA_PASSIVE = "Passive";
	protected static final String PROPERTY_ANTENNA_ACTIVE = "Active";

	/**
	 * Service property that defines active Antenna type.
	 */
	private static final String EXTERNAL_ANTENNA_PROPERTY = "gps.antenna.external";

	/**
	 * Number of millis to wait before checking the GPS sync status.
	 */
	private static final long GPS_STATUS_SCAN_INTERVAL = 5000;

	private NMEASentenceProvider nmeaProvider;
	private GPSControl gpsControl;

	private Timer timer;

	private String serviceName = "Location";

	private boolean suspended;

	private InputStream gpsInputStream;


	/**
	 * @param context
	 * @param slotId
	 * @param moduleId
	 * @param moduleName
	 */

	public GPSModlet(BundleContext context, int slotId, String moduleId, String moduleName, BMIDevice properties2) {
		super(context, moduleId, properties2, moduleName);		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.bug.module.pub.IModlet#start()
	 */
	public void start() throws Exception {
		boolean retry = false;
		int count = 0;
		do {
			getLog().log(LogService.LOG_INFO, "GPSModlet setting active (external) antenna");
			try {
				setActiveAntenna();
				retry = false;
			} catch (IOException e) {
				getLog().log(LogService.LOG_ERROR, "Failed to set GPS antenna to active (external) antenna", e);
				retry = true;
				Thread.sleep(200);
				count++;
			}
		} while (retry && count < 10);

		//gpsd.start();
		nmeaProvider.start();

		Dictionary properties = createBasicServiceProperties();
		properties.put("Power State", suspended ? "Suspended": "Active");
		moduleReg = context.registerService(IModuleControl.class.getName(), this, properties);
		registerService(IModuleLEDController.class.getName(), this, createBasicServiceProperties());
		registerService(IGPSModuleControl.class.getName(), this, createBasicServiceProperties());
		registerService(INMEARawFeed.class.getName(), new NMEARawFeed(gpsInputStream), createBasicServiceProperties());
		registerService(INMEASentenceProvider.class.getName(), nmeaProvider, createBasicServiceProperties());
		registerService(PublicWSProvider.class.getName(), this, null);

		timer = new Timer();
		timer.schedule(new GPSFIXLEDStatusTask(this, getLog()), 500, GPS_STATUS_SCAN_INTERVAL);

		registerService(IPositionProvider.class.getName(), this, createBasicServiceProperties());
		
		context.addServiceListener(nmeaProvider, FilterUtil.generateServiceFilter(
				INMEASentenceSubscriber.class.getName(), 
				IPositionSubscriber.class.getName()));
	}

	private Dictionary createBasicServiceProperties() {
		Dictionary d = getCommonProperties();		

		try {
			d.remove(EXTERNAL_ANTENNA_PROPERTY);
			d.put(EXTERNAL_ANTENNA_PROPERTY, "" + isAntennaExternal());
		} catch (IOException e) {
			getLog().log(LogService.LOG_ERROR, "Unable to access GPS antenna state.", e);
		}
		
		return d;
	}

	private void updateIModuleControlProperties(){
		if (moduleReg!=null){
			Dictionary modProperties = createBasicServiceProperties();
			modProperties.put("Power State", suspended ? "Suspended": "Active");
			moduleReg.setProperties(modProperties);
		}
	}

	private boolean isAntennaExternal() throws IOException {
		return (getStatus() & 0xC0) == 0x40;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.bug.module.pub.IModlet#stop()
	 */
	public void stop() throws Exception {
		timer.cancel();
		context.removeServiceListener(nmeaProvider);
		moduleReg.unregister();
		nmeaProvider.interrupt();
		gpsInputStream.close();
		gpsControl.close();
		super.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.bug.module.gps.pub.IPositionProvider#getPosition()
	 */
	public Position getPosition() {
		RMC rmc = nmeaProvider.getLastRMC();

		if (rmc != null) {
			try {
				Position pos = new Position(new Measurement(rmc.getLatitudeAsDMS().toDecimalDegrees() * Math.PI / 180.0, Unit.rad), new Measurement(rmc.getLongitudeAsDMS()
						.toDecimalDegrees()
						* Math.PI / 180.0, Unit.rad), new Measurement(0.0d, Unit.m), null, null);
				return pos;
			} catch (NumberFormatException e) {
				getLog().log(LogService.LOG_ERROR, "Unable to parse position.", e);
				return null;
			}
		} else {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.module.IModuleControl#getModuleProperties()
	 */
	public List<IModuleProperty> getModuleProperties() {
		List<IModuleProperty> mprops = super.getModuleProperties();

		try {
			int status = getStatus();
			status &= 0xFF;

			String status_str = Integer.toHexString(status);
			mprops.add(new ModuleProperty(PROPERTY_IOX, "0x" + status_str, "Integer", false));

			mprops.add(new ModuleProperty(PROPERTY_GPS_FIX, Boolean.toString((status &= 0x1) == 0)));

			String antenna = PROPERTY_ANTENNA_ACTIVE;

			if ((status & 0xC0) == IGPSModuleControl.STATUS_PASSIVE_ANTENNA) {
				antenna = PROPERTY_ANTENNA_PASSIVE;
			}
			mprops.add(new ModuleProperty(PROPERTY_ANTENNA, antenna, "String", false));
		} catch (IOException e) {
			getLog().log(LogService.LOG_ERROR, "Exception occured while getting module properties.", e);
		}		

		return mprops;
	}

	public boolean setModuleProperty(IModuleProperty property) {
		if (!property.isMutable()) {
			return false;
		}

		if (property.getName().equals("State")) {
			return true;
		} else if (property.getName().equals(PROPERTY_ANTENNA)) {
			if (property.getValue().toString().equals(PROPERTY_ANTENNA_PASSIVE)) {
				gpsControl.ioctl_BMI_GPS_PASSIVE_ANT();
				System.out.println("Passive");
			} else {
				gpsControl.ioctl_BMI_GPS_ACTIVE_ANT();
				System.out.println("Active");
			}
			return true;
		}  else {
			return super.setModuleProperty(property);
		}
	}

	public int resume() throws IOException {
		int result = -1;

		result = gpsControl.ioctl_BMI_GPS_RESUME();
		suspended = false;
		if (result < 0) {
			throw new IOException("ioctl BMI_GPS_RESUME failed");
		}
		suspended = false;
		updateIModuleControlProperties();
		return result;
	}

	public int suspend() throws IOException {
		int result = -1;

		result = gpsControl.ioctl_BMI_GPS_SUSPEND();
		if (result < 0) {
			throw new IOException("ioctl BMI_GPS_SUSPEND failed");
		}
		suspended = true;
		updateIModuleControlProperties();
		return result;
	}

	public PublicWSDefinition discover(int operation) {
		if (operation == PublicWSProvider2.GET) {
			return new PublicWSDefinition() {

				public List<String> getParameters() {
					return null;
				}

				public String getReturnType() {
					return "text/xml";
				}
			};
		}

		return null;
	}

	public IWSResponse execute(int operation, String input) {
		if (operation == PublicWSProvider2.GET) {
			return new WSResponse(getPositionXml(), "text/xml");
		}
		return null;
	}

	private String getPositionXml() {
		Position p = getPosition();

		XmlNode root = new XmlNode("Location");

		if (p != null) {
			if (p.getLatitude() != null) {
				root.addChild(new XmlNode("Latitude", p.getLatitude().toString()));
			}

			if (p.getLongitude() != null) {
				root.addChild(new XmlNode("Longitude", p.getLongitude().toString()));
			}

			if (p.getAltitude() != null) {
				root.addChild(new XmlNode("Altitude", p.getAltitude().toString()));
			}

			RMC rmc = nmeaProvider.getLastRMC();

			DegreesMinutesSeconds dmsLat = rmc.getLatitudeAsDMS();
			DegreesMinutesSeconds dmsLon = rmc.getLongitudeAsDMS();

			if (dmsLat != null) {
				root.addChild(new XmlNode("LatitudeDegrees", Double.toString(dmsLat.toDecimalDegrees())));
			}

			if (dmsLon != null) {
				root.addChild(new XmlNode("LongitudeDegrees", Double.toString(dmsLon.toDecimalDegrees())));
			}
		}
		
		return root.toString();
	}

	public String getPublicName() {
		return serviceName;
	}

	public String getDescription() {
		return "Returns location as provided by GPS module.";
	}

	public void setup() throws Exception {
		String devnode_gps = "/dev/ttyBMI" + Integer.toString(getSlotId());
		String devnode_gpscontrol = "/dev/bmi_gps_control_m" + Integer.toString(getSlotId());

		//Creation and initialization of this device is necessary to access the control device, below.
		GPS gps = new GPS();
		CharDeviceUtils.openDeviceWithRetry(gps, devnode_gps, FCNTL_H.O_RDWR | FCNTL_H.O_NONBLOCK, 2);

		int result = gps.init();
		if (result < 0) {
			throw new RuntimeException("Unable to initialize gps device: " + devnode_gpscontrol);
		}

		gpsControl = new GPSControl();
		getLog().log(LogService.LOG_DEBUG, "Opening GPS control port: " + devnode_gpscontrol);
		CharDeviceUtils.openDeviceWithRetry(gpsControl, devnode_gpscontrol, 2);
		gps.close();
		getLog().log(LogService.LOG_DEBUG, "Opening GPS data port: " + devnode_gps);
		gpsInputStream = new FileInputStream(devnode_gps);
		nmeaProvider = new NMEASentenceProvider(gpsInputStream, context, getLog());
	}

	public LatLon getLatitudeLongitude() {
		com.buglabs.nmea2.RMC rmc = nmeaProvider.getLastRMC();

		if (rmc != null) {
			return new LatLon(rmc.getLatitudeAsDMS().toDecimalDegrees(), rmc.getLongitudeAsDMS().toDecimalDegrees());
		}
		return null;

	}

	public int LEDGreenOff() throws IOException {
		int result = -1;

		if (gpsControl != null) {
			result = gpsControl.ioctl_BMI_GPS_GLEDOFF();
		}

		if (result < 0) {
			throw new IOException("ioctl BMI_GPS_GLEDOFF failed");
		}

		return result;
	}

	public int LEDGreenOn() throws IOException {
		int result = -1;

		if (gpsControl != null) {
			result = gpsControl.ioctl_BMI_GPS_GLEDON();
		}

		if (result < 0) {
			throw new IOException("ioctl BMI_GPS_GLEDON failed");
		}

		return result;
	}

	public int LEDRedOff() throws IOException {
		int result = -1;

		if (gpsControl != null) {
			result = gpsControl.ioctl_BMI_GPS_RLEDOFF();
		}

		if (result < 0) {
			throw new IOException("ioctl BMI_GPS_RLEDOFF failed");
		}

		return result;
	}

	public int LEDRedOn() throws IOException {
		int result = -1;

		if (gpsControl != null) {
			result = gpsControl.ioctl_BMI_GPS_RLEDON();
		}

		if (result < 0) {
			throw new IOException("ioctl BMI_GPS_RLEDON failed");
		}

		return result;
	}

	public int getStatus() throws IOException {
		int result = -1;

		if (gpsControl != null) {
			result = gpsControl.ioctl_BMI_GPS_GETSTAT();
		}

		if (result < 0) {
			throw new IOException("ioctl BMI_GPS_GETSTAT failed");
		}

		return result;
	}

	public int setActiveAntenna() throws IOException {
		int result = -1;

		if (gpsControl != null) {
			result = gpsControl.ioctl_BMI_GPS_ACTIVE_ANT();
			System.out.println("Result: "+result);
		}

		if (result < 0) {
			throw new IOException("ioctl BMI_GPS_ACTIVE_ANT failed");
		}

		return result;
	}

	public int setPassiveAntenna() throws IOException {
		int result = -1;

		if (gpsControl != null) {
			result = gpsControl.ioctl_BMI_GPS_PASSIVE_ANT();
			System.out.println("Result: "+result);
		}

		if (result < 0) {
			throw new IOException("ioctl BMI_GPS_PASSIVE_ANT failed");
		}

		return result;
	}

	public int setLEDGreen(boolean state) throws IOException {
		int result = -1;

		if (gpsControl != null) {
			if (state) {
				return LEDGreenOn();
			} else {
				return LEDGreenOff();
			}
		}

		return result;
	}

	public int setLEDRed(boolean state) throws IOException {
		int result = -1;

		if (gpsControl != null) {
			if (gpsControl != null) {
				if (state) {
					return LEDRedOn();
				} else {
					return LEDRedOff();
				}
			}
		}
		return result;
	}

	public void setPublicName(String name) {
		serviceName = name;
	}

	public boolean isSuspended() {	
		return suspended;
	}
}
