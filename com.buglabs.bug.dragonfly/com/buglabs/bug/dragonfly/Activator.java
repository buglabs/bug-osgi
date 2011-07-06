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
package com.buglabs.bug.dragonfly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;

import com.buglabs.util.osgi.ServiceTrackerHelper.ManagedInlineRunnable;
import com.buglabs.module.IModuleControl;
import com.buglabs.util.osgi.FilterUtil;
import com.buglabs.util.osgi.LogServiceUtil;
import com.buglabs.util.xml.XmlNode;

/**
 * This bundle is responsible for accepting clients for callback notifications
 * based on model change events.
 * 
 * @author ken
 * 
 */
public class Activator implements BundleActivator, ServiceListener, ManagedInlineRunnable {
	public static final int MODEL_CHANGE_EVENT_LISTEN_PORT = 8990;

	private static final String DRAGONFLY_WS_PATH = "/event";

	private Map eventMap;

	private BundleContext context;

	private LogService logService;

	private HttpService httpService;
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		logService = LogServiceUtil.getLogService(context);
		eventMap = new Hashtable();

		context.addServiceListener(this, FilterUtil.generateServiceFilter(IModuleControl.class.getName()));
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		context.removeServiceListener(this);		
	}

	/**
	 * Generate XML message based on event.
	 * @param topic
	 * @param type
	 * @param module
	 * @return
	 */
	private String createMessage(String topic, String type, String module) {
		XmlNode root = new XmlNode("event");
		new XmlNode(root, "topic", topic);
		new XmlNode(root, "source", module);
		new XmlNode(root, "type", type);

		return root.toString();
	}

	/**
	 * Send message to subscriber.
	 * 
	 * @param s
	 * @param message
	 * @throws IOException
	 */
	private void notifySubscriber(DragonflyEventSubscriber s, String message) throws IOException {
		logService.log(LogService.LOG_DEBUG, "Notifying " + s.getUrl() + " of model update.");

		URL url = new URL(s.getUrl());
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);

		OutputStreamWriter osr = new OutputStreamWriter(conn.getOutputStream());
		osr.write(message);
		osr.flush();

		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line, resp = new String("");
		while ((line = rd.readLine()) != null) {
			resp = resp + line + "\n";
		}
		osr.close();
		rd.close();
	}

	public void serviceChanged(ServiceEvent event) {
		ServiceReference sr = event.getServiceReference();

		IModuleControl control = (IModuleControl) context.getService(sr);

		String type;
		// TODO fix this so the event queue name is no longer needed. this is
		// artifact of using EventAdmin originally.
		if (event.getType() == ServiceEvent.REGISTERED) {
			type = "INSERT";
		} else if (event.getType() == ServiceEvent.UNREGISTERING) {
			type = "REMOVE";
		} else {
			// For now we ignore service modification events.
			return;
		}
		String module = control.getModuleName();
		String topic = "com/buglabs/event/module";

		logService.log(LogService.LOG_INFO, "Received new " + type + " model change event for module " + module);

		List subList = (List) eventMap.get(topic);
		List removeList = null;

		if (subList != null) {
			for (Iterator i = subList.iterator(); i.hasNext();) {
				DragonflyEventSubscriber s = (DragonflyEventSubscriber) i.next();

				try {
					notifySubscriber(s, createMessage(topic, type, module));
				} catch (IOException e) {
					if (!e.getMessage().equals("Unexpected end of file from server") && !e.getMessage().equals("Connection reset")) {
						if (removeList == null) {
							removeList = new ArrayList();
						}
						logService.log(LogService.LOG_WARNING, "Removing subscriber " + s.getUrl() + " due to I/O Exception: " + e.getMessage());
						removeList.add(s);
					}
				}
			}

			if (removeList != null) {
				for (Iterator i = removeList.iterator(); i.hasNext();) {
					subList.remove(i.next());
				}
			}
		}
	}

	public void run(Map services) {
		httpService = (HttpService) services.get(HttpService.class.getName());

		try {
			httpService.registerServlet(DRAGONFLY_WS_PATH, new DragonflyEventServlet(eventMap), null , null);
			logService.log(LogService.LOG_INFO, "Registered servlet at: " + DRAGONFLY_WS_PATH);			
		} catch (Exception e) {
			logService.log(LogService.LOG_ERROR, "Failed to register servlet.", e);
		}
	}

	public void shutdown() {
		if (httpService != null)
			httpService.unregister(DRAGONFLY_WS_PATH);
	}	
}
