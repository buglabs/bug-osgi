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
package com.buglabs.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

/**
 * To be used by generators.
 * 
 * @author Angel Roman
 * 
 */
public class ServiceFilterGenerator {

	/**
	 * @param services
	 *            A set of strings of class names.
	 * @return A LDAP filter string that can be used to construct a Filter.
	 */
	public static String generateServiceFilter(List services) {
		if (services.size() == 1) {
			return "(" + Constants.OBJECTCLASS + "=" + ((String) services.get(0)) + ")";
		} else if (services.size() > 1) {
			return "(|" + generateServiceFilter(services.subList(0, 1)) + generateServiceFilter(services.subList(1, services.size())) + ")";
		}

		return "";
	}

	/**
	 * Generate a Filter object from an array of Service names and a bundle
	 * context.
	 * 
	 * @param context
	 * @param services
	 * @return
	 * @throws InvalidSyntaxException
	 */
	public static Filter generateServiceFilter(BundleContext context, String[] services) throws InvalidSyntaxException {
		String filterString = generateServiceFilter(Arrays.asList(services));

		return context.createFilter(filterString);
	}

	/**
	 * servicesMap is SortedMap<String, Map<String, String>> where the key is
	 * the service name and the map is a map of service properties
	 * 
	 * (| (& (objectClass=com.buglabs.bug.module.bugbee.pub.IBUGBeeControl) (&
	 * (Provider=com.buglabs.bug.module.bugbee.BUGBeeModlet)(Slot=2))) (| (&
	 * (objectClass=com.buglabs.module.IModuleControl)) (&
	 * (objectClass=org.osgi.service.http.HttpService)(port=8082))))
	 * 
	 */
	public static String generateServiceFilter(SortedMap servicesMap) {
		if (servicesMap.size() == 1) {
			return "(&(" + Constants.OBJECTCLASS + "=" + ((String) servicesMap.firstKey()) + ")"
					+ generatePropertiesFilter(new TreeMap((Map) servicesMap.get(servicesMap.firstKey()))) + ")";
		} else if (servicesMap.size() > 1) {
			return "(|" + generateServiceFilter(servicesMap.subMap(servicesMap.firstKey(), servicesMap.firstKey() + "\0"))
					+ generateServiceFilter(servicesMap.tailMap(servicesMap.firstKey() + "\0")) + ")";
		}
		return "";
	}

	/**
	 * (& (prop1=value1) (& (prop2=value1)))
	 * 
	 * @param propertiesMap
	 * @return
	 */
	public static String generatePropertiesFilter(SortedMap propertiesMap) {
		if (propertiesMap.size() == 1) {
			return "(" + (String) propertiesMap.firstKey() + "=" + ((String) propertiesMap.get(propertiesMap.firstKey())) + ")";
		} else if (propertiesMap.size() > 1) {
			return "(&" + generatePropertiesFilter(propertiesMap.subMap(propertiesMap.firstKey(), propertiesMap.firstKey() + "\0"))
					+ generatePropertiesFilter(propertiesMap.tailMap(propertiesMap.firstKey() + "\0")) + ")";
		}
		return "";
	}

}
