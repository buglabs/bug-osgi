package webadmin.controller.software;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webadmin.App;
import webadmin.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelRoot;

public class AppViewerController extends ApplicationController {

	public String getTemplateName() {
		return "software_app_viewer.fml";
	}
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		String title = ""; 
		if (params.get("title") != null)
			title = params.get("title"); 
		SimpleHash root = new SimpleHash();
		
		if (!title.equals("")) {
			App app = new App(title);
			app.lookupByTitle(); 
			
			root.put("title", title);
			root.put("version", app.getVersion()); 
			root.put("author", app.getAuthor()); 
			root.put("download", app.getDownloads());
			root.put("description", app.getDescription());
			root.put("rating", app.getRating());
			root.put("url", app.getUrl());
			root.put("screenshot", app.getMedium());
			root.put("icon", app.getIcon()); 
			root.put("apiVersion", app.getApiVersion()); 
			root.put("category", app.getCategory());
			root.put("maturity", app.getMaturity());
			root.put("packages", app.displayPackages());
			root.put("modules", getModuleIconList(app.getModules())); 
			root.put("createdAtFormatted", app.getCreatedAtFormatted() );
			root.put("admins", getUserProfileList(app.getAdmins())); 
			root.put("collaborators", getUserProfileList(app.getCollaborators())); 
		}
		return root; 
	}
	
	private String getUserProfileList(List users) {
		String out = ""; 
		String login; 
		Iterator itr = users.iterator(); 
		while (itr.hasNext()) {
			login = itr.next().toString(); 
			out += "<a href=\"" + App.getAuthorURL(login) + "\" title=\"" + login + "\" target=\"_bugnet\">" + login + "</a>&nbsp;"; 
		}
		return out; 
	}
	
	private String getModuleIconList(List modules) {
		String module;
		String module_icons = "";
		Iterator itr_modules = modules.iterator(); 
		while (itr_modules.hasNext()) {
			module = itr_modules.next().toString(); 
			module_icons += "<img src='" + App.getModuleIcon(module, "medium") + "' title='" + module + "' />&nbsp;";
		}		
		return module_icons; 
	}


}
