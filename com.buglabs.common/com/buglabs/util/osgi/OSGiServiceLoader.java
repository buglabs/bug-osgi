package com.buglabs.util.osgi;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A utility for loading a set of pre-existing registered services into client-defined state.
 * @author kgilmer
 *
 */
public final class OSGiServiceLoader {
	
	/**
	 * Utility class, not constructible.
	 */
	private OSGiServiceLoader() {		
	}
	
	/**
	 * This method will be called on every service that matches the criteria passed into loadServices().
	 * @author kgilmer
	 *
	 */
	public interface IServiceLoader {
		/**
		 * @param service service instance
		 * @throws Exception for any error
		 */
		void load(Object service) throws Exception;
	}
	
	/**
	 * Load services from the OSGi service registry.  Refer to BundleContext.getServiceReferences() for more information.
	 * @param context BundleContext
	 * @param clazz Name of class that IServiceLoader should be applied to
	 * @param filter Further filter on services returned, NULL legal for no filter.
	 * @param loader Function to be applied to each service reference.
	 * @throws Exception 
	 */
	public static void loadServices(BundleContext context, String clazz, String filter, IServiceLoader loader) throws Exception {
		ServiceReference[] sr = context.getServiceReferences(clazz, filter);
		
		if (sr != null) {
			for (int i = 0; i < sr.length; ++i) {		
				loader.load(context.getService(sr[i]));
			}
		}
	}
	
	/**
	 * @param context BundleContext
	 * @param clazz Class
	 * @param filter Filter as a String
	 * @return A list of service instances that match the input parameters.
	 * @throws Exception on any error
	 */
	public static List<Object> getServices(BundleContext context, String clazz, String filter) throws Exception {
		final List<Object> svcList = new ArrayList<Object>();
		
		loadServices(context, clazz, filter, new IServiceLoader() {
			
			public void load(Object service) throws Exception {
				svcList.add(service);
			}
		});
		
		return svcList;
	}
}
