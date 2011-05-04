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
package com.buglabs.bug.base.pub;

import java.io.IOException;
import java.util.List;

/**
 * Provides control of BUGbase 20 LEDs.
 * 
 */
public interface IBUG20BaseControl {
	public static int COLOR_BLUE = 0;
	public static int COLOR_RED = 1;
	public static int COLOR_GREEN = 2;

	public static int LED_BATTERY = 0;
	public static int LED_POWER = 1;
	public static int LED_WLAN = 2;
	public static int LED_BLUETOOTH = 3;
	
	/**
	 * Turn on or off a specific color on a multi color LED.
	 * 
	 * @param led IBUGBaseControl.LED_*
	 * @param color  IBUGBaseControl.COLOR_*
	 * @param on TRUE if LED is to be on, false otherwise.
	 * @throws IOException is thrown if invalid LED or color value is passed
	 */
	public void setLEDColor(int led, int color, boolean on) throws IOException;
	
	/**
	 * @param led
	 * @param color
	 * @throws IOException
	 */
	public int getLEDBrightness(int led, int color) throws IOException;
	
	/**
	 * Set the trigger for a given LED and Color
	 * @param led
	 * @param color
	 * @param trigger
	 * @throws IOException
	 */
	public void setLEDTrigger(int led, int color, String trigger) throws IOException;
	
	/**
	 * @param led
	 * @param color
	 * @return The trigger a given light is currently set to.
	 * @throws IOException
	 */
	public String getLEDTrigger(int led, int color) throws IOException;
	
	/**
	 * @param led
	 * @param color
	 * @return A list of string names of triggers that can be set for LEDs.
	 * @throws IOException
	 */
	public String[] getLEDTriggers(int led, int color) throws IOException;
	
	/**
	 * Set brightness level for PWM-controlled LED.
	 * 
	 * @param led IBUGBaseControl.LED_*
	 * @param brightness 0 - 255
	 * @throws IOException is thrown if invalid LED value is passed.
	 */
	public void setLEDBrightness(int led, int brightness) throws IOException;
	
	/**
	 * @return A list of LEDDevices controllable on BUG 2.0 base.
	 */
	public List getLEDDevices();
}
