package com.buglabs.app.bugdash2;
import java.net.URL;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import com.buglabs.app.bugdash2.servicetracker.BUGwebAdminServiceTracker;
import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.osgi.sewing.pub.ISewingService;
import com.buglabs.util.osgi.BUGBundleConstants;
import com.buglabs.util.osgi.FilterUtil;
import com.buglabs.util.osgi.ServiceTrackerUtil;
import com.buglabs.util.ui.IDesktopApp;

public class Activator implements BundleActivator, IDesktopApp, ServiceListener {
	
	/**
	 * List of OSGi services that must be available before dash will run.
	 */
	private static final String [] services = {
		com.buglabs.osgi.sewing.pub.ISewingService.class.getName(),
		org.osgi.service.cm.ConfigurationAdmin.class.getName(),
		com.buglabs.util.shell.pub.IShellService.class.getName(),
		com.buglabs.app.bugdash2.IBatteryInfoProvider.class.getName(),
		LogService.class.getName()
	};
	
	private ServiceRegistration appReg;
	private static boolean isVirtualBUG = false;
	private static BundleContext context;
	private static IModuleControl[] modules;

	private static IBatteryInfoProvider batteryProvider = null;
	private ServiceRegistration batteryReg;

	private ServiceTracker stc;

	private ServiceTracker moduleTracker;

	private ISewingService service;
	
	public void start(BundleContext context) throws Exception {		
		Activator.context = context;
		modules = new IModuleControl[BUGBundleConstants.BUG_TOTAL_BMI_SLOTS];
		checkSetting(); 
		
		batteryProvider = new BatteryInfoProvider();
		batteryReg = context.registerService(IBatteryInfoProvider.class.getName(), batteryProvider, null);
		stc = ServiceTrackerUtil.openServiceTracker(context, new DashApplication(context), services);
		
		new BUGwebAdminServiceTracker(context);
		
		appReg = context.registerService(IDesktopApp.class.getName(), this, null);	
		
		//Listen for add/remove of IModuleControls to be notified of BUG module changes.
		context.addServiceListener(this, FilterUtil.generateServiceFilter(IModuleControl.class.getName()));
	}
	
	public void stop(BundleContext context) throws Exception {
		context.removeServiceListener(this);
		appReg.unregister();
		ShellUtil.destroySession();
		stc.close();
		
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

	/* (non-Javadoc)
	 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
	 */
	@Override
	public void serviceChanged(ServiceEvent event) {
		//In this method we track IModuleControl and maintain the active state of attached BUG modules.
		Object svc = null;
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			svc = context.getService(event.getServiceReference());
	        if (svc instanceof IModuleControl) {	
	        	IModuleControl module = (IModuleControl) svc;
	        	Activator.setModule(module);
	        }
			break;
		case ServiceEvent.UNREGISTERING:
			svc = context.getService(event.getServiceReference());
		     if (svc instanceof IModuleControl) {
		         IModuleControl mc = (IModuleControl) svc;
		         Activator.clearModule(mc.getSlotId());
		     }
			break;
		default:
			break;
		}
	}
}
