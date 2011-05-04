package webadmin.controller.system;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webadmin.LogFile;
import webadmin.TemplateHelper;
import webadmin.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;


public class ReadBufferController extends ApplicationController {
	
	private LogController log_controller;

	public ReadBufferController(
			LogController logController) {
		log_controller = logController;
	}	
	
	public String getTemplateName() {
		return "message.fml";
	}

	public TemplateModelRoot get(RequestParameters params, 
			HttpServletRequest req, HttpServletResponse resp) {
		Map logs = log_controller.getLogs();
		Object logObj = logs.get(params.get("key"));
		if (logObj == null) return null; 
			
		LogFile log = (LogFile)logObj;
		SimpleHash root = new SimpleHash(); 
		root.put("message", new SimpleScalar(TemplateHelper.listToText(log.getThreadBuffer())));
		return root;
	}	
}
