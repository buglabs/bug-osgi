package com.buglabs.app.bugdash2;

import java.util.List;

import com.buglabs.bug.base.pub.ICommandResponseHandler;

/**
 * Saves results from ShellService in a list 
 * @author akweon
 *
 */
public class ListCommandHandler implements ICommandResponseHandler {
	private List results; 
	
	public ListCommandHandler(List results) {
		this.results = results; 
	}
	
	public void response(String line, boolean isError) {
		results.add(line);
	}

}
