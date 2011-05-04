package webadmin.controller.system;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import com.buglabs.util.StringUtil;

import webadmin.AdminConfigManager;
import webadmin.LogManager;
import webadmin.controller.ApplicationController;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.TemplateModelRoot;

public class ConfigurationController extends ApplicationController {

	public ConfigurationController() {

	}
	
	public String getTemplateName() { return "system_manage_configuration.fml"; }
	
	public TemplateModelRoot get(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		
		Configuration[] configs = null; 
		try {
			configs = AdminConfigManager.getConfigurations();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		} 
		SimpleHash root = new SimpleHash();
		SimpleList config_list = new SimpleList();
		SimpleHash item; 
		String pid; 
		for (int i=0; i<configs.length; i++) {
			pid = configs[i].getPid(); 
			item = new SimpleHash(); 
			item.put("pid", pid); 
			item.put("pid_cleaned", StringUtil.replace(pid, ".", "_"));
			config_list.add(item); 
		}
		root.put("config_list", config_list); 
		root.put("created", params.get("created"));
		return root;
	}

	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		
		String task = params.get("task"); 
		if (task.equals("create")) {
			String txt_config_pid = params.get("txt_config_pid"); 	
			try {
				AdminConfigManager.createConfig(txt_config_pid);
				params.put("created", txt_config_pid);
			} catch (IOException e) {
				LogManager.logWarning(e.getMessage());
			}
		} else if (task.equals("update")) {
			// not implemented
		} else if (task.equals("delete")) {
			String txt_remove_pid = params.get("txt_remove_pid"); 
			try {
				AdminConfigManager.deleteConfig(txt_remove_pid);
			} catch (IOException e) {
				LogManager.logWarning(e.getMessage());
			} 
		}
		return get(params, req, resp);
	}
	
	

}
