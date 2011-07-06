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

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Vector;

import net.connman.Service;

import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.Variant;

import com.buglabs.bug.networking.IPv4SettingsMethod;
import com.buglabs.bug.networking.pub.IIPv4Settings;
import com.buglabs.bug.networking.pub.IIPv4SettingsMethod;

public class IPv4Settings implements IIPv4Settings {

	private Service service;
	
	private IIPv4SettingsMethod method;
	private String address;
	private String netmask;
	private String gateway;
	private Collection<String> nameservers;
	private Collection<String> searchDomains;
	private Map<String, Variant> propertiesCache;
	
	private static final String IPV4_KEY = "IPv4";
	private static final String IPV4_CONFIGURATION_KEY = "IPv4.Configuration";

	private static final String METHOD_KEY = "Method";
	private static final String ADDRESS_KEY = "Address";
	private static final String NETMASK_KEY = "Netmask";
	private static final String GATEWAY_KEY = "Gateway";
	private static final String NAMESERVERS_KEY = "Nameservers";
	private static final String NAMESERVERS_CONFIGURATION_KEY = "Nameservers.Configuration";
	private static final String SEARCHDOMAINS_KEY = "Domains";
	private static final String SEARCHDOMAINS_CONFIGURATION_KEY = "Domains.Configuration";
	
	private static final String DHCP_METHOD = "dhcp";
	private static final String MANUAL_METHOD = "manual";
	private static final String OFF_METHOD = "off";
	
	public IPv4Settings(Service service) {
		this.service = service;
		propertiesCache = service.GetProperties();
		method = null;
		address = null;
		netmask = null;
		gateway = null;
		nameservers = null;
		searchDomains = null;
	}
	
	public String toString() {
		return "IPv4 Settings for " + service;
	}
	
	public IIPv4SettingsMethod getMethod() {
		if (method == null) {
			method = new IPv4SettingsMethod();
			setMethodFromString((String) getServiceIPv4Property(METHOD_KEY), method);
		}
		return method;
	}
	
	public void setMethod(IIPv4SettingsMethod method) {
		this.method = method;
	}
	
	public String getAddress() {
		if (address == null ) {
			address = (String) getServiceIPv4Property(ADDRESS_KEY); 
		}
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNetmask() {
		if (netmask == null) {
			netmask = (String) getServiceIPv4Property(NETMASK_KEY);
		}
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getGateway() {
		if (gateway == null) {
			gateway = (String) getServiceIPv4Property(GATEWAY_KEY);
		}
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public Collection<String> getNameservers() {
		if (nameservers == null) {
			nameservers = (Vector<String>) getServiceProperty(NAMESERVERS_KEY);
		}
		return nameservers;
	}

	public void setNameservers(Collection<String> nameservers) {
		this.nameservers = nameservers;
	}

	public Collection<String> getSearchDomains() {
		if (searchDomains == null) {
			searchDomains = (Vector<String>) getServiceProperty(SEARCHDOMAINS_KEY);
		}
		return searchDomains;
	}

	public void setSearchDomains(Collection<String> searchDomains) {
		this.searchDomains = searchDomains;
	}

	static public void applySettings(IIPv4Settings settings, Service service) {
		Map <String, String> newIpv4Properties = new HashMap<String, String>();
		
		if (settings.getMethod() != null) {
			newIpv4Properties.put(METHOD_KEY, methodToString(settings.getMethod()));
			Activator.logDebug(METHOD_KEY + " set to '" + methodToString(settings.getMethod()) + "'");
		}
		if (settings.getAddress() != null) {
			newIpv4Properties.put(ADDRESS_KEY, settings.getAddress());
			Activator.logDebug(ADDRESS_KEY + " set to '" + settings.getAddress() + "'");
		}
		if (settings.getNetmask() != null) {
			newIpv4Properties.put(NETMASK_KEY, settings.getNetmask());
			Activator.logDebug(NETMASK_KEY + " set to '" + settings.getNetmask() + "'");
		}
		if (settings.getGateway() != null) {
			newIpv4Properties.put(GATEWAY_KEY, settings.getGateway());
			Activator.logDebug(GATEWAY_KEY + " set to '" + settings.getGateway() + "'");
		}

		if (newIpv4Properties.size() > 0) {
			Variant updatedProperties = new Variant(newIpv4Properties, "a{ss}");
			service.SetProperty(IPV4_CONFIGURATION_KEY, updatedProperties);
		}
		
		if (settings.getNameservers() != null) {
			String aNameservers[] = settings.getNameservers().toArray(new String[0]);
			service.SetProperty(NAMESERVERS_CONFIGURATION_KEY, new Variant(aNameservers));
		}
		if (settings.getSearchDomains() != null) {
			String aSearchDomains[] = settings.getSearchDomains().toArray(new String[0]);
			service.SetProperty(SEARCHDOMAINS_CONFIGURATION_KEY, new Variant(aSearchDomains));
		}
		
	}
	
	private Object getServiceProperty(String key) {
		Variant variant = (Variant) propertiesCache.get(key);
		if (variant != null) {
			return variant.getValue();
		} 
		return null; 
	}
	
	private Map<String, Variant> getServiceIPv4Properties() {
		Variant ipv4 = propertiesCache.get(IPV4_KEY);
		return (Map<String, Variant>) ipv4.getValue();
	}

	private Object getServiceIPv4Property(String key) {
		Map<String, Variant> ipv4Properties = getServiceIPv4Properties();
		Variant v = ipv4Properties.get(key);
		Object value;
		if (v == null) {
			Activator.logWarning("No value found for key '" + key + "'");
			value = null;
		} else {
			value = v.getValue();
		}
		return value;
	}
	
	private void setServiceIPv4Property(String key, String value) {
		Activator.logDebug("setting '" + key + "' to '" + value + "'");
		Map<String, Variant> ipv4Properties = getServiceIPv4Properties();

		Map <String, String> newIpv4Properties = new HashMap<String, String>();
		
		newIpv4Properties.put(key, value);
		// Only copy the other key/value pairs if the Method setting is not DHCP.
		if (!(key.compareTo(METHOD_KEY) == 0 && ((String) value).compareTo(DHCP_METHOD) == 0)) {
			for (String oldKey : ipv4Properties.keySet()) {
				if (key.compareTo(oldKey) != 0) {
					newIpv4Properties.put(oldKey, (String) ipv4Properties.get(oldKey).getValue());
				}
			}
		}
		Variant updatedProperties = new Variant(newIpv4Properties, "a{ss}");
		service.SetProperty(IPV4_CONFIGURATION_KEY, updatedProperties);
	}
	
	static private String methodToString(IIPv4SettingsMethod method) {
		String methodString = "";
		if (method.isAuto()) {
			methodString = DHCP_METHOD;
		} else if (method.isDisabled()) {
			methodString = OFF_METHOD;
		} else if (method.isManual()) {
			methodString = MANUAL_METHOD;
		} else {
			// TODO Throw an exception.
			Activator.logWarning("Got method '" + methodString +"', which does not correspond to a string");
		}
		return methodString; 
	}
	
	private void setMethodFromString(String methodString, IIPv4SettingsMethod method) {
		if (methodString.compareTo(DHCP_METHOD) == 0) {
			method.setAuto();
		} else if (methodString.compareTo(MANUAL_METHOD) == 0) {
			method.setManual();
		} else if (methodString.compareTo(OFF_METHOD) == 0) {
			method.setDisabled();
		} else {
			Activator.logWarning("Got method '" + methodString +"', which does not correspond to a string");
		}
	}
}
