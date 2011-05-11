package com.buglabs.common.tests;

import junit.framework.TestSuite;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.common.tests.osgi.OSGiTestCommon;

public class Activator implements BundleActivator {

	private static Activator instance;
	private BundleContext context;
	private ServiceRegistration sr;
	
	public Activator()	{
		instance = this;
	}
	
	public void start(BundleContext context) throws Exception {
		this.context = context;
		sr = context.registerService(TestSuite.class.getName(), new TestSuite(OSGiTestCommon.class), null);
	}

	public static synchronized Activator getDefault() {
		return instance;
	}
	
	public BundleContext getContext() {
		return context;
	}
	
	public void stop(BundleContext context) throws Exception {
		sr.unregister();
	}
}