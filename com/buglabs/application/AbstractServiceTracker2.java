package com.buglabs.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.buglabs.util.ServiceFilterGenerator;

/**
 * DO NOT USE THIS CLASS
 * 
 * The functionality for filtering on Services w/ service properties was moved
 * into the generated applications activator, combined with
 * AbstractServiceTracker
 * 
 * @author bballantine
 * 
 */
public abstract class AbstractServiceTracker2 implements ServiceTrackerCustomizer, IServiceProvider {

	private BundleContext context;
	private LogService logService;
	private String trackerName;
	private Map trackedServices;
	private boolean isRunning;
	private Map serviceProperties;

	public AbstractServiceTracker2(BundleContext bundleContext) {
		context = bundleContext;
		trackedServices = new HashMap();
		logService = getLogService(context);
		trackerName = getTrackerName(context);
		isRunning = false;
		initServices();
	}

	/**
	 * Invoked when all services dependencies have been met
	 * 
	 */
	public abstract void doStart();

	/**
	 * Invoked when service dependencies are no longer met
	 * 
	 */
	public abstract void doStop();

	/**
	 * Overridable function for determining if app can start
	 * 
	 */
	public boolean canStart() {
		return true;
	}

	/**
	 * Implementations should add services of interest
	 */
	public void initServices() {
		logService.log(LogService.LOG_DEBUG, trackerName + " A tracker in this bundle is tracking no services.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(ServiceReference reference) {
		//not a service we care about, return
		// TODO - returning null might be an issue
		if (!inServicePropertiesMap(reference))
			return null;

		Object serviceObject = trackService(reference) ? getServiceObject(reference) : null;

		if (areServiceDependenciesMet() && !isRunning()) {
			if (canStart())
				performStart();
		}
		return serviceObject;
	}

	/**
	 * Called when a service is modified
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		//not a service we care about, return
		if (!inServicePropertiesMap(reference))
			return;
		trackService(reference);

		// if dependencies are met, update the trackedServices
		if (areServiceDependenciesMet() && !isRunning()) {
			if (canStart())
				performStart();
		} else if (isRunning()) {
			performStop();
		}
	}

	/**
	 * Called when a service is removed
	 */
	public void removedService(ServiceReference reference, Object service) {
		//not a service we care about, return
		if (!inServicePropertiesMap(reference))
			return;
		trackService(reference);

		if (!areServiceDependenciesMet() && isRunning()) {
			performStop();
		}

	}

	/**
	 * Used to retrieve a list of qualified service names that we are filtering
	 * on.
	 * 
	 * @return a list of Strings containing the fully qualified name of each
	 *         service.
	 * 
	 */
	public final List getServices() {
		return new ArrayList(getServicePropertiesMap().keySet());
	}

	/**
	 * Stop the service tracker
	 */
	public final void stop() {
		if (isRunning()) {
			doStop();
			setIsRunning(false);
		}
	}

	/**
	 * Helps set up the serviceProperties map created to make generated code
	 * simpler
	 * 
	 * @param serviceName
	 * @param properties
	 */
	protected final void addServiceFilters(String serviceName, String[][] properties) {
		Map propMap = new HashMap();
		for (int i = 0; i < properties.length; i++) {
			propMap.put(properties[i][0], properties[i][1]);
		}
		getServicePropertiesMap().put(serviceName, propMap);
	}

	/**
	 * returns the map of services and properties that represent the service
	 * dependencies for this tracker the map, in pseudo-generic code, is:
	 * Map<String serviceName, Map<String propertyKey, String propertyVal>>
	 * 
	 * @return
	 */
	public final Map getServicePropertiesMap() {
		if (serviceProperties == null) {
			serviceProperties = new HashMap();
		}
		return serviceProperties;
	}

	/**
	 * Decides if the service reference is for a service that matches our
	 * property filters. If so, it tracks it.
	 * 
	 * If it doesn't satisfy our property filters, and we're already tracking
	 * it, then stop tracking it.
	 * 
	 * @param reference
	 * @return
	 */
	private boolean trackService(ServiceReference reference) {
		if (satisfiesServiceDependency(reference)) {
			trackedServices.put(getServiceName(reference), getServiceObject(reference));
			return true;
		} else {
			if (trackedServices.containsKey(getServiceName(reference)) && trackedServices.get(getServiceName(reference)).equals(getServiceObject(reference)))
				trackedServices.remove(getServiceName(reference));
			return false;
		}
	}

	/**
	 * Helper to see if a ServiceReference satisfies one of our service
	 * dependencies
	 * 
	 * @param reference
	 * @return
	 */
	private boolean satisfiesServiceDependency(ServiceReference reference) {
		ServiceReference[] srs = getServiceReferences(getServiceName(reference), (Map) getServicePropertiesMap().get(getServiceName(reference)));

		if (srs == null || srs.length < 1)
			return false;

		if (Arrays.asList(srs).contains(reference))
			return true;

		return false;
	}

	/**
	 * Check the running services against the services and their properties
	 * using a service filter, to decide if all the services with their
	 * properties match
	 * 
	 * @return
	 */
	private boolean areServiceDependenciesMet() {
		Iterator servicesItr = getServicePropertiesMap().keySet().iterator();
		String service;
		ServiceReference[] srs;
		while (servicesItr.hasNext()) {
			service = (String) servicesItr.next();
			srs = getServiceReferences(service, new TreeMap((Map) getServicePropertiesMap().get(service)));
			if (srs == null || srs.length < 1)
				return false;
		}
		return true;
	}

	/**
	 * Helper to get an array of ServiceReferences based on a service name and a
	 * map of required service properties
	 * 
	 * @param serviceName
	 * @param serviceProperties
	 * @return
	 */
	private ServiceReference[] getServiceReferences(String serviceName, Map serviceProperties) {

		SortedMap servicesMap = new TreeMap();
		servicesMap.put(serviceName, serviceProperties);

		String serviceFilter = ServiceFilterGenerator.generateServiceFilter(servicesMap);

		ServiceReference[] srs = null;
		try {
			srs = context.getServiceReferences(serviceName, serviceFilter);
		} catch (InvalidSyntaxException e) {
			logService.log(LogService.LOG_ERROR, e.getMessage());
		}
		return srs;
	}

	/**
	 * If we've called doStart() keep track that the tracker is running
	 * 
	 * @param value
	 */
	private void setIsRunning(boolean value) {
		isRunning = value;
	}

	/**
	 * As far as we know, is the tracker running?
	 * 
	 * @return
	 */
	private boolean isRunning() {
		return isRunning;
	}

	/**
	 * Helper to do the start stuff
	 */
	private void performStart() {
		logService.log(LogService.LOG_DEBUG, trackerName + " Starting tracker, all service dependencies have been met.");
		doStart();
		setIsRunning(true);
	}

	/**
	 * Helper to do the stop stuff
	 */
	private void performStop() {
		logService.log(LogService.LOG_DEBUG, trackerName + " Stopping tracker, service dependencies are no longer met.");
		doStop();
		setIsRunning(false);
	}

	/**
	 * return an instance of the service if we're tracking it
	 */
	public Object getService(Class clazz) {
		return trackedServices.get(clazz);
	}

	/**
	 * Helper to determine if we care about a given ServiceReference
	 * 
	 * @param reference
	 * @return
	 */
	private boolean inServicePropertiesMap(ServiceReference reference) {
		return getServicePropertiesMap().containsKey(getServiceName(reference));
	}

	/**
	 * Helper to pull service name from ServiceReference
	 * 
	 * @param reference
	 * @return
	 */
	private String getServiceName(ServiceReference reference) {
		String[] objClassName = (String[]) reference.getProperty(Constants.OBJECTCLASS);
		return objClassName[0];
	}

	/**
	 * Helper to get the service object from a service reference
	 * 
	 * @param reference
	 * @return
	 */
	private Object getServiceObject(ServiceReference reference) {
		return context.getService(reference);
	}

	/**
	 * Assume that a log service is always available.
	 * 
	 * @param context
	 * @return
	 */
	private LogService getLogService(BundleContext context) {
		ServiceReference sr = context.getServiceReference(LogService.class.getName());
		return (LogService) context.getService(sr);
	}

	/**
	 * Helper to get the tracker name
	 * 
	 * @param context2
	 * @return
	 */
	private String getTrackerName(BundleContext context2) {
		String name = (String) context2.getBundle().getHeaders().get("Bundle-SymbolicName");

		if (name == null) {
			name = context.getBundle().getLocation();
		}

		name = name + " [" + this.getClass().toString() + "]";

		return name;
	}
}
