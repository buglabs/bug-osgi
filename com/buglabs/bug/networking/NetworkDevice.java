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

import net.connman.Manager;
import net.connman.Service;

import org.freedesktop.dbus.Variant;

import com.buglabs.bug.networking.pub.INetworkingDevice;

public class NetworkDevice implements INetworkingDevice {
	private Manager manager;
	
	private final String AVAILABLE_TECHNOLOGIES = "AvailableTechnologies";
	private final String ENABLED_TECHNOLOGIES = "EnabledTechnologies";
	private final String ETHERNET_TECHNOLOGY_NAME = "ethernet";
	private final String WIFI_TECHNOLOGY_NAME = "wifi";
	private final Technology technology;

	public NetworkDevice(Manager manager, Technology technology) {
		this.manager = manager;
		this.technology = technology;
	}
	
	public boolean isAvailable() {
		Variant enabledTechnologies = manager.GetProperties().get(AVAILABLE_TECHNOLOGIES);
		for (String technology : (Vector<String>) enabledTechnologies.getValue()) {
			if (technology.compareTo(getTechnologyName()) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public void setEnabled(boolean enabled) {
		if (enabled) {
			manager.EnableTechnology(getTechnologyName());
		} else {
			manager.DisableTechnology(getTechnologyName());
		}
	}

	public boolean isEnabled() {
		Variant enabledTechnologies = manager.GetProperties().get(ENABLED_TECHNOLOGIES);
		for (String technology : (Vector<String>) enabledTechnologies.getValue()) {
			if (technology.compareTo(getTechnologyName()) == 0) {
				return true;
			}
		}
		return false;
	}

	public Technology getTechnology() {
		return technology;
	}
	
	private String getTechnologyName() {
		switch(technology) {
		case ETHERNET_TECHNOLOGY:
			return ETHERNET_TECHNOLOGY_NAME;
		case WIFI_TECHNOLOGY:
			return WIFI_TECHNOLOGY_NAME;
		default:
			// TODO: This should probably generate an exception. 
			return "";
		}
	}
}
