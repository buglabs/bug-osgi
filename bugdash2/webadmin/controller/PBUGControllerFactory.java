package webadmin.controller;

import webadmin.controller.bugnet.BUGnetController;
import webadmin.controller.login.BugResetPasswordController;
import webadmin.controller.login.LoginHomepageController;
import webadmin.controller.software.PackageNewUpdateControllerPBUG;
import webadmin.controller.software.PackageUpgradeControllerPBUG;
import webadmin.controller.software.PackageViewerControllerPBUG;
import webadmin.controller.software.PackageInstallControllerPBUG;
import webadmin.controller.system.DeviceRenameController;
import webadmin.controller.system.LogControllerPBUG;
import webadmin.controller.system.SystemInfoController;

import com.buglabs.osgi.sewing.pub.SewingController;

public class PBUGControllerFactory extends AdminControllerFactory {

	/**
	 * Overview 
	 */
	public SewingController getOverviewController() {
		return new OverviewControllerPBUG(); 
	}
	/**
	 * Login
	 */
	public SewingController getLoginHomepageController() {
		return new LoginHomepageController();
	}
	
	/**
	 * Software - View installed packages
	 */	
	public SewingController getPackageViewerController() {
		return new PackageViewerControllerPBUG();	
	}
	
	/**
	 * Software - Run ipkg update 
	 */	
	public SewingController getUpgradePackagesController() {
		return new PackageUpgradeControllerPBUG();		
	}
	
	/**
	 * Software - Install ipkg
	 */
	public SewingController getPackageInstallController() {
		return new PackageInstallControllerPBUG(); 
	}
	
	/**
	 * Software - View new updates
	 */
	public SewingController getPackageNewUpdateController() {
		return new PackageNewUpdateControllerPBUG(); 
	}	

	/**
	 * System - View logs 
	 */	
	public SewingController getLogController() {
			return new LogControllerPBUG();		
	}	
	
	/**
	 * System - System Info
	 * 
	 */
	public SewingController getSystemInfoController() {
		return new SystemInfoController();
	}	
	
	/**
	 * System - Device rename
	 */
	public SewingController getDeviceRenameController() {
		return new DeviceRenameController(); 
	}
	/**
	 * System - Password Reset
	 */
	public SewingController getBugResetPasswordController() {
		return new BugResetPasswordController();
	}
	
	/**
	 * BUGnet - Login to BUGnet
	 */
	public SewingController getBUGnetController() {
		return new BUGnetController();
	}	
}
