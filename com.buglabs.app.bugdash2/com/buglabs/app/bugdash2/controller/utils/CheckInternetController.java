package com.buglabs.app.bugdash2.controller.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.App;
import com.buglabs.osgi.sewing.pub.SewingController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public class CheckInternetController extends SewingController {

	public String getTemplateName() { return "message.fml"; }
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		
		SimpleHash root = new SimpleHash();
		boolean connected = App.checkNetworkConnection(); 
		root.put("message", new SimpleScalar("{connected: " + connected + "}"));
		return root;
	}


}
