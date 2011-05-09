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

import com.buglabs.bug.networking.pub.IIPv4SettingsMethod;

public class IPv4SettingsMethod implements IIPv4SettingsMethod {
	private enum Method {AUTO, MANUAL, DISABLED};
	private Method method;
	
	public IPv4SettingsMethod() {
		this.method = Method.DISABLED;
	}
	
	public IPv4SettingsMethod(Method method) {
		this.method = method;
	}
	
	private static IIPv4SettingsMethod auto = new IPv4SettingsMethod(Method.AUTO);
	
	private static IIPv4SettingsMethod disabled = new IPv4SettingsMethod(Method.DISABLED);
	
	private static IIPv4SettingsMethod manual = new IPv4SettingsMethod(Method.MANUAL);
	
	public static IIPv4SettingsMethod Auto() {
		return auto;
	}
	
	public static IIPv4SettingsMethod Disabled() {
		return disabled;
	}
	
	public static IIPv4SettingsMethod Manual() {
		return manual;
	}
	
	@Override
	public boolean isAuto() {
		return method == Method.AUTO;
	}

	@Override
	public boolean isDisabled() {
		return method == Method.DISABLED;
	}

	@Override
	public boolean isManual() {
		return method == Method.MANUAL;
	}

	@Override
	public void setAuto() {
		method = Method.AUTO;
	}
	
	@Override
	public void setDisabled() {
		method = Method.DISABLED;
	}
	
	@Override
	public void setManual() {
		method = Method.MANUAL;
	}
}
