
package com.buglabs.util.shell.pub;

/**
 * A callback interface for handling output resulting from command execution
 * in a shell session.
 * 
 * @author kgilmer
 */
public interface ICommandResponseHandler {
	/**
	 * Client implements this method to handle output from shell session.
	 * @param line Line of output
	 * @param isError true if data was read from stderr, false if read from stdout.
	 */
	void response(String line, boolean isError);
}
