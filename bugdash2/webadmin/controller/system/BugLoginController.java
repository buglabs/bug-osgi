package webadmin.controller.system;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webadmin.AdminConfigManager;
import webadmin.LogManager;
import webadmin.TemplateHelper;
import webadmin.WebAdminSettings;
import webadmin.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

/**
 * @author Jeff Scinckler
 * 
 * UPDATE:
 * 	2010-07-30 AK added a condition for empty passwords and updated the form to use the global status bar for messages 
 *  2010-08-10 AK made it to use getConfigPropertyValue and saveConfigProperty as they take care of decoding/encoding 
 * 
 */
public class BugLoginController extends ApplicationController {

	public String getTemplateName() {
		return "system_dash_login.fml";
	}
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		//get the boolean property from the dictionary
		try {
			String save = AdminConfigManager.getConfigPropertyValue(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_REQUIRE_LOGIN);;
			String username = AdminConfigManager.getConfigPropertyValue(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_USERNAME);
			String password = AdminConfigManager.getConfigPropertyValue(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_PASSWORD);

			if (save != null && save.equals("false"))
				password = ""; // dash is not required, don't populate password 
			
			//pass that value to the FML so that it can be read
			SimpleHash root = new SimpleHash();
			root.put("save_value", save);
			root.put("username_value", username);
			root.put("passwd_value", password);
			return root;
		} catch (IOException e) {
			LogManager.logDebug(this.getClass().getName() + ": " + e.getMessage());
		}
		return super.get(params, req, resp);
	}

	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {

		String msg 			= ""; 
		String boxChecked 	= params.get("inp_uselogin"); //get the values from the fml
		String user			= params.get("inp_username");
		String pass1 		= params.get("inp_password");
		String pass2 		= params.get("inp_repassword");
		String save_value 	= "";
		String output 		= "";
		String username 	= "";
		String password 	= "";		
		
		//check to see what settings the user selected
		if(boxChecked == null)
		{
			boxChecked = "";
		}
		
		//either save the values to the dictionary or return an error
		try {
			//check to see if the user wanted to save their login and password
			if(boxChecked.compareTo("activate") == 0)
			{
				save_value = "true"; // this leaves the checkbox checked if there's an error 
				if(!pass1.equals("") && !pass2.equals("") && passwordCheck(pass1, pass2))
				{
					AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_REQUIRE_LOGIN, "true");
					AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_USERNAME, user);
					AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_PASSWORD, pass1);
					msg = TemplateHelper.getGlobalStatusJSONString("info", "Login info is saved successfully"); 
				}
				else
				{
					// error with password-- blank it so that the user can fill it out again 
					AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_PASSWORD, "");
					password = ""; 
					output = "Passwords do not match. Please try again."; 
					msg = TemplateHelper.getGlobalStatusJSONString("error", "Passwords do not match. Please try again."); 
				}
			}
			else
			{
				AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_REQUIRE_LOGIN, "false");
				save_value = "false";
				msg = TemplateHelper.getGlobalStatusJSONString("info", "Login info is saved successfully");
			}
			//get the values again
			username = AdminConfigManager.getConfigPropertyValue(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_USERNAME);
			password = AdminConfigManager.getConfigPropertyValue(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_PASSWORD);

			TemplateModelRoot root = get(params, req, resp);
			root.put("output", new SimpleScalar(output));
			root.put("save_value", new SimpleScalar(save_value));
			root.put("username_value", new SimpleScalar(username));
			root.put("passwd_value", new SimpleScalar(password));
			root.put("js_submit_status", new SimpleScalar(msg));
			root.put("submitted", new SimpleScalar("true")); 
			return root;
		} catch (IOException e) {
			LogManager.logWarning(e.getMessage());
			output = "An error occured.";
			TemplateModelRoot root = get(params, req, resp);
			root.put("output", new SimpleScalar(output));
			root.put("js_submit_status", new SimpleScalar(msg));
			return root;
		}
	}
	
	private boolean passwordCheck(String passOne, String passTwo)
	{
		return (passOne.compareTo(passTwo) == 0);
	}
}
