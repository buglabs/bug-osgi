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
package com.buglabs.bug.module.lcd.pub;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

import com.buglabs.bug.accelerometer.pub.IAccelerometerRawFeed;
import com.buglabs.bug.accelerometer.pub.IAccelerometerSampleFeed;
import com.buglabs.bug.accelerometer.pub.IAccelerometerSampleProvider;
import com.buglabs.bug.jni.common.CharDevice;
import com.buglabs.bug.jni.common.CharDeviceInputStream;
import com.buglabs.bug.jni.common.CharDeviceUtils;
import com.buglabs.bug.jni.lcd.LCDControl;
import com.buglabs.bug.module.lcd.accelerometer.LCDAccelerometerInputStreamProvider;
import com.buglabs.bug.module.lcd.accelerometer.LCDAccelerometerSampleProvider;
import com.buglabs.bug.module.motion.pub.AccelerationWS;
import com.buglabs.bug.module.pub.BMIModuleProperties;
import com.buglabs.bug.module.pub.IModlet;
import com.buglabs.module.IModuleControl;
import com.buglabs.module.IModuleLEDController;
import com.buglabs.module.IModuleProperty;
import com.buglabs.module.ModuleProperty;
import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.util.LogServiceUtil;
import com.buglabs.util.RemoteOSGiServiceConstants;

/**
 * LCD Modlet class for 1x LCD module.
 * 
 * @author kgilmer
 * 
 */
public class LCDModlet implements IModlet, ILCDModuleControl, IModuleControl, IModuleDisplay, IModuleLEDController {

	private static final String POINTERCAL_PATH = "/etc/pointercal";
	private static final int FILE_SCAN_MILLIS = 2000;
	private final BundleContext context;
	private final int slotId;
	private final String moduleId;
	private final String moduleName;

	private ServiceRegistration moduleRef;
	private ServiceRegistration moduleDisplayServReg;
	private final int LCD_WIDTH = 320;
	private final int LCD_HEIGHT = 200;
	private ServiceRegistration lcdControlServReg;
	private LCDAccelerometerInputStreamProvider lcd_acc_is_prov;
	private ServiceRegistration accIsProvRef;
	private ServiceRegistration accRawFeedProvRef;
	private ServiceRegistration accSampleProvRef;
	private CharDevice accel;
	private CharDeviceInputStream accIs;
	private ServiceRegistration lcdRef;
	private LogService log;
	private LCDControl lcdcontrol;
	private Hashtable props;
	private boolean suspended;
	protected static final String PROPERTY_MODULE_NAME = "moduleName";
	private final BMIModuleProperties properties;
	private ServiceRegistration wsReg;

	public LCDModlet(BundleContext context, int slotId, String moduleId) {
		this.context = context;
		this.slotId = slotId;
		this.moduleId = moduleId;
		this.properties = null;
		this.moduleName = "LCD";
		this.log = LogServiceUtil.getLogService(context);
	}

	public LCDModlet(BundleContext context, int slotId, String moduleId, BMIModuleProperties properties) {
		this.context = context;
		this.slotId = slotId;
		this.moduleId = moduleId;
		this.properties = properties;
		this.moduleName = "LCD";
		this.log = LogServiceUtil.getLogService(context);
	}

	public void setup() throws Exception {
		String devnode_control = "/dev/bmi_lcd_ctl_m" + (slotId + 1);

		lcdcontrol = new LCDControl();
		CharDeviceUtils.openDeviceWithRetry(lcdcontrol, devnode_control, 2);
		String lcd_accel_devnode = "/dev/bmi_lcd_acc_m" + (slotId + 1);
		accel = new CharDevice();
		try {
			CharDeviceUtils.openDeviceWithRetry(accel, lcd_accel_devnode, 2);
			accIs = new CharDeviceInputStream(accel);
			lcd_acc_is_prov = new LCDAccelerometerInputStreamProvider(accIs);
		} catch (IOException e) {
			LogServiceUtil.logBundleException(log, "Failed to access LCD control device.", e);
		}
	}

