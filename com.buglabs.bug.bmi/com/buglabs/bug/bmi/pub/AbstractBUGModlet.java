package com.buglabs.bug.bmi.pub;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

import com.buglabs.bug.bmi.sysfs.BMIDevice;
import com.buglabs.util.osgi.LogServiceUtil;

public abstract class AbstractBUGModlet implements IModlet {

	protected final BundleContext context;
	protected final int slotId;
	protected final String moduleId;
	private final BMIDevice properties;
	protected final LogService logService;
	private List<ServiceRegistration> registrationList;

	public AbstractBUGModlet(BundleContext context, int slotId, String moduleId, BMIDevice properties) {
		this.context = context;
		this.slotId = slotId;
		this.moduleId = moduleId;
		this.properties = properties;
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

}
