/* Copyright (c) 2007, 2008 Bug Labs, Inc.
 * All rights reserved.
 *   
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License version  
 * 2 only, as published by the Free Software Foundation.   
 *   
 * This program is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * General Public License version 2 for more details (a copy is  
 * included at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html).   
 *   
 * You should have received a copy of the GNU General Public License  
 * version 2 along with this work; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA   
 *
 */
package com.buglabs.bug.ws.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogService;

import com.buglabs.bug.ws.Activator;
import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.util.ConfigAdminUtil;
import com.buglabs.util.xml.XmlNode;
import com.buglabs.util.xml.XmlParser;

/**
 * A servlet that interacts with bug's configuration
 * 
 * @author akravets
 * 
 */
public class ConfigAdminServlet extends HttpServlet {
	private static final long serialVersionUID = -4552308543439876937L;
	private ConfigurationAdmin configAdmin;
	private boolean isStateSet = false;

	public ConfigAdminServlet(BundleContext context, ConfigurationAdmin configAdmin) {
		this.configAdmin = configAdmin;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String response = null;
		isStateSet = false;
		resp.setContentType("text/xml");

		try {
			response = getConfigurationXml(response);
			resp.getWriter().print(response);
		} catch (InvalidSyntaxException e) {
			Activator.getLog().log(LogService.LOG_ERROR, "Unable to get configuration xml.", e);
			resp.sendError(500);			
		}
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BufferedReader br;
		StringBuffer reqStr = null;
		String response = "";

		resp.setContentType("text/xml");
		try {
			br = new BufferedReader(req.getReader());
			reqStr = new StringBuffer();
			String line;

			while ((line = br.readLine()) != null) {
				reqStr.append(line);
			}

			updateConfiguration(reqStr);
		} catch (IOException e) {
			response = getErrorDetails(e);
		}
		resp.getWriter().print(response);
	}

	/**
	 * Updates configuration
	 * 
	 * @param reqStr
	 *            payload in xml format
	 * @throws IOException
	 */
	private void updateConfiguration(StringBuffer reqStr) throws IOException {
		XmlNode node = XmlParser.parse(reqStr.toString());
		String type = node.getAttribute("type");

		if (type.equals("PROPERTY_TYPE_REGULAR")) {
			XmlNode child = node.getChild("property");

			String pid = child.getAttribute("pid");
			String moduleId = child.getAttribute("id");
			String newValue = child.getAttribute("newValue");

			Configuration configuration = configAdmin.getConfiguration(pid);
			Dictionary props = new Hashtable();

			// normalize property name
			if (pid.indexOf(PublicWSProvider.PACKAGE_ID) != -1 && moduleId.indexOf("enabled") != -1) {
				moduleId = moduleId.substring(moduleId.indexOf("-") + 1, moduleId.length());
				props.put(moduleId, new Boolean(newValue));
			} else {
				props.put(moduleId, newValue);
			}
			configuration.update(props);
		} else if (type.equals("PROPERTY_TYPE_APP_STATE")) {
			XmlNode child = node.getChild("property");

			String pid = child.getAttribute("pid");
			String applicationName = child.getAttribute("applicationName");
			String newValue = child.getAttribute("newValue");

			Configuration configuration = configAdmin.getConfiguration(pid);
			Dictionary properties = ConfigAdminUtil.getPropertiesSafely(configuration);
			Map appState = (Map) properties.get("app.state");
			String bundlePath = (String) properties.get("app.bundle.path");

			String keyByAppName = bundlePath + File.separator + applicationName + ".jar";

			appState.remove(keyByAppName);
			appState.put(new File(keyByAppName), new Integer(newValue));

			properties.put("app.state", appState);

			configuration.update(properties);
		}
	}

	/**
	 * 
	 * @param response
	 * @return Returns XML representing current state of all
	 *         {@link Configuration}s for the bug
	 * @throws IOException
	 * @throws SelfReferenceException
	 * @throws InvalidSyntaxException
	 */
	private String getConfigurationXml(String response) throws IOException, InvalidSyntaxException {
		Configuration[] configuration = configAdmin.listConfigurations(null);
		XmlNode configurationsNode = new XmlNode("configurations");

		if (configuration == null) {
			return configurationsNode.toString();
		}
		
		for (int i = 0; i < configuration.length; i++) {
			XmlNode configurationNode = new XmlNode("configuration");
			configurationNode.addAttribute("pid", configuration[i].getPid());

			Dictionary properties = ConfigAdminUtil.getPropertiesSafely(configuration[i]);

			Enumeration keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				Object value = properties.get(key);

				XmlNode property = new XmlNode("property");
				if (key.equals(Constants.SERVICE_PID)) {
					continue;
				}
				if (key.equals("app.state")) {
					if (!isStateSet) {
						createAppStateProperties(configurationNode, value);
						isStateSet = true;
						continue;
					}
				}
				property.addAttribute("name", key);
				property.addAttribute("value", String.valueOf(value));
				configurationNode.addChild(property);
			}

			configurationsNode.addChild(configurationNode);
		}
		return configurationsNode.toString();
	}

	private void createAppStateProperties(XmlNode configurationNode, Object map) {
		XmlNode property = new XmlNode("property");
		property.addAttribute("name", "app.state");
		for (Iterator it = ((Map) map).entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String app = ((File) entry.getKey()).toString();
			String state = ((Integer) entry.getValue()).toString();

			XmlNode childProperty = new XmlNode("applicationState");
			childProperty.addAttribute("appName", app.substring(app.lastIndexOf(File.separator) + 1, app.indexOf(".jar")));
			childProperty.addAttribute("state", state);
			property.addChild(childProperty);
		}
		configurationNode.addChild(property);
	}

	private String getErrorDetails(Exception e) {
		StringWriter wr = new StringWriter();
		e.printStackTrace(new PrintWriter(wr));
		return "<error>" + wr.toString() + "</error>";
	}
}
