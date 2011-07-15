/**
 * Used to handle the login and logout processes on the BUGnet page of bugdash.
 */
package com.buglabs.app.bugdash2.controller.bugnet;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.AdminConfigManager;
import com.buglabs.app.bugdash2.LogManager;
import com.buglabs.app.bugdash2.TemplateHelper;
import com.buglabs.app.bugdash2.WebAdminSettings;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.app.bugdash2.utils.Utils;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import com.buglabs.util.simplerestclient.BasicAuthenticationConnectionProvider;
import com.buglabs.util.simplerestclient.HTTPException;
import com.buglabs.util.simplerestclient.HTTPRequest;
import com.buglabs.util.simplerestclient.HTTPResponse;
import com.buglabs.util.simplerestclient.IConnectionProvider;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

/**
 * @author Michael Angerville
 * 
 * UPDATES: 
 * AK 2010-08-09: fixed the clear text password and added a "login key" to client cookie and ConfigAdmin to support multiple users 
 * 
 */
public class BUGnetController extends ApplicationController
{
	private String 	myTemplate;
	
	public String getTemplateName() { return this.myTemplate; }
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		this.myTemplate = "bugnet_bugnet.fml"; 
		SimpleHash root = new SimpleHash();
		root.put("logged_in", WebAdminSettings.isLoggedIn());
		root.put("username", WebAdminSettings.bugnetLogin);
		return root;
	}

	/**
	 * Obtains the user-inputted username and password for BUGnet and attempts to
	 * authenticate the user with these login credentials.
	 * @author Michael Angerville
	 * 
	 * UPDATES: 
	 * AK 2010-08-09: added ajax support 
	 * 
	 */
	public TemplateModelRoot post(RequestParameters params,
            HttpServletRequest req, HttpServletResponse resp)
	{
		boolean use_json = params.get("format") != null && params.get("format").equals("json"); 
		this.myTemplate = (use_json) ? "message.fml" : "bugnet_bugnet.fml"; 
		
		TemplateModelRoot root = null;
		String message = ""; 
		String username = params.get("inp_username");
        String password = params.get("inp_password");
        boolean logged_in; 

        if (params.get("btn_submit") != null && params.get("btn_submit").equals("Logout")) {
        	logout(resp); 
        	message = TemplateHelper.getGlobalStatusJSONString("info", "Logged out successfully"); 
        	
        } else if (username != null && !username.equals("") && password != null && !password.equals("")) {
        	logged_in = login(username, password); 
        	if (logged_in) {
        		message = TemplateHelper.getGlobalStatusJSONString("info", "Logged in to BUGnet successfully"); 
        		// because I removed "remember_me" control, it won't get here..
        		if (params.get("remember_me") != null && params.get("remember_me").equals("yes")) {
        			String loginKey = Utils.sessionIdGenerator();
        				
        			try {
						AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGNET_USERNAME+"."+loginKey, username);
						AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGNET_PASSWORD+"."+loginKey, username);			
						AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGNET_LOGIN_KEY, loginKey);
						
	        			Cookie cookie = new Cookie(WebAdminSettings.BUGNET_LOGIN_KEY_COOKIE, loginKey);
	        			cookie.setMaxAge(60*60*24*30); // 30 days -setting cookie's expiration date doesn't work..
	        			resp.addCookie(cookie);
					} catch (IOException e) {
						message = TemplateHelper.getGlobalStatusJSONString("error", "There was a problem logging in"); 
						e.printStackTrace();
					}
        		} 
    			WebAdminSettings.bugnetLogin = username; 
    			WebAdminSettings.bugnetPwd = password;         		
        	} else {
        		message = TemplateHelper.getGlobalStatusJSONString("error", "Authentication failed"); 
        	}
        	
        } else {
        	message = TemplateHelper.getGlobalStatusJSONString("error", "Both username and password are required"); 
        }
		
		if (use_json) {
			root = new SimpleHash(); 
			root.put("message", new SimpleScalar(message));
		} else {
			root = get(params, req, resp);
			root.put("username", new SimpleScalar(username));
			root.put("js_submit_status", new SimpleScalar(message));
		}
        return root;
    }
	
	private boolean login(String user, String pass)
	{
		boolean success = false; 
		IConnectionProvider basicAuthConnection = new BasicAuthenticationConnectionProvider(user, pass);
		HTTPRequest httpRequest = new HTTPRequest(basicAuthConnection);
		try
		{
			HTTPResponse httpResponse = httpRequest.get("http://api.buglabs.net/v2/users/verify");
		    if (httpResponse.getResponseCode() == 200)
		    	success = true; 
		}
		catch(HTTPException e)
		{
			LogManager.logDebug("cannot log in to BUGnet: " + e.getMessage()); 
		}
		catch(IOException e)
		{
			LogManager.logDebug("cannot log in to BUGnet: " + e.getMessage()); 
		}
		return success; 
	}
	
	public static void logout(HttpServletResponse resp)
	{
		try
		{
			Cookie cookie = new Cookie(WebAdminSettings.BUGNET_LOGIN_KEY_COOKIE, "");
			cookie.setMaxAge(0); // expire 
			resp.addCookie(cookie);			
			
			WebAdminSettings.bugnetLogin = ""; 
			WebAdminSettings.bugnetPwd = ""; 
			
			AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGNET_USERNAME, "");
			AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGNET_PASSWORD, "");
			AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGNET_LOGIN_KEY, "");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}	
	}
	
/*
	public static boolean containsConfig(String pid) throws IOException, InvalidSyntaxException
	{
		Configuration[] configs = AdminConfigManager.getConfigurations();
		for(int i = 0; i < configs.length; i++)
		{
			if(pid.equals(configs[i].getPid()))
				return true;
		}
		return false;
	} */

}