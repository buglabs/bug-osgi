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

/**
 * 
 * @author aturley
 *
 * This class represents a wifi access point.
 *
 */
public interface IAccessPoint {
	/**
	 * Returns the SSID of the access point.
	 * 
	 * @ return the SSID of the access point.
	 */
	public String getName();

	/**
	 * Returns the passphrase that has been set for the access point.
	 *
	 * @return the passphrase for the access point
	 */
	public String getPassphrase();

	/**
	 * Returns the securtity setting for the access point.
	 * 
	 * @return the security setting
	 * @see IAccessPointSecurity
	 */
	public IAccessPointSecurity getSecurity();

	/**
	 * Connects to the access point. The current access point settings will
	 * be used to establish a connection.
	 */
	public void connect();

	/**
	 * Connects to the access point using the given passphrase.
	 * 
	 * @param passphrase the passphrase to use to connect to the access point.
	 */
	public void connect(String passphrase);

	/**
	 * Returns true if the system is connected to the access point, false otherwise.
	 * 
	 * @return true if the system is connected to the access point, false otherwise
	 */
	public boolean isConnected();

	/**
	 * Disconnects from the access point.
	 */
	public void disconnect();

	/**
	 * Automatically connect to this access point when available.
	 * 
	 * @param autoConnect true to autoconnect, false otherwise
	 */
	public void setAutoConnect(Boolean autoConnect);
	
	/**
	 * Returns true if autoconnect, false otherwise.
	 * @return
	 */
	public Boolean getAutoConnect();
	
	/**
	 * Returns true if the access point requires a passphrase, false otherwise.
	 * 
	 * @return true if the access point requires a passphrase, false otherwise
	 */
	public boolean requiresPassphrase();

	/**
	 * Returns a value between 0 and 100 specifying strength of the access
	 * point's signal.
	 * 
	 * @return the signal strength
	 */
	public int getStrength();
}

