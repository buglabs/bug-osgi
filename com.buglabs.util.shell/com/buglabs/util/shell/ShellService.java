package com.buglabs.util.shell;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.buglabs.util.shell.pub.IShellService;
import com.buglabs.util.shell.pub.IShellSession;
import com.buglabs.util.shell.pub.ShellSession;


/**
 * Default implementation of IShellService.
 * 
 * @author kgilmer
 */
public class ShellService implements IShellService {

	public IShellSession createShellSession() throws IOException {
		return new ShellSession(new File("/tmp"), null);
	}
	
	public IShellSession createShellSession(File directory, Writer output) throws IOException {
		return new ShellSession(directory, output);
	}

}
