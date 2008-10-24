/* Copyright (c) 2007, 2008 Bug Labs, Inc.
 * All rights reserved.
 *   
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License version  
 * 2 only, as published by the Free Software Foundation.   
 *   
 * This program is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * General Public License version 2 for more details (a copy is  
 * included at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html).   
 *   
 * You should have received a copy of the GNU General Public License  
 * version 2 along with this work; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA   
 *
 */
package com.buglabs.bug.jni.lcd;

import com.buglabs.bug.jni.common.CharDevice;

public class LCDControl extends CharDevice {
	
	static {
		System.loadLibrary("LCD");
	}
	
	// IOCTL commands for BMI LCD driver
	public native int ioctl_BMI_LCD_RLEDOFF(int slot);		// turn off Red LED
	public native int ioctl_BMI_LCD_RLEDON(int slot);		// turn on Red LED
	public native int ioctl_BMI_LCD_GLEDOFF(int slot);		// turn off Green LED
	public native int ioctl_BMI_LCD_GLEDON(int slot);		// turn on Green LED

	public native int ioctl_BMI_LCD_VSYNC_DIS(int slot);		// Enable VSYNC output buffer
	public native int ioctl_BMI_LCD_VSYNC_EN(int slot);		// Disable VSYNC output buffer
	public native int ioctl_BMI_LCD_EN(int slot);		// Enable LCD component
	public native int ioctl_BMI_LCD_DIS(int slot);		// Disable LCD component
	public native int ioctl_BMI_LCD_SER_EN(int slot);		// Enable Seriallizer component
	public native int ioctl_BMI_LCD_SER_DIS(int slot);		// Disable Seriallizer component
	public native int ioctl_BMI_LCD_SETRST(int slot);		// Disable entire module
	public native int ioctl_BMI_LCD_CLRRST(int slot);		// Enable entire module
	public native int ioctl_BMI_LCD_SET_BL(int slot, int val);		// Set IOX backlight bits [2:0]
	public native int ioctl_BMI_LCD_GETSTAT(int slot);		// Get IOX state
	public native int ioctl_BMI_LCD_ACTIVATE(int slot);		// Activate SER, TS, ACCEL
	public native int ioctl_BMI_LCD_DEACTIVATE(int slot);	// Deactivate SER, TS, ACCEL
	public native int ioctl_BMI_LCD_SUSPEND(int slot);	// Power down module
	public native int ioctl_BMI_LCD_RESUME(int slot);	// Power up module	
}
