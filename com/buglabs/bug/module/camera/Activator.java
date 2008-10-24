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
package com.buglabs.bug.module.camera;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

import com.buglabs.bug.input.pub.InputEventProvider;
import com.buglabs.bug.module.pub.IModlet;
import com.buglabs.bug.module.pub.IModletFactory;
import com.buglabs.util.LogServiceUtil;

/**
 * Java API bundle for Camera module.
 * @author kgilmer
 *
 */
public class Activator implements BundleActivator, IModletFactory {

	private BundleContext context;
	private ServiceRegistration sr;
	private LogService logService;
	private InputEventProvider bep;
	
	

	public void start(BundleContext context) throws Exception {
		this.context = context;
		logService = LogServiceUtil.getLogService(context);
		
		sr = context.registerService(IModletFactory.class.getName(), this, null);
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
	}

	public IModlet createModlet(BundleContext context, int slotId) {
	
		return new CameraModlet(context, slotId, getModuleId());
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

}
