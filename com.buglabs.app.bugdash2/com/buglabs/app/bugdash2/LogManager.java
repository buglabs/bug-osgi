package com.buglabs.app.bugdash2;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * LogManager -- provided by bballantine
 * @author akweon
 *
 */
public class LogManager {

	private final static String APP_MARKER = "[BUGdash] "; 
	private static LogService logService = null;
	private static BundleContext context = null;
	
	public static void setContext(BundleContext context) {
		LogManager.context = context;
	}
	
	public static void logInfo(String message) {
		if (message != null && !message.equals("")) {
			LogService logger = getLogService();
			if (logger != null) logger.log(LogService.LOG_INFO, APP_MARKER + message);
			else System.out.println("[INFO] " + APP_MARKER + message);
		}
	}
	
	public static void logDebug(String message) {
		if (message != null && !message.equals("")) { 
			LogService logger = getLogService();
			if (logger != null) logger.log(LogService.LOG_DEBUG, APP_MARKER + message);
			else System.out.println("[DEBUG] " + APP_MARKER + message);	
		}
	}
	
	public static void logError(String message) {
		if (message != null && !message.equals("")) {
			LogService logger = getLogService();
			if (logger != null) logger.log(LogService.LOG_ERROR,  APP_MARKER + message);
			else System.out.println("[ERROR] " + APP_MARKER + message);
		}
	}
	
	public static void logWarning(String message) {
		if (message != null && !message.equals("")) {
			LogService logger = getLogService();
			if (logger != null) logger.log(LogService.LOG_WARNING,  APP_MARKER + message);
			else System.out.println("[WARN] " + APP_MARKER + message);	
		}
	}
	
	private static LogService getLogService() {
		if (logService == null && context != null) {
			ServiceReference sr = context.getServiceReference(LogService.class.getName());
			if (sr != null) logService = (LogService) context.getService(sr);				
		}
		return logService;
	}
}
