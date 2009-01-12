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
package com.buglabs.bug.jni.lcd;

import com.buglabs.bug.jni.common.CharDevice;

public class LCDControl extends CharDevice {

	static {
		System.loadLibrary("LCD");
	}

	// IOCTL commands for BMI LCD driver
	public native int ioctl_BMI_LCD_RLEDOFF(int slot); // turn off Red LED

	public native int ioctl_BMI_LCD_RLEDON(int slot); // turn on Red LED

	public native int ioctl_BMI_LCD_GLEDOFF(int slot); // turn off Green LED

	public native int ioctl_BMI_LCD_GLEDON(int slot); // turn on Green LED

	public native int ioctl_BMI_LCD_VSYNC_DIS(int slot); // Enable VSYNC
															// output buffer

	public native int ioctl_BMI_LCD_VSYNC_EN(int slot); // Disable VSYNC output
														// buffer

	public native int ioctl_BMI_LCD_EN(int slot); // Enable LCD component

	public native int ioctl_BMI_LCD_DIS(int slot); // Disable LCD component

	public native int ioctl_BMI_LCD_SER_EN(int slot); // Enable Seriallizer
														// component

	public native int ioctl_BMI_LCD_SER_DIS(int slot); // Disable Seriallizer
														// component

	public native int ioctl_BMI_LCD_SETRST(int slot); // Disable entire module

	public native int ioctl_BMI_LCD_CLRRST(int slot); // Enable entire module

	public native int ioctl_BMI_LCD_SET_BL(int slot, int val); // Set IOX
																// backlight
																// bits [2:0]

	public native int ioctl_BMI_LCD_GETSTAT(int slot); // Get IOX state

	public native int ioctl_BMI_LCD_ACTIVATE(int slot); // Activate SER, TS,
														// ACCEL

	public native int ioctl_BMI_LCD_DEACTIVATE(int slot); // Deactivate SER,
															// TS, ACCEL

	public native int ioctl_BMI_LCD_SUSPEND(int slot); // Power down module

	public native int ioctl_BMI_LCD_RESUME(int slot); // Power up module
}
