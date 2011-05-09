package com.buglabs.app.bugdash2;
import java.net.URL;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.buglabs.app.bugdash2.servicetracker.BUGwebAdminServiceTracker;
import com.buglabs.application.IDesktopApp;
import com.buglabs.module.IModuleControl;
import com.buglabs.util.ServiceFilterGenerator;

public class Activator implements BundleActivator, IDesktopApp {
	
	private BUGwebAdminServiceTracker stc;
	private ServiceTracker st;
	private ServiceRegistration appReg;
	private static boolean isVirtualBUG = false;
	private static BundleContext context;
	private static IModuleControl[] modules;

	private static IBatteryInfoProvider batteryProvider = null;
	private ServiceRegistration batteryReg;
	
	public void start(BundleContext context) throws Exception {		
		Activator.context = context;
		modules = new IModuleControl[4];
		checkSetting(); 
		
		batteryProvider = new BatteryInfoProvider();
		batteryReg = context.registerService(IBatteryInfoProvider.class.getName(), batteryProvider, null);
		stc = new BUGwebAdminServiceTracker(context);
		Filter f = context.createFilter(ServiceFilterGenerator.generateServiceFilter(stc.getServices()));
		st = new ServiceTracker(context, f, stc);
		st.open();
		
		appReg = context.registerService(IDesktopApp.class.getName(), this, null);	
	}
	
	public void stop(BundleContext context) throws Exception {
		appReg.unregister();
		ShellUtil.destroySession();
		stc.stop();
		st.close();
		if (batteryReg != null)
			batteryReg.unregister();
	}
	
	private void checkSetting() {
		// check if it's vbug
		String property = context.getProperty("com.buglabs.virtual.bug"); 
		isVirtualBUG = (property != null && property.length() > 0);
		// check if it's 2.0 base 
		property = context.getProperty("bug.base.version"); 
		if (property != null && property.equals("2.0")) {
			// 2.0 configuration here 
			Package.setIpkgCommand( "opkg" ); 
		}
	}

	public static BundleContext getContext() {
		return context;
	}
	
	public static boolean isVirtualBUG() {
		return isVirtualBUG;
	}
	
	public static IModuleControl[] getModules() {
		return modules; 
	}

	public static void setModule(IModuleControl control) { 
		modules[control.getSlotId()] = control;
	}
	
	public static IModuleControl getModule(int index) { 
		return modules[index];
	}
	
	public static void clearModule(int index) {
		modules[index] = null;
	}

	public void click() {
		// Do nothing here since this is a webapp.
	}

	public URL getIcon(int width, int height, int depth) {
		return context.getBundle().getResource("images/icon.png");
	}

	public String[] getMenuItems() {
		//Contribute no new menu items.
		return null;
	}

	public void menuSelected(String item) {		
		//No menu functionality.
	}
	public static double getBatteryLife(String path) {
		return batteryProvider.getValue(path); 
	}
}