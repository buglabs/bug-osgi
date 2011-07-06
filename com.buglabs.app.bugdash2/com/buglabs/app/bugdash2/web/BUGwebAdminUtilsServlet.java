package com.buglabs.app.bugdash2.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.Activator;
import com.buglabs.app.bugdash2.ShellUtil;
import com.buglabs.app.bugdash2.controller.AdminControllerFactory;
import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.osgi.sewing.pub.SewingController;
import com.buglabs.osgi.sewing.pub.SewingHttpServlet;
import com.buglabs.osgi.sewing.pub.util.ControllerMap;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import com.buglabs.util.StringUtil;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public class BUGwebAdminUtilsServlet extends SewingHttpServlet {

	private static final long serialVersionUID = -1972679380816482873L;

	public BUGwebAdminUtilsServlet() { }
	
	public ControllerMap getControllerMap() {

		ControllerMap controllers = new ControllerMap();
		controllers.put("internet_status", AdminControllerFactory.getInstance().getCheckInternetController());
		controllers.put("dashboard_status", new checkStatus()); 
		controllers.put("task", new performTask()); 
		return controllers;
	}
	
	/**
	 * Simply returns text; used to determine if admin tool is accessible 
	 *
	 */
	public class checkStatus extends SewingController {

		public String getTemplateName() { return "message.fml"; }

		public TemplateModelRoot get(RequestParameters params,
				HttpServletRequest req, HttpServletResponse resp) {
			SimpleHash root = new SimpleHash();
			root.put("message", new SimpleScalar("{status: 'on'}"));
			return root;
		}
	}	
	
	/**
	 * Does various tasks on GET/POST action; mainly used by XHR to perform small tasks
	 * - delete_storage_and_restart: (POST) removes storage directory and then reboots (part of ipkg upgrade process) 
	 * - display_processes: (GET) runs ps command 
	 *
	 */
	public class performTask extends SewingController {
		
		public final String DELETE_STORAGE_AND_RESTART 	= "rm -rf /usr/shared/java/storage & reboot";
		public final String DISPLAY_PROCESS 			= "ps";	
		public final String REBOOT						= "reboot"; 
 
		public String getTemplateName() { return "message.fml"; }

		public TemplateModelRoot get(RequestParameters params,
				HttpServletRequest req, HttpServletResponse resp) {

			String action = params.get("task"); 
			SimpleScalar result = new SimpleScalar("");  
			
			if (!action.equals("")) {
				if (action.equals("display_process"))
					result = ShellUtil.getSimpleScalar(DISPLAY_PROCESS); 
				else if (action.equals("display_modules")) {
					List modules_attached = new ArrayList(); 
					IModuleControl[] list = Activator.getModules();
					for (int i=0; i<list.length; i++) {
						if (list[i] != null ) {
							modules_attached.add(list[i].getModuleName());
						}
					}	
					//com.buglabs.util.StringUtilStringUtils.join(modules_attached, "','");
					result = new SimpleScalar("['" + StringUtil.join(modules_attached, "','") + "']");
				}
				else 
					result = new SimpleScalar("task not found"); 
			}
			SimpleHash root = new SimpleHash();
			root.put("message", result);
			return root;
		}

		public TemplateModelRoot post(RequestParameters params,
				HttpServletRequest req, HttpServletResponse resp) {
			
			String action 		= params.get("task"); 
			SimpleScalar result = new SimpleScalar("");  
			
			if (!action.equals("")) {
				if (action.equals("delete_storage_and_restart")) {
					result = ShellUtil.getSimpleScalar(DELETE_STORAGE_AND_RESTART);
				}
				else if (action.equals("reboot")) {
					result = ShellUtil.getSimpleScalar(REBOOT); 
				}
				else { 
					result = new SimpleScalar("task not found");
				}
			}
			SimpleHash root = new SimpleHash();
			root.put("message", result);
			return root;
		}	
	}
}
