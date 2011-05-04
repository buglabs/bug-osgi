package com.buglabs.app.bugdash2.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.buglabs.app.bugdash2.AdminConfigManager;
import com.buglabs.app.bugdash2.WebAdminSettings;
import com.buglabs.app.bugdash2.controller.AdminControllerFactory;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.SewingHttpServlet;
import com.buglabs.osgi.sewing.pub.util.ControllerMap;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;


public class BUGwebAdminServlet extends SewingHttpServlet {

	private static final long serialVersionUID = 1916741933048860262L; 
	
	public BUGwebAdminServlet() {}

	/*
	 * (non-Javadoc)
	 * @see com.buglabs.osgi.sewing.pub.SewingHttpServlet#getControllerMap()
	 */
	public ControllerMap getControllerMap() {
		ControllerMap controllers = new ControllerMap();
		controllers.put("index", new index());
		
		controllers.put("dashboard", AdminControllerFactory.getInstance().getOverviewController()); 
		controllers.put("forgotPassword", AdminControllerFactory.getInstance().getBugResetPasswordController());
		controllers.put("login", AdminControllerFactory.getInstance().getLoginHomepageController());
		controllers.put("logout", AdminControllerFactory.getInstance().getLogoutController());
		return controllers;
	}
	
	
	
	/* Create your controllers as inner classes below */
	
	public class index extends ApplicationController {
		
		public TemplateModelRoot get(RequestParameters params,
				HttpServletRequest req, HttpServletResponse resp) {
			
			// check uesr's login cookie 
			String require_login = "false";
			try {
				require_login = AdminConfigManager.getConfigPropertyValue(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_REQUIRE_LOGIN);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			SimpleHash root = new SimpleHash();
			root.put("require_login", new SimpleScalar(require_login));
			return root;
		}

		public TemplateModelRoot post(RequestParameters params,
				HttpServletRequest req, HttpServletResponse resp) {
			return null;
		}
		
		
	}
	


}
