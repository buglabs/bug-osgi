/*******************************************************************************
 * Copyright (c) 2008, 2009, 2011 Bug Labs, Inc.
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
package com.buglabs.bug.bmi.pub;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

import com.buglabs.bug.bmi.sysfs.BMIDevice;
import com.buglabs.util.osgi.BUGBundleConstants;
import com.buglabs.util.osgi.LogServiceUtil;

/**
 * Common abstract BundleActivator for bundles that contribute IModletFactories, ie
 * bundles that provide API and service code for BUG modules.
 * 
 * @author kgilmer
 * 
 */
public abstract class AbstractBUGModuleActivator implements BundleActivator, IModletFactory {

	private static BundleContext context;
	private ServiceRegistration sr;
	private Dictionary headers;
	private static LogService log;
	
	/**
	 * @return an instance of LogService or null if bundle has not been started or has been stopped.
	 */
	public static LogService getLog() {
		return log;
	}
	
	/**
	 * @return an instance of local BundleContext or null if bundle has not been started or has been stopped.
	 */
	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext context) throws Exception {
		AbstractBUGModuleActivator.context = context;
		AbstractBUGModuleActivator.log = LogServiceUtil.getLogService(context);
		this.headers = context.getBundle().getHeaders();
		
		sr = context.registerService(IModletFactory.class.getName(), this, getModletProperties());		
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		sr = null;
		context = null;
		log = null;
	}

	/* (non-Javadoc)
	 * @see com.buglabs.bug.bmi.pub.IModletFactory#getModuleId()
	 */
	public String getModuleId() {
		return headers.get("Bug-Module-Id").toString();
	}

	/* (non-Javadoc)
	 * @see com.buglabs.bug.bmi.pub.IModletFactory#getName()
	 */
	public String getName() {
		return headers.get("Bundle-SymbolicName").toString();
	}

	/* (non-Javadoc)
	 * @see com.buglabs.bug.bmi.pub.IModletFactory#getVersion()
	 */
	public String getVersion() {
		return headers.get("Bundle-Version").toString();
	}

	/**
	 * @return driver id
	 */
	public String getModuleDriver() {
		return headers.get("Bug-Module-Driver-Id").toString();
	}
	
	/**
	 * Get the service properties associated with the IModletFactory registration in the OSGi service registry.
	 * 
	 * Clients can override this to provide custom/additional properties.
	 * 
	 * @return A dictionary with some default values.  
	 */
	public Dictionary getModletProperties() {
		Dictionary d = new Hashtable();
		
		d.put(BUGBundleConstants.MODLET_FACTORY_PROVIDER, getName());
		d.put(BUGBundleConstants.MODLET_FACTORY_SOURCE, this.getClass().getName());
		d.put(BUGBundleConstants.MODLET_FACTORY_ID, getModuleId());
		
		return d;
	}

	public abstract IModlet createModlet(BundleContext context, int slotId, BMIDevice device);
}
