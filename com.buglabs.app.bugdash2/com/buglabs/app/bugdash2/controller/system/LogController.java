package com.buglabs.app.bugdash2.controller.system;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.LogFile;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public abstract class LogController extends ApplicationController {

	private Map logs; 
	
	public Map getLogs() {
		return logs;
	}
	
	public final String tailLog(String path) {
		return "tail -f " + path; 
	}	

	public abstract String getOSGiLogPath();
	public abstract String getMessagesPath();
	
	public LogController() {
		this.logs = new HashMap();
		this.logs.put("OSGi", new LogFile("OSGi log", getOSGiLogPath(), this)); 
		this.logs.put("messages", new LogFile("Messages log", getMessagesPath(), this));
	}
	
	
	public String getTemplateName() {
		return "system_display_logs.fml";
	}

	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		SimpleList logs_list = new SimpleList();
		Iterator itr = logs.keySet().iterator(); 
		Object current; 
		while (itr.hasNext()) {
			SimpleHash item = new SimpleHash();
			current = itr.next(); 
			LogFile logfile = (LogFile)logs.get(current); 
			item.put("key", current.toString());
			item.put("description", logfile.getDescription());
			item.put("path", logfile.getPath());
			item.put("updatedAt", new SimpleScalar(logfile.getUpdatedAt().toString()));
			item.put("size", logfile.getSize());
			logs_list.add(item); 
		}
		SimpleHash root = new SimpleHash();
		root.put("logs_list", logs_list);
		return root;
	}

}
