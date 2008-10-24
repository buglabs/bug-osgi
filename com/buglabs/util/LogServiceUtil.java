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

import java.io.PrintWriter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * Utility functions relating to OSGi log service.
 * 
 * @author kgilmer
 * 
 */
public class LogServiceUtil {
	/**
	 * @param context
	 * @return Either the first LogService available in the runtime if available
	 *         or a SysoutLogService.
	 */
	public static LogService getLogService(BundleContext context) {
		final LogService logService;
		boolean created = false;
		//See if LogService is available.
		ServiceReference sr = context.getServiceReference(LogService.class.getName());
		
		
		if (sr != null) {
			logService = (LogService) context.getService(sr);
		} else {
			//No service available, we need to provide one.
			String quiet = context.getProperty("ch.ethz.iks.concierge.log.quiet");
			//Determine if quiet operation is desired.
			if (quiet == null || quiet.equals("false")) {
				//Return a log service that outputs to stout and sterr.
				logService = new LogService() {

					public void log(int level, String message) {
						System.out.println(levelString(level) + message);
					}

					public void log(int level, String message, Throwable exception) {
						System.out.println(levelString(level) + message + "\n" + exception.toString());
						if (level == LogService.LOG_ERROR) {
							System.err.println(levelString(level) + message + "\n" + exception.toString());
						}
						exception.printStackTrace(new PrintWriter(System.out, true));
					}

					public void log(ServiceReference sr, int level, String message) {
						System.out.println(levelString(level) + "Service Reference: " + sr.toString() + " " + message);
						if (level == LogService.LOG_ERROR) {
							System.err.println(levelString(level) + "Service Reference: " + sr.toString() + " " + message);
						}
					}

					public void log(ServiceReference sr, int level, String message, Throwable exception) {
						System.out.println(levelString(level) + message + "\n" + exception.toString());
						exception.printStackTrace(new PrintWriter(System.out, true));
						if (level == LogService.LOG_ERROR) {
							System.err.println(levelString(level) + message + "\n" + exception.toString());
							exception.printStackTrace(new PrintWriter(System.err, true));
						}
					}

					private String levelString(int level) {
						switch (level) {
						case LogService.LOG_DEBUG:
							return "[DEBUG]   ";
						case LogService.LOG_ERROR:
							return "[ERROR]   ";
						case LogService.LOG_INFO:
							return "[INFO]    ";
						case LogService.LOG_WARNING:
							return "[WARNING] ";
						default:
							return "[UNKNOWN] ";
						}
					}
				};
				created = true;
			} else {
				//Return a log service that absorbs all messages.
				logService = new LogService() {

					public void log(int level, String message) {
					}

					public void log(int level, String message, Throwable exception) {
					}

					public void log(ServiceReference sr, int level, String message) {
					}

					public void log(ServiceReference sr, int level, String message, Throwable exception) {
					}
				};
				created = true;
			}
		}
		
		if (created) {
			// Register the service with the framework so that future calls to this static method need not create new objects.
			context.registerService(LogService.class.getName(), logService, null);
		}

		return logService;
	}
}
