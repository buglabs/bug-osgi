package com.buglabs.app.bugdash2;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.buglabs.bug.base.pub.IShellService;

public class ShellManager {

	private static IShellService shellService = null;
	private static BundleContext context = null;
	
	public static void setContext(BundleContext context) {
		ShellManager.context = context;
	}	
	
	public static IShellService getShell() {
		if (shellService == null && context != null) {
			ServiceReference sr = context.getServiceReference(IShellService.class.getName());
			if (sr != null) 
				shellService = (IShellService)context.getService(sr); 

		}
		return shellService; 
	}
}

