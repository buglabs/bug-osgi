/*******************************************************************************
 * Copyright (c) 2011 Bug Labs, Inc.
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

package com.buglabs.bug.networking;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Map;
import java.net.URLDecoder;
import java.io.Writer;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;


import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.Variant;

import com.buglabs.bug.networking.pub.IIPv4Settings;

public class SettingsServlet extends HttpServlet {	
	private KitchenSink ks;
	public SettingsServlet(KitchenSink ks) {
		super();
		this.ks = ks;
	}
	
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		String technology = arg0.getPathInfo().replaceFirst("/", "");
		IIPv4Settings settings = null;
		if (technology.compareTo(Technology.ETHERNET_TECHNOLOGY.typeString()) == 0) {
			settings = ks.getEthernetIPv4Settings();
		} else if (technology.compareTo(Technology.WIFI_TECHNOLOGY.typeString()) == 0) {
			settings = ks.getWifiIPv4Settings();
		} else {
			Activator.logWarning("Unknown technology: '" + technology + "'");
		}
		if (settings != null) {
			Writer writer = arg1.getWriter();
			
			writer.write("'method':" + settings.getMethod().toString() + "\n");
			writer.write("'address':" + settings.getAddress() + "\n");
			writer.write("'netmask':" + settings.getNetmask() + "\n");
			writer.write("'gateway':" + settings.getGateway() + "\n");
			writer.write("'nameservers':" + settings.getNameservers() + "\n");
			writer.write("'domains':" + settings.getSearchDomains() + "\n");
		} else {
			Activator.logWarning("Could not get settings for technology: '" + technology + "'");
		}
	}
	
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		String technology = arg0.getPathInfo().replaceFirst("/", "");
		IIPv4Settings settings = null;
		if (technology.compareTo(Technology.ETHERNET_TECHNOLOGY.typeString()) == 0) {
			settings = ks.getEthernetIPv4Settings();
		} else if (technology.compareTo(Technology.WIFI_TECHNOLOGY.typeString()) == 0) {
			settings = ks.getWifiIPv4Settings();
		} else {
			Activator.logWarning("Unknown technology: '" + technology + "'");
		}
		
		Activator.logDebug("parameters");
		
		if (settings != null) {
			Map<String, String> pmap = arg0.getParameterMap();
			for (String key : pmap.keySet()) {
				Activator.logDebug(key + " = '" + pmap.get(key) + "'");
				if (key.compareTo("method") == 0) {
					if (pmap.get(key).compareTo("auto") == 0) {
						Activator.logDebug("setting method to AUTO");
						settings.setMethod(IPv4SettingsMethod.Auto());
					}
					if (pmap.get(key).compareTo("manual") == 0) {
						Activator.logDebug("setting method to MANUAL");
						settings.setMethod(IPv4SettingsMethod.Manual());
					}
					if (pmap.get(key).compareTo("disable") == 0) {
						Activator.logDebug("setting method to DISABLE");
						settings.setMethod(IPv4SettingsMethod.Disabled());
					}
				}
				if (key.compareTo("address") == 0) {
					Activator.logDebug("setting Address");
					settings.setAddress(pmap.get(key));
				}
				if (key.compareTo("netmask") == 0) {
					Activator.logDebug("setting Netmask");
					settings.setNetmask(pmap.get(key));
				}
				if (key.compareTo("gateway") == 0) {
					Activator.logDebug("setting Gateway");
					settings.setGateway(pmap.get(key));
				}
				if (key.compareTo("nameservers") == 0) {
					Activator.logDebug("setting Nameservers");
					List<String> nameservers = Arrays.asList(pmap.get(key).split(","));
					settings.setSearchDomains(nameservers);
				}
				if (key.compareTo("searchdomains") == 0) {
					Activator.logDebug("setting SearchDomains");
					List<String> searchDomains = Arrays.asList(pmap.get(key).split(","));
					settings.setSearchDomains(searchDomains);
				}
			}
			if (technology.compareTo(Technology.ETHERNET_TECHNOLOGY.typeString()) == 0) {
				ks.setEthernetIPv4Settings(settings);
			} else if (technology.compareTo(Technology.WIFI_TECHNOLOGY.typeString()) == 0) {
				ks.setWifiIPv4Settings(settings);
			}
		}
	}
}
