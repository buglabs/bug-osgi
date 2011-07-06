package com.buglabs.app.bugdash2.servicetracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.buglabs.app.bugdash2.Activator;
import com.buglabs.bug.dragonfly.module.IModuleControl;

public class BUGwebAdminModuleControlServiceTracker implements
		ServiceTrackerCustomizer {
	
	private BundleContext context;
	
	public BUGwebAdminModuleControlServiceTracker(BundleContext context) {
		this.context = context; 
	}

	public Object addingService(ServiceReference reference) {
		if (context == null) return null; 
		Object svc = context.getService(reference);
        if (svc instanceof IModuleControl) {	
        	IModuleControl module = (IModuleControl) svc;
        	Activator.setModule(module);
        }
        return reference;
	}

	public void modifiedService(ServiceReference reference, Object service) {
	}

	public void removedService(ServiceReference reference, Object service) {
	     Object svc = context.getService(reference);
	     if (svc instanceof IModuleControl) {
	         IModuleControl mc = (IModuleControl) svc;
	         Activator.clearModule(mc.getSlotId());
	     }
	}
}
