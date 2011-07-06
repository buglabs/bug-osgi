package com.buglabs.app.bugdash2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import com.buglabs.util.Base64;
import com.buglabs.util.osgi.ConfigAdminUtil;

/**
 * @author iocanto
 * */
public class AdminConfigManager {
	
	private static BundleContext context;
	private static ConfigurationAdmin configAdmin;
	
	public static void setContext(BundleContext context) {
		AdminConfigManager.context = context;
	}		
	
	public static ConfigurationAdmin getService() {
		if (configAdmin == null && context != null) {
			ServiceReference sr = context.getServiceReference(ConfigurationAdmin.class.getName());
			if (sr != null)
				configAdmin = (ConfigurationAdmin)context.getService(sr); 
		} 
		return configAdmin; 
	}
	

	/**
	 * 
	 * Create a new configuration instance  
	 * @param : pid of the new configuration 
	 *  
	 *  @throws IOException
	 * */
	public static void createConfig (String pid) throws IOException {	
		Dictionary dic = new Hashtable();		
		dic.put("service.pid",pid);				
		
		createConfig(pid, dic);
	} 
	
	/**
	 * 
	 * Create a new configuration instance  
	 * @param : pid of the new configuration, and a dictionary 
	 *  
	 *  @throws  IOException
	 * */
	
	public static void createConfig(String pid, Dictionary dic) throws IOException{
		Configuration config = getService().getConfiguration(pid);
		LogManager.logInfo("AdminConfigManager: config null? " + (config == null));
		// new set of properties		
		dic.put("service.pid",pid);				
		
		//try to update created Configuration				
		config.update(dic);
	}
	

	/**
	 * @param pid of the wanted configuration 
	 * 
	 * @return Configuration 
	 * 
	 * @throws IOException
	 * */
	
	public static Configuration getConfiguration(String pid) throws IOException{
		return getService().getConfiguration(pid);
	}

	/**
	 * @return An array of the all configurations
	 * */
	
	public static Configuration[] getConfigurations() throws IOException, InvalidSyntaxException {
		return getConfigurations(null);
	}


	/**
	 * @return An array of configuration that matches the filer. 
	 * Pass null if want all configurations   
	 * */
	
	public static Configuration[] getConfigurations(String filter) throws IOException, InvalidSyntaxException {

		Configuration[] result = getService().listConfigurations(filter);

		return result;
		
	}


	/**
	 * @param pid of the configuration to be deleted 
	 * 
	 * @throws IOException
	 * */
	
	public static void deleteConfig(String pid) throws IOException {
		Configuration c = getService().getConfiguration(pid);		
		
		if (c != null) {
			c.delete();
		}
	}


	/**
	 *
	 * If the property key does not exist creates a new one
	 * 
	 * @param pid = pid of the configuration, key = properties name, and 
	 * value = value of the property
	 * 
	 * @throws IOException
	 * 
	 * */
	
	public static void saveConfigProperty(String pid, String key, Object value) throws IOException {
		Configuration c =  getService().getConfiguration(pid); 
		if (c == null) {
			createConfig(pid); 
			c = getService().getConfiguration(pid);
		}
		Dictionary obj = ConfigAdminUtil.getPropertiesSafely(c);		
		if (is_secure(key)) {
			value = Base64.encodeBytes(value.toString().getBytes());
		}
		obj.put(key, value);		
		c.update(obj);		
	}


	/**
	 * Delete a property form a configuration object
	 * 
	 * @param pid of the Configuration and key property to be removed 
	 * 
	 * */
	
	public static void deleteConfigProperty(String pid, String key) throws IOException {
		Configuration c =  getService().getConfiguration(pid);
		Dictionary obj = ConfigAdminUtil.getPropertiesSafely(c);		
		
		if (obj != null) {
			obj.remove(key);
			c.delete();
			c = getService().getConfiguration(pid);
			c.update(obj);	
		}
	}


	/**
	 * @return a list of all configurations 
	 * */
	
	public static List getConfigurationList() throws IOException,
			InvalidSyntaxException {		
		return getConfigurationList(null);
	}

	
	/**
	 * @param filter
	 * 
	 * @return a list of configuration 
	 * */

	public static List getConfigurationList(String filter) throws IOException,
			InvalidSyntaxException {
		List result = new ArrayList();
		
		Configuration[] c = getConfigurations(filter);
		
		for (int i =0 ; i < c.length ; ++i ) {
			result.add(c[i]);
		}
		
		return result;
	}
	
	/**
	 * @param pid
	 * @return a list of properties 
	 * @throws IOException 
	 */
	public static List getConfigPropertiesList(String pid) throws IOException {
		List result = new ArrayList(); 
		Dictionary props = getConfiguration(pid).getProperties();
		
		if (props != null) {
		    Enumeration en = props.keys();
		    while (en.hasMoreElements()) {
		    	String key = (String) en.nextElement();
		    	result.add(new ConfigPropEntry(key, props.get(key).toString()) ); 
		    }
		}
	    return result; 
	}

	public static ConfigPropEntry getConfigProperty(String pid, String key) throws IOException {
		ConfigPropEntry result = null; 
		Dictionary props = getConfiguration(pid).getProperties();
		
		if (props != null) {
		    Enumeration en = props.keys();
		    while (en.hasMoreElements()) {
		    	String pkey = (String) en.nextElement();
		    	if (pkey.equals(key)) {
		    		result = new ConfigPropEntry(key, props.get(pkey).toString()); 
		    	}
		    }	    
		}
		return result; 
	}
	
	/**
	 * Given pid and key, return its property value. 
	 * The key follows the convention [app].[category].[type].[name], and 
	 * if type == "secure", perform base64 decode and return its text
	 *  
	 * @param pid
	 * @param key
	 * @return property value (null if config or property is not found )
	 * @throws IOException
	 */
	public static String getConfigPropertyValue(String pid, String key) throws IOException {
		String output = null; 
		ConfigPropEntry result = null; 
		Configuration c = getConfiguration(pid); 
		if (c == null)
			return output; 
		Dictionary props = c.getProperties();
		
		if (props == null)
			return output; 
		
	    Enumeration en = props.keys();
	    while (en.hasMoreElements()) {
	    	String pkey = (String) en.nextElement();
	    	if (pkey.equals(key)) {
	    		result = new ConfigPropEntry(key, props.get(pkey).toString()); 
	    	}
	    }	    
	    if (result != null) {
	    	if (is_secure(result.getKey())) {
	    		output = new String(Base64.decode(result.getValue())); 
	    	} else {
	    		output  = result.getValue();
	    	}
	    }
		return output; 
	}	
	
	private static boolean is_secure(String key) {
		String[] arr_prop_key = key.split(".");
		return (arr_prop_key.length > 2 && arr_prop_key[2].equals("secure"));
	}
}