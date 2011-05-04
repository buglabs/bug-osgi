package com.buglabs.app.bugdash2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.buglabs.util.shell.pub.ICommandResponseHandler;
import com.buglabs.util.shell.pub.IShellService;
import com.buglabs.util.shell.pub.IShellSession;

/**
 * This class uses ShellService to execute a command and stores the output via BufferManager. 
 * Use this class for commands with continous output. 
 * @author akweon
 *
 */
public class ShellThread extends Thread {

	private final long TIMEOUT_DURATION = 2*60*1000; // 2 min 
	
	private IShellService shell; 
	private IShellSession session = null;
	private String command; 
	private String[] commands; 
	private int currentIndex; 	// when String[] commands is used, read this to determine what's running
	private List buffer;
	private Date lastAccessed;   
	
	public ShellThread(IShellService shell, String command) {
		this.shell = shell;
		this.command = command;
		this.commands = new String[0]; 
		buffer = new ArrayList();
	} 
	
	public ShellThread(IShellService shell, String[] commands) {
		this.shell = shell; 
		this.commands = commands; 
		buffer = new ArrayList(); 
	}
	
	public IShellService getShell() {
		return shell;
	}

	public void setShell(IShellService shell) {
		this.shell = shell;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public List getBuffer() {
		List tmp;
		synchronized(buffer) {
			tmp = new ArrayList(buffer);
			buffer.clear();
			this.lastAccessed = new Date();
		}
		return tmp;
	}
	
	public void addToBuffer(String data) {
		Date now = new Date(); 
		if (this.lastAccessed != null && (now.getTime()-this.lastAccessed.getTime()) > TIMEOUT_DURATION) {
			data = "[TIMEOUT]";
			this.session.dispose();
		}
		synchronized (buffer) {
			buffer.add(data);
		}
	}
	
	
	public int getCurrentIndex() {
		return currentIndex;
	}

	public void run() {
		try {
			session = shell.createShellSession();  
			if (this.commands.length > 0) {
				for (int i=0; i<this.commands.length; i++) {
					this.currentIndex = i; 
					LogManager.logDebug("ShellThread run [" + i + "]: " + this.commands[i]); 
					session.execute(this.commands[i], new CommandHandler());
				}
			} else {
				LogManager.logDebug("ShellThread run: " + this.command);
				session.execute(this.command, new CommandHandler());
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void cancel() {
		if (session != null) {
			session.dispose();
			session = null;
		}
	}
	
	public String getDataAsString() {
		String str = "";
		for(int i=0; i<this.buffer.size(); i++) {
			str += this.buffer.get(i) + "\n";
		}
		return str;
	}
	
	public class CommandHandler implements ICommandResponseHandler {

		public void response(String line, boolean isError) {
			String prefix = (isError) ? "[ERROR] " : "";
			addToBuffer(prefix + line);
		}
		
	}
	
}
