package com.buglabs.app.bugdash2.controller.software;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import com.buglabs.app.bugdash2.Activator;
import com.buglabs.app.bugdash2.App;
import com.buglabs.app.bugdash2.AppResultManager;
import com.buglabs.app.bugdash2.LogManager;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public class AppsBrowserController extends ApplicationController {

	public 	List 	appsList; 
	private List 	servicesList;
	public 	int 	installIndex = 0; 
	private String 	myTemplate; 

	public String getTemplateName() 	{ return this.myTemplate; }
	public String getInstallPath() 		{ return App.LOCAL_PATH; }

	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		
		this.myTemplate 	= "software_apps_from_bugnet.fml";
		boolean connected 	= App.checkNetworkConnection();
		
		// see which modules are attached 
		List modules_attached = new ArrayList(); 
		IModuleControl[] list = Activator.getModules();
		for (int i=0; i<list.length; i++) {
			if (list[i] != null ) {
				modules_attached.add(list[i].getModuleName());
			}
		}

		String search = "";
		String packages = ""; 
		String filter_by_packages = "false"; 
		int page = 1; 
		if (params.get("search") != null)
			search = params.get("search"); 
		if (params.get("packages") != null)
			search = params.get("packages"); 	
		if (params.get("page") != null)
			page = Integer.parseInt(params.get("page")); 
		if (params.get("filter_by_packages") != null)
			filter_by_packages = params.get("filter_by_packages"); 
		if (filter_by_packages.equals("true")) {
			StringBuilder sb = new StringBuilder();
			
			for (String pkg : getPackages()) {
				sb.append(pkg);
				sb.append(',');
			}
				
			packages = sb.toString();
			packages = packages.substring(0, packages.length() - 2);
		}
	
		AppResultManager manager = new AppResultManager(); 
		SimpleList apps_list = new SimpleList();
		if (connected) {
			manager = App.lookup(search, packages, page);
			appsList = manager.getResults();
			SimpleHash item; 
			Iterator itr = appsList.iterator();
			Iterator itr_modules; 
			App app; 
			int index = 1;
			String module_icons, module; 
			while(itr.hasNext()) {
				app = (App)itr.next(); 
				item = new SimpleHash();
				/* get a list of module icons */
				module_icons = "";
				itr_modules = app.getModules().iterator(); 
				while (itr_modules.hasNext()) {
					module = itr_modules.next().toString(); 
					module_icons += "<img src='" + App.getModuleIcon(module) + "' title='" + module + "' />&nbsp;";
				}
				/* rest of app info */
				item.put("index", index+((page-1)*manager.getPageSize())); 
				item.put("title", app.getTitle() );
				item.put("author", app.getAuthor() );
				item.put("downloads", app.getDownloads() );
				item.put("description", app.getDescription() );
				item.put("rating", app.getRating() );
				item.put("url", app.getUrl() );
				item.put("thumbnail", app.getThumbnail() );
				item.put("api_version", app.getApiVersion() ); 
				item.put("category", app.getCategory());
				item.put("maturity", app.getMaturity());
				item.put("module_icons", module_icons); 
				apps_list.add(item);
				index++; 
			}
		}
		SimpleHash root = new SimpleHash();
		root.put("apps_list", apps_list);
		root.put("modules", getModuleIconList(modules_attached));
		
		/* paging and filter */
		root.put("search", search);
		root.put("result_count", new SimpleNumber(manager.getSize()) );
		root.put("page", new SimpleNumber(page) );
		root.put("page_size", new SimpleNumber(manager.getPageSize()) );
		root.put("last_page", new SimpleNumber(  (long) Math.ceil(manager.getSize()/(float)manager.getPageSize()) ));
		root.put("filter_by_packages", filter_by_packages); 
		root.put("connected", new SimpleScalar(connected));

		return root;	
	}

	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		LogManager.logDebug("App to install: " + params.get("install")); 
		this.myTemplate = "message.fml"; 
		String msg = ""; 
		App app = new App(params.get("install")); 

		try {
			app.install(getInstallPath());
			LogManager.logDebug("App installed ok");			
			msg = "{status: 'OK', message: 'Installed successfully'}"; 
		} catch (IOException e) {
			LogManager.logWarning("App installed error: " + e.getMessage());		
			msg = "{status: 'ERROR', message: '" + e.getMessage() + "'}";
		}
		SimpleHash root = new SimpleHash();
		root.put("message", msg);		
		return root; 
	}
	
	
	private List<String> getPackages() {
		Bundle[] bundles = Activator.getContext().getBundles();
		String[] ps;
		String p; 
		List<String> packages = new ArrayList<String>(); 
		for(int i=0; i<bundles.length; i++) {
			p = (String)bundles[i].getHeaders().get("Export-Package"); 
			if (p != null) {
				ps = p.split(",");
				for (int j=0; j<ps.length; j++) {
					if (!omitPackage(ps[j])) {
						packages.add(stripName(ps[j]).trim());
					}
				}
			}
		}		
		return packages; 
	}
	
	// These private methods are used to retrieve a list of packages in com.buglabs.bug.module namespace; 
	// copied from com.buglabs.bug.module/com/buglabs/bug/module/PackageServlet.java
	private List populateServiceList() {
	    Bundle[] bundles = Activator.getContext().getBundles();
	    List services = new ArrayList();
	    ServiceReference[] sr; 
	    for (int i = 0; i < bundles.length; ++i) {
	        Bundle b = bundles[i]; 
	        sr = b.getRegisteredServices();
	        if (b != null && sr != null) {
	            services.addAll(getServiceRoots(sr));
	        }
	    }
	    return services;
	}	
	
	private boolean omitPackage(String packageName) {
	    if (!packageName.startsWith("com.buglabs.bug.module.")) {
	        return false;
	    }
	    servicesList = populateServiceList();
	    return !servicesList.contains(packageName);
	}
	
	private List getServiceRoots(ServiceReference[] registeredServices) {
	    List s = new ArrayList();
	    for (int i = 0; i < registeredServices.length; ++i) {
	        Object props = registeredServices[i].getProperty("objectClass");
	        String svcName = ((String[]) props)[0];

	        s.add(stripServiceName(svcName));
	    }
	    return s;
	}
	
	private String stripServiceName(String svcName) {
	   return svcName.substring(0, svcName.lastIndexOf('.'));
	}
	
	private String stripName(String headerValue) {
		return headerValue.split(";")[0];
	}	
	private String getModuleIconList(List list) {
		String result = "";
		for (int i=0; i <list.size(); i++) {
			if (Activator.getContext().getBundle().getResource("images/module_" + list.get(i).toString().toLowerCase() + ".jpg") != null)
				result += "<img src=\"/admin.images/module_" + list.get(i).toString().toLowerCase() + ".jpg\" title=\"" + list.get(i) + "\" /> ";
			else 
				result += "<img src=\"/admin.images/module_generic.jpg\" title=\"" + list.get(i) + "\" /> ";
		}
		return result; 
	}
	
}
