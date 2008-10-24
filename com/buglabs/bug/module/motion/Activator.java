package com.buglabs.bug.module.motion;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.bug.module.pub.IModlet;
import com.buglabs.bug.module.pub.IModletFactory;

public class Activator implements BundleActivator, IModletFactory {
	private BundleContext context;
	private ServiceRegistration sr;

	private static Activator instance;
	
	public Activator() {
		instance = this;
	}
	
	public void start(BundleContext context) throws Exception {
		this.context = context;
		sr = context.registerService(IModletFactory.class.getName(), this, null);	
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
	}

	public IModlet createModlet(BundleContext context, int slotId) {
		MotionModlet modlet = new MotionModlet(context, slotId, getModuleId(), "Motion");
		
		return modlet;
	}

	public String getModuleId() {
		return (String) context.getBundle().getHeaders().get("Bug-Module-Id");
	}

	public String getName() {		
		return (String) context.getBundle().getHeaders().get("Bundle-SymbolicName");
	}

	public String getVersion() {		
		return (String) context.getBundle().getHeaders().get("Bundle-Version");
	}

	public static Activator getInstance() {
		return instance;
	}

	public BundleContext getBundleContext() {
		return context;
	}
}
