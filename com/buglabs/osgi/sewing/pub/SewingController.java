/*
 * Sewing: a Simple framework for Embedded-OSGi Web Development
 * Copyright (C) 2009 Bug Labs
 * Email: bballantine@buglabs.net
 * Site: http://www.buglabs.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */

package com.buglabs.osgi.sewing.pub;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.log.LogService;

import com.buglabs.osgi.sewing.LogManager;
import com.buglabs.osgi.sewing.RedirectInfo;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.TemplateModelRoot;

/**
 * Implement SewingController to handle the requests to your Sewing App
 * Typically these are public inner classes in your SewingHttpServlet, but they
 * don't have to be. The important thing is to map the url to the controller in
 * your SewingHttpServlet.getControllerMap() method.
 * 
 * Lastly, the convention is that the controller class name matches the template
 * name which matches the url. But this doesn't have to be the case.
 * 
 * To map a different url to a different controller, do so in your
 * SewingHttpServlet implementation's getControllerMap() method. If you only do
 * this, the controller will still use the url to map to the template. To use a
 * different template, override getTemplateName() in your SewingController.
 * 
 * 
 * @author bballantine
 * 
 */
public abstract class SewingController {

	private boolean pending_redirect = false;
	private RedirectInfo pending_redirect_info = null;

	/**
	 * Override this method to handle get requests
	 * 
	 * Default implementation will simply render the correctly named template
	 * without any processing
	 * 
	 * @param params
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	public TemplateModelRoot get(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		return null;
	}

	/**
	 * Override this method to handle post requests
	 * 
	 * Default implementation will simply render the correctly named template
	 * without any processing
	 * 
	 * @param params
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	public TemplateModelRoot post(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		return null;
	}

	/**
	 * Override this method if you need to set a name for your template that is
	 * not part of the conventions. Include the extension in the name you
	 * provide.
	 * 
	 * SewingHttpServlet calls this when looking for a template. If it gets
	 * null, it uses the url to find the template. For example, if the url ends
	 * with 'foobar', and this method has not been overridden in the controller
	 * for foobar, then it will try to load foobar.fml from the templates folder
	 * 
	 * @return
	 */
	public String getTemplateName() {
		return null;
	}

	/**
	 * Tells servlet to process a redirect if one is pending
	 * 
	 * @return
	 */
	public final boolean doRedirect() {
		return pending_redirect;
	}

	/**
	 * 
	 * @return
	 */
	public RedirectInfo getRedirectInfo() {
		return pending_redirect_info;
	}

	/**
	 * 
	 */
	public void clearRedirect() {
		pending_redirect = false;
		pending_redirect_info = null;
	}

	/**
	 * Called from controller to set up for a redirect to a different controller
	 * This is the best way to pass params to a new controller
	 * 
	 * @param requestType
	 * @param controller
	 * @param params
	 * @param req
	 * @param resp
	 */
	protected final void render(int requestType, String controllerName, RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		pending_redirect_info = new RedirectInfo(requestType, controllerName, params, req, resp);
		pending_redirect = true;
	}

	/**
	 * This does a simple redirect by setting the meta refresh tag in the html
	 * page
	 * 
	 * @param url
	 */
	protected final void redirectTo(String url) {
		pending_redirect_info = new RedirectInfo(url);
		pending_redirect = true;
	}

	/**
	 * Go ahead and have fun
	 * 
	 * @param params
	 */
	protected final void paramDebug(RequestParameters params) {
		LogManager.log(LogService.LOG_DEBUG, "*********************************");
		LogManager.log(LogService.LOG_DEBUG, "Writing Params");
		LogManager.log(LogService.LOG_DEBUG, "*********************************");

		Set s = params.getInnerMap().entrySet();
		Iterator itr = s.iterator();
		while (itr.hasNext()) {
			Entry e = (Entry) itr.next();
			LogManager.log(LogService.LOG_DEBUG, e.getKey().toString() + " -> " + e.getValue().toString());
		}

		if (params.getFile() != null) {
			LogManager.log(LogService.LOG_DEBUG, "Writing file to /tmp/" + params.getFile().getFilename());
			LogManager.log(LogService.LOG_DEBUG, "Content Type: " + params.getFile().getContentType());
			try {
				FileOutputStream fostream = new FileOutputStream("/tmp/" + params.getFile().getFilename());
				fostream.write(params.getFile().getBytes());
				fostream.flush();
				fostream.close();
			} catch (IOException e) {
				LogManager.log(LogService.LOG_ERROR, "Could not write file to /tmp/", e);
				LogManager.log(LogService.LOG_DEBUG, LogManager.stackTraceAsString(e));
			}
		}
		LogManager.log(LogService.LOG_DEBUG, "*********************************");
		LogManager.log(LogService.LOG_DEBUG, "*********************************");
	}

}
