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
package com.buglabs.bug.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.buglabs.bug.base.pub.IBUG20BaseControl;
import com.buglabs.bug.base.pub.LEDDevice;
import com.buglabs.bug.sysfs.SysfsNode;

/**
 * Impl of IBUG20BaseControl that uses sysfs file API to control BUGbase LEDs.
 * 
 * @author kgilmer
 * 
 */
public class BUGBaseControl extends SysfsNode implements IBUG20BaseControl {
	/*
	 * LEDs in /sys/class/leds look like this: omap3bug:blue:battery
	 * omap3bug:blue:power omap3bug:green:battery omap3bug:red:wlan
	 * omap3bug:blue:bt omap3bug:blue:wlan omap3bug:green:wlan
	 */
	private static final String LED_ROOT = "/sys/class/leds/";

	private LEDDevice batteryLED;
	private LEDDevice wlanLED;
	private LEDDevice powerLED;
	private LEDDevice btLED;

	/**
	 * @throws FileNotFoundException on sysfs error
	 */
	public BUGBaseControl() throws FileNotFoundException {
		super(new File(LED_ROOT));

		batteryLED = new LEDDevice(root, "battery");
		wlanLED = new LEDDevice(root, "wifi");
		powerLED = new LEDDevice(root, "power", "blue");
		btLED = new LEDDevice(root, "bt", "blue");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.bug.base.pub.IBUG20BaseControl#setLEDBrightness(int,
	 * int)
	 */
	public void setLEDBrightness(int led, int brightness) throws IOException {
		if (brightness > 255 || led > 3) {
			throw new IOException("Invalid LED or brightness parameter value.");
		}

		LEDDevice ld = getLEDDevice(led);

		if (ld.getType() == LEDDevice.TYPE_MONO_COLOR) {
			ld.setBrightness(LEDDevice.COLOR_MONO, brightness);
		} else {
			ld.setBrightness(IBUG20BaseControl.COLOR_RED, brightness);
			ld.setBrightness(IBUG20BaseControl.COLOR_GREEN, brightness);
			ld.setBrightness(IBUG20BaseControl.COLOR_BLUE, brightness);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.buglabs.bug.base.pub.IBUG20BaseControl#setLEDColor(int, int,
	 * boolean)
	 */
	public void setLEDColor(int led, int color, boolean on) throws IOException {
		if (color < 0 || color > 3) {
			throw new IOException("Color " + color + " is not valid.");
		}

		LEDDevice ld = getLEDDevice(led);

		if (ld.getType() == LEDDevice.COLOR_MONO) {
			throw new IOException("LED " + led + " does not allow color to be set.");
		}

		int value = on ? 128 : 0;

		ld.setBrightness(color, value);
	}

	/**
	 * @param index of LED device (0-3)
	 * @return the output stream for a given LED
	 * @throws IOException on file I/O error
	 */
	private LEDDevice getLEDDevice(int index) throws IOException {
		switch (index) {
		case 0:
			return batteryLED;
		case 1:
			return powerLED;
		case 2:
			return wlanLED;
		case 3:
			return btLED;
		default:
			throw new IOException("LED index out of bounds: " + index);
		}
	}

	/* (non-Javadoc)
	 * @see com.buglabs.bug.base.pub.IBUG20BaseControl#getLEDBrightness(int, int)
	 */
	public int getLEDBrightness(int led, int color) throws IOException {
		LEDDevice ld = getLEDDevice(led);

		if (ld.getType() == LEDDevice.TYPE_MONO_COLOR && color != LEDDevice.COLOR_MONO) {
			throw new IOException("This LED device doesn't support color: " + color);
		}

		return ld.getBrightness(color);
	}

	/* (non-Javadoc)
	 * @see com.buglabs.bug.base.pub.IBUG20BaseControl#setLEDTrigger(int, int, java.lang.String)
	 */
	public void setLEDTrigger(int led, int color, String trigger) throws IOException {

		LEDDevice ld = getLEDDevice(led);

		ld.setTrigger(color, trigger);
	}

	/* (non-Javadoc)
	 * @see com.buglabs.bug.base.pub.IBUG20BaseControl#getLEDTrigger(int, int)
	 */
	public String getLEDTrigger(int led, int color) throws IOException {
		LEDDevice ld = getLEDDevice(led);

		return ld.getLEDTrigger(color);
	}

	/* (non-Javadoc)
	 * @see com.buglabs.bug.base.pub.IBUG20BaseControl#getLEDTriggers(int, int)
	 */
	public String[] getLEDTriggers(int led, int color) throws IOException {
		LEDDevice ld = getLEDDevice(led);

		return ld.getLEDTriggers(color);
	}

	/* (non-Javadoc)
	 * @see com.buglabs.bug.base.pub.IBUG20BaseControl#getLEDDevices()
	 */
	public List<LEDDevice> getLEDDevices() {
		return Arrays.asList(new LEDDevice[] {batteryLED, wlanLED, btLED, powerLED});
	}
}
