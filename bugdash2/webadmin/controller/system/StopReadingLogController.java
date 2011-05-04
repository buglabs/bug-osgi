package webadmin.controller.system;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webadmin.LogFile;
import webadmin.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelRoot;

public class StopReadingLogController extends ApplicationController {
	private LogController log_controller;

	public String getTemplateName() { return "message.fml"; }
	
	public StopReadingLogController(LogController logController) {
		log_controller = logController;
	}
	
	public TemplateModelRoot get(RequestParameters params, 
			HttpServletRequest req, HttpServletResponse resp) {
		Map logs 		= log_controller.getLogs();
		String key 		= params.get("key"); 			
		Object logObj 	= logs.get(key);
		
		if (logObj == null) return null; 
		LogFile log = (LogFile)logObj;
		log.stopThread();
		SimpleHash root = new SimpleHash(); 
		root.put("message", key + " stopped"); 
		return root;	
	}
}