	public void start() throws Exception {
		Properties modProperties = createBasicServiceProperties();
		modProperties.put("Power State", suspended ? "Suspended" : "Active");
		moduleRef = context.registerService(IModuleControl.class.getName(), this, modProperties);

		lcdRef = context.registerService(IModuleLEDController.class.getName(), this, createRemotableProperties(null));
		props = new Hashtable();
		props.put("width", new Integer(LCD_WIDTH));
		props.put("height", new Integer(LCD_HEIGHT));
		props.put("Slot", "" + slotId);

		lcdControlServReg = context.registerService(ILCDModuleControl.class.getName(), this, createRemotableProperties(null));

		if (lcd_acc_is_prov != null) {
			lcd_acc_is_prov.start();
			accIsProvRef = context.registerService(IAccelerometerSampleFeed.class.getName(), lcd_acc_is_prov, createRemotableProperties(createBasicServiceProperties()));
			accRawFeedProvRef = context.registerService(IAccelerometerRawFeed.class.getName(), lcd_acc_is_prov, createRemotableProperties(createBasicServiceProperties()));

			LCDAccelerometerSampleProvider accsp = new LCDAccelerometerSampleProvider(lcd_acc_is_prov);

			accSampleProvRef = context.registerService(IAccelerometerSampleProvider.class.getName(), accsp, createRemotableProperties(createBasicServiceProperties()));
			AccelerationWS accWs = new AccelerationWS(accsp, LogServiceUtil.getLogService(context));
			wsReg = context.registerService(PublicWSProvider.class.getName(), accWs, null);
		} else {
			log.log(LogService.LOG_ERROR, "Unable to access the accelerometer device.");
		}

		if (calibFileExists()) {
			// Calibration has occurred so we know the screen is usable.
			moduleDisplayServReg = context.registerService(IModuleDisplay.class.getName(), this, createRemotableProperties(props));
		} else {
			// The calibration program is running. Only register the service
			// once the the calibration has occurred.
			scheduleTimer();
		}
	}

	/**
	 * Create a timer task to monitor the status of a file.
	 */
	private void scheduleTimer() {
		final Timer t = new Timer();
		t.schedule(new TimerTask() {

			public void run() {
				if (calibFileExists()) {
					t.cancel();
					moduleDisplayServReg = context.registerService(IModuleDisplay.class.getName(), LCDModlet.this, createRemotableProperties(props));
				}
			}

		}, FILE_SCAN_MILLIS, FILE_SCAN_MILLIS);
	}

	/**
	 * @return true if lcd calibration file exists
	 */
	private boolean calibFileExists() {
		File f = new File(POINTERCAL_PATH);
		return f.exists() && f.isFile();
	}

	public void stop() throws Exception {
		if (lcd_acc_is_prov != null) {
			log.log(LogService.LOG_DEBUG, "closing accel");
			lcd_acc_is_prov.interrupt();
			wsReg.unregister();
			accIsProvRef.unregister();
			accRawFeedProvRef.unregister();
			accSampleProvRef.unregister();
			accIs.close();
			accel.close();
			accel = null;
		}

		lcdcontrol.close();
		moduleRef.unregister();
		lcdRef.unregister();
		if (moduleDisplayServReg != null) {
			// this could be null if pointercal never completes.
			moduleDisplayServReg.unregister();
		}
		lcdControlServReg.unregister();
	}

	/**
	 * @return A dictionary with R-OSGi enable property.
	 */
	private Dictionary createRemotableProperties(Dictionary ht) {
		if (ht == null) {
			ht = new Hashtable();
			ht.put("Slot", "" + slotId);
		}

		ht.put(RemoteOSGiServiceConstants.R_OSGi_REGISTRATION, "true");

		return ht;
	}

	private Properties createBasicServiceProperties() {
		Properties p = new Properties();
		p.put("Provider", this.getClass().getName());
		p.put("Slot", Integer.toString(slotId));

		if (properties != null) {
			p.put("ModuleDescription", properties.getDescription());
			p.put("ModuleSN", properties.getSerial_num());
			p.put("ModuleVendorID", "" + properties.getVendor());
			p.put("ModuleRevision", "" + properties.getRevision());
		}

		return p;
	}

