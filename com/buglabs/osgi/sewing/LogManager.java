package com.buglabs.osgi.sewing;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * Simple abstraction of logging so that sewing can have less dependencies on
 * the BUG libraries
 * 
 * @author bballantine
 * 
 */
public class LogManager {

	private static LogService logService = null;
	private static BundleContext context = null;

	public static synchronized void setContext(BundleContext context) {
		LogManager.context = context;
	}

	public static synchronized void log(int level, String message) {
		log(level, message, null);
	}

	public static synchronized void log(int level, String message, Throwable exception) {
		LogService logger = getLogService();
		if (logger != null) {
			if (exception == null)
				logger.log(level, message);
			else
				logger.log(level, message, exception);
		} else {
			printLog(level, message, exception);
		}
	}

	public static String stackTraceAsString(Throwable throwable) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		throwable.printStackTrace(printWriter);
		return writer.toString();
	}

	private static void printLog(int level, String message, Throwable exception) {
		System.out.println('[' + getLevelString(level) + ']' + ' ' + message + " " + exception.getMessage());
		if (level == LogService.LOG_DEBUG)
			exception.printStackTrace();
	}

	private static String getLevelString(int level) {
		switch (level) {
		case LogService.LOG_DEBUG:
			return "DEBUG";
		case LogService.LOG_ERROR:
			return "ERROR";
		case LogService.LOG_INFO:
			return "INFO";
		case LogService.LOG_WARNING:
		default:
			return "WARNING";
		}
	}

	private static synchronized LogService getLogService() {
		if (logService == null && context != null) {
			ServiceReference sr = context.getServiceReference(LogService.class.getName());
			if (sr != null)
				logService = (LogService) context.getService(sr);
		}
		return logService;
	}

}
