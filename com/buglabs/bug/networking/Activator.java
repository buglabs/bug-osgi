/*******************************************************************************
 * Copyright (c) 2011 Bug Labs, Inc.
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

package com.buglabs.bug.networking;

import com.buglabs.bug.networking.pub.IAccessPoint;
import com.buglabs.bug.networking.pub.INetworking;

import java.util.Map;
import java.util.List;
import java.util.Vector;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.NamespaceException;

import org.freedesktop.DBus;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import net.connman.Manager;
import net.connman.Struct1;
import net.connman.Service;

import org.osgi.service.http.HttpService;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import org.osgi.service.log.LogService;		
import com.buglabs.util.LogServiceUtil;

public class Activator implements BundleActivator {
	private static LogService logger = null;
	private static BundleContext context;

	private DBusConnection bus;
	private Manager manager;
	private KitchenSink ks;
	
	private HttpService httpService;
	
	private ServiceRegistration registration;
	private ServiceTracker httpServiceTracker;
	
	private final String CONNECT_SERVLET_ALIAS = "/connect";
	private final String DISCONNECT_SERVLET_ALIAS = "/disconnect";
	private final String SETTINGS_SERVLET_ALIAS = "/settings";
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		context = bundleContext;
		logger = LogServiceUtil.getLogService(context);
		
        // bus = DBusConnection.getConnection(DBusConnection.SESSION);
        bus = DBusConnection.getConnection(DBusConnection.SYSTEM);
        manager = bus.getRemoteObject("net.connman", "/", Manager.class);

        KitchenSink.createInstance(bus, context);
        
        ks = KitchenSink.getInstance();
        
        ks.setup();
        
        httpServiceTracker = new ServiceTracker(context, HttpService.class.getName(), null) {
        	public Object addingService(ServiceReference sref) {
        		try {
	        		httpService = (HttpService) context.getService(sref);
	        		try {
	                	httpService.registerServlet(CONNECT_SERVLET_ALIAS, new ConnectServlet(ks), null, null);
	                } catch (NamespaceException e) {
	                	httpService.unregister(CONNECT_SERVLET_ALIAS);
	                	httpService.registerServlet(CONNECT_SERVLET_ALIAS, new ConnectServlet(ks), null, null);
	                }
	                
	                try {
	                	httpService.registerServlet(DISCONNECT_SERVLET_ALIAS, new DisconnectServlet(ks), null, null);
	                } catch (NamespaceException e) {
	                	httpService.unregister(DISCONNECT_SERVLET_ALIAS);
	                	httpService.registerServlet(DISCONNECT_SERVLET_ALIAS, new DisconnectServlet(ks), null, null);
	                }
	                
	                try {
	                	httpService.registerServlet(SETTINGS_SERVLET_ALIAS, new SettingsServlet(ks), null, null);
	                } catch (NamespaceException e) {
	                	httpService.unregister(SETTINGS_SERVLET_ALIAS);
	                	httpService.registerServlet(SETTINGS_SERVLET_ALIAS, new SettingsServlet(ks), null, null);
	                }
        		}
                catch (Exception se) {
                	
                }
                
        		return sref;
        	}
        	
        	public void remove(ServiceReference sref) {
        		stopHttpService();
        	}
        };

        httpServiceTracker.open();
        
        registration = context.registerService(INetworking.class.getName(), ks, null);
        
        logDebug("Waiting...");

	}
	
    /**
	 * @return an instance of the LogService.
	 */
	public static LogService getLogger() {
		return logger;
	}
	
	public static void log(int level, String msg) {
		getLogger().log(level, msg);
	}
	
	public static void logError(String msg) {
		log(LogService.LOG_ERROR, msg);
	}

	public static void logWarning(String msg) {
		log(LogService.LOG_WARNING, msg);
	}
	
	public static void logDebug(String msg) {
		log(LogService.LOG_DEBUG, msg);
	}
	
	public static void logInfo(String msg) {
		log(LogService.LOG_INFO, msg);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		if (registration != null) {
			registration.unregister();
			registration = null;
		}
		ks.teardown();
		// bus.removeSigHandler(Manager.PropertyChanged.class, handler);
		bus.disconnect();
		stopHttpService();
		httpServiceTracker.close();
	}	

	private void stopHttpService() {
		if (httpService != null) {
			httpService.unregister(CONNECT_SERVLET_ALIAS);
			httpService.unregister(DISCONNECT_SERVLET_ALIAS);
			httpService.unregister(SETTINGS_SERVLET_ALIAS);
			httpService = null;
		}
	}
}
