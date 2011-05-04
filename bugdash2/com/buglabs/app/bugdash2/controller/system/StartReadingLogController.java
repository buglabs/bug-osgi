package com.buglabs.app.bugdash2.controller.system;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.LogFile;
import com.buglabs.app.bugdash2.ShellManager;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelRoot;

public class StartReadingLogController extends ApplicationController {
	
	private LogController log_controller;
	
	public String getTemplateName() { return "message.fml"; }

	public StartReadingLogController(LogController displayLogController) {
		log_controller = displayLogController;
	}

	public TemplateModelRoot get(RequestParameters params, 
			HttpServletRequest req, HttpServletResponse resp) {
		
		String key 		= params.get("key");
		Map logs 		= log_controller.getLogs();
		Object logObj 	= logs.get(key);
		
		if (logObj != null) {
			LogFile log = (LogFile)logObj;
			log.runThread(ShellManager.getShell()); 
		}
		SimpleHash root = new SimpleHash(); 
		root.put("message", key + " started"); 
		return root; 
	}
	
}
