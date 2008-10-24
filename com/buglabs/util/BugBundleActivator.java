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
package com.buglabs.util;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * Utility base bundle activator ensures some log service is available.
 * 
 * @author ken
 * 
 */
public class BugBundleActivator implements BundleActivator, LogService {

	private LogService logService;

	private static BugBundleActivator activatorReference;

	public void start(BundleContext context) throws Exception {
		ServiceReference sr = context.getServiceReference(LogService.class.getName());
		if (sr != null) {
			logService = (LogService) context.getService(sr);
		}

		activatorReference = this;
	}

	public void stop(BundleContext context) throws Exception {
		activatorReference = null;
	}

	public void log(int level, String message, Throwable exception) {
		if (logService != null) {
			logService.log(level, message, exception);
		} else {
			System.out.println(message + " Exception: " + exception.getMessage() + "  level: " + level);
		}
	}

	public void log(int level, String message) {
		if (logService != null) {
			logService.log(level, message);
		} else {
			System.out.println(message + "  level: " + level);
		}
	}

	public void log(ServiceReference sr, int level, String message, Throwable exception) {
		if (logService != null) {
			logService.log(sr, level, message, exception);
		} else {
			System.out.println(message + "Service Reference: " + sr.getClass().getName() + "  Exception: " + exception.getMessage()
					+ "  level: " + level);
		}
	}

	public void log(ServiceReference sr, int level, String message) {
		if (logService != null) {
			logService.log(sr, level, message);
		} else {
			System.out.println(message + "Service Reference: " + sr.getClass().getName() + "  level: " + level);
		}
	}

	public static BugBundleActivator getDefault() {
		return activatorReference;
	}
}
