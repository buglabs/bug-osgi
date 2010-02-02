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
package com.buglabs.bug.base;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.servlet.ServletException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import com.buglabs.application.IServiceProvider;
import com.buglabs.application.RunnableWithServices;
import com.buglabs.application.ServiceTrackerHelper;
import com.buglabs.bug.base.pub.IBUG20BaseControl;
import com.buglabs.bug.base.pub.IBaseAudioPlayer;
import com.buglabs.bug.base.pub.IShellService;
import com.buglabs.bug.base.pub.ITimeProvider;
import com.buglabs.support.SupportInfoTextFormatter;
import com.buglabs.support.SupportInfoXMLFormatter;
import com.buglabs.util.LogServiceUtil;

/**
 * This bundle offers base unit features to the runtime, such as date/time and
 * power information.
 * 
 * @author kgilmer
 * 
 */
public class Activator implements BundleActivator, ITimeProvider {

	private static final String BUG_BASE_VERSION_KEY = "bug.base.version";

	private static final String INFO_SERVLET_PATH = "/support";

	private static final String INFO_SERVLET_HTML_PATH = "/support.html";

	private ServiceRegistration timeReg;

	private ServiceRegistration audioReg;

	private ServiceTracker menuTracker;

	private LogService logService;

	private BUGBaseControl bbc;

	private ServiceRegistration baseControlReg;

	private SoundPlayer soundplayer;

	private ServiceTracker httpTracker;

	private ServiceRegistration btReg;

	private ServiceRegistration sr;

	public Date getTime() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * Register all the OSGi services that this bundle provides.
	 * 
	 * @param context
	 */
	private void registerServices(BundleContext context) {
		timeReg = context.registerService(ITimeProvider.class.getName(), this, null);
		baseControlReg = context.registerService(IBUG20BaseControl.class.getName(), bbc, getBaseControlServiceProperties());
		
		if (soundplayer != null) {
			audioReg = context.registerService(IBaseAudioPlayer.class.getName(), soundplayer, null);
		}
		
		try {
			btReg = context.registerService(LocalDevice.class.getName(), LocalDevice.getLocalDevice(), null);
			// I'm also going to need to figure out whether hci0 is the base
			// bluetooth or whatever
			// need to think this through more
		} catch (BluetoothStateException e) {
			logService.log(LogService.LOG_INFO, "No Bluetooth Device found.  Not registering javax.bluetooth.LocalDevice as a service");
		}
	}

	/**
	 * @return A dictionary with properties for base control
	 */
	private Dictionary getBaseControlServiceProperties() {
		Dictionary d = new Hashtable();

		d.put(BUG_BASE_VERSION_KEY, getBaseVersion());

		return d;
	}

	/**
	 * @return version of BUG base this code is running on.
	 */
	private String getBaseVersion() {
		return "2.0";
	}

	public void start(final BundleContext context) throws Exception {
		logService = LogServiceUtil.getLogService(context);

		// Set base version property.
		System.setProperty(BUG_BASE_VERSION_KEY, getBaseVersion());

		soundplayer = new SoundPlayer("hw:0,0");
	
		registerServices(context);

		// Create a ST for the HTTP Service, create the 'info' servlet when
		// available.
		httpTracker = ServiceTrackerHelper.createAndOpen(context, HttpService.class.getName(), new RunnableWithServices() {
			public void allServicesAvailable(IServiceProvider serviceProvider) {
				try {
					logService.log(LogService.LOG_INFO, "Registering info service.");
					HttpService httpService = (HttpService) serviceProvider.getService(HttpService.class);
					// register xml version
					httpService.registerServlet(INFO_SERVLET_PATH, new SupportServlet(new BUGSupportInfo(context), new SupportInfoXMLFormatter()), null, null);
					// register html version
					httpService.registerServlet(INFO_SERVLET_HTML_PATH, new SupportServlet(new BUGSupportInfo(context), new SupportInfoTextFormatter()), null, null);
				} catch (ServletException e) {
					logService.log(LogService.LOG_ERROR, "An error occurred launching Info servlet: " + e.getMessage());
				} catch (NamespaceException e) {
					logService.log(LogService.LOG_ERROR, "An error occurred launching Info servlet: " + e.getMessage());
				}
			}

			public void serviceUnavailable(IServiceProvider serviceProvider, ServiceReference sr, Object service) {
				((HttpService) serviceProvider.getService(HttpService.class)).unregister(INFO_SERVLET_PATH);
			}

		});

		sr = context.registerService(IShellService.class.getName(), new ShellService(), null);

		signalStartup();
	}

	/**
	 * Signal to user that OSGi runtime is up and running.
	 */
	private void signalStartup() {
		Thread t = new Thread(new Runnable() {

			public void run() {
				try {
					for (int i = 0; i < 4; ++i) {
						bbc.setLED(i);
						Thread.sleep(300);
					}
					Thread.sleep(500);
					for (int i = 0; i < 4; ++i) {
						bbc.clearLED(i);
					}
				} catch (Exception e) {
				}
			}

		});
		t.start();
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		httpTracker.close();
		menuTracker.close();
		unregisterServices(context);
	}

	private void unregisterServices(BundleContext context) {
		timeReg.unregister();
		baseControlReg.unregister();
		audioReg.unregister();
		btReg.unregister();
	}
}