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

import com.buglabs.util.LogServiceUtil;

/**
 * This class adapts AbstractServiceTracker to RunnableWithServices
 * 
 * @author kgilmer
 * 
 */
class ServiceTrackerCustomizerAdapter implements ServiceTrackerCustomizer, IServiceProvider {

	private final RunnableWithServices runnable;
	private final BundleContext context;
	final private Map servicesMap;
	private boolean started = false;
	private LogService logService;

	public ServiceTrackerCustomizerAdapter(BundleContext context,
			RunnableWithServices runnable, List services) {
		this.context = context;
		this.runnable = runnable;
		logService = LogServiceUtil.getLogService(context);
		servicesMap = populateServiceMap(services);
	}

	private Map populateServiceMap(List services) {
		Map map = new HashMap();

		for (Iterator i = services.iterator(); i.hasNext();) {
			map.put(i.next(), null);
		}

		return map;
	}

	private void doStart() {
		runnable.allServicesAvailable(this);
		started = true;
	}

	public void doStop(ServiceReference reference, Object service) {
		runnable.serviceUnavailable(this, reference, service);
		started = false;
	}

	public Object addingService(ServiceReference reference) {
		Object obj = context.getService(reference);

		if (obj == null) {
			throw new RuntimeException("Service tracker registered null service: " + reference.toString());
		}
		
		String[] objClassName = (String[]) reference
				.getProperty(Constants.OBJECTCLASS);

		//If the client used ServiceChangeListener, give granular service notifications.
		if (runnable instanceof ServiceChangeListener) {
			((ServiceChangeListener)runnable).serviceAvailable(obj);
		}
		
		if (servicesMap.containsKey(objClassName[0]) && servicesMap.get(objClassName[0]) == null) {
			logService.log(LogService.LOG_DEBUG, context.getBundle().getLocation() + "  Tracking Service: " + objClassName[0] + " with implementation: " + obj.toString());			
			servicesMap.put(objClassName[0], obj);
			
			if (canStart()) {
				logService.log(LogService.LOG_DEBUG, "Tracker Complete.");
				doStart();
			}
		}
		
		return obj;
	}

	private boolean canStart() {
		if (started) {
			logService.log(LogService.LOG_DEBUG, "Tracker unable to start because already started.");
			return false;
		}

		for (Iterator i = servicesMap.keySet().iterator(); i.hasNext();) {
			Object serviceKey = i.next();
			if (servicesMap.get(serviceKey) == null) {
				logService.log(LogService.LOG_DEBUG, "Tracker unable to start because service " + serviceKey.toString() + " is unavailable.");
				return false;
			}
		}

		return true;
	}

	public void modifiedService(ServiceReference arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	public void removedService(ServiceReference reference, Object service) {
		doStop(reference, service);
		String[] objClassName = (String[]) reference
				.getProperty(Constants.OBJECTCLASS);
		servicesMap.put(objClassName[0], null);
	}

	public Object getService(Class clazz) {
		return servicesMap.get(clazz.getName());
	}

	public List getAvailableServices() {
		List svcs = new ArrayList();

		for (Iterator i = servicesMap.keySet().iterator(); i.hasNext();) {
			Object svc = servicesMap.get(i.next());

			if (svc != null) {
				svcs.add(svc);
			}
		}

		return svcs;
	}

	public List getUnavailableServiceNames() {
		List svcs = new ArrayList();

		for (Iterator i = servicesMap.keySet().iterator(); i.hasNext();) {
			String svcName = (String) i.next();
			Object svc = servicesMap.get(svcName);

			if (svc == null) {
				svcs.add(svcName);
			}
		}

		return svcs;
	}
}