package com.buglabs.app.bugdash2.web;


import com.buglabs.app.bugdash2.controller.AdminControllerFactory;
import com.buglabs.osgi.sewing.pub.SewingHttpServlet;
import com.buglabs.osgi.sewing.pub.util.ControllerMap;

public class BUGwebAdminBUGnetServlet extends SewingHttpServlet {

	private static final long serialVersionUID = 8320840401401378353L;
	
	public BUGwebAdminBUGnetServlet() {

	}

	public ControllerMap getControllerMap() {
		ControllerMap controllers = new ControllerMap();
		
		controllers.put("bugnet", 
				AdminControllerFactory.getInstance().getBUGnetController());
		
		return controllers; 
	}

}
