/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
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
