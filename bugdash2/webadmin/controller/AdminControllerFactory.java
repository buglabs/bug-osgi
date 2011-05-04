package webadmin.controller;

import com.buglabs.osgi.sewing.pub.SewingController;

import webadmin.Activator;
import webadmin.controller.bugnet.*;
import webadmin.controller.hardware.*;
import webadmin.controller.login.BugResetPasswordController;
import webadmin.controller.login.LoginHomepageController;
import webadmin.controller.login.LogoutController;
import webadmin.controller.software.*;
import webadmin.controller.system.*;
import webadmin.controller.utils.*;

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
		return new fileBrowserController();
	}
	public SewingController getFileNavigatorController() {
		return new fileNavigatorController();
	}
	public SewingController getFileViewerController() {
		return new fileViewerController();
	}
	
	/**
	 * Login/Logout - Secure Login/Logout for BUG
	 */
	public SewingController getBugLoginController(){
		return new BugLoginController();
	}
	public SewingController getLogoutController() {
		return new LogoutController();
	}
	public SewingController getLoginHomepageController() {
		return new LoginHomepageController();
	}

	public SewingController getFileDownloadController() {
		return new fileDownloadController();
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
