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

import java.util.Collection;
import java.util.Vector;

import com.buglabs.bug.networking.pub.IIPv4Settings;
import com.buglabs.bug.networking.pub.IIPv4SettingsMethod;


// TODO What to do here?
public class IPv4UnconnectedSettings implements IIPv4Settings {
	private IIPv4SettingsMethod methodDisabled = new IPv4SettingsMethod();
	public IIPv4SettingsMethod getMethod() {
		// TODO Auto-generated method stub
		return methodDisabled;
	}
	
	public void setMethod(IIPv4SettingsMethod method) {
		// TODO Auto-generated method stub
	}
	
	public String getAddress() {
		// TODO Auto-generated method stub
		return "";
	}

	public void setAddress(String address) {
		// TODO Auto-generated method stub

	}

	public String getNetmask() {
		// TODO Auto-generated method stub
		return "";
	}

	public void setNetmask(String netmask) {
		// TODO Auto-generated method stub

	}

	public String getGateway() {
		// TODO Auto-generated method stub
		return "";
	}

	public void setGateway(String gateway) {
		// TODO Auto-generated method stub

	}

	public Collection<String> getNameservers() {
		// TODO Auto-generated method stub
		return new Vector<String>();
	}

	public void setNameservers(Collection<String> nameservers) {
		// TODO Auto-generated method stub

	}

	public Collection<String> getSearchDomains() {
		// TODO Auto-generated method stub
		return new Vector<String>();
	}

	public void setSearchDomains(Collection<String> searchDomains) {
		// TODO Auto-generated method stub

	}

	public String toString() {
		return "Unconnected IPv4 Settings";
	}
}
