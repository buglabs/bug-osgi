package com.buglabs.util.shell.pub;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * This service is accessed by clients to create a IShellSession service.
 * 
 * @author kgilmer
 */
public interface IShellService {
	
	/**
	 * @return A default instance.  Directory defaults to /tmp.
	 * @throws IOException on File I/O error
	 */
	IShellSession createShellSession() throws IOException;
	
	/**
	 * @param directory directory where shell should begin.
	 * @param output A writer where all output should go, or null if not required.
	 * @return instance of IShellSession
	 * @throws IOException on File I/O error
	 */
	IShellSession createShellSession(File directory, Writer output) throws IOException;
}
