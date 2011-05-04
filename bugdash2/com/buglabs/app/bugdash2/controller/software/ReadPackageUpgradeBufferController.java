package com.buglabs.app.bugdash2.controller.software;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.buglabs.app.bugdash2.TemplateHelper;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public class ReadPackageUpgradeBufferController extends ApplicationController {

	private PackageUpgradeController upgrade_controller; 
	
	public ReadPackageUpgradeBufferController(PackageUpgradeController controller) {
		upgrade_controller = controller; 
	}
	
	public String getTemplateName() { return "message.fml"; }

	public TemplateModelRoot get(RequestParameters params, 
			HttpServletRequest req, HttpServletResponse resp) {

		String task = params.get("task"); 
		if (task != null && task.equals("stop")) {
			upgrade_controller.stopThread(); 
			return null; 
		}
		
		SimpleHash root = new SimpleHash(); 
		String output = "{" +
							"command: '" + upgrade_controller.getCurrentCommand() + "'," + 
							"percent: '" + upgrade_controller.getCurrentPercent() + "'," + 
							"description: '" + upgrade_controller.getCurrentDescription() + "'," + 
							"output: '" + TemplateHelper.makeJSFriendly(TemplateHelper.listToText(upgrade_controller.getThreadBuffer()))  + "'" + 
						 "}"; 
		
		root.put("message", new SimpleScalar(output));
		return root;
	}	
	
	
}
