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

import java.util.List;

/**
 * 
 * The main entrypoint for the networking API.
 * 
 * @author aturley
 *
 */
public interface INetworking {
	/**
	 * Initiate a scan for available wifi access points.
	 */
	public void scanAccessPoints();
	
	/**
	 * Get a list of the available wifi access points.
	 * @return
	 */
	public List<IAccessPoint> getAccessPoints();
	
	/**
	 * Connect to an access point. This is used for connecting to hidden access
	 * points that do not appear in the list returned by getAccessPoints().
	 *  
	 * @param ssid the SSID of the access point to which you want to connect
	 * @param security the security method used by the access point to which
	 * you want to connect
	 * @param passphrase the passphrase for the access point to which you
	 * want to connect.
	 */
	public void connectToAccessPoint(String ssid, IAccessPointSecurity security, String passphrase);
	
	/**
	 * Get an object representing the ethernet device.
	 * @return
	 */
	public INetworkingDevice getEthernetDevice();
	
	/**
	 * Get an object representing the wifi device.
	 * @return
	 */
	public INetworkingDevice getWifiDevice();
	
	/**
	 * Get the settings for the network to which the ethernet device is
	 * connected.
	 * @return
	 */
	public IIPv4Settings getEthernetIPv4Settings();
	
	/**
	 * Get the settings for the network to which the wifi device is
	 * connected.
	 * @return
	 */
	public IIPv4Settings getWifiIPv4Settings();

	/**
	 * Set the settings for the network to which the ethernet device is
	 * connected.
	 * @return
	 */
	public void setEthernetIPv4Settings(IIPv4Settings settings);
	
	/**
	 * Set the settings for the network to which the wifi device is
	 * connected.
	 * @return
	 */
	public void setWifiIPv4Settings(IIPv4Settings settings);
	
	/**
	 * A convenience method for getting an AccessPointSecurity object.
	 * connected.
	 * @return
	 */
	public IAccessPointSecurity accessPointSecurityFactory();
	
	/**
	 * A convenience method for getting an IPv3SettingsMethod object.
	 * connected.
	 * @return
	 */
	public IIPv4SettingsMethod IPv4SettingsMethodFactory();
}
