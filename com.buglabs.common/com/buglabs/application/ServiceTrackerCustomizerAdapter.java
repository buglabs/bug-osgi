/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
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

	public ServiceTrackerCustomizerAdapter(BundleContext context, RunnableWithServices runnable, List services) {
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

		String[] objClassName = (String[]) reference.getProperty(Constants.OBJECTCLASS);

		// If the client used ServiceChangeListener, give granular service
		// notifications.
		if (runnable instanceof ServiceChangeListener) {
			((ServiceChangeListener) runnable).serviceAvailable(obj);
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
		String[] objClassName = (String[]) reference.getProperty(Constants.OBJECTCLASS);
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