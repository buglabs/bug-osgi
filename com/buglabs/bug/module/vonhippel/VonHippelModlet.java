package com.buglabs.bug.module.vonhippel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.buglabs.bug.jni.common.CharDeviceUtils;
import com.buglabs.bug.jni.vonhippel.VonHippel;
import com.buglabs.bug.menu.pub.StatusBarUtils;
import com.buglabs.bug.module.pub.IModlet;
import com.buglabs.bug.module.vonhippel.pub.IVonHippelModuleControl;
import com.buglabs.bug.module.vonhippel.pub.VonHippelWS;
import com.buglabs.module.IModuleControl;
import com.buglabs.module.IModuleProperty;
import com.buglabs.module.ModuleProperty;
//import com.buglabs.util.RemoteOSGiServiceConstants;
import com.buglabs.util.trackers.PublicWSAdminTracker;

public class VonHippelModlet implements IModlet, IVonHippelModuleControl, IModuleControl{

	private BundleContext context;

	private boolean deviceOn = true;

	private int slotId;

	private final String moduleId;

	private ServiceRegistration moduleRef;
	private ServiceTracker wsMotionTracker, wsAccTracker;
	
	protected static final String PROPERTY_MODULE_NAME = "moduleName";

	public static final String MODULE_ID = "0007";

	private final String moduleName;

	
	private VonHippel vhDevice;

	

	private String regionKey;

	private ServiceRegistration vhModuleRef;



	private static boolean icon[][] = 
	{
		{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false},
		{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false},
		{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false},
		{false,false,true,true,true,true,true,true,true,true,true,true,true,true,false,false},
		{false,true,false,false,false,false,false,false,false,false,false,false,false,false,true,false},
		{false,true,false,true,false,true,false,true,false,true,false,true,false,true,true,false},
		{false,true,true,false,true,false,true,false,true,false,true,false,true,false,true,false},
		{false,true,false,false,false,false,false,false,false,false,false,false,false,false,true,false},
		{false,true,false,true,false,false,false,true,false,true,false,false,true,false,true,false},
		{false,true,false,true,false,false,false,true,false,true,false,false,true,false,true,false},
		{false,true,false,true,false,false,false,true,false,true,false,false,true,false,true,false},
		{false,true,false,true,false,false,false,true,false,true,true,true,true,false,true,false},
		{false,true,false,false,true,false,true,false,false,true,false,false,true,false,true,false},
		{false,true,false,false,false,true,false,false,false,true,false,false,true,false,true,false},
		{false,true,false,false,false,false,false,false,false,false,false,false,false,false,true,false},
		{false,true,true,true,true,true,true,true,true,true,true,true,true,true,true,false},
		{false,true,true,true,true,true,true,true,true,true,true,true,true,true,true,false},
		{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false},
		{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false},
		{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false}
	};

	

	

	
	
	public VonHippelModlet(BundleContext context, int slotId, String moduleId, String moduleName) {
		this.context = context;
		this.slotId = slotId;
		this.moduleName = moduleName;
		this.moduleId = moduleId;
	}

	public void start() throws Exception {
		moduleRef = context.registerService(IModuleControl.class.getName(), this, createBasicServiceProperties());
		vhModuleRef = context.registerService(IVonHippelModuleControl.class.getName(), this, createBasicServiceProperties());
		VonHippelWS vhWS = new VonHippelWS(vhDevice);
		wsMotionTracker = PublicWSAdminTracker.createTracker(context, vhWS);
		//mdaccRef = context.registerService(IMDACCModuleControl.class.getName(), this, createBasicServiceProperties());	
		regionKey = StatusBarUtils.displayImage(context, icon, this.getModuleName());
	}


	public void stop() throws Exception {
		StatusBarUtils.releaseRegion(context, regionKey);
		if(wsMotionTracker != null) {
			wsMotionTracker.close();
		}

		if(wsAccTracker != null) {
			wsAccTracker.close();
		}
		//TODO: Throw exception at some point if we encounter a failure
		if (moduleRef != null) {
			moduleRef.unregister();
		}
		
		if (vhModuleRef != null) {
			vhModuleRef.unregister();
		}
	}

	private Properties createBasicServiceProperties() {
		Properties p = new Properties();
		p.put("Provider", this.getClass().getName());
		p.put("Slot", Integer.toString(slotId));
		//p.put(RemoteOSGiServiceConstants.R_OSGi_REGISTRATION, "true");
		return p;
	}
	
	public List getModuleProperties() {
		List properties = new ArrayList();

		properties.add(new ModuleProperty(PROPERTY_MODULE_NAME, getModuleName()));
		properties.add(new ModuleProperty("Slot", "" + slotId));
		properties.add(new ModuleProperty("State", Boolean.toString(deviceOn), "Boolean", true));

		return properties;
	}

	public boolean setModuleProperty(IModuleProperty property) {
		if (!property.isMutable()) {
			return false;
		}
		if (property.getName().equals("State")) {
			deviceOn = Boolean.valueOf((String) property.getValue()).booleanValue();
			return true;
		}
		return false;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getModuleId() {
		return moduleId;
	}

	public int getSlotId() {
		return slotId;
	}

	public void setup() throws Exception {
		int slot = slotId + 1;
		String devnode_vh = "/dev/bmi_vh_control_m" + slot; 
		vhDevice = new VonHippel();
		CharDeviceUtils.openDeviceWithRetry(vhDevice, devnode_vh, 2);
	}
	
	public int LEDGreenOff() throws IOException {
		if( vhDevice != null) {
			return vhDevice.ioctl_BMI_VH_GLEDOFF();
		}
		return -1;
	}

	public int LEDGreenOn() throws IOException {
		if( vhDevice != null) {
			return vhDevice.ioctl_BMI_VH_GLEDON();
		}
		return -1;
	}

	public int LEDRedOff() throws IOException {
		if( vhDevice != null) {
			return vhDevice.ioctl_BMI_VH_RLEDOFF();
		}
		return -1;
	}

	public int LEDRedOn() throws IOException {
		if( vhDevice != null) {
			return vhDevice.ioctl_BMI_VH_RLEDON();
		}
		return -1;
	}

	public void clearGPIO(int pin) {
		// TODO Auto-generated method stub
		
	}

	public void clearIOX(int pin) {
		// TODO Auto-generated method stub
		
	}

	public void doADC() {
		// TODO Auto-generated method stub
		
	}

	public void doDAC() {
		// TODO Auto-generated method stub
		
	}

	public int getRDACResistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void makeGPIOIn(int pin) {
		// TODO Auto-generated method stub
		
	}

	public void makeGPIOOut(int pin) {
		// TODO Auto-generated method stub
		
	}

	public void makeIOXIn(int pin) {
		// TODO Auto-generated method stub
		
	}

	public void makeIOXOut(int pin) {
		// TODO Auto-generated method stub
		
	}

	public int readADC() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void readDAC() {
		// TODO Auto-generated method stub
		
	}

	public void setGPIO(int pin) {
		// TODO Auto-generated method stub
		
	}

	public void setIOX(int pin) {
		// TODO Auto-generated method stub
		
	}

	public void setRDACResistance(int resistance) {
		// TODO Auto-generated method stub
		
	}


}
