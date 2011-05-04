package webadmin.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lib.Utils;

import webadmin.AdminConfigManager;
import webadmin.ConfigPropEntry;
import webadmin.LogManager;
import webadmin.WebAdminSettings;

//import com.buglabs.osgi.sewing.pub.RedirectInfo;
import com.buglabs.osgi.sewing.pub.RedirectInfo;
import com.buglabs.osgi.sewing.pub.SewingController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
/**
 * This is a simple authentication implementation for bugdash. 
 * A user is authenticated and a session id create at the point of login is stored as a client's cookie and in ConfigAdmin on BUG. 
 * The user can log out of BUGdash in two ways-- (1) manually log out; (2) when the browser window is closed (session cookie removed)
 *
 * @author akweon
 */
public class ApplicationController extends SewingController {

	private boolean authenticated; 
	
	public void beforeGet(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		try {
			checkPermission(req);
			this.skip_action = !authenticated; // don't process GET or POST if not authenticated 
		} catch (IOException e) {
			LogManager.logWarning("ApplicationController: exception from checkPermission- " + e.getMessage()); 
		}
	}

	public void beforePost(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		try {
			checkPermission(req);
			this.skip_action = !authenticated;
		} catch (IOException e) {
			LogManager.logWarning("ApplicationController: exception from checkPermission- " + e.getMessage()); 
		}
	}

	public RedirectInfo getRedirectInfo() {
		if (authenticated) return null; 
		else {
			return new com.buglabs.osgi.sewing.pub.RedirectInfo("/admin/login");
		}
	}
	
	public final boolean doRedirect() {
		return !authenticated;
	}

	/**
	 * Return to login page if a login is required and a session cookie does not match 
	 * @param req
	 * @throws IOException
	 */
	private void checkPermission(HttpServletRequest req) throws IOException {
		/*
		 * Check if login is required
		 *  - if yes, check if a session cookie exists. 
		 *  	- if yes, set authenticated = true 
		 *  	- else 
		 *  - if no, set authenticated = true 
		 */
		authenticated = false; 
		ConfigPropEntry requireLogin = AdminConfigManager.getConfigProperty( WebAdminSettings.CONFIG_PID_BUGDASH, 
																			 WebAdminSettings.CONFIG_KEY_BUGDASH_REQUIRE_LOGIN);	
		if (requireLogin != null) {
			if (requireLogin.getValue().equals("true")) {
				ConfigPropEntry configSession = AdminConfigManager.getConfigProperty(	WebAdminSettings.CONFIG_PID_BUGDASH, 
																						WebAdminSettings.CONFIG_KEY_BUGDASH_SESSIONS);
				String cookieHeader = req.getHeader("Cookie");
				if (cookieHeader != null) {
					Cookie[] cookies = req.getCookies();			
					if(cookies == null) {
						authenticated = false;
					}
					else {
						String clientSessionId = Utils.readCookie(cookies, WebAdminSettings.SESSION_COOKIE_NAME); 
						if (configSession != null && !clientSessionId.equals("")) {
							authenticated = WebAdminSettings.containsSessionId(clientSessionId); 
						}
					}
				} 
			} else {
				authenticated = true; 
			}
		} else {
			authenticated = true; 
		}
	}

}
