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
import java.net.URLDecoder;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import com.buglabs.bug.networking.pub.IAccessPoint;


public class ConnectServlet extends HttpServlet {
	KitchenSink ks;
	public ConnectServlet(KitchenSink ks) {
		super();
		this.ks = ks;
	}
	
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		String accessPointPath = arg0.getPathInfo().replaceFirst("/", "");
		String accessPointName = URLDecoder.decode(accessPointPath);

		String passphrase = arg0.getParameter("pass");
		Activator.logDebug("got connect request");
		Activator.logDebug("Connect to '" + accessPointName + "' with passphrase '" + passphrase + "'");
		if (accessPointName != null) {
			List<IAccessPoint> accessPoints = ks.getAccessPoints();
			IAccessPoint accessPoint = null;
			for (IAccessPoint ap : accessPoints) {
				if (ap.getName().compareTo(accessPointName) == 0) {
					accessPoint = ap;
					break;
				}
			}
			if (accessPoint != null) {
				if (passphrase == null) {
					Activator.logDebug("Connect to '" + accessPointName + "' with no passphrase");
					accessPoint.connect();
				} else {
					Activator.logDebug("Connect to '" + accessPointName + "' with passphrase '" + passphrase + "'");
					accessPoint.connect(passphrase);
				}
			} else {
				Activator.logWarning("Could not find '" + accessPointName + "'");
			}
		}
	}
}
