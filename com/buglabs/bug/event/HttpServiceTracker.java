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
package com.buglabs.bug.event;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Servlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.buglabs.util.LogServiceUtil;

/**
 * A utility class that will handle management of servlets when containers come
 * in and out of runtime scope.
 * 
 * @author ken
 * 
 */
public class HttpServiceTracker implements ServiceTrackerCustomizer {
	protected Hashtable config;

	protected BundleContext context;

	protected HttpService httpService;

	protected final Map servlets;

	protected final Servlet servlet;

	protected final LogService logService;

	protected final String alias;

	/**
	 * @param context
	 * @param config
	 * @param servlets -
	 *            a Map of servlets, with the key being the path of the servlet
	 *            on the http container.
	 */
	public HttpServiceTracker(BundleContext context, Hashtable config, Map servlets, LogService logService) {
		this.config = config;
		this.context = context;
		this.servlets = servlets;
		this.logService = logService;
		this.servlet = null;
		this.alias = null;
	}

	/**
	 * @param context
	 * @param config
	 * @param servlet
	 * @param logService
	 */
	public HttpServiceTracker(BundleContext context, Hashtable config, Servlet servlet, String alias, LogService logService) {
		this.config = config;
		this.context = context;
		this.alias = alias;
		this.servlets = null;
		this.logService = logService;
		this.servlet = servlet;
	}

	public HttpServiceTracker(BundleContext context2, Hashtable config2, Map servlets2) {
		this.config = config2;
		this.context = context2;
		this.servlets = servlets2;
		alias = null;
		servlet = null;

		logService = LogServiceUtil.getLogService(context);
	}

	public Object addingService(ServiceReference reference) {
		httpService = (HttpService) context.getService(reference);

		try {
			if (servlet == null && servlets != null) {
				for (Iterator i = servlets.keySet().iterator(); i.hasNext();) {
					String path = (String) i.next();
					httpService.registerServlet(path, (Servlet) servlets.get(path), config, null);
					logService.log(LogService.LOG_INFO, "Registered servlet at " + path + ".");
				}
			} else {
				httpService.registerServlet(alias, servlet, config, null);
				logService.log(LogService.LOG_INFO, "Registered servlet at " + alias + ".");
			}
		} catch (Exception e) {
			logService.log(LogService.LOG_ERROR, "Failed to register servlet.", e);
		}

		return reference;
	}

	public void modifiedService(ServiceReference reference, Object service) {

	}

	public void removedService(ServiceReference reference, Object service) {
		for (Iterator i = servlets.keySet().iterator(); i.hasNext();) {
			String path = (String) i.next();
			httpService.unregister(path);
			logService.log(LogService.LOG_DEBUG, "Unregistered servlet at " + path + ".");
		}
	}
}
