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
package com.buglabs.bug.ws.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.buglabs.util.xml.XmlNode;

public class PackageServlet extends HttpServlet {
	private static final long serialVersionUID = -2460404903765995674L;
	private final BundleContext context;
	private List<String> serviceList;

	public PackageServlet(BundleContext context) {
		this.context = context;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Flush known services per request.
		serviceList = null;

		resp.setContentType("text/xml");

		resp.getWriter().print(buildPackageModel().toString());
	}

	private XmlNode buildPackageModel() {
		Bundle[] bundles = context.getBundles();

		XmlNode root = new XmlNode("packages");

		for (int i = 0; i < bundles.length; ++i) {
			Bundle b = bundles[i];

			if (b != null) {
				String exportPackage = (String) b.getHeaders().get("Export-Package");
				if (exportPackage != null) {
					splitHeaderValue(b, exportPackage, root);
				}
			}
		}

		return root;
	}

	private void splitHeaderValue(Bundle b, String value, XmlNode root) {
		String[] packages = value.split(",");

		for (int i = 0; i < packages.length; ++i) {
			if (!omitPackage(packages[i])) {
				XmlNode n = new XmlNode("package");
				n.addAttribute("name", stripName(packages[i]));
				n.addAttribute("provider", getBestName(b));				
				addAttribs(n, packages[i]);
				root.addChild(n);
			}
		}
	}

	/**
	 * This method looks for a corresponding OSGi service to match against a
	 * package to determine if a given package should be usable. This is hack
	 * due to the IModlet design. Essentially all packages for all known modules
	 * are always exported. This method is a bandaid by manually omiting
	 * packages that don't have corresponding services with the same root
	 * namespace of com.buglabs.bug.module.
	 * 
	 * @param packageName
	 * @return
	 */
	private boolean omitPackage(String packageName) {
		if (!packageName.startsWith("com.buglabs.bug.module.")) {
			return false;
		}

		if (serviceList == null) {
			serviceList = populateServiceList();
		}

		return !serviceList.contains(packageName);
	}

	private List<String> populateServiceList() {
		Bundle[] bundles = context.getBundles();

		List<String> services = new ArrayList<String>();

		for (int i = 0; i < bundles.length; ++i) {
			Bundle b = bundles[i];

			if (b != null && b.getRegisteredServices() != null) {
				services.addAll(getServiceRoots(b.getRegisteredServices()));
			}
		}

		return services;
	}

	private List<String> getServiceRoots(ServiceReference[] registeredServices) {
		List<String> s = new ArrayList<String>();

		for (int i = 0; i < registeredServices.length; ++i) {
			Object props = registeredServices[i].getProperty("objectClass");
			String svcName = ((String[]) props)[0];

			s.add(stripServiceName(svcName));
		}

		return s;
	}

	private String stripServiceName(String svcName) {
		return svcName.substring(0, svcName.lastIndexOf('.'));
	}

	private void addAttribs(XmlNode n, String headerValue) {
		String[] elems = headerValue.split(";");

		if (elems.length > 1) {
			elems = elems[1].split(",");
			for (int i = 0; i < elems.length; ++i) {
				addAttrib(n, elems[i]);
			}
		}
	}

	private void addAttrib(XmlNode n, String nameValue) {
		String[] elems = nameValue.split("=");

		if (elems.length == 2) {
			n.addChild(new XmlNode(stripNonAlphaNumeric(elems[0].trim()), elems[1].trim()));
		}
	}

	/**
	 * Strip non alpha numeric characters from a String.
	 * @param instr
	 * @return
	 */
	private String stripNonAlphaNumeric(String instr) {
		
		return instr.replaceAll("[^A-Za-z0-9]", "");
	}

	private String stripName(String headerValue) {
		return headerValue.split(";")[0];
	}

	/**
	 * Get the best available name for a bundle given it's metadata.
	 * 
	 * @param bundle
	 * @return
	 */
	private static String getBestName(Bundle bundle) {
		if (hasHeader(bundle, "Bundle-SymbolicName")) {
			return formatName(getHeader(bundle, "Bundle-SymbolicName"));
		}

		if (hasHeader(bundle, "Bundle-Name")) {
			return formatName(getHeader(bundle, "Bundle-Name"));
		}

		return bundle.getLocation();
	}

	private static String formatName(String name) {
		String ss[] = name.split(";");

		return ss[0];
	}

	public static boolean hasHeader(Bundle bundle, String headerName) {
		Dictionary d = bundle.getHeaders();

		if (d != null) {
			return d.get(headerName) != null;
		}

		return false;
	}

	public static String getHeader(Bundle bundle, String headerName) {
		Dictionary d = bundle.getHeaders();

		if (d != null) {
			return (String) d.get(headerName);
		}

		return null;
	}
}
