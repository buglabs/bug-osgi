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
import freemarker.template.TemplateModelRoot;

public class LogoutController extends SewingController {
	public static final String REDIR_KEY = "redirect";
	public String getTemplateName() {
		return "loginRedirect.fml";
	}
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		String sessionId = "";
		String login = "";
		ConfigPropEntry required_login = null;
		try {
			required_login = AdminConfigManager.getConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_REQUIRE_LOGIN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(required_login != null)
		{
			login = required_login.getValue();
		}
		if(login.compareTo("false") == 0)
		{
			SimpleHash root = new SimpleHash();
			root.put(REDIR_KEY, "admin");
			return root;
		} else {
			Cookie[] cookies = req.getCookies();
			sessionId = Utils.readCookie(cookies, WebAdminSettings.SESSION_COOKIE_NAME);
			WebAdminSettings.removeSessionId(sessionId);
			//process to delete a cookie
			Cookie cookie = new Cookie(WebAdminSettings.SESSION_COOKIE_NAME, "");
			cookie.setMaxAge(0);
			cookie.setPath("/"); //cookie.setPath("/admin/");
			resp.addCookie(cookie);
			SimpleHash root = new SimpleHash();
	 		root.put(REDIR_KEY, "login");
			return root;
		}
	}
}
