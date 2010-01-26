package com.buglabs.bug.base;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import com.buglabs.support.SupportInfo;

/**
 * Bug's version of SupportInfo
 * 
 * @author bballantine
 *
 */
public class BUGSupportInfo extends SupportInfo {

	public BUGSupportInfo(BundleContext context) {
		super(context);
	}

	/**
	 * Get the kernel version from /etc/version
	 */
	protected String getKernelVersion() {

		BufferedReader br = getBufferedReader("/etc/version");
		if (br == null) return "";
		
		// kernel version is first line
		String line = null;
		try {
			line = br.readLine();
			br.close();
		} catch (IOException e) {
			getLogService().log(
					LogService.LOG_ERROR, "Error reading /etc/version", e);
		}

		if (line == null) return null;
		else return line.trim();
	}

	/**
	 * Get the rootfs version info from /etc/buildinfo
	 */
	protected String getRootfsVersion() {
		BufferedReader br = getBufferedReader("/etc/buildinfo");
		if (br == null) return "";
		
		StringBuffer sb = new StringBuffer();
		try {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line.trim());
				sb.append("\n");
			}
			br.close();
		} catch (IOException e) {
			getLogService().log(
					LogService.LOG_ERROR, "Error reading /etc/buildinfo", e);
		}
		
		return sb.toString();
	}
	
	
	/**
	 * Helper to get a buffered reader to a file
	 * 
	 * @param filename
	 * @return
	 */
	private BufferedReader getBufferedReader(String filename) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			getLogService().log(LogService.LOG_ERROR, filename + " not found", e);
		}
		return br;		
	}
	
}
