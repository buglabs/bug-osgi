package com.buglabs.app.bugdash2.controller.software;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.App;
import com.buglabs.app.bugdash2.ConfigFile;
import com.buglabs.app.bugdash2.LogManager;
import com.buglabs.app.bugdash2.Package;
import com.buglabs.app.bugdash2.ShellUtil;
import com.buglabs.app.bugdash2.TemplateHelper;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.nmea.StringUtil;
import com.buglabs.osgi.sewing.pub.util.FormFile;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelRoot;

public abstract class PackageInstallController extends ApplicationController {

	public List ipkg_sources; 
	public String getTemplateName() { return "software_package_install.fml"; }
	
	public final static String CUSTOM_CONFIG_PREFIX = "user_custom"; 

	public abstract String getConfigPath(); // where .conf files are stored e.g.: /etc/ipkg
	public abstract String getConfigFile();	// where main repo info is stored e.g.: /etc/ipkg.conf 
	public abstract String getDestPath(); 	// where uploaded file is saved 
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		
		TemplateModelRoot root = new SimpleHash();
		root.put("ipkg_sources", get_sources());
		root.put("ipkg_sources_size", new SimpleNumber(ipkg_sources.size()) );
		return root;
	}

	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		
		SimpleScalar result 			= new SimpleScalar("");
		String ipkg_name 				= params.get("ipkg_name"); 
		String ipkg_repo_url 			= params.get("ipkg_repo_url"); 
		String ipkg_repo_name 			= params.get("ipkg_repo_name");
		String ipkg_delete_repo 		= params.get("ipkg_delete_repo");
		String cmd, js_submit_status 	= "{category: '', message: '', detail: ''}";
		
		//paramDebug(params);
		
		// ipkg_repo_name is provided-- update ipkg.config 
		// TODO: validate URL 
		if (ipkg_repo_url != null && !ipkg_repo_url.equals("")) {
			if (ipkg_repo_name.equals(""))
				ipkg_repo_name = "myrepo";
			if (!exists_in_ipkg_sources(ipkg_repo_url)) {
				cmd = "echo \"src/gz " + ipkg_repo_name + " http://" + ipkg_repo_url + "\" >> " + getConfigPath() + "/" + CUSTOM_CONFIG_PREFIX + "_" + ipkg_repo_name + ".conf"; 
				LogManager.logDebug("updating config: " + cmd); 
				result = ShellUtil.getSimpleScalar(cmd); 
				js_submit_status = "{category: 'info', message: 'Repository information is saved successfully'}";
			}
		}
		// ipkg name is provided-- run install command 
		// TODO: validate ipkg name; don't allow & 
		if (ipkg_name != null && !ipkg_name.equals("")) {
			boolean connected 	= App.checkNetworkConnection();
			if (connected) {
				result = Package.install(ipkg_name);
				try {
					LogManager.logDebug("------ " + result.getAsString());
					if (result.getAsString().indexOf("error") > -1) {
						js_submit_status = "{category: 'error', message: 'There was a problem while installing " + ipkg_name + "', detail: '" + TemplateHelper.makeJSFriendly(result.getAsString()) + "'}";
					} else {
						js_submit_status = "{category: 'info', message: 'The package " + ipkg_name + " is installed successfully', detail: '" + TemplateHelper.makeJSFriendly(result.getAsString()) + "'}";
					}
				} catch (TemplateModelException e) {
					e.printStackTrace();
				}
			} else {
				result = new SimpleScalar("No network connection");
				js_submit_status = "{category: 'error', message: 'No network connection'}";
			}
		}
		// ipkg_delete_repo specified-- remove the file  
		if (ipkg_delete_repo != null && !ipkg_delete_repo.equals("")) {
			LogManager.logDebug("delete repo: " + ipkg_delete_repo); 
			if (ipkg_delete_repo.indexOf(CUSTOM_CONFIG_PREFIX) > -1) {
				cmd = "rm " + ipkg_delete_repo;
				result = ShellUtil.getSimpleScalar(cmd);
				js_submit_status = "{category: 'info', message: 'The config " + ipkg_delete_repo + " is removed'}";
			} else {
				js_submit_status = "{category: 'error', message: 'The config " + ipkg_delete_repo + " cannot be removed'}";
			}
		}
		// a file is provided-- upload and install the file 
		FormFile file = params.getFile(); 
		if (file != null) {
			byte[] ipkg_data = file.getBytes();  
			String output_path = getDestPath() + "/" + file.getFilename();
			LogManager.logDebug("saved a file: " + output_path);
			FileOutputStream fos;
			String result_msg; 
			String error_msg = "There was a problem while installing the file";
			try {
				fos = new FileOutputStream(output_path);
				fos.write(ipkg_data); 
				fos.close();		
				//cmd = "cd " + getDestPath() + " & ipkg install " + file.getFilename();
				cmd = "ipkg install " + getDestPath() + "/" + file.getFilename(); 
				LogManager.logDebug("install ipkg: " + cmd);
				result = ShellUtil.getSimpleScalar(cmd);
				result_msg = result.getAsString(); 
				if (result_msg.indexOf("error") == -1)
					js_submit_status = "{category: 'info', message: 'The package " + file.getFilename() + " is installed successfully', detail: '" + TemplateHelper.makeJSFriendly(result_msg) + "'}";
				else 
					js_submit_status = "{category: 'error', message: '" + error_msg + "', detail: '"+ TemplateHelper.makeJSFriendly(result_msg) + "'}";
			} catch (FileNotFoundException e) {
				js_submit_status = "{category: 'error', message: '" + error_msg + "', detail: '"+ TemplateHelper.makeJSFriendly(e.getMessage()) + "'}";
				LogManager.logWarning(e.getMessage());
			} catch (IOException e) {
				js_submit_status = "{category: 'error', message: '" + error_msg + "', detail: '"+ TemplateHelper.makeJSFriendly(e.getMessage()) + "'}";
				LogManager.logWarning(e.getMessage());
			} catch (TemplateModelException e) {
				js_submit_status = "{category: 'error', message: '" + error_msg + "', detail: '"+ TemplateHelper.makeJSFriendly(e.getMessage()) + "'}";
				e.printStackTrace();
			}
		}
		TemplateModelRoot root = new SimpleHash();
		root.put("result", result); 
		root.put("js_submit_status", new SimpleScalar(js_submit_status));
		root.put("ipkg_sources", get_sources());
		root.put("ipkg_sources_size", new SimpleNumber(ipkg_sources.size()) );		
		return root;
	}
	
	private boolean exists_in_ipkg_sources(String url) {
		boolean exists = false;
		for (int i=0; i<ipkg_sources.size(); i++) {
			if (ipkg_sources.get(i).toString().indexOf(url) > -1) {
				exists = true;	break;
			}
		}		
		return exists; 
	}
	
	private SimpleList get_sources() {
		// Read the main repo info from /etc/ipkg.conf 
		List repo_main = ConfigFile.getContentAsList(getConfigFile(), "src"); // source entries in ipkg.conf start with "src" prefix
		ipkg_sources = new ArrayList();  
		ipkg_sources.addAll(repo_main);
		SimpleList ipkg_sources_list = new SimpleList(); 
		SimpleHash item; 
		String[] source_entry; 
		for (int i=0; i<repo_main.size(); i++) {
			item = new SimpleHash();
			source_entry = StringUtil.split(repo_main.get(i).toString(), " "); 
			item.put("name", source_entry[1]);
			item.put("url", source_entry[2]);
			item.put("readonly", "true");
			item.put("file", getConfigFile()); 
			ipkg_sources_list.add(item);
		}		
		// Read all conf files in /etc/ipkg/
		File ipkg_config_path = new File( getConfigPath() );
		File[] files = ipkg_config_path.listFiles();
		List repo_universe; 
		if (files == null) return null; 
		for (int i=0; i<files.length; i++) {
			if (files[i].getName().endsWith(".conf")) {
				repo_universe = ConfigFile.getContentAsList(files[i].getAbsolutePath(), "src"); 
				if (repo_universe.size() > 0)
					ipkg_sources.addAll(repo_universe); 
				for (int j=0; j<repo_universe.size(); j++) {
					item = new SimpleHash();
					source_entry = StringUtil.split(repo_universe.get(j).toString(), " "); 
					item.put("name", source_entry[1]);
					item.put("url", source_entry[2]);
					item.put("readonly", (!files[i].getName().startsWith(CUSTOM_CONFIG_PREFIX + "_"))+""); // hackish way to pass in "false"
					item.put("file", files[i].getAbsolutePath()); 
					ipkg_sources_list.add(item);		
				}
			}
		}
		return ipkg_sources_list; 
	}

	
}
