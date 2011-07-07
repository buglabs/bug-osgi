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
 * @author kgilmer
 *
 */
public abstract class AbstractBUGModlet implements IModlet, IModuleControl {

	protected final BundleContext context;
	protected final int slotId;
	protected final String moduleId;
	private final BMIDevice properties;
	protected final LogService logService;
	private List<ServiceRegistration> registrationList;
	private final String name;
	
	public AbstractBUGModlet(BundleContext context, int slotId, String moduleId, BMIDevice properties, String name) {
		this.context = context;
		this.slotId = slotId;
		this.moduleId = moduleId;
		this.properties = properties;
		this.name = name;
		this.logService = LogServiceUtil.getLogService(context);
	}

	@Override
	public final String getModuleId() {
		return moduleId;
	}

	@Override
	public final int getSlotId() {
		return slotId;
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
	 * @param clazz
	 * @param service
	 * @param properties
	 */
	protected final void registerService(String clazz, Object service, Dictionary properties) {
		
		if (registrationList == null)
			registrationList = new ArrayList<ServiceRegistration>();
		
		registrationList.add(
				context.registerService(clazz, service, properties));
	}

	public final Dictionary getCommonProperties() {
		Dictionary p = new Hashtable();
		p.put("Provider", this.getClass().getName());
		p.put("Slot", Integer.toString(slotId));

		if (properties != null) {
			if (properties.getDescription() != null)
				p.put("ModuleDescription", properties.getDescription());

			if (properties.getSerialNum() != null)
				p.put("ModuleSN", properties.getSerialNum());

			// these are ints so don't need a null check
			p.put("ModuleVendorID", "" + properties.getVendor());
			p.put("ModuleRevision", "" + properties.getRevision());
		}

		return p;
	}
	
	public abstract boolean isSuspended();
	
	public List<IModuleProperty> getModuleProperties() {
		List<IModuleProperty> modProps = new ArrayList<IModuleProperty>();

		modProps.add(new ModuleProperty(BUGBundleConstants.PROPERTY_MODULE_NAME, getModuleName()));
		modProps.add(new ModuleProperty("Slot", "" + slotId));
		modProps.add(new ModuleProperty("Power State", isSuspended() ? "Suspended": "Active", "String", true));
		
		if (properties != null) {
			modProps.add(new ModuleProperty("Module Description", properties.getDescription()));
			modProps.add(new ModuleProperty("Module SN", properties.getSerialNum()));
			modProps.add(new ModuleProperty("Module Vendor ID", "" + properties.getVendor()));
			modProps.add(new ModuleProperty("Module Revision", "" + properties.getRevision()));
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
