package com.buglabs.application;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

class ClosingServiceTracker extends ServiceTracker {

	private final ServiceTrackerCustomizer customizer2;


	public ClosingServiceTracker(BundleContext context, Filter filter, ServiceTrackerCustomizer customizer) {
		super(context, filter, customizer);
		customizer2 = customizer;
	}
	
	
	public void close() {
		customizer2.removedService(new ServiceReference() {
			
			public boolean isAssignableTo(Bundle bundle, String s) {			
				return false;
			}
			
			public Bundle[] getUsingBundles() {
				return null;
			}
			
			public String[] getPropertyKeys() {			
				return null;
			}
			
			public Object getProperty(String s) {			
				return null;
			}
			
			public Bundle getBundle() {
				return null;
			}
			
			public int compareTo(Object obj) {			
				return 0;
			}
		}, null);
		
		super.close();
	}
}