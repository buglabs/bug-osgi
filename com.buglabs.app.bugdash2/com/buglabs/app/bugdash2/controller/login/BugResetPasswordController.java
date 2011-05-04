package com.buglabs.app.bugdash2.controller.login;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.AdminConfigManager;
import com.buglabs.app.bugdash2.LogManager;
import com.buglabs.app.bugdash2.WebAdminSettings;
import com.buglabs.osgi.sewing.pub.SewingController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public class BugResetPasswordController extends SewingController {

	private String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private int alphabetLength = alphabet.length();
	private static String fileName = "";
	private static final String UNIX_ERROR = "The file was not found/an error has occured. Please carefully follow the instructions and try again";
	//private static final String BUG_LOGIN_KEY = "BUG_LOGIN_INFO";
	//private static final String BUG_LOGIN_PASSWORD_KEY = "bugdash.credential.secure.password";
	
	public String getTemplateName() {
		return "forgotPassword.fml";
	}
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		fileName = generateFileName();
		TemplateModelRoot root = new SimpleHash();
		root.put("name_string", new SimpleScalar(fileName));
		return root;
	}

	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		String output = "";
		String password = "";
		//String encodedPassword = "";
		String path = "/home/root/" + fileName;
		boolean exists = (new File(path)).exists(); 
		LogManager.logDebug("file exists? " + exists);
		
		if(exists)
		{
			password = generatePassword();
			//encodedPassword = com.buglabs.util.Base64.encodeBytes(password.getBytes());
			output = "Your new password is: " + password + ".  <a href=\"/admin\">Click here to log in.</a>";
			try {
				AdminConfigManager.saveConfigProperty(WebAdminSettings.CONFIG_PID_BUGDASH, WebAdminSettings.CONFIG_KEY_BUGDASH_PASSWORD, password);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			

		}
		else
		{
			
			output = UNIX_ERROR;
		}
		TemplateModelRoot root = new SimpleHash();
		root.put("name_string", new SimpleScalar(fileName));
		root.put("output", new SimpleScalar(output));
		return root;
		
	}
	
	private String generateFileName()
	{
		String fileName = "";
		Random rand = new Random();
		for(int i = 0; i < 29; i++)
		{
			if((i+1) % 6 == 0 && i != 0)
			{
				fileName = fileName + "-";
			}
			else
			{
				fileName = fileName + alphabet.charAt(rand.nextInt(alphabetLength));
			}
		}
		fileName = fileName + ".txt";
		return fileName;
	}
	
	private String generatePassword()
	{
		String password = "";
		Random rand = new Random();
		for(int i = 0; i < 8; i++)
		{
			password = password + alphabet.charAt(rand.nextInt(alphabet.length()));
		}
		return password;
	}

}
