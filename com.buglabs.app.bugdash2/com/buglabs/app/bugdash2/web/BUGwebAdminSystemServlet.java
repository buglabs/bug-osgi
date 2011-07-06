package com.buglabs.app.bugdash2.web;


import com.buglabs.app.bugdash2.controller.AdminControllerFactory;
import com.buglabs.app.bugdash2.controller.system.LogController;
import com.buglabs.osgi.sewing.pub.SewingController;
import com.buglabs.osgi.sewing.pub.SewingHttpServlet;
import com.buglabs.osgi.sewing.pub.util.ControllerMap;

public class BUGwebAdminSystemServlet extends SewingHttpServlet {

	private static final long serialVersionUID = 7039099300536873964L;
	
	public BUGwebAdminSystemServlet() 
	{

	}

	public ControllerMap getControllerMap() {
		ControllerMap controllers = new ControllerMap();
		
		// View logs 
		SewingController logController = AdminControllerFactory.getInstance().getLogController(); 
		controllers.put("display_logs", logController);
		controllers.put("start_reading", AdminControllerFactory.getInstance().getStartReadingLogController((LogController)logController));
		controllers.put("read_buffer", 	AdminControllerFactory.getInstance().getReadBufferController((LogController)logController));
		controllers.put("stop_reading", AdminControllerFactory.getInstance().getStopReadingLogController((LogController)logController));
		
		controllers.put("display_datetime", AdminControllerFactory.getInstance().getDateTimeController());
		controllers.put("display_system_info", AdminControllerFactory.getInstance().getSystemInfoController());
		controllers.put("manage_configuration", AdminControllerFactory.getInstance().getConfigurationController()); 
		controllers.put("ipv6_setup", AdminControllerFactory.getInstance().getIpv6SetupController()); 
		controllers.put("manage_configuration_property", AdminControllerFactory.getInstance().getConfigurationPropertyController()); 
		controllers.put("rename_device", AdminControllerFactory.getInstance().getDeviceRenameController());
		
		controllers.put("login", AdminControllerFactory.getInstance().getBugLoginController());
		controllers.put("fileBrowser", AdminControllerFactory.getInstance().getFileBrowserController());
		controllers.put("navigator", AdminControllerFactory.getInstance().getFileNavigatorController());
		controllers.put("viewer", AdminControllerFactory.getInstance().getFileViewerController());
		controllers.put("download", AdminControllerFactory.getInstance().getFileDownloadController());
		
		return controllers;
	}
}
