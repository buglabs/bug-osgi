package com.buglabs.app.bugdash2.controller;

import com.buglabs.app.bugdash2.Activator;
import com.buglabs.app.bugdash2.controller.bugnet.BUGnetController;
import com.buglabs.app.bugdash2.controller.hardware.BUGmoduleController;
import com.buglabs.app.bugdash2.controller.hardware.BUGmodulePropertiesController;
import com.buglabs.app.bugdash2.controller.login.BugResetPasswordController;
import com.buglabs.app.bugdash2.controller.login.LoginHomepageController;
import com.buglabs.app.bugdash2.controller.login.LogoutController;
import com.buglabs.app.bugdash2.controller.software.AppViewerController;
import com.buglabs.app.bugdash2.controller.software.AppsBrowserController;
import com.buglabs.app.bugdash2.controller.software.AppsManagerController;
import com.buglabs.app.bugdash2.controller.software.AppsRecommendedController;
import com.buglabs.app.bugdash2.controller.software.PackageNewUpdateController;
import com.buglabs.app.bugdash2.controller.software.PackageUpgradeController;
import com.buglabs.app.bugdash2.controller.software.ReadPackageNewUpdateBufferController;
import com.buglabs.app.bugdash2.controller.software.ReadPackageUpgradeBufferController;
import com.buglabs.app.bugdash2.controller.system.BUGLoginController;
import com.buglabs.app.bugdash2.controller.system.ConfigurationController;
import com.buglabs.app.bugdash2.controller.system.ConfigurationPropertyController;
import com.buglabs.app.bugdash2.controller.system.DateTimeController;
import com.buglabs.app.bugdash2.controller.system.DeviceRenameController;
import com.buglabs.app.bugdash2.controller.system.Ipv6SetupController;
import com.buglabs.app.bugdash2.controller.system.LogController;
import com.buglabs.app.bugdash2.controller.system.ReadBufferController;
import com.buglabs.app.bugdash2.controller.system.StartReadingLogController;
import com.buglabs.app.bugdash2.controller.system.StopReadingLogController;
import com.buglabs.app.bugdash2.controller.system.FileBrowserController;
import com.buglabs.app.bugdash2.controller.system.FileDownloadController;
import com.buglabs.app.bugdash2.controller.system.FileNavigatorController;
import com.buglabs.app.bugdash2.controller.system.FileViewerController;
import com.buglabs.app.bugdash2.controller.utils.CheckInternetController;
import com.buglabs.osgi.sewing.pub.SewingController;


public abstract class AdminControllerFactory {

	private static boolean isVirtualBUG() { return Activator.isVirtualBUG(); }
	
	public static AdminControllerFactory getInstance() {
		if (isVirtualBUG())
			return new VBUGControllerFactory();
		else
			return new PBUGControllerFactory();
	}
	
	/**
	 * Overview
	 */
	public SewingController getOverviewController() {
		return new DefaultAdminController(); 
	}
	
	
	/**
	 * Hardware - Display BUGmodules
	 */
	public SewingController getBUGmoduleController() {
		return new BUGmoduleController(); 
	}
	
	public SewingController getBUGmodulePropertiesController() {
		return new BUGmodulePropertiesController();
	}
	
	/**
	 * Software - View installed packages
	 */

	public SewingController getPackageViewerController() {
		return new DefaultAdminController();
	}
	
	
	/**
	 * Software - Run ipkg update 
	 */
	public SewingController getUpgradePackagesController() {
		return new DefaultAdminController();		
	}
	
	public SewingController getReadPackageUpgradeBufferController(PackageUpgradeController controller) {
		return new ReadPackageUpgradeBufferController(controller); 		
	}
	
	/**
	 * Software - Install ipkg
	 */
	public SewingController getPackageInstallController() {
		return new DefaultAdminController(); 
	}
	
	/**
	 * Software - View new updates
	 */
	public SewingController getPackageNewUpdateController() {
		return new DefaultAdminController(); 
	}
	
	public SewingController getReadPackageNewUpdateBufferController(PackageNewUpdateController controller) {
		return new ReadPackageNewUpdateBufferController(controller);
	}
	
	/**
	 * Software - Manage apps
	 */
	public SewingController getManageBUGappsController() {
		return new DefaultAdminController(); 
	}
	
	public SewingController getAppsBrowserController() {
		return new AppsBrowserController(); 
	}
	
	public SewingController getAppsManagerController() {
		return new AppsManagerController(); 
	}
	
	public SewingController getAppViewerController() {
		return new AppViewerController(); 
	}
	
	public SewingController getAppsRecommendedController() {
		return new AppsRecommendedController(); 
	}
	
	/**
	 * System - View logs 
	 * @return
	 */
	public SewingController getLogController() {
		return new DefaultAdminController();		
	}

	public SewingController getStartReadingLogController(LogController logController) {
		return new StartReadingLogController(logController);
	}

	public SewingController getStopReadingLogController(LogController logController) {
		return new StopReadingLogController(logController);
	}

	public SewingController getReadBufferController(LogController logController) {
		return new ReadBufferController(logController);
	}

	public SewingController getDateTimeController() {
		return new DateTimeController();
	}

	public SewingController getSystemInfoController() {
		return new DefaultAdminController();
	}	

	
	/**
	 * System - Manage configuration 
	 */
	public SewingController getConfigurationController() {
		return new ConfigurationController(); 
	}
	
	public SewingController getConfigurationPropertyController() {
		return new ConfigurationPropertyController(); 
	}
	
	public SewingController getIpv6SetupController() {
		return new Ipv6SetupController(); 
	}
	public SewingController getDeviceRenameController() {
		return new DeviceRenameController();
	}
	public SewingController getBugResetPasswordController() {
		return new BugResetPasswordController();
	}
	/**
	 * System - File Browser
	 */
	public SewingController getFileBrowserController() {
		return new FileBrowserController();
	}
	public SewingController getFileNavigatorController() {
		return new FileNavigatorController();
	}
	public SewingController getFileViewerController() {
		return new FileViewerController();
	}
	
	/**
	 * Login/Logout - Secure Login/Logout for BUG
	 */
	public SewingController getBugLoginController(){
		return new BUGLoginController();
	}
	public SewingController getLogoutController() {
		return new LogoutController();
	}
	public SewingController getLoginHomepageController() {
		return new LoginHomepageController();
	}

	public SewingController getFileDownloadController() {
		return new FileDownloadController();
	}

	public SewingController getBUGnetController() {
		return new BUGnetController(); 
	}
	
	/**
	 * Utils 
	 */
	public SewingController getCheckInternetController() {
		return new CheckInternetController(); 
	}
	
	/**
	 * Login
	 */

}
