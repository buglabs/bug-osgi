package com.buglabs.common.tests;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static Activator instance;
	private BundleContext context;
	
	public Activator()
	{
		instance = this;
	}
	
	public void start(BundleContext context) throws Exception {
		this.context = context;
	}

	public static synchronized Activator getDefault() {
		return instance;
	}
	
	public BundleContext getContext() {
		return context;
	}
	
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}
}