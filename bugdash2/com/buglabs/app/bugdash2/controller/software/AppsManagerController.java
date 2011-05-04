package com.buglabs.app.bugdash2.controller.software;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;


import com.buglabs.app.bugdash2.Activator;
import com.buglabs.app.bugdash2.App;
import com.buglabs.app.bugdash2.LogManager;
import com.buglabs.app.bugdash2.TemplateHelper;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.bug.program.pub.IUserAppManager;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import com.buglabs.osgi.shell.pub.BundleUtils;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.TemplateModelRoot;

public class AppsManagerController extends ApplicationController {

	private String myTemplate; 
	
	
	public String getTemplateName() {
		return myTemplate;
	}
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		
		this.myTemplate = "software_apps_installed.fml"; 
		
		// does it make sense to represent installed apps, or bundles, as App 
		Bundle[] apps_installed = Activator.getContext().getBundles();
		
		SimpleList installed_list = new SimpleList(); 
		SimpleHash item; 
		App app;
		boolean connected = App.checkNetworkConnection(); 
		int counter = 0; 
		for(int i=0; i<apps_installed.length; i++) {
			if (isApp(apps_installed[i])) {
				app = new App(BundleUtils.getBestName(apps_installed[i]));
				if (connected) {
					app.lookupByTitle();
				}
				item = new SimpleHash(); 
				item.put("index", i+1);
				item.put("name", app.getTitle()); 
				item.put("state", getBundleStatus(apps_installed[i].getState()));
				item.put("bundleId", apps_installed[i].getBundleId()); 
				
				item.put("url", app.getUrl()); 
				item.put("icon", app.getIcon() );
				item.put("dependencies", app.displayPackages()); 
				installed_list.add(item); 
				counter++;
			}
		}
		
		SimpleHash root = new SimpleHash();
		root.put("installed_list", installed_list); 
		root.put("installed_size", counter); 
		root.put("disable", params.get("disable"));
		return root;	
	}
	
	
	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		// little messy.. it takes care of uninstall, start, and stop 
		this.myTemplate 	= "message.fml"; 
		SimpleHash root 	= new SimpleHash();
		String msg 			= "App not provided"; 
		String task 		= params.get("task"); 

		if (params.get("bundleId") == null) {
			root.put("message", TemplateHelper.getStatusJSONString("ERROR", msg)); return root; 
		}
		long bundleId 			= Long.parseLong(params.get("bundleId")); 
		BundleContext context  	= Activator.getContext();
		Bundle bundle 			= context.getBundle(bundleId); 
		if (bundle == null) {
			root.put("message", TemplateHelper.getStatusJSONString("ERROR", "Could not find the app")); return root; 
		}
		int bundle_status 		= bundle.getState();
		String bundle_name 		= BundleUtils.getBestName(bundle); 		
		if (task.equals("uninstall")) {
			ServiceReference sr = context.getServiceReference(IUserAppManager.class.getName());
			IUserAppManager userAppManager = (IUserAppManager) context.getService(sr);
			try {
				LogManager.logDebug("Remove app: " + bundle_name);
				userAppManager.removeApplication(bundle.getLocation());
				bundle.uninstall();
				msg = TemplateHelper.getStatusJSONString("OK", "App uninstalled successfully"); 
				LogManager.logDebug("App removed ok");
			} catch (IOException e) {
				msg = TemplateHelper.getStatusJSONString("ERROR", e.getMessage());
				LogManager.logWarning("App removed error: " + e.getMessage());
			} catch (BundleException e) {
				msg = TemplateHelper.getStatusJSONString("ERROR", e.getMessage());
				LogManager.logWarning("App removed error: " + e.getMessage());
			}
		} else if (task.equals("start")) {
			if (bundle_status == Bundle.ACTIVE) {
				msg = TemplateHelper.getStatusJSONString("WARNING", "App " + bundle_name + " is already started");
			} else if (bundle_status == Bundle.STARTING) {
				msg = TemplateHelper.getStatusJSONString("WARNING", "App " + bundle_name + " is currently starting");
			} else if (bundle_status == Bundle.UNINSTALLED) {
				msg = TemplateHelper.getStatusJSONString("WARNING", "App " + bundle_name + " is uninstalled");
			} else {
				try {
					LogManager.logDebug("Start app: " + bundle_name);
					bundle.start();
					msg = TemplateHelper.getStatusJSONString("OK", "App " + bundle_name + " is started");
				} catch (BundleException e) {
					msg = TemplateHelper.getStatusJSONString("ERROR", e.getMessage());
				}
			}
		} else if (task.equals("stop")) {
			if (bundle_status == Bundle.INSTALLED) {
				msg = TemplateHelper.getStatusJSONString("WARNING", "App " + bundle_name + " is not started");
			} else if (bundle_status == Bundle.STARTING) {
				msg = TemplateHelper.getStatusJSONString("WARNING", "App " + bundle_name + " is currently starting");
			} else if (bundle_status == Bundle.UNINSTALLED) {
				msg = TemplateHelper.getStatusJSONString("WARNING", "App " + bundle_name + " is uninstalled");
			} else {
				try {
					LogManager.logDebug("Stop app: " + bundle_name);
					bundle.stop();
					msg = TemplateHelper.getStatusJSONString("OK", "App " + bundle_name + " is stopped");
				} catch (BundleException e) {
					msg = TemplateHelper.getStatusJSONString("ERROR", e.getMessage());
				} 
			}
		}
		root.put("message", msg);		
		return root; 
	}

	/** 
	 * From AppUI 
	 * @param b
	 * @return true if a given bundle is a BUG app
	 */
	private boolean isApp(Bundle b) {
		Object o = b.getHeaders().get("Bug-Bundle-Type");
		if (o != null && ((String) o).equals("Application")) {
			return true;
		}
		return false;
	}


	private String getBundleStatus(int state) {
		if (state == Bundle.ACTIVE)
			return "Active"; 
		else if (state == Bundle.INSTALLED)
			return "Installed";
		else if (state == Bundle.RESOLVED)
			return "Resolved";
		return "";
	}
}
