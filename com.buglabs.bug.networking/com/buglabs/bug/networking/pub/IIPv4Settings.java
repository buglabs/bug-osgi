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

package com.buglabs.bug.networking.pub;

import java.util.Collection;

/**
 * 
 * This interface provides a method for getting and setting network settings
 * (configuration method, address, netmask, gateway, nameservers, and search
 * domains) for a particular network connection.
 * 
 * @author aturley
 *
 */
public interface IIPv4Settings {
	/**
	 * Returns the method for configuring the network settings. 
	 * 
	 * @return the method for configuring the network settings
	 * @see IIPv4SettingsMethod
	 */
	public IIPv4SettingsMethod getMethod();
	
	/**
	 * Set the method for configuring the network settings.
	 * 
	 * @param method the method to use for configuring the network settings
	 * @see IIPv4SettingsMethod
	 */
	public void setMethod(IIPv4SettingsMethod method);
	
	/**
	 * Get the IPv4 address.
	 * 
	 * @return the IPv4 address
	 */
	public String getAddress();
	
	/**
	 * Set the IPv4 address.
	 * 
	 * @param address the IPv4 address
	 */
	public void setAddress(String address);
	
	/**
	 * Get the netmask.
	 * 
	 * @return the netmask
	 */
	public String getNetmask();
	/**
	 * Set the netmask.
	 * 
	 * @param netmask the netmask
	 */
	public void setNetmask(String netmask);
	
	/**
	 * Get the gateway.
	 * 
	 * @return the gateway
	 */
	public String getGateway();
	/**
	 * Set the gateway.
	 * 
	 * @param gateway the gateway
	 */
	public void setGateway(String gateway);
	
	/**
	 * Get the nameservers.
	 * 
	 * @return a collection of the nameservers
	 */
	public Collection<String> getNameservers();
	
	/**
	 * Set the nameservers.
	 * 
	 * @param a collection of nameservers 
	 */
	public void setNameservers(Collection<String> nameservers);
	
	/**
	 * Get the search domains.
	 * 
	 * @return the search domains.
	 */
	public Collection<String> getSearchDomains();
	
	/**
	 * Set the search domains.
	 * 
	 * @param searchDomains the search domains.
	 */
	public void setSearchDomains(Collection<String> searchDomains);
}
