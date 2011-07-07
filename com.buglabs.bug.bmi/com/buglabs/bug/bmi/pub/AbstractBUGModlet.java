package com.buglabs.bug.bmi.pub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

import com.buglabs.bug.bmi.sysfs.BMIDevice;
import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.bug.dragonfly.module.IModuleProperty;
import com.buglabs.bug.dragonfly.module.ModuleProperty;
import com.buglabs.util.osgi.BUGBundleConstants;
import com.buglabs.util.osgi.LogServiceUtil;

/**
 * Abstract base class that handles standard IModlet and IModuleControl behavior.
 * 
 * @author kgilmer
 *
 */
public abstract class AbstractBUGModlet implements IModlet, IModuleControl {

	protected final BundleContext context;
	protected final String moduleId;
	private final BMIDevice bmiDevice;
	protected final LogService logService;
	private List<ServiceRegistration> registrationList;
	private final String name;
	private Hashtable commonProperties;
	
	/**
	 * @param context
	 * @param moduleId
	 * @param bmiDevice
	 * @param name
	 */
	public AbstractBUGModlet(BundleContext context, String moduleId, BMIDevice bmiDevice, String name) {
		this.context = context;
		this.moduleId = moduleId;
		this.bmiDevice = bmiDevice;
		this.name = name;
		this.logService = LogServiceUtil.getLogService(context);
	}

	@Override
	public final String getModuleId() {
		return moduleId;
	}

	@Override
	public final int getSlotId() {
		return bmiDevice.getSlot();
	}
	
	/**
	 * @return instance of LogService
	 */
	public final LogService getLog() {
		return logService;
	}
	
	/**
	 * @return the BMI device associated with this modlet.
	 */
	public final BMIDevice getBMIDevice() {
		return bmiDevice;
	}

	@Override
	public abstract void setup() throws Exception;

	@Override
	public abstract void start() throws Exception;

	@Override
	public void stop() throws Exception {
		if (registrationList != null)
			for (ServiceRegistration sr : registrationList)
				sr.unregister();
	}
	
	/**
	 * This will manage the service references and unregister them upon shutdown.
	 * It is equivalent to <code>BundleContext.registerService()</code>
	 * 
	 * @param clazz name of service
	 * @param service instance of service
	 * @param properties properties of service
	 */
	protected final void registerService(String clazz, Object service, Dictionary properties) {
		
		if (registrationList == null)
			registrationList = new ArrayList<ServiceRegistration>();
		
		registrationList.add(
				context.registerService(clazz, service, properties));
	}

	/**
	 * @return A dictionary with common BUG service properties.
	 */
	public final Dictionary getCommonProperties() {
		if (commonProperties == null) {
			commonProperties = new Hashtable();
			commonProperties.put(BUGBundleConstants.MODULE_PROVIDER_KEY, this.getClass().getName());
			commonProperties.put("Slot", Integer.toString(bmiDevice.getSlot()));
	
			if (bmiDevice != null) {
				if (bmiDevice.getDescription() != null)
					commonProperties.put(BUGBundleConstants.MODULE_DESC_KEY, bmiDevice.getDescription());
	
				if (bmiDevice.getSerialNum() != null)
					commonProperties.put(BUGBundleConstants.MODULE_SERIAL_KEY, bmiDevice.getSerialNum());
	
				// these are ints so don't need a null check
				commonProperties.put(BUGBundleConstants.MODULE_VENDOR_KEY, "" + bmiDevice.getVendor());
				commonProperties.put(BUGBundleConstants.MODULE_VERSION_KEY, "" + bmiDevice.getRevision());
			}
		}

		return commonProperties;
	}
	
	public abstract boolean isSuspended();
	
	public List<IModuleProperty> getModuleProperties() {
		List<IModuleProperty> modProps = new ArrayList<IModuleProperty>();

		modProps.add(new ModuleProperty(BUGBundleConstants.PROPERTY_MODULE_NAME, getModuleName()));
		modProps.add(new ModuleProperty("Slot", "" + bmiDevice.getSlot()));
		modProps.add(new ModuleProperty("Power State", isSuspended() ? "Suspended": "Active", "String", true));
		
		if (bmiDevice != null) {
			modProps.add(new ModuleProperty("Module Description", bmiDevice.getDescription()));
			modProps.add(new ModuleProperty("Module SN", bmiDevice.getSerialNum()));
			modProps.add(new ModuleProperty("Module Vendor ID", "" + bmiDevice.getVendor()));
			modProps.add(new ModuleProperty("Module Revision", "" + bmiDevice.getRevision()));
		}
		
		return modProps;
	}

	public String getModuleName() {
		return name;
	}

	public boolean setModuleProperty(IModuleProperty property) {
		if (!property.isMutable()) {
			return false;
		}
		if (property.getName().equals("Power State")) {
			if (((String) property.getValue()).equals("Suspend")){
				try{
				suspend();
				}
			 catch (IOException e) {
				 LogServiceUtil.logBundleException(logService, e.getMessage(), e);
			}
			}
			else if (((String) property.getValue()).equals("Resume")){
				
				try {
					resume();
				} catch (IOException e) {
					LogServiceUtil.logBundleException(logService, e.getMessage(), e);
				}
			}
		}
		
		return false;
	}

}
