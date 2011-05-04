package com.buglabs.app.bugdash2.controller.software;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.App;
import com.buglabs.app.bugdash2.ShellManager;
import com.buglabs.app.bugdash2.ShellThread;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelRoot;

public abstract class PackageNewUpdateController extends ApplicationController {
	
	protected ShellThread thread;

	public abstract String getCommand(); 
	
	public String getTemplateName() { return "message.fml"; }
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		String js_submit_status;
		if (this.thread != null) {
			this.thread.cancel(); this.thread = null; 			
		}
		boolean connected 	= App.checkNetworkConnection();
		
		if (connected) {
			this.thread = new ShellThread(ShellManager.getShell(), getCommand()); 
			this.thread.start(); 	
			js_submit_status = "{category: 'info', message: 'Checking for new updates...'}";
		} else {
			js_submit_status = "{category: 'error', message: 'No network connection'}";
		}
		SimpleHash root = new SimpleHash();
		root.put("message", js_submit_status);		
		return root;
	}

	public void stopThread() {
		if (this.thread != null) {
			this.thread.cancel();
			this.thread = null; 			
		}		
	}
	
	public List getThreadBuffer() {
		if (this.thread == null) 
			return null;
		return this.thread.getBuffer();
	}
}
