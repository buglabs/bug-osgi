package com.buglabs.support;

import java.util.Iterator;
import java.util.Map;

import com.buglabs.util.SelfReferenceException;
import com.buglabs.util.XmlNode;

public class SupportInfoXMLFormatter implements ISupportInfoFormatter {

	public String getContentType() {
		return "text/xml";
	}

	public String buildResponse(String description, String kernelVersion, String rootfsVersion, Map jvmProperties, Map bundleVersions) {

		XmlNode responseXml = new XmlNode("support");
		try {
			responseXml.addChildElement(new XmlNode("description", description));
			responseXml.addChildElement(new XmlNode("kernel_version", kernelVersion));
			responseXml.addChildElement(new XmlNode("rootfs_version", "<![CDATA[" + rootfsVersion + "]]>"));

			// JVM Properties
			XmlNode jvmPropertiesNode = responseXml.addChildElement(new XmlNode("jvm_properties"));
			Iterator itr = jvmProperties.keySet().iterator();
			String key;
			XmlNode helper;
			while (itr.hasNext()) {
				key = (String) itr.next();
				helper = jvmPropertiesNode.addChildElement(new XmlNode("property"));
				helper.addAttribute("key", key);
				helper.addAttribute("value", (String) jvmProperties.get(key));
			}

			// JVM Properties
			XmlNode bundleVersionsNode = responseXml.addChildElement(new XmlNode("bundle_versions"));
			itr = bundleVersions.keySet().iterator();
			while (itr.hasNext()) {
				key = (String) itr.next();
				helper = bundleVersionsNode.addChildElement(new XmlNode("bundle"));
				helper.addAttribute("name", key);
				helper.addAttribute("version", (String) bundleVersions.get(key));
			}
		} catch (SelfReferenceException e) {

		}

		return responseXml.toString();
	}

}
