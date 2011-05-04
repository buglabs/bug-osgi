package webadmin.controller;

import webadmin.controller.bugnet.BUGnetController;
import webadmin.controller.software.PackageInstallControllerVBUG;
import webadmin.controller.software.PackageNewUpdateControllerPBUG;
import webadmin.controller.software.PackageNewUpdateControllerVBUG;
import webadmin.controller.software.PackageUpgradeControllerVBUG;
import webadmin.controller.software.PackageViewerControllerVBUG;
import webadmin.controller.system.LogControllerVBUG;

import com.buglabs.osgi.sewing.pub.SewingController;

public class VBUGControllerFactory extends AdminControllerFactory {
	
	/**
	 * Overview 
	 */
	public SewingController getOverviewController() {
		return new OverviewControllerVBUG(); 
	}
	
	/**
	 * Software - View installed packages
	 */		
	public SewingController getPackageViewerController() {
		return new PackageViewerControllerVBUG();
	}
	
	/**
	 * Software - Run ipkg update 
	 */
	public SewingController getUpgradePackagesController() {
		return new PackageUpgradeControllerVBUG();		
	}	
	
	
	/**
	 * Software - View new updates
	 */
	public SewingController getPackageNewUpdateController() {
		return new PackageNewUpdateControllerVBUG(); 
	}	
	/**
	 * System - View logs 
	 */	
	public SewingController getLogController() {
		return new LogControllerVBUG();		
	}
	
	/**
	 * BUGnet
	 */
	public SewingController getBUGnetController() {
		return new BUGnetController(); 
	}
}
