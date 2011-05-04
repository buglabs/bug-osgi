package com.buglabs.app.bugdash2.controller.software;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.buglabs.app.bugdash2.ShellManager;
import com.buglabs.app.bugdash2.ShellThread;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelRoot;

// this class contains a thread that will be accessed by other controllers 

public abstract class PackageUpgradeController extends ApplicationController {
	
	protected ShellThread thread; 
	protected CommandItem[] commands; 
	
	public PackageUpgradeController() { }
	
	public abstract CommandItem[] getCommands(); 

	public String getTemplateName() { return "message.fml"; }	
	
	// this needs to be POST later 
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		
		commands = getCommands(); 
		String[] cmd_list = CommandItem.getCommandText(commands); 

		if (this.thread != null) {
			this.thread.cancel();
			this.thread = null; 			
		}
		this.thread = new ShellThread(ShellManager.getShell(), cmd_list); 
		this.thread.start(); 
		
		SimpleHash root = new SimpleHash();
		root.put("message", "upgrade started"); 		
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

	public String getCurrentCommand() {
		if (this.thread == null)
			return ""; 
		return this.commands[this.thread.getCurrentIndex()].command;
	}
	
	public int getCurrentPercent() {
		if (this.thread == null)
			return 0; 
		return this.commands[this.thread.getCurrentIndex()].percentage;
	}
	
	public String getCurrentDescription() {
		if (this.thread == null)
			return ""; 
		return this.commands[this.thread.getCurrentIndex()].description;
	}	
	
	public static class CommandItem {
		private String command; 
		private String description; 
		private int percentage; 
		
		public CommandItem(String cmd, String desc, int i) {
			this.command = cmd; 
			this.description = desc; 
			this.percentage = i; 
		}

		public static String[] getCommandText(CommandItem[] list) {
			String[] arr = new String[list.length];
			for (int i=0; i<list.length; i++) {
				arr[i] = list[i].command;
			}
			return arr; 
		}
	}
	
	
}
