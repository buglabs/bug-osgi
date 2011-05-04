package com.buglabs.app.bugdash2;

import com.buglabs.util.shell.pub.ICommandResponseHandler;

import freemarker.template.SimpleScalar;

public class TemplateCommandHandler implements ICommandResponseHandler {
	private static final String ERROR = "[ERROR]";
	private SimpleScalar results;
	private String holder;
	
	public TemplateCommandHandler(SimpleScalar results) {
		this.results = results;
		holder = "";
	}

	public void response(String line, boolean isError) {
		if (isError) holder += ERROR + " ";
		holder += line + "\n";
		results.setValue(holder);
	}
}
