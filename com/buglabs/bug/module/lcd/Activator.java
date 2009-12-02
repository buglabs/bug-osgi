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
package com.buglabs.bug.module.lcd;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.bug.module.lcd.pub.LCDModlet;
import com.buglabs.bug.module.pub.BMIModuleProperties;
import com.buglabs.bug.module.pub.IModlet;
import com.buglabs.bug.module.pub.IModletFactory;

public class Activator implements BundleActivator, IModletFactory {
	private BundleContext context;
	private ServiceRegistration sr;
	private static Activator instance;

	public Activator() {
		instance = this;
	}

	public void start(BundleContext context) throws Exception {
		this.context = context;
		sr = context.registerService(IModletFactory.class.getName(), this, null);
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
	}

	public static Activator getInstance() {
		synchronized (instance) {
			return instance;
		}
	}

	public IModlet createModlet(BundleContext context, int slotId) {
		return new LCDModlet(context, slotId, getModuleId());
	}

	public String getModuleId() {
		return (String) context.getBundle().getHeaders().get("Bug-Module-Id");
	}

	public String getName() {
		return (String) context.getBundle().getHeaders().get("Bundle-SymbolicName");
	}

	public String getVersion() {
		return (String) context.getBundle().getHeaders().get("Bundle-Version");
	}

	public BundleContext getBundleContext() {
		return context;
	}

	public String getModuleDriver() {
		return (String) context.getBundle().getHeaders().get("Bug-Module-Driver-Id");
	}

	public IModlet createModlet(BundleContext context, int slotId, BMIModuleProperties properties) {
		return new LCDModlet(context, slotId, getModuleId(), properties);
	}
}
