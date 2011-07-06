package com.buglabs.bug.base;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import com.buglabs.support.SupportInfo;

/**
 * Bug's version of SupportInfo.
 * 
 * @author bballantine
 * 
 */
public class BUGSupportInfo extends SupportInfo {

	private static final String VERSION_FILENAME = "/etc/version";
	private static final String BUILDINFO_FILENAME = "/etc/buildinfo";

	/**
	 * @param context BundleContext
	 */
	public BUGSupportInfo(BundleContext context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.support.SupportInfo#getKernelVersion()
	 */
	protected String getKernelVersion() {
		FileInputStream stream = null;
		String version = null;

		try {
			stream = new FileInputStream(VERSION_FILENAME);
			LineIterator it = IOUtils.lineIterator(stream, "UTF-8");

			version = it.nextLine();
		} catch (IOException e) {
			getLogService().log(LogService.LOG_ERROR, "Error reading " + VERSION_FILENAME, e);
		} finally {
			IOUtils.closeQuietly(stream);
		}

		if (version == null)
			return null;
		else
			return version.trim();
	}
	
	/* (non-Javadoc)
	 * @see com.buglabs.support.SupportInfo#getRootfsVersion()
	 */
	protected String getRootfsVersion() {
		FileInputStream stream = null;
		StringBuilder sb = new StringBuilder();
		try {
			stream = new FileInputStream(BUILDINFO_FILENAME);

			for (String line : IOUtils.readLines(stream)) {
				sb.append(line);
				sb.append('\n');
			}
		} catch (IOException e) {
			getLogService().log(LogService.LOG_ERROR, "Error reading " + BUILDINFO_FILENAME, e);
		} finally {
			IOUtils.closeQuietly(stream);
		}

		return sb.toString();
	}
}
