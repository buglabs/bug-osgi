package webadmin.controller.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webadmin.AdminConfigManager;
import webadmin.ConfigPropEntry;
import webadmin.LogManager;
import webadmin.ShellThread;
import webadmin.ShellUtil;
import webadmin.TemplateHelper;
import webadmin.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelRoot;

public class Ipv6SetupController extends ApplicationController {

	private static final String IPV6_CONFIG_KEY 		= "IPV6_ACCT_INFO"; 
	private static final String IPV6_USERNAME_KEY 		= "username"; 
	private static final String IPV6_PWD_KEY 			= "password"; 
	
	private static final String PREFIX_USERNAME			= "userid=";
	private static final String PREFIX_PASSWORD			= "passwd=";
	private static final String IPV6_CONFIG_PATH		= "/etc/gw6c/gw6c.conf"; 
	private static final String IPV6_CONFIG_SAMPLE_PATH	= "/usr/bin/gw6c.conf.sample"; 
	private static final String USER_FREENET_URL		= ".broker.freenet6.net"; 
	
	/* commands */
	private static final String COMMAND_WHICH_CLIENT	= "which gw6c";
	private static final String COMMAND_INSTALL			= "ipkg install http://repo.buglabs.net/armv6-extras/gw6c_6.0.1-r2_armv6.ipk";
	private static final String COMMAND_STOP			= "killall gw6c"; 
	private static final String COMMAND_RUN_INIT		= "yes | gw6c -f /etc/gw6c/gw6c.conf"; //"/bin/sh -c yes | gw6c -f /etc/gw6c/gw6c.conf"
	private static final String COMMAND_RUN				= "yes | gw6c -f /etc/gw6c/gw6c.conf";
	
	protected ShellThread thread; // thread for installation 

	public Ipv6SetupController() {

	}
	
	public String getTemplateName() { return "system_ipv6_setup.fml"; }
	
