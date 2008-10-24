/* Copyright (c) 2007, 2008 Bug Labs, Inc.
 * All rights reserved.
 *   
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License version  
 * 2 only, as published by the Free Software Foundation.   
 *   
 * This program is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * General Public License version 2 for more details (a copy is  
 * included at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html).   
 *   
 * You should have received a copy of the GNU General Public License  
 * version 2 along with this work; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA   
 *
 */
package com.buglabs.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Service tracker for the BugApplication Bundle;
 * 
 */
public abstract class AbstractServiceTracker implements ServiceTrackerCustomizer, IServiceProvider {

	protected BundleContext context;

	private Map servicesMap;

	protected List services;

	boolean started = false;

	private LogService logService;

	private String trackerName;

	public AbstractServiceTracker(BundleContext context) {
		this.context = context;
		servicesMap = new HashMap();
		logService = getLogService(context);
		trackerName = getTrackerName(context);
		initServices();
	}

	private String getTrackerName(BundleContext context2) {
		String name = (String) context2.getBundle().getHeaders().get("Bundle-SymbolicName");
		
		if (name == null) {
			name = context.getBundle().getLocation();
		}
		
		name = name + " [" + this.getClass().toString() + "]";
		
		return name;
	}

	/**
	 * Assume that a log service is always available.
	 * @param context
	 * @return
	 */
	private LogService getLogService(BundleContext context) {
		ServiceReference sr = context.getServiceReference(LogService.class.getName());
		return (LogService) context.getService(sr);
	}
	
	/**
	 * Invoked when all services have been registered.
	 * 
	 */
	public abstract void doStart();

	/**
	 * Invoked when a service is unregistered.
	 * 
	 */
	public abstract void doStop();

	/**
	 * Implementations should add services of interest
	 */
	public void initServices() {
		logService.log(LogService.LOG_DEBUG, trackerName + " A tracker in this bundle is tracking no services.");
	}

	/**
	 * Used by OSGi to signal that a service was added.
	 * 
	 */
	public Object addingService(ServiceReference reference) {
		Object obj = context.getService(reference);
		String[] objClassName = (String[]) reference.getProperty(Constants.OBJECTCLASS);
		Object retval = null;
		logService.log(LogService.LOG_DEBUG, trackerName + " Tracker found service: " + objClassName[0]);
		
		if (!servicesMap.containsKey(objClassName[0])) {
			servicesMap.put(objClassName[0], obj);
			logService.log(LogService.LOG_DEBUG, trackerName + " Service added to service map: " + objClassName[0]);
			retval = obj;

			if (canStart()) {
				logService.log(LogService.LOG_DEBUG, trackerName + " Starting tracker, all services are available.");
				doStart();
				started = true;
			}
		}
		return retval;
	}

	public void modifiedService(ServiceReference reference, Object service) {

	}

	/**
	 * Used by OSGi to signal that a service was removed.
	 * 
	 */
	public void removedService(ServiceReference reference, Object service) {
		Object obj = context.getService(reference);
		boolean serviceRemoved = false;

		String[] objClassName = (String[]) reference.getProperty(Constants.OBJECTCLASS);

		if (servicesMap.get(objClassName[0]).equals(obj)) {
			logService.log(LogService.LOG_DEBUG, trackerName + " removing service: " + objClassName[0]);
			servicesMap.remove(objClassName[0]);
			serviceRemoved = true;
		}

		if (hasStarted() && serviceRemoved) {
			try {
				logService.log(LogService.LOG_DEBUG, trackerName + " stopping tracker.");
				doStop();				
			} catch (Exception e) {
				logService.log(LogService.LOG_ERROR, e.getMessage());
			}

			started = false;
		}
	}

	/**
	 * Determines if the application can be started.
	 * 
	 * @returns true when the application is not running and there's a handle
	 *          for each service.
	 */
	protected boolean canStart() {
		if (hasStarted()) {
			return false;
		}

		Iterator servicesIter = services.iterator();

		while (servicesIter.hasNext()) {
			if (servicesMap.get((String) servicesIter.next()) == null) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Helper method to retrieve a service of type class.
	 * 
	 */
	public Object getService(Class clazz) {
		return servicesMap.get(clazz.getName());
	}

	/**
	 * Used to retrieve a list of qualified service names. If you want your
	 * application to depend on another service, simply add the fully qualified
	 * name of the service to this list.
	 * 
	 * @return a list of Strings containing the fully qualified name of each
	 *         service.
	 * 
	 */
	public List getServices() {
		if (services == null) {
			services = new ArrayList();
		}

		return services;
	}

	public BundleContext getBundleContext() {
		return context;
	}

	public boolean hasStarted() {
		return started;
	}

	public void stop() {
		if (hasStarted()) {
			doStop();
			started = false;
		}
	}
}
