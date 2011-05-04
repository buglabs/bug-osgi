package com.buglabs.app.bugdash2.controller.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.buglabs.app.bugdash2.AdminConfigManager;
import com.buglabs.app.bugdash2.ConfigPropEntry;
import com.buglabs.app.bugdash2.LogManager;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import com.buglabs.util.StringUtil;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateModelRoot;

public class ConfigurationPropertyController extends ApplicationController {

	//private IAdminConfigManager adminConfig;
	private String template; 
	private final static String SERVICE_PID = "service.pid"; 
	
	public ConfigurationPropertyController() {
		//this.adminConfig = config; 
	}

	public String getTemplateName() {
		return template;
	}
	
	public TemplateModelRoot get(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		// based on pid, return a list of properties 
		template = "system_config_property_json.fml";
		String pid = params.get("pid"); 
		SimpleList properties_list = new SimpleList(); 
		List properties = new ArrayList(); 
		boolean has_service_pid = false; 
		if (pid != null) {
			try {
				properties = AdminConfigManager.getConfigPropertiesList(pid);
			} catch (IOException e) {
				LogManager.logWarning(e.getMessage());
			} 
			ConfigPropEntry prop; SimpleHash item; 
			int count = 0; // i could skip if there's service pid 
			for (int i=0; i<properties.size(); i++) {
				prop = (ConfigPropEntry)properties.get(i); 
				if (!prop.getKey().equals(SERVICE_PID)) {
					item = new SimpleHash(); 
					
					item.put("index", new SimpleNumber(count));
					item.put("key", prop.getKey());
					item.put("value", prop.getValue());
					properties_list.add(item); 
					count++;
				} else 
				{ 
					has_service_pid = true; 
				}
				
			}
		}
		SimpleHash root = new SimpleHash();
		root.put("properties_list", properties_list); 
		root.put("properties_size", new SimpleNumber(properties.size()-(has_service_pid ? 1 : 0))); // minus SERVICE_PID
		return root;
	}

	public TemplateModelRoot post(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		// handles CRUD based on task ("create", "update", "delete")
		template 		= "message.fml";

		String task 	= params.get("task"); 
		String message 	= ""; 
		
		if (task.equals("create")) {

		} else if (task.equals("delete")) {

		} else if (task.equals("update")) {
			message 	= "Update successful";
			//paramDebug(params);
			String pid 							= params.get("pid");
			String properties_to_delete 		= params.get("chb_delete");
			String prop_value 					= params.get("prop_value");  
			String prop_key 					= params.get("prop_key"); 
			String txt_new_property_key			= params.get("txt_new_property_key");
			String txt_new_property_value		= params.get("txt_new_property_value"); 
			
			String[] arr_properties_to_delete 	= StringUtil.split(properties_to_delete, ",");
			String[] arr_prop_values 			= StringUtil.split(prop_value, ",");
			String[] arr_prop_keys 				= StringUtil.split(prop_key, ",");
			
			// delete properties 
			for (int i=0; i<arr_properties_to_delete.length; i++) {
				if (!arr_properties_to_delete[i].equals("")) {
					try {
						AdminConfigManager.deleteConfigProperty(pid, arr_properties_to_delete[i]);
					} catch (IOException e) {
						message = e.getMessage(); 
						LogManager.logWarning(e.getMessage());
					}
				}
			}
			// update property values
			for (int i=0; i<arr_prop_keys.length; i++) {
				if (!arr_prop_keys[i].equals("")) {
					if (!contains(arr_properties_to_delete, arr_prop_keys[i])) {
						try {
							AdminConfigManager.saveConfigProperty(pid, arr_prop_keys[i], arr_prop_values[i]);
						} catch (IOException e) {
							message = e.getMessage(); 
							LogManager.logWarning(e.getMessage());
						}
					}
				}
			}
			if (!txt_new_property_key.equals("") && !txt_new_property_value.equals("")) {
				try {
					AdminConfigManager.saveConfigProperty(pid, txt_new_property_key, txt_new_property_value);
				} catch (IOException e) {
					message = e.getMessage(); 
					LogManager.logWarning(e.getMessage());
				}				
			}
		}
		SimpleHash root = new SimpleHash();
		root.put("message", message); 
		return root;
	}
	
	private static boolean contains(String[] list, String obj) {
		boolean found = false; 
		for (int i=0; i<list.length; i++) {
			if (list[i].equals(obj)) {
				found = true;
				break; 
			}
		}
		return found; 
	}

	
}