	public TemplateModelRoot get(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		return loadCurrentConfiguration(); 
	}

	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		
		String action = params.get("step"); 
		String js_submit_status 	= "{category: '', message: '', detail: ''}";
		SimpleScalar output; 
		if (action.equals("save-login")) {
			try {
				AdminConfigManager.saveConfigProperty(IPV6_CONFIG_KEY, IPV6_USERNAME_KEY, params.get("inp_username"));
				AdminConfigManager.saveConfigProperty(IPV6_CONFIG_KEY, IPV6_PWD_KEY, params.get("inp_password"));
				updateConfigFile();
				js_submit_status = "{category: 'info', message: 'Your information has been saved successfully'}";
			} catch (IOException e) {
				LogManager.logError(e.getMessage());
				js_submit_status = "{category: 'error', message: 'There was a problem while saving info', detail: '" + TemplateHelper.makeJSFriendly(e.getMessage()) + "'}";
			} 
			
		} else if (action.equals("install")) {
			output = ShellUtil.getSimpleScalar(COMMAND_INSTALL);
			updateConfigFile(); 
			ShellUtil.getSimpleScalar(COMMAND_RUN_INIT); 
			try {
				js_submit_status = "{category: 'info', message: 'IPv6 client is installed successfully', detail: '" + TemplateHelper.makeJSFriendly(output.getAsString()) + "'}";
			} catch (TemplateModelException e) {
				js_submit_status = "{category: 'error', message: 'There was a problem while installing the client', detail: '" + TemplateHelper.makeJSFriendly(e.getMessage()) + "'}";
			}
	
		} else if (action.equals("configure")) {
			// no longer being used
			if (params.get("enable_configure").equals("true")) {
				updateConfigFile(); 
				js_submit_status = "{category: 'info', message: 'Configuration is successful'}"; 
			}
		} else if (action.equals("run")) {
			if (params.get("btn_run").equals("Run")) {
				output = ShellUtil.getSimpleScalar(COMMAND_RUN); 
				try {
					js_submit_status = "{category: 'info', message: 'IPv6 client is started', detail: '" + TemplateHelper.makeJSFriendly(output.getAsString()) + "'}";
				} catch (TemplateModelException e) {
					e.printStackTrace();
				} 
			} else {
				// stop 
				output = ShellUtil.getSimpleScalar(COMMAND_STOP); 
				try {
					js_submit_status = "{category: 'info', message: 'IPv6 client is stopped', detail: '" + TemplateHelper.makeJSFriendly(output.getAsString()) + "'}";
				} catch (TemplateModelException e) {
					LogManager.logDebug(e.getMessage()); 
				} 
			}
		}
		SimpleHash root = (SimpleHash) loadCurrentConfiguration(); 
		root.put("js_submit_status", new SimpleScalar(js_submit_status));	
		return root;
	}
	
	
	private TemplateModelRoot loadCurrentConfiguration() {
		// setup an account with freenet6
		// -- read config admin 
		String username = null; 
		String pwd = null; 
		boolean display_address = false; 
		boolean enable_configure = false; 
		try {
			ConfigPropEntry config_user = AdminConfigManager.getConfigProperty(IPV6_CONFIG_KEY, IPV6_USERNAME_KEY);
			ConfigPropEntry config_pwd = AdminConfigManager.getConfigProperty(IPV6_CONFIG_KEY, IPV6_PWD_KEY); 
			if (config_user != null)
				username = config_user.getValue(); 
			if (config_pwd != null)
				pwd = config_pwd.getValue(); 
		} catch (IOException e) {
			LogManager.logDebug(e.getMessage()); 
		} 
		// -- check gw6c client 
		SimpleScalar client_location = ShellUtil.getSimpleScalar(COMMAND_WHICH_CLIENT); 
		
		// -- config file setup  
		boolean match_config = false;
		File conf = new File(IPV6_CONFIG_PATH); 
		enable_configure = (conf.exists() && username != null && pwd != null);
		display_address = enable_configure; 
		if (enable_configure) {
			List read_config_user = ShellUtil.getList("cat " + IPV6_CONFIG_PATH + " | grep " + PREFIX_USERNAME);
			List read_config_pwd = ShellUtil.getList("cat " + IPV6_CONFIG_PATH + " | grep " + PREFIX_PASSWORD);
			// check if it's found 
			// 	if found, check userid is the same as _username_
			// 			  check passwd is the same as _pwd_
			Iterator itr;  String line; 
			if (read_config_user != null) {
				itr = read_config_user.iterator();
				while (itr.hasNext()) {
					line = (String)itr.next();
					if (line != null && line.startsWith(PREFIX_USERNAME)) {
						match_config = line.endsWith(username); 
					}
				}
			}
	
			if (read_config_pwd != null) {
				itr = read_config_pwd.iterator(); 
				while (itr.hasNext()) {
					line = (String)itr.next(); 
					if (line != null && line.startsWith(PREFIX_PASSWORD)) {
						match_config = match_config && line.endsWith(pwd); 
					}
				}
			}
		}
		conf = null; 
		
		// -- run the client 
		boolean client_running = false; 
		SimpleScalar check_process = ShellUtil.getSimpleScalar("ps aux | grep gw6c"); 
		// see if gw6c is running 
		try {
			String check_process_output = check_process.getAsString(); 
			if (check_process_output.indexOf("gw6c.conf") > -1) {
				client_running = true; 
			}
		} catch (TemplateModelException e) {
			LogManager.logDebug(e.getMessage()); 
		}
		display_address = display_address && client_running && !username.equals(""); 
		SimpleHash root = new SimpleHash();
		root.put("account_user", username); 
		root.put("account_pwd", pwd); 
		root.put("client_location", client_location); 
		root.put("enable_configure",enable_configure+""); 
		root.put("match_config", match_config+"");
		root.put("client_running", client_running+"");
		root.put("display_address", display_address+""); 
		root.put("freenet_url", "http://"+getFreenetURL(username)); 
		return root; 		
	}
	
	public static String getFreenetURL(String username) {
		return username+USER_FREENET_URL;
	}
	
	public static String getIpv6AccountUsername() {
		String username = ""; 
		try {
			ConfigPropEntry prop = AdminConfigManager.getConfigProperty(IPV6_CONFIG_KEY, IPV6_USERNAME_KEY);
			if (prop != null)
				username = prop.getValue(); 
		} catch (IOException e) {
		} 
		return username; 
	}
	
	public void stopThread() {
		if (this.thread != null) {
			this.thread.cancel();
			this.thread = null; 			
		}		
	}
	
	public List getThreadBuffer() {
		if (this.thread == null) 
			return null;
		return this.thread.getBuffer();
	}	
	
	private void setupConfigFile() {
		File f = new File("/etc/gw6c");
		if (!f.exists()) 
			f.mkdir(); 
		f = new File(IPV6_CONFIG_PATH);
		if (!f.exists())
			copyfile(IPV6_CONFIG_SAMPLE_PATH, IPV6_CONFIG_PATH); 
		f = new File("/etc/gw6c/template"); 
		if (!f.exists()) 
			f.mkdir();
		f = new File("/etc/gw6c/template/gw6c.sh"); 
		if (!f.exists())
			copyfile("/usr/bin/gw6c.sh", "/etc/gw6c/template/linux.sh");
	}
	
	/**
	 * Update gw6c.conf with the credential provided by 
	 */
	private void updateConfigFile() {
		ConfigPropEntry user = null; 
		ConfigPropEntry pwd = null;  
		setupConfigFile(); 
		
		// server=broker.freenet6.net
		// auth_method=any
		// gw6_dir=/usr/bin/gw6c
		// broker_list=/etc/gw6c/tsp-broker-list.txt
		// last_server=/etc/gw6c/tsp-last-server.txt		
		HashMap update_entries = new HashMap(); 
		update_entries.put("server=", "broker.freenet6.net"); 
		update_entries.put("auth_method=", "any"); 
		update_entries.put("gw6_dir=", "/etc/gw6c");
		update_entries.put("broker_list=", "/etc/gw6c/tsp-broker-list.txt"); 
		update_entries.put("last_server=", "/etc/gw6c/tsp-last-server.txt"); 
		update_entries.put("#log_file=", "3");
		update_entries.put("log_filename=", "/home/root/gw6c.log");
		update_entries.put("tunnel_mode=", "v6udpv4"); // required by gsm 

		try {
			user = AdminConfigManager.getConfigProperty(IPV6_CONFIG_KEY, IPV6_USERNAME_KEY);
			pwd  = AdminConfigManager.getConfigProperty(IPV6_CONFIG_KEY, IPV6_PWD_KEY); 
			
		} catch (IOException e1) {
			LogManager.logDebug(e1.getMessage()); 
		}
		if (user != null && user.getValue().length() > 0 && pwd != null && pwd.getValue().length() > 0) {
			BufferedReader r;
			Iterator itr; 
			boolean updated = false; 
			try {
				r = new BufferedReader(new InputStreamReader(new FileInputStream(IPV6_CONFIG_PATH)));
				StringBuffer result = new StringBuffer();
				
				String line; String entry; 
				while ((line = r.readLine()) != null) {
					if (line.trim().length() > 0) {
						if (line.startsWith(PREFIX_USERNAME)) {
							result.append(PREFIX_USERNAME+user.getValue()+"\n"); 
						} else if (line.startsWith(PREFIX_PASSWORD)) {
							result.append(PREFIX_PASSWORD+pwd.getValue()+"\n"); 
						} else {
							itr = update_entries.keySet().iterator(); 
							while (itr.hasNext()) {
								entry = (String)itr.next(); 
								if (line.startsWith(entry)) {
									result.append(entry+update_entries.get(entry)+"\n"); 
									updated= true; 
								}
							}
							if (!updated)
								result.append(line+"\n");
						}
					}
					updated = false; 
				}
				r.close();
				
				OutputStream filout = new FileOutputStream(IPV6_CONFIG_PATH);
				filout.write(result.toString().getBytes());
				filout.close();
			
			} catch (FileNotFoundException e) {
				LogManager.logDebug(e.getMessage()); 
			} catch (IOException e) {
				LogManager.logDebug(e.getMessage()); 
			}
		}	
	}
	
	private static void copyfile(String srFile, String dtFile){
		try{
		  File f1 = new File(srFile);
		  File f2 = new File(dtFile);
		  InputStream in = new FileInputStream(f1);
		  OutputStream out = new FileOutputStream(f2);
		  byte[] buf = new byte[1024];
		  int len;
		  while ((len = in.read(buf)) > 0){
		    out.write(buf, 0, len);
		  }
		  in.close();
		  out.close();
		}
		catch(FileNotFoundException ex){
			LogManager.logDebug(ex.getMessage()); 
		}
		catch(IOException ex1){
			LogManager.logDebug(ex1.getMessage()); 
		}
	}
	
}

	