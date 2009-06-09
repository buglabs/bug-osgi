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
package com.buglabs.bug.module.lcd.pub;

import java.io.IOException;

import com.buglabs.module.IModuleControl;
import com.buglabs.module.IModuleLEDController;

/**
 * The interface that controls functions of the LCD module.
 */
public interface ILCDModuleControl extends IModuleControl, IModuleLEDController {
	/**
	 * LCD module backlight setting to OFF
	 */
	public static final int BACKLIGHT_OFF = 0;
	/**
	 * LCD module backlight setting to LOW
	 */
	public static final int BACKLIGHT_LOW = 1;
	/**
	 * LCD module backlight setting to MED
	 */
	public static final int BACKLIGHT_MED = 3;
	/**
	 * LCD module backlight setting to HIGH
	 */
	public static final int BACKLIGHT_HIGH = 2;
	
	/**
	 * @param val
	 *            Set's the intensity of the backlight 0-7.
	 * 
	 */
	public int setBackLight(int val) throws IOException; // Set IOX backlight
															// bits [2:0]

	/**
	 * @param val
	 *            Set's the intensity of the backlight 0-7.
	 * @deprecated use setBackLight.
	 */
	public int setBlackLight(int val) throws IOException; // Set IOX backlight
															// bits [2:0]

	/**
	 * 
	 * @return The stat of IOX. The 3 LSBs reprersent the state of the
	 *         backlight.
	 */
	public int getStatus() throws IOException; // Get IOX state

	public int disable() throws IOException; // Power down module

	public int enable() throws IOException; // Power up module
}
