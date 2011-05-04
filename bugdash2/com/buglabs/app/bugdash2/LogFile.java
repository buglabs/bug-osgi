package com.buglabs.app.bugdash2;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.buglabs.app.bugdash2.controller.system.LogController;
import com.buglabs.util.shell.pub.IShellService;

/**
 * Represents a log file entity for log readers
 * @author akweon
 *
 */
public class LogFile {
	private String description; 
	private String path; 
	private Date updatedAt; 
	private long size; 
	
	private ShellThread thread; 
	private LogController cmd; 
	
	public LogFile(String desc, String path, LogController cmd) {
		this.description = desc; 
		this.path = path;
		this.cmd = cmd; 
		getFileInfo();
	}
	
	public String getDescription() {
		return description;
	}
	public String getPath() {
		return path;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public long getSize() {
		return size;
	}
	
	private void getFileInfo() {
		File file = new File(this.path);
		this.size = file.length(); 
		this.updatedAt = new Date(file.lastModified()); 
	}
	
	public void runThread(IShellService shell) {
		stopThread(); 
		
		this.thread = new ShellThread(shell, cmd.tailLog(this.path));
		this.thread.start(); 
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
		else 
			return this.thread.getBuffer();
	}
	
	
}
