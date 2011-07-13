/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
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
package com.buglabs.bug.bmi;

import java.io.IOException;

import com.buglabs.bug.bmi.sysfs.BMIDevice;
import com.buglabs.bug.bmi.sysfs.BMIDeviceHelper;

/**
 * An event from the BMI system. Format [moduleId] [version] [slot] [event]
 * 
 * @author ken
 * 
 */
public class BMIModuleEvent {
	/**
	 * Set of possible BMI Module events.
	 *
	 */
	public enum EVENT_TYPE { INSERT, REMOVE };
		
	private String raw;

	private int slot;

	private EVENT_TYPE event;

	private String moduleId;

	private String version;

	private BMIDevice bmiDevice;

	/**
	 * @param raw raw event string
	 * @throws IOException on parse error
	 */
	public BMIModuleEvent(String raw) throws IOException {
		this.raw = raw;
		if (!parse()) 
			throw new IOException("Failed to parse: " + raw);
	}
	
	/**
	 * @param device sysfs device
	 * @param slot slot number, overrides value in device.
	 */
	public BMIModuleEvent(BMIDevice device, int slot) {
		this.bmiDevice = device;
		this.moduleId = device.getProductId();
		this.version = "" + device.getRevision();
		this.slot = slot;
		this.event = EVENT_TYPE.INSERT;
	}
	
	/**
	 * @param device sysfs device
	 */
	public BMIModuleEvent(BMIDevice device) {
		this.bmiDevice = device;
		this.moduleId = device.getProductId();
		this.version = "" + device.getRevision();
		this.slot = device.getSlot();
		this.event = EVENT_TYPE.INSERT;
	}
	
	/**
	 * @return type of event
	 */
	public EVENT_TYPE getType() {
		return event;
	}

	/**
	 * @return id of module
	 */
	public String getModuleId() {
		return moduleId;
	}

	/**
	 * @return slot index module is attached to
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * @return version of module
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * See http://lurcher/wiki/BMI_-_Runtime_Interface#Message_Definition.
	 * 
	 * @return true if parse successful
	 * @throws IOException on File I/O error
	 */
	private boolean parse() throws IOException {
		String[] toks = raw.split(" ");

		if (toks.length != 4) {
			return false;
		}

		this.moduleId = toks[0].trim();
		this.version = toks[1].trim();
		try {
			this.slot = Integer.parseInt(toks[2]);
		} catch (NumberFormatException e) {
			return false;
		}

		String action = toks[3].trim().toUpperCase();

		if (action.equals("ADD")) {
			this.event = EVENT_TYPE.INSERT;
			this.bmiDevice = BMIDeviceHelper.getDevice(Activator.getContext(), slot);
		} else if (action.equals("REMOVE")) {
			this.event = EVENT_TYPE.REMOVE;
		} else {
			return false;
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String eventStr = "";

		switch (event) {
		case INSERT:
			eventStr = "add";
			break;
		case REMOVE:
			eventStr = "remove";
			break;
		default:
			eventStr = "[UNKNOWN]";
			break;
		}

		return moduleId + " " + version + " " + slot + " " + eventStr + "\n";
	}

	/**
	 * @return The BMI device associated with this event, if exists.  REMOVE events do not have devices.
	 */
	public BMIDevice getBMIDevice() {		
		return bmiDevice;
	}
}
