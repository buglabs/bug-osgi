package com.buglabs.support;

import java.util.Iterator;
import java.util.Map;


public class SupportInfoTextFormatter implements ISupportInfoFormatter {

	public String getContentType() {
		return "text/plain";
	}

	public String buildResponse(String description, String kernelVersion,
			String rootfsVersion, Map jvmProperties, Map bundleVersions) {
		
		// Description
		StringBuffer sb = new StringBuffer();
		sb.append(description);
		sb.append("\n\n");
		
		// Kernel Version
		sb.append("Kernel Version --------------------------- \n");
		sb.append(kernelVersion);
		sb.append("\n\n");		
		
		// Root Filesystem Version
		sb.append("Root Filesystem Build Information --------------------------- \n");
		sb.append(rootfsVersion);
		sb.append("\n\n");		
		
		// JVM Properties
		sb.append("JVM Properties --------------------------- \n");
		Iterator itr = jvmProperties.keySet().iterator();
		String key;
		while (itr.hasNext()) {
			key = (String) itr.next();
			sb.append(key);
			sb.append(" = ");
			sb.append(jvmProperties.get(key));
			sb.append("\n");
		}
		sb.append("\n\n");
		
		// OSGi Bundle Versions
		sb.append("OSGi Bundle Versions --------------------------- \n");
		itr = bundleVersions.keySet().iterator();
		while (itr.hasNext()) {
			key = (String) itr.next();
			sb.append(key);
			sb.append(" = ");
			sb.append(bundleVersions.get(key));
			sb.append("\n");
		}
		sb.append("\n\n");
		
		return sb.toString();
	}

}
