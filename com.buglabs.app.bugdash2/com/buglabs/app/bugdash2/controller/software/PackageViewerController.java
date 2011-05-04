package com.buglabs.app.bugdash2.controller.software;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.LogManager;
import com.buglabs.app.bugdash2.Package;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.TemplateModelRoot;

public abstract class PackageViewerController extends ApplicationController {

	public abstract String getIpkgStatus();
	
	public String getTemplateName() {
		return "software_packages_installed.fml";
	}

	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		SimpleHash root 			= new SimpleHash();			
		SimpleList packages_list 	= new SimpleList();
		List packages 				= new ArrayList(); 		// containing Package objects
		
		try {
			FileReader input 		= new FileReader(getIpkgStatus());
			BufferedReader br 		= new BufferedReader(input);
			List data 				= new ArrayList();
			String line; 
			while ((line = br.readLine()) != null) {
				data.add(line); 
				if (line.equals("")) {
					packages.add(Package.importData(data));
					data.clear(); 
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			LogManager.logWarning(e.getMessage());
		} catch (IOException e) {
			LogManager.logWarning(e.getMessage());
		} 
		
		Iterator itr = packages.iterator(); 
		Package p; 
		SimpleHash item;
		int count = 1; 
		while (itr.hasNext()) {
			p = (Package)itr.next(); 
			item = new SimpleHash();
			item.put("name", p.getName());
			item.put("version", p.getVersion());
			item.put("architecture", p.getArchitecture());
			item.put("status", p.getStatus());
			item.put("dependency", p.getDependency()); 
			item.put("index", count); 
			packages_list.add(item); 
			count++; 
		}
		root.put("packages", packages_list);
		packages = null; 
		return root;
	}

}
