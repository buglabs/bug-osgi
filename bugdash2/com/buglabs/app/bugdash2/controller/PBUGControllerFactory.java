package com.buglabs.app.bugdash2.controller;


import com.buglabs.app.bugdash2.controller.bugnet.BUGnetController;
import com.buglabs.app.bugdash2.controller.login.BugResetPasswordController;
import com.buglabs.app.bugdash2.controller.login.LoginHomepageController;
import com.buglabs.app.bugdash2.controller.software.PackageInstallControllerPBUG;
import com.buglabs.app.bugdash2.controller.software.PackageNewUpdateControllerPBUG;
import com.buglabs.app.bugdash2.controller.software.PackageUpgradeControllerPBUG;
import com.buglabs.app.bugdash2.controller.software.PackageViewerControllerPBUG;
import com.buglabs.app.bugdash2.controller.system.DeviceRenameController;
import com.buglabs.app.bugdash2.controller.system.LogControllerPBUG;
import com.buglabs.app.bugdash2.controller.system.SystemInfoController;
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
