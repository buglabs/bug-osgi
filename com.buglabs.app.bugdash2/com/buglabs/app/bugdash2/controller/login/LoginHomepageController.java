package com.buglabs.app.bugdash2.controller.login;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.buglabs.app.bugdash2.AdminConfigManager;
import com.buglabs.app.bugdash2.ConfigPropEntry;
import com.buglabs.app.bugdash2.WebAdminSettings;
import com.buglabs.app.bugdash2.utils.Utils;
import com.buglabs.osgi.sewing.pub.SewingController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public class LoginHomepageController extends SewingController {

	public String getTemplateName() {
		return "homelogin.fml";
	}

	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		String loginBoolean = "";
		String loginCorrect = "false";
		
		try {
			ConfigPropEntry config_needLogin = AdminConfigManager.getConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, 
																					WebAdminSettings.CONFIG_KEY_BUGDASH_REQUIRE_LOGIN);
			if(config_needLogin != null)
			{
				loginBoolean = config_needLogin.getValue();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		SimpleHash root = new SimpleHash();
		root.put("login_needed", new SimpleScalar(loginBoolean));
		root.put("login_correct", new SimpleScalar(loginCorrect));
		return root;
	}

	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		//the user has submitted a username and password. It needs to be checked against
		//the real username and password
		String userAttempt 		= params.get("inp_username");
		String passwordAttempt 	= params.get("inp_password");
		String realUser 		= "";
		String realPassword 	= "";
		String loginCorrect 	= "";
		String error 			= "";
		try {
			realUser 		= AdminConfigManager.getConfigPropertyValue( WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_USERNAME);
			realPassword 	= AdminConfigManager.getConfigPropertyValue( WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_PASSWORD);			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//now that real username and real password are retrieved, lets check to see if they are equal!
		if(userAttempt.compareTo(realUser) == 0 && passwordAttempt.compareTo(realPassword) == 0)
		{
			//return something so that we can access the page!
			loginCorrect = "true";
			/*
			 * AK: This is probably not used. right? we decided not to save the login status flag in ConfigAdmin because it will affect other users 
			try {
				AdminConfigManager.saveConfigProperty(BUG_LOGIN_KEY, BUG_LOGIN_ENTRY_CORRECT, "true");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */
			//generate the session id
			String sessionID = Utils.sessionIdGenerator(); 
			//store that session id as a cookie on the browser
			Cookie cookie = new Cookie(WebAdminSettings.SESSION_COOKIE_NAME, sessionID);
			cookie.setPath("/");
			resp.addCookie(cookie);
			//add the session id to the configuration property
			String sessions = "";
			ConfigPropEntry session_cookie = null;
			try {
				session_cookie = AdminConfigManager.getConfigProperty(	WebAdminSettings.CONFIG_PID_BUGDASH, 
																		WebAdminSettings.CONFIG_KEY_BUGDASH_SESSIONS);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//if the value is null, then this is the first session and we will save anyway. if it does exist, grab it, append to the string, and save it.
			if(session_cookie != null)
			{
				sessions = session_cookie.getValue();
			}
			sessions += sessionID + "|";
			try {
				AdminConfigManager.saveConfigProperty(	WebAdminSettings.CONFIG_PID_BUGDASH, 
														WebAdminSettings.CONFIG_KEY_BUGDASH_SESSIONS, sessions);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			//return an error and redo the page
			loginCorrect = "false";
			/* AK: is this used? 
			try {
				AdminConfigManager.saveConfigProperty(BUG_LOGIN_KEY, BUG_LOGIN_ENTRY_CORRECT, "false");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */
			error = "Incorrect username/password. Please try again";
		}
		
		SimpleHash root = new SimpleHash();
		root.put("login_correct", new SimpleScalar(loginCorrect));
		root.put("login_needed", new SimpleScalar(true));
		root.put("error", error);
		return root;
	}

}
