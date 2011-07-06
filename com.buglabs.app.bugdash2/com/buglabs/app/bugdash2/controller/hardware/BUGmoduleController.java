package com.buglabs.app.bugdash2.controller.hardware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.Activator;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.TemplateModelRoot;

public class BUGmoduleController extends ApplicationController {

	public String getTemplateName() { return "hardware_bugmodules.fml"; }
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {

		SimpleHash modules_hash = new SimpleHash(); 
		SimpleList services_list = new SimpleList();
		SimpleHash item; 
		
		// retrieve modules 
		IModuleControl[] modules = Activator.getModules(); 
		for(int i=0; i<modules.length; i++) {
			if (modules[i] != null) {
				modules_hash.put("slot"+modules[i].getSlotId(), modules[i].getModuleName()); 
			}
		}
		
		SimpleHash root = new SimpleHash();
		root.put("modules_hash", modules_hash); 
		//TODO: add a list of services 
		//root.put("services_list", getServicesList()); 
		return root;
	}

}
