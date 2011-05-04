package com.buglabs.app.bugdash2.controller.software;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.TemplateHelper;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public class ReadPackageNewUpdateBufferController extends ApplicationController {

	private PackageNewUpdateController update_controller;
	
	public ReadPackageNewUpdateBufferController(PackageNewUpdateController controller) {
		this.update_controller = controller;
	}
	
	public String getTemplateName() { return "message.fml"; }
	
	public TemplateModelRoot get(RequestParameters params, 
			HttpServletRequest req, HttpServletResponse resp) {

		String task = params.get("task"); 
		if (task != null && task.equals("stop")) {
			update_controller.stopThread(); 
			return null; 
		}
			
		SimpleHash root = new SimpleHash(); 
		String output = TemplateHelper.makeJSFriendly(TemplateHelper.listToText(update_controller.getThreadBuffer())); 
		root.put("message", new SimpleScalar(output));
		
		return root;
	}	
}
