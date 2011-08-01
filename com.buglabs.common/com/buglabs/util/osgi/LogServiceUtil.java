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
package com.buglabs.util.osgi;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * Utility functions relating to OSGi log service.
 * 
 * @author kgilmer
 * 
 */
public final class LogServiceUtil {

	/**
	 * Utility class.
	 */
	private LogServiceUtil() {
	}
	/**
	 * @param context
	 *            if context is null, it will return a log service that uses
	 *            stdout.
	 * @return Either the first LogService available in the runtime if available
	 *         or a SysoutLogService.
	 */
	public static LogService getLogService(BundleContext context) {
		final LogService logService;
		boolean created = false;
		// See if LogService is available.
		// also protect against context being null
		ServiceReference sr = null;
		if (context != null)
			sr = context.getServiceReference(LogService.class.getName());

		if (sr != null) {
			logService = (LogService) context.getService(sr);
		} else {
			// Return a log service that outputs to stout and sterr.
			logService = new LogService() {

				public void log(int level, String message) {
					System.out.println(levelString(level) + message);
				}

				/* (non-Javadoc)
				 * @see org.osgi.service.log.LogService#log(int, java.lang.String, java.lang.Throwable)
				 */
				public void log(int level, String message, Throwable exception) {
					System.out.println(levelString(level) + message + "\n" + exception.toString());
					if (level == LogService.LOG_ERROR) {
						System.err.println(levelString(level) + message + "\n" + exception.toString());
					}
					exception.printStackTrace(new PrintWriter(System.out, true));
				}

				/* (non-Javadoc)
				 * @see org.osgi.service.log.LogService#log(org.osgi.framework.ServiceReference, int, java.lang.String)
				 */
				public void log(ServiceReference sr, int level, String message) {
					if (sr == null) {
						log(level, message);
						return;
					}
					
					if (message == null)
						message = "[null]";								
					
					System.out.println(levelString(level) + "Service Reference: " + sr.toString() + " " + message);
					if (level == LogService.LOG_ERROR) {
						System.err.println(levelString(level) + "Service Reference: " 
								+ sr.toString() + " " + message);
					}
				}

				/* (non-Javadoc)
				 * @see org.osgi.service.log.LogService#log(org.osgi.framework.ServiceReference, int, java.lang.String, java.lang.Throwable)
				 */
				public void log(ServiceReference sr, int level, String message, Throwable exception) {
					if (exception == null) {
						log(sr, level, message);
						return;
					}
					
					System.out.println(levelString(level) + message + "\n" + exception.toString());
					exception.printStackTrace(new PrintWriter(System.out, true));
					if (level == LogService.LOG_ERROR) {
						System.err.println(levelString(level) + message + "\n" + exception.toString());
						exception.printStackTrace(new PrintWriter(System.err, true));
					}
				}

				/**
				 * Convert the log level int into a human-readable string.  See org.osgi.service.log.LogService for details.
				 * @param level log level
				 * @return human-readable string
				 */
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
		}
		
		if (created && context != null) {
			// Register the service with the framework so that future calls to
			// this static method need not create new objects.
			context.registerService(LogService.class.getName(), logService, null);
		}

		return logService;
	}

	/**
	 * Log a bundle exception and print nested exception if it exists.
	 * 
	 * @param logService LogService
	 * @param message message
	 * @param exception Exception to log
	 */
	public static void logBundleException(LogService logService, String message, BundleException exception) {
		// Add error handling to be specific about what exactly happened.
		logService.log(LogService.LOG_ERROR, message + ": " + exception.getMessage() + "\n" + stackTraceToString(exception));
		stackTraceToString(exception);

		if (exception.getNestedException() != null) {
			logService.log(LogService.LOG_ERROR, "Nested Exception: " + exception.getNestedException().getMessage() 
					+ "\n" + stackTraceToString(exception.getNestedException()));
		}
	}

	/**
	 * Log an exception and print nested exception if it exists.
	 * 
	 * @param logService LogService
	 * @param message message
	 * @param exception Exception to log
	 */
	public static void logBundleException(LogService logService, String message, Exception exception) {
		// Add error handling to be specific about what exactly happened.
		logService.log(LogService.LOG_ERROR, message + ": " + exception.getMessage() + "\n" + stackTraceToString(exception));
	}

	/**
	 * @param throwable Throwable
	 * @return A stack trace as a string.
	 */
	private static String stackTraceToString(Throwable throwable) {
		StringWriter sw = new StringWriter();
		throwable.printStackTrace(new PrintWriter(sw));
		return sw.getBuffer().toString();
	}
}
