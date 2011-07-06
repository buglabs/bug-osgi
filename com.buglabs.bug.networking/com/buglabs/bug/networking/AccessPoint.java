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

import java.util.Vector;
import java.util.Map;

import org.freedesktop.dbus.Variant;
import com.buglabs.bug.networking.pub.IAccessPoint;
import com.buglabs.bug.networking.pub.IAccessPointSecurity;

import net.connman.Service;

public class AccessPoint implements IAccessPoint {
	private Service service;
	private AccessPointSecurity security;
	private static final String STATE_READY = "ready";
	private static final String STATE_ONLINE = "online";
	
	private static final String SECURITY_NONE = "none";
	private static final String SECURITY_WEP = "wep";
	private static final String SECURITY_PSK = "psk";
	private static final String SECURITY_WPA = "wpa";
	private static final String SECURITY_RSN = "rsn";

	public AccessPoint(Service service) {
		this.service = service;
		security = new AccessPointSecurity();
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#getName()
	 */
	@Override
	public String getName() {
		Variant name = getProperty("Name");
		if (name == null) {
			return "";
		}
		return (String) name.getValue();
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#getPassphrase()
	 */
	@Override
	public String getPassphrase() {
		String passphraseString = null;
		Variant passphraseVariant = getProperty("Passphrase");
		if (passphraseVariant != null) {
			passphraseString = (String) passphraseVariant.getValue();
		}
		if (passphraseString == null) {
			passphraseString = new String();
		}
		return passphraseString;
	}
	
	private void setSecurityFromSecurityString(String securityString, IAccessPointSecurity security) {
		if (securityString.compareTo(SECURITY_NONE) == 0) {
			security.setNone();
		} else if (securityString.compareTo(SECURITY_WEP) == 0) {
			security.setWEP();
		} else if (securityString.compareTo(SECURITY_PSK) == 0) {
			security.setWPA();
		} else if (securityString.compareTo(SECURITY_WPA) == 0) {
			security.setWPA();
		} else if (securityString.compareTo(SECURITY_RSN) == 0) {
			security.setWPA();
		} else {
			Activator.logWarning("security string was '" + securityString + "' which didn't match any known security setting.");
			security.setOther();
		}
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#getSecurity()
	 */
	@Override
	public IAccessPointSecurity getSecurity() {
		Variant securityVariant = getProperty("Security");
		if (securityVariant != null) {
			String securitySig = securityVariant.getSig();
			Activator.logDebug("security sig is "+ securitySig);
			
			if (securitySig.compareTo("s") == 0) {
				String securityString = (String) securityVariant.getValue();
				if (securityString != null) {
					setSecurityFromSecurityString(securityString, security);
				} else {
					security.setNone();
				}
			} else if (securitySig.compareTo("as") == 0) {
				Vector<String> securityStringVector = (Vector<String>) securityVariant.getValue();
				if (securityStringVector != null) {
					setSecurityFromSecurityString(securityStringVector.get(0), security);
				} else {
					security.setNone();
				}
			}
		}
		return security;
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#connect()
	 */
	@Override
	public void connect() {
		service.Connect();
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#connect(java.lang.String)
	 */
	@Override
	public void connect(String passphrase) {
		Variant<String> passphraseVariant = new Variant<String>(passphrase);
		service.SetProperty("Passphrase", passphraseVariant);
		service.Connect();
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#isConnected()
	 */
	@Override
	public boolean isConnected() {
		Variant stateVariant = getProperty("State");
		if (stateVariant == null) {
			return false;
		}
		
		String state = (String) stateVariant.getValue();
		if (state != null) {
			if (state.compareTo(STATE_READY) == 0 || state.compareTo(STATE_ONLINE) == 0) {
				return true;
			}
		} else {
			// TODO: This is an odd situation, we might want to throw an exception, or at least log it.
			Activator.logWarning("'State' is null");
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#disconnect()
	 */
	@Override
	public void disconnect() {
		service.Disconnect();
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#setAutoConnect()
	 */
	@Override
	public void setAutoConnect(Boolean autoConnect) {
		service.SetProperty("AutoConnect", new Variant(autoConnect));
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#getAutoConnect()
	 */
	@Override
	public Boolean getAutoConnect() {
		Variant autoConnect = getProperty("AutoConnect");
		if (autoConnect == null) {
			return false;
		}
		return (Boolean) autoConnect.getValue();
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#requiresPassphrase()
	 */
	@Override
	public boolean requiresPassphrase() {
		Variant requiresPassphraseVariant = getProperty("PassphraseRequired");
		if (requiresPassphraseVariant == null) {
			return false;
		}
		return (Boolean) requiresPassphraseVariant.getValue();
	}
	
	/* (non-Javadoc)
	 * @see networkingapi.impl.IAccessPoint#getStrength()
	 */
	@Override
	public int getStrength() {
		Variant strength = getProperty("Strength");
		if (strength == null) {
			return 0;
		}
		return (Byte) strength.getValue();
	}
	
	private Variant getProperty(String key) {
		Map<String, Variant> properties = null;
		try {
			properties = service.GetProperties();
		} catch (Exception e) {
			return null;
		}
		
		return properties.get(key); 
	}
}