	private void updateIModuleControlProperties() {
		if (moduleRef != null) {
			Properties modProperties = createBasicServiceProperties();
			modProperties.put("Power State", suspended ? "Suspended" : "Active");
			moduleRef.setProperties(modProperties);
		}
	}

	public List getModuleProperties() {
		List mprops = new ArrayList();
		mprops.add(new ModuleProperty("Slot", "" + slotId));
		mprops.add(new ModuleProperty("Width", "" + LCD_WIDTH));
		mprops.add(new ModuleProperty("Height", "" + LCD_HEIGHT));
		mprops.add(new ModuleProperty(PROPERTY_MODULE_NAME, getModuleName()));
		mprops.add(new ModuleProperty("Power State", suspended ? "Suspended" : "Active", "String", true));

		if (properties != null) {
			mprops.add(new ModuleProperty("Module Description", properties.getDescription()));
			mprops.add(new ModuleProperty("Module SN", properties.getSerial_num()));
			mprops.add(new ModuleProperty("Module Vendor ID", "" + properties.getVendor()));
			mprops.add(new ModuleProperty("Module Revision", "" + properties.getRevision()));
		}

		return mprops;
	}

	public boolean setModuleProperty(IModuleProperty property) {
		if (!property.isMutable()) {
			return false;
		}
		if (property.getName().equals("State")) {
			return true;
		}
		if (property.getName().equals("Power State")) {
			if (((String) property.getValue()).equals("Suspend")) {

				try {
					suspend();
				} catch (IOException e) {
					LogServiceUtil.logBundleException(log, "An error occured while changing suspend state.", e);
				}
			} else if (((String) property.getValue()).equals("Resume")) {

				try {
					resume();

				} catch (IOException e) {
					LogServiceUtil.logBundleException(log, "An error occured while changing suspend state.", e);
				}
			}

		}

		return false;
	}

	public int getSlotId() {
		return slotId;
	}

	public int resume() throws IOException {
		int result = -1;

		result = lcdcontrol.ioctl_BMI_LCD_RESUME(slotId);

		if (result < 0) {
			throw new IOException("ioctl BMI_LCD_RESUME failed");
		}

		suspended = false;
		updateIModuleControlProperties();
		return result;
	}

	public int suspend() throws IOException {
		int result = -1;

		result = lcdcontrol.ioctl_BMI_LCD_SUSPEND(slotId);

		if (result < 0) {
			throw new IOException("ioctl BMI_LCD_SUSPEND failed");
		}

		suspended = true;
		updateIModuleControlProperties();
		return result;
	}

	public Frame getFrame() {
		Frame frame = new Frame();
		frame.setSize(LCD_WIDTH, LCD_HEIGHT);
		frame.setResizable(false);
		frame.setVisible(true);
		return frame;
	}

	public String getModuleId() {
		return moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public int setLEDGreen(boolean state) throws IOException {
		if (state) {
			return lcdcontrol.ioctl_BMI_LCD_GLEDON(slotId);
		} else {
			return lcdcontrol.ioctl_BMI_LCD_GLEDOFF(slotId);
		}
	}

	public int setLEDRed(boolean state) throws IOException {
		if (state) {
			return lcdcontrol.ioctl_BMI_LCD_RLEDON(slotId);
		} else {
			return lcdcontrol.ioctl_BMI_LCD_RLEDOFF(slotId);
		}
	}

	public int getStatus() throws IOException {
		return lcdcontrol.ioctl_BMI_LCD_GETSTAT(slotId);
	}

	public int disable() throws IOException {
		return lcdcontrol.ioctl_BMI_LCD_SETRST(slotId);
	}

	public int enable() throws IOException {
		return lcdcontrol.ioctl_BMI_LCD_CLRRST(slotId);
	}

	public int setBlackLight(int val) throws IOException {
		return setBackLight(val);
	}

	public int setBackLight(int val) throws IOException {
		return lcdcontrol.ioctl_BMI_LCD_SET_BL(slotId, val);
	}
}
