package com.buglabs.util.shell.pub;

import java.io.IOException;


/**
 * This is a shell session that's accessible from Java.  An instance of this service
 * represents a separate shell process running concurrently with the Java program.
 * 
 * @author kgilmer
 *
 */
public interface IShellSession {

	/**
	 * @param command executable
	 * @return stdout of command
	 * @throws IOException if any data on stderr is produced.
	 */
	String execute(String command) throws IOException;

	/**
	 * Execute a command and pass in a ICommandResponseHandler to deal with the output.
	 * @param command executable
	 * @param handler handle the response 
	 * @throws IOException on File I/O error
	 */
	void execute(String command, ICommandResponseHandler handler) throws IOException;
	
	/**
	 * Terminate the session and free any resources.
	 */
	void dispose();
	
	/**
	 * Interrupt any running programs.
	 */
	void interrupt();
}
