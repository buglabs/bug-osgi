package com.buglabs.app.bugdash2;

import java.io.IOException;

public class WebAdminSettings {
	public static final String CONFIG_PID_BUGDASH				= "BUGDASH";
	public static final String CONFIG_KEY_BUGDASH_REQUIRE_LOGIN	= "bugdash.setting.boolean.require_login";
	public static final String CONFIG_KEY_BUGDASH_USERNAME		= "bugdash.credential.string.username";
	public static final String CONFIG_KEY_BUGDASH_PASSWORD		= "bugdash.credential.secure.password";
	public static final String CONFIG_KEY_BUGDASH_AUTHENTICATED = "bugdash.setting.boolean.authenticated"; // don't use this 
	public static final String CONFIG_KEY_BUGDASH_SESSIONS		= "bugdash.data.string.active_sessions"; 
	//public static final String CONFIG_KEY_BUGDASH_LAST_ACTION  	= "bugdash.data.datetime.last_action";
	
	public static final String CONFIG_KEY_BUGNET_LOGIN_KEY		= "bugnet.setting.string.current_login_key";
	public static final String CONFIG_KEY_BUGNET_USERNAME 		= "bugnet.credential.string.username";
	public static final String CONFIG_KEY_BUGNET_PASSWORD		= "bugnet.credential.secure.password";
	
	public static final double INACTIVE_TIMEOUT_DURATION 		= 1000*60*15;  // 15 min 
	
	public static final String SESSION_COOKIE_NAME				= "bugdashSessionId"; 
	public static final String BUGNET_LOGIN_KEY_COOKIE			= "bugnetLoginKey"; 
	
	public static String bugnetLogin, bugnetPwd; 
		
	public static void addSessionId(String sessionId) {
		// TODO: given a sessionId, store it in ConfigAdmin in form of session_id0|session_id1|session_id2|...
	}
	
	public static void removeSessionId(String sessionId) {
		// TODO: given a sessionId, remove it from ConfigAdmin; do clean up here too
		String sessionBank = getSessionBank();
		int index = sessionBank.indexOf(sessionId);
		int length = sessionBank.length();
		String newSessionBank = "";
		newSessionBank = sessionBank.substring(0, index);
		newSessionBank += sessionBank.substring(index + sessionId.length() + 1, length);
		try {
			AdminConfigManager.saveConfigProperty(CONFIG_PID_BUGDASH, CONFIG_KEY_BUGDASH_SESSIONS, newSessionBank);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static boolean containsSessionId(String sessionId) {
		String sessionBank = getSessionBank();
		if(sessionBank.indexOf(sessionId) != -1) {
			return true;
		} else {
			return false;
		}
		
	}
	private static String getSessionBank()
	{
		String sessionBank = "";
		try {
			ConfigPropEntry session_entry = AdminConfigManager.getConfigProperty(CONFIG_PID_BUGDASH, CONFIG_KEY_BUGDASH_SESSIONS);
			if(session_entry != null) {
				sessionBank = session_entry.getValue();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sessionBank;
	}
	
	public static void populateBugnetLogin(String loginKey) {
		ConfigPropEntry entry;
		try {
			entry = AdminConfigManager.getConfigProperty(CONFIG_PID_BUGDASH, CONFIG_KEY_BUGNET_USERNAME+"."+loginKey);
			if (entry != null)
				bugnetLogin = entry.getValue(); 	
			entry = AdminConfigManager.getConfigProperty(CONFIG_PID_BUGDASH, CONFIG_KEY_BUGNET_PASSWORD+"."+loginKey);
			if (entry != null)
				bugnetPwd = entry.getValue();
			AdminConfigManager.saveConfigProperty(CONFIG_PID_BUGDASH, CONFIG_KEY_BUGNET_LOGIN_KEY, loginKey);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			cleanupBugnetLogin();
		}
	}
	
	public static void cleanupBugnetLogin() {
		// TODO: cleanup unused BUGnet keys (delete all except for one with CONFIG_KEY_BUGNET_LOGIN_KEY) 
	}
	
	public static boolean isLoggedIn() {
		return (bugnetLogin != null && !bugnetLogin.equals("") && bugnetPwd != null && !bugnetPwd.equals("")); 
	}
}
