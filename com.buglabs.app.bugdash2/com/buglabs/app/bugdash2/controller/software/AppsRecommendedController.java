package com.buglabs.app.bugdash2.controller.software;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.App;
import com.buglabs.app.bugdash2.AppResultManager;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public class AppsRecommendedController extends ApplicationController {

	public static String FEATURED_TAG = "featured"; 
	public List apps; 
	public int currentPage = 1; // support just one page
	
	public String getTemplateName() { return "software_apps_recommended.fml"; }
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {

		boolean connected 		= App.checkNetworkConnection();
		SimpleList apps_list 	= new SimpleList();
		apps 					= new ArrayList();
		AppResultManager manager = new AppResultManager(); 
		if (connected) {
			manager = App.lookupByTag(FEATURED_TAG, currentPage);
			apps = manager.getResults();		
			Iterator itr = apps.iterator();
			Iterator itr_modules; 
			int index = 1;
			String module_icons, module; 
			App app; SimpleHash item; 
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
				item.put("index", index+((currentPage-1)*manager.getPageSize())); 
				item.put("title", app.getTitle() );
				item.put("author", app.getAuthor() );
				item.put("downloads", app.getDownloads() );
				item.put("description", app.getDescription() );
				item.put("rating", app.getRating() );
				item.put("url", app.getUrl() );
				item.put("icon", app.getIcon() );
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
		root.put("apps_size", apps.size());
		root.put("connected", new SimpleScalar(connected));
		return root; 
	}

	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		return null;
	}

}
