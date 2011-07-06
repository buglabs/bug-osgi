package com.buglabs.util.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * A ServiceTracker that calls ServiceTrackerCustomizer.removedService() for all bound services.  
 * This allows the application to shutdown when the ServiceTracker is closed.  This is _not_ standard OSGi behavior.
 * 
 * This class is not intended to be used by clients
 * @author kgilmer
 *
 */
final class ClosingServiceTracker extends ServiceTracker {

	private final ServiceTrackerCustomizer customizer2;
	private final String[] services;

	public ClosingServiceTracker(BundleContext context, Filter filter, ServiceTrackerCustomizer customizer, String[] services) {
		super(context, filter, customizer);
		customizer2 = customizer;
		this.services = services;
	}
	
	
	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTracker#close()
	 */
	public void close() {
		for (int i = 0; i < services.length; ++i) {
			customizer2.removedService(new SimpleServiceReference(services[i]), null);
		}
		
		super.close();
	}
	
	/**
	 * Stub ServiceReference implementation to pass in as notification for bundle shutdown.
	 * @author kgilmer
	 *
	 */
	private class SimpleServiceReference implements ServiceReference {

		private final String objectClass;

		public SimpleServiceReference(String objectClass) {
			this.objectClass = objectClass;
		}

		public int compareTo(Object arg0) {
			return 0;
		}

		public Bundle getBundle() {
			return null;
		}

		public Object getProperty(String arg0) {	
			if (arg0.equals(Constants.OBJECTCLASS)) {
				return new String[] {objectClass};
			}
			
			return null;
		}

		public String[] getPropertyKeys() {
			return new String[] {Constants.OBJECTCLASS};
		}

		public Bundle[] getUsingBundles() {
			return null;
		}

		public boolean isAssignableTo(Bundle arg0, String arg1) {			
			return false;
		}	
	}
}