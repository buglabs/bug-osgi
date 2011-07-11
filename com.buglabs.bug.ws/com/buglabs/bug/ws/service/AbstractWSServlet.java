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
package com.buglabs.bug.ws.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogService;

import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.services.ws.PublicWSProvider2;
import com.buglabs.util.osgi.FilterUtil;
import com.buglabs.util.osgi.LogServiceUtil;
import com.buglabs.util.osgi.OSGiServiceLoader;

/**
 * Abstract class to hold common functionality for different Service views
 * currently extended by WSServlet (default, xml view) and WSHtmlServlet (html
 * view).
 * 
 * 
 */
public abstract class AbstractWSServlet extends HttpServlet implements ServiceListener {
	private static final long serialVersionUID = 3348932042438010201L;

	private final Map<String, PublicWSProvider> serviceMap;
	private static int serviceCounter = 2;
	private static volatile boolean listenerRegistered = false;

	private ConfigurationAdmin configAdmin;

	private final BundleContext context;

	private LogService log;

	/**
	 * @param context
	 *            BundleContext
	 * @param serviceMap
	 *            services
	 */
	public AbstractWSServlet(BundleContext context, Map<String, PublicWSProvider> serviceMap) {
		this.context = context;
		this.log = LogServiceUtil.getLogService(context);
		this.serviceMap = serviceMap;

		synchronized (serviceMap) {
			try {
				if (!listenerRegistered) {
					listenerRegistered = true;
					addExistingServices(context, serviceMap);
					context.addServiceListener(this,
							FilterUtil.generateServiceFilter(
									PublicWSProvider2.class.getName(), 
									PublicWSProvider.class.getName()));
				}
			} catch (Exception e) {
				// Since our filter does not change, this should never occur.
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param context
	 *            BundleContext
	 * @param serviceMap
	 *            services
	 * @param configAdmin
	 *            ConfigAdmin instance
	 */
	public AbstractWSServlet(BundleContext context, Map<String, PublicWSProvider> serviceMap, ConfigurationAdmin configAdmin) {
		this(context, serviceMap);
		this.configAdmin = configAdmin;
	}

	/**
	 * Add services that are already registered into the service map.
	 * 
	 * @param context2
	 *            BundleContext
	 * @param serviceMap2
	 *            services
	 * @throws Exception
	 *             on OSGi Filter error
	 */
	private void addExistingServices(BundleContext context2, Map<String, PublicWSProvider> serviceMap2) throws Exception {

		OSGiServiceLoader.loadServices(context, PublicWSProvider.class.getName(), null, new OSGiServiceLoader.IServiceLoader() {
			public void load(Object service) {
				addPublicService((PublicWSProvider) service);
			}
		});

		OSGiServiceLoader.loadServices(context, PublicWSProvider2.class.getName(), null, new OSGiServiceLoader.IServiceLoader() {
			public void load(Object service) {
				addPublicService((PublicWSProvider) service);
			}
		});
	}

	/**
	 * Override this method to handle all default requests.
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param reqMethod
	 *            reqMethod
	 * @throws IOException
	 *             on IO exception
	 * @throws ServletException
	 *             on internal servlet exception
	 */
	protected abstract void executeHttpMethod(HttpServletRequest req, HttpServletResponse resp, int reqMethod) throws IOException,
			ServletException;

	/**
	 * All the main entry points by default call executeHttpMethod, but these
	 * methods can be overridden in the implementor if a different behavior is
	 * desired.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		executeHttpMethod(req, resp, PublicWSProvider.GET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		executeHttpMethod(req, resp, PublicWSProvider.DELETE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		executeHttpMethod(req, resp, PublicWSProvider.POST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		executeHttpMethod(req, resp, PublicWSProvider.PUT);
	}

	/**
	 * @param provider
	 *            PublicWSProvider
	 */
	public void addPublicService(PublicWSProvider provider) {
		if (!serviceMap.containsKey(provider.getPublicName().toUpperCase())) {
			serviceMap.put(provider.getPublicName().toUpperCase(), provider);
		} else if (provider instanceof PublicWSProvider2) {
			String newName = provider.getPublicName() + serviceCounter;
			((PublicWSProvider2) provider).setPublicName(newName);
			serviceMap.put(newName.toUpperCase(), provider);
			serviceCounter++;
		} else {
			log.log(LogService.LOG_ERROR, "Unable to add service " + provider.getPublicName() + ": name is already being used.");
		}
	}

	/**
	 * @param provider
	 *            PublicWSProvider
	 */
	public void removePublicService(PublicWSProvider provider) {
		if (serviceMap.containsKey(provider.getPublicName().toUpperCase())) {
			serviceMap.remove(provider.getPublicName().toUpperCase());
		} else {
			log.log(LogService.LOG_ERROR, "Unable to remove service " + provider.getPublicName() + ": doesn't exist.");
		}
	}

	/**
	 * Use in child class to get list of services available.
	 * 
	 * @return service map
	 */
	protected Map<String, PublicWSProvider> getServiceMap() {
		return serviceMap;
	}

	/**
	 * Use in child class to get a reference to the current ConfigurationAdmin.
	 * 
	 * @return ConfigAdmin instance.
	 */
	protected ConfigurationAdmin getConfigurationAdmin() {
		return configAdmin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.
	 * ServiceEvent)
	 */
	public void serviceChanged(ServiceEvent event) {
		Object svc = context.getService(event.getServiceReference());
		PublicWSProvider p = null;

		if (svc instanceof PublicWSProvider || svc instanceof PublicWSProvider2) {
			p = (PublicWSProvider) svc;
		}

		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			addPublicService(p);
			break;
		case ServiceEvent.UNREGISTERING:
			removePublicService(p);
			break;
		default:
			throw new RuntimeException("Unknown event type");
		}
	}

	/**
	 * Checks whether service is enabled.
	 * 
	 * @param admin
	 *            {@link ConfigurationAdmin}
	 * @param serviceName
	 *            Name of the service for which enabled property to be checked
	 * @return Returns <code>true</code> if service is enabled,
	 *         <code>false</code> otherwise. Note that true will be returned if
	 *         configuration does not have enabled property.
	 * @throws IOException on configadmin error
	 */
	protected static boolean isServiceEnabled(ConfigurationAdmin admin, String serviceName) throws IOException {
		Configuration configuration = admin.getConfiguration(PublicWSProvider.PACKAGE_ID + "." + serviceName);

		if (configuration == null) {
			return true;
		}

		if (configuration.getProperties() == null) {
			return true;
		} else {
			Object enabledProperty = configuration.getProperties().get("enabled");
			if (enabledProperty == null) {
				return true;
			}

			return ((Boolean) enabledProperty).booleanValue();
		}
	}
}
