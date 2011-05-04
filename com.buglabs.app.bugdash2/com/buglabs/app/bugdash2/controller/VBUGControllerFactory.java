package com.buglabs.app.bugdash2.controller;


import com.buglabs.app.bugdash2.controller.bugnet.BUGnetController;
import com.buglabs.app.bugdash2.controller.software.PackageNewUpdateControllerVBUG;
import com.buglabs.app.bugdash2.controller.software.PackageUpgradeControllerVBUG;
import com.buglabs.app.bugdash2.controller.software.PackageViewerControllerVBUG;
import com.buglabs.app.bugdash2.controller.system.LogControllerVBUG;
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
