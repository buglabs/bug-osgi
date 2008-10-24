package com.buglabs.bug.bmi;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.service.log.LogService;

import com.buglabs.bug.bmi.pub.IBundleCache;

public class DefaultBundleCache implements IBundleCache {

	private File storageDir;

	private Map bundles;

	private final LogService logService;

	public DefaultBundleCache(String storagePath, LogService logService) {
		this.logService = logService;
		storageDir = new File(storagePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.bug.bmi.IBundleCache#clear()
	 */
	public void clear() {
		if (bundles != null) {
			bundles.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.bug.bmi.IBundleCache#get(java.lang.String)
	 */
	public String[] get(String bundleId) {
		if (bundles == null) {
			bundles = loadBundles();
		}

		return null;
	}

	private Map loadBundles() {
		Map b = new Hashtable();

		if (!storageDir.exists() || !storageDir.isDirectory()) {
			logService.log(LogService.LOG_ERROR, "There is a filesystem issue with the bundle storage directory: "
					+ storageDir.getAbsolutePath());
			return b;
		}

		File[] jars = storageDir.listFiles(new FilenameFilter() {

			public boolean accept(File arg0, String arg1) {
				return arg1.toUpperCase().endsWith(".JAR") || arg1.toUpperCase().endsWith(".ZIP");
			}

		});

		for (int i = 0; i < jars.length; ++i) {

			// we need to determine how (if) a bundle associates itself with a
			// given bundle, or if some other associating is required.
		}

		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.bug.bmi.IBundleCache#put(org.osgi.framework.Bundle)
	 */
	public String put(Bundle bundle) {
		if (bundles == null) {
			bundles = loadBundles();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.bug.bmi.IBundleCache#remove(org.osgi.framework.Bundle)
	 */
	public void remove(Bundle bundle) {
		if (bundles != null) {
			bundles.remove(bundle);
		}
	}
}
