package com.buglabs.app.bugdash2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.buglabs.nmea.StringUtil;
import freemarker.template.SimpleScalar;


/**
 * Represents an ipkg-- used by PackageViewerController
 * @author akweon
 *
 */
public class Package {
	private String name; 
	private String version; 
	private String status; 
	private String architecture; 
	private String dependency; 
	
	private static final String PACKAGE_KEY = "Package"; 
	private static final String VERSION_KEY = "Version"; 
	private static final String STATUS_KEY = "Status"; 
	private static final String ARCHITECTURE_KEY = "Architecture";
	private static final String DEPENDENCY_KEY = "Depends"; 
	private static final String RECOMMENDATION_KEY = "Recommends"; 
	
	private static String ipkg = "ipkg"; 
	public static final String UPDATE_AND_UPGRADE_COMMAND = ipkg + " update && " + ipkg + " -noaction upgrade"; 
	
	public Package() { }
	
	public String getName() 		{ return name; }
	public String getVersion() 		{ return version; }
	public String getStatus() 		{ return status; }
	public String getArchitecture() { return architecture; 	}
	public String getDependency() 	{ return dependency; }

	/**
	 * Parses input data and returns an instance of ipackage 
	 * @param input (ArrayList)
	 * @return Package object 
	 */
	public static Package importData(List input) {
		// an element in input array represents a line from ipkg status file containing ipkg information; 
		// data can span multiple lines. 
		Package p 			= new Package(); 
		Iterator itr 		= input.iterator();
		String line 		= ""; 
		String key 			= ""; 
		HashMap metadata	= new HashMap(); 
		String[] line_data; 
		while (itr.hasNext()) {
			line = (String)itr.next(); 
			if (line.length() == 0) break; 
			line_data = StringUtil.split(line, ": "); 	// key and value are separated by ": "
			if (line_data.length == 0) break; 
			
			if (line_data.length > 1) {
				key = line_data[0]; 
				metadata.put(key, line_data[1]); 
			} else {
				
				metadata.put(key, metadata.get(key) + line_data[0]); // line without key-- associate it with previous key 
			}
			if (metadata.containsKey(PACKAGE_KEY))
				p.name = (String)metadata.get(PACKAGE_KEY);
			if (metadata.containsKey(VERSION_KEY))
				p.version = (String)metadata.get(VERSION_KEY); 
			if (metadata.containsKey(STATUS_KEY)) {
				p.status = (String)metadata.get(STATUS_KEY);
			}
			if (metadata.containsKey(ARCHITECTURE_KEY))
				p.architecture = (String)metadata.get(ARCHITECTURE_KEY); 
			if (metadata.containsKey(DEPENDENCY_KEY))
				p.dependency = (String)metadata.get(DEPENDENCY_KEY); 
 		}
		return p; 
	}
	
	public static SimpleScalar update() {
		return ShellUtil.getSimpleScalar(ipkg + " update");
	}
	
	public static SimpleScalar info(String name) {
		if (name == null || name.equals("")) return null; 
		return ShellUtil.getSimpleScalar(ipkg + " info " + name);
	}
	
	public static SimpleScalar downloadExtra() {	
		return ShellUtil.getSimpleScalar("wget http://repo.buglabs.net/ipkg-extras.conf -O /etc/ipkg/ipkg-extras.conf"); 	
	}
	
	public static SimpleScalar install(String name) {
		if (name == null || name.equals("")) return null; 
		return ShellUtil.getSimpleScalar(ipkg + " install " + name); 
	}
	
	public static void setIpkgCommand(String cmd) {
		ipkg = cmd; 
	}
	
	public static String getIpkgCommand() {
		return ipkg;
	}
}
