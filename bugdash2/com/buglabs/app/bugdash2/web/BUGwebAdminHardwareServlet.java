package com.buglabs.app.bugdash2.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.controller.AdminControllerFactory;
import com.buglabs.osgi.sewing.pub.SewingController;
import com.buglabs.osgi.sewing.pub.SewingHttpServlet;
import com.buglabs.osgi.sewing.pub.util.ControllerMap;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import freemarker.template.TemplateModelRoot;

public class BUGwebAdminHardwareServlet extends SewingHttpServlet {

	private static final long serialVersionUID = -7029570391076216346L;
	
	public BUGwebAdminHardwareServlet() { }

	public ControllerMap getControllerMap() {

		ControllerMap controllers = new ControllerMap();
		controllers.put("display_bugmodules", AdminControllerFactory.getInstance().getBUGmoduleController() );
		controllers.put("display_bugmodule_properties", AdminControllerFactory.getInstance().getBUGmodulePropertiesController() );
		controllers.put("reboot", new reboot());
		return controllers;
	}

	public class reboot extends SewingController {
		
		public String getTemplateName() { return "hardware_reboot.fml"; }

		public TemplateModelRoot get(RequestParameters params, 
				HttpServletRequest req, HttpServletResponse resp) {
			return null;
		}
	}
	

}
