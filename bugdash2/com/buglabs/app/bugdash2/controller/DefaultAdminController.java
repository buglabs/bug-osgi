package com.buglabs.app.bugdash2.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.osgi.sewing.pub.SewingController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelRoot;

public class DefaultAdminController extends SewingController {
	
	public String getTemplateName() {
		return "message.fml";
	}


	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		return featureNotImplemented();
	}
	
	
	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		return get(params, req, resp);
	}


	private TemplateModelRoot featureNotImplemented() {
		SimpleHash hash = new SimpleHash();
		hash.put("message", "This functionality is not available");
		return hash;
	}
		
}
