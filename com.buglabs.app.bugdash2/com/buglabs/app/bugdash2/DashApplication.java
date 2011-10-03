package com.buglabs.app.bugdash2;

import java.util.Map;

import org.osgi.framework.BundleContext;

import com.buglabs.app.bugdash2.web.BUGwebAdminBUGnetServlet;
import com.buglabs.app.bugdash2.web.BUGwebAdminHardwareServlet;
import com.buglabs.app.bugdash2.web.BUGwebAdminServlet;
import com.buglabs.app.bugdash2.web.BUGwebAdminSoftwareServlet;
import com.buglabs.app.bugdash2.web.BUGwebAdminSystemServlet;
import com.buglabs.app.bugdash2.web.BUGwebAdminUtilsServlet;
import com.buglabs.app.bugdash2.web.BUGwebFileServlet;
import com.buglabs.osgi.sewing.pub.ISewingService;
import com.buglabs.util.osgi.ServiceTrackerUtil.ManagedRunnable;

/**
 * This class acts to construct the bugdash application given
 * that all dependent services are available.
 *
 */
public class DashApplication implements ManagedRunnable {
	private ISewingService sewingService;
	private BUGwebAdminServlet mainServlet;
	private BUGwebAdminHardwareServlet hardwareServlet;
	private BUGwebAdminSystemServlet systemServlet;
	private BUGwebAdminSoftwareServlet softwareServlet;
	private BUGwebAdminBUGnetServlet bugnetServlet;
	private BUGwebAdminUtilsServlet utilsServlet;
	private BUGwebFileServlet imageServlet;
	private final BundleContext context;
	
	public DashApplication(BundleContext context) {
		this.context = context;		
	}

	@Override
	public void run(Map<String, Object> services) {
		LogManager.logInfo("BUGwebAdminServiceTracker: start");
		sewingService = (ISewingService) services.get(ISewingService.class.getName());
		    	
		mainServlet = new BUGwebAdminServlet();
		hardwareServlet = new BUGwebAdminHardwareServlet(); 
		systemServlet = new BUGwebAdminSystemServlet();
		softwareServlet = new BUGwebAdminSoftwareServlet(); 
		bugnetServlet = new BUGwebAdminBUGnetServlet(); 
		utilsServlet = new BUGwebAdminUtilsServlet();
		imageServlet = new BUGwebFileServlet();

		sewingService.register(context, "/admin", mainServlet);
		sewingService.register(context, "/admin_hardware", hardwareServlet); 
		sewingService.register(context, "/admin_system", systemServlet);
		sewingService.register(context, "/admin_software", softwareServlet);
		sewingService.register(context, "/admin_bugnet", bugnetServlet); 
		sewingService.register(context, "/admin_util", utilsServlet); 
		sewingService.register(context, "/admin_imageviewer", imageServlet);
	}

	@Override
	public void shutdown() {
		LogManager.logInfo("BUGwebAdminServiceTracker: stop");
		sewingService.unregister(mainServlet);
		sewingService.unregister(hardwareServlet); 
		sewingService.unregister(systemServlet);
		sewingService.unregister(softwareServlet);
		sewingService.unregister(bugnetServlet); 
		sewingService.unregister(utilsServlet);
		sewingService.unregister(imageServlet);
		ShellUtil.destroySession(); 		
	}
}
