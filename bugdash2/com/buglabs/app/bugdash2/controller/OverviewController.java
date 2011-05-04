package com.buglabs.app.bugdash2.controller;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.buglabs.util.StringUtil;
import com.buglabs.app.bugdash2.Activator;
import com.buglabs.app.bugdash2.LogManager;
import com.buglabs.app.bugdash2.ShellUtil;
import com.buglabs.osgi.sewing.pub.SewingController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public abstract class OverviewController extends SewingController {

	public abstract String getBuildInfoCommand();
	public abstract String getKernelInfoCommand(); 
	public abstract String getDiskUsageCommand();	
	public abstract String getBatteryInfoPath(); 
	public abstract String getHostName();

	
	public String getTemplateName() { return "overview.fml"; }
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		TemplateModelRoot root = new SimpleHash();
		root.put("rootfs_info", 	getSoftwareInfo());
		root.put("device_name", 	getDeviceName());
		root.put("kernel_info", 	getKernelInfo()); 
		root.put("disk_info", 		getDiskUsage()); 
		root.put("network_info", 	getNetworkInfo());
		return root; 
	}
	
	/**
	 * @return BUG SW version info 
	 */
	private SimpleScalar getSoftwareInfo() {
		String result = ""; 
		SimpleScalar output = ShellUtil.getSimpleScalar(getBuildInfoCommand());
		String str_output = output.toString();
		// TODO: would be more accurate to use regexp
		String[] buildinfo = StringUtil.split(str_output, "\n");
		if (str_output.indexOf("bug-image-production") > -1) { // check if it's older format 
 			result += buildinfo[2] + "<br />"; 
			result += "<span class='quiet'>" + buildinfo[1] + "</span>";
		} else {
			String[] line; 
			for (int i=0; i<buildinfo.length; i++) {
				line = StringUtil.split(buildinfo[i], ":");
				if (line[0].equals("Version")) {
					result += buildinfo[i] + "<br />";
				} else if (line[0].equals("Build Time") || line[0].equals("Revision")) {
					result += "<span class='quiet'>" + buildinfo[i] + "</span><br />";
				} 
			}
		}
		return new SimpleScalar(result); 
	}
	
	private SimpleScalar getDeviceName() {
		String output = ShellUtil.getSimpleScalar(getHostName()).toString();
		return new SimpleScalar(StringUtil.replace(output, "\n",""));
	}
	
	/**
	 * @return kernel info 
	 */
	private SimpleScalar getKernelInfo() {
		String result = ""; 
		SimpleScalar output = ShellUtil.getSimpleScalar(getKernelInfoCommand()); 
		String[] info = StringUtil.split(output.toString(), " ");
		if (info.length > 3)
			result += info[0] + " " + info[1] + " " + info[2];  
		return new SimpleScalar(result); 		
	}
	
	private SimpleScalar getDiskUsage() {
		String result = ""; 
		SimpleScalar output = ShellUtil.getSimpleScalar(getDiskUsageCommand()); 
		
		String[] diskinfo = StringUtil.split(output.toString(), "\n"); 
		if (diskinfo.length > 1) { 
			String[] usageinfo = StringUtil.split(StringUtil.squeeze(diskinfo[1]), " "); 

			result += "<table>"; 
			if (usageinfo.length > 5) {
				result += "<tr><td class='quiet'>Total size</td><td>" + usageinfo[1] + "</td>"; 
				result += "<td class='quiet'>Used</td><td>" + usageinfo[2] + "</td></tr>"; 
				result += "<tr><td class='quiet'>Available</td><td>" + usageinfo[3] + "</td>"; 
				result += "<td class='quiet'>Used %</td><td>" + usageinfo[4] +"</td></tr>"; 
			}
			result += "</table>";
		}
		// AK 2010-04-08 added battery life
		// JC, updated path for 2.0
		result += "<br /><br />Battery life: " + Activator.getBatteryLife(getBatteryInfoPath()) + "%"; 
		return new SimpleScalar(result); 
	}
	
	
	private SimpleScalar getNetworkInfo() {
		String result = ""; 
		try {
			Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				final NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
				result += ni.getDisplayName() + ": "; 
				Enumeration addrss = ni.getInetAddresses();
				while (addrss.hasMoreElements()) {
					InetAddress addr = (InetAddress) addrss.nextElement();
					if (addr instanceof Inet4Address) {
						String ipaddr = addr.toString().substring(1);
						result += ipaddr; 
					}
				}
				result += "<br />"; 
			}
		} catch (SocketException e) {
			LogManager.logWarning(e.getMessage());
		}
		return new SimpleScalar(result); 
	}

	
}
