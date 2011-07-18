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
package com.buglabs.util.osgi;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * A set of utility methods for common functionality.
 * 
 * @author kgilmer
 * 
 */
public final class BundleUtils {

	/**
	 * Utility class, not constructible.
	 */
	private BundleUtils() {
	}
	
	/**
	 * Search (n) for a bundle with given bundleId.
	 * 
	 * @param context BundleContext
	 * @param bundleId bundle id
	 * @return Bundle or null if not found.
	 */
	public static Bundle findBundle(BundleContext context, long bundleId) {
		Bundle[] bundles = context.getBundles();

		for (int i = 0; i < bundles.length; ++i) {
			Bundle b = bundles[i];

			if (b.getBundleId() == bundleId) {
				return b;
			}
		}

		return null;
	}

	/**
	 * Search for Bundle with header "Bundle-Name" as passed bundleName
	 * parameter.
	 * 
	 * @param context BundleContext
	 * @param bundleName name of bundle
	 * @return Bundle or null if not found.
	 */
	public static Bundle findBundle(BundleContext context, String bundleName) {
		Bundle[] bundles = context.getBundles();

		for (int i = 0; i < bundles.length; ++i) {
			Bundle b = bundles[i];
			Dictionary h = b.getHeaders();

			if (h != null) {
				if (h.get("Bundle-Name") != null) {
					if (h.get("Bundle-Name").equals(bundleName)) {
						return b;
					}
				}

				if (h.get("BundleSymbolic-Name") != null) {
					if (h.get("BundleSymbolic-Name").equals(bundleName)) {
						return b;
					}
				}
			}

		}

		return null;
	}

	/**
	 * Find a bundle based on id or name.
	 * 
	 * @param context Bundle context
	 * @param key either id or name of bundle
	 * @return Bundle or null if not found.
	 */
	public static Bundle findBundle(BundleContext context, Object key) {
		if (isLong(key)) {
			return BundleUtils.findBundle(context, Long.parseLong((String) key));
		} else {
			return BundleUtils.findBundle(context, (String) key);
		}
	}

	/**
	 * @param o input object
	 * @return true of input object is of Long type.
	 */
	private static boolean isLong(Object o) {
		try {
			Long.parseLong((String) o);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	/**
	 * Find bundle with given name/value pair in header.
	 * 
	 * @param context BundleContext
	 * @param headerName header name
	 * @param headerValue header value
	 * @return Bundle or null if not found.
	 */
	public static Bundle findBundle(BundleContext context, String headerName, String headerValue) {
		Bundle[] bundles = context.getBundles();

		for (int i = 0; i < bundles.length; ++i) {
			Bundle b = bundles[i];
			Dictionary h = b.getHeaders();

			if (h != null) {
				if (h.get(headerName) != null) {
					if (h.get(headerName).equals(headerValue)) {
						return b;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Get the best available name for a bundle given it's metadata.
	 * 
	 * @param bundle Bundle
	 * @return best name (human readable) available for given Bundle
	 */
	public static String getBestName(Bundle bundle) {
		if (hasHeader(bundle, "Bundle-SymbolicName")) {
			return formatName(getHeader(bundle, "Bundle-SymbolicName"));
		}

		if (hasHeader(bundle, "Bundle-Name")) {
			return formatName(getHeader(bundle, "Bundle-Name"));
		}

		return bundle.getLocation();
	}

	/**
	 * @param name raw input
	 * @return formatted name without extra metadata
	 */
	private static String formatName(String name) {
		String [] ss = name.split(";");

		return ss[0];
	}

	/**
	 * @param bundle Bundle
	 * @param headerName header name (key)
	 * @return true of bundle has defined header, false otherwise.
	 */
	public static boolean hasHeader(Bundle bundle, String headerName) {
		Dictionary d = bundle.getHeaders();

		if (d != null) {
			return d.get(headerName) != null;
		}

		return false;
	}

	/**
	 * @param bundle Bundle
	 * @param headerName header key
	 * @return value of header key
	 */
	public static String getHeader(Bundle bundle, String headerName) {
		Dictionary d = bundle.getHeaders();

		if (d != null) {
			return (String) d.get(headerName);
		}

		return null;
	}

	/**
	 * Return state label as defined in OSGi spec.
	 * 
	 * @param state Bundle State
	 * @return human-readable form of bundle state.
	 */
	public static String getStateName(int state) {
		switch (state) {
		case 0x00000001:
			return "uninstalled";
		case 0x00000002:
			return "installed";
		case 0x00000004:
			return "resolved";
		case 0x00000008:
			return "starting";
		case 0x00000010:
			return "stopping";
		case 0x00000020:
			return "active";
		default:
			return "undefined";
		}
	}
}
