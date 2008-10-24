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
package com.buglabs.bug.jni.gps;

import com.buglabs.bug.jni.common.CharDevice;

public class GPS extends CharDevice {
	
	//Control LEDs
	public static final int BMI_GPS_RLEDOFF = 0x1;
	public static final int BMI_GPS_RLEDON = 0x2;
	public static final int BMI_GPS_GLEDOFF = 0x3;
	public static final int BMI_GPS_GLEDON = 0x4;
	
	//Don't care about these
	public static final int BMI_GPS_SETBOOT = 0x5;
	public static final int BMI_GPS_CLRBOOT = 0x6;
	public static final int BMI_GPS_SETWAKE = 0x7;
	public static final int BMI_GPS_CLRWAKE = 0x8;
	public static final int BMI_GPS_SETRST = 0xA;
	public static final int BMI_GPS_CLRRST = 0xB;
	
	//Passive is the internal antenna
	//Active is the external antenna
	//Default is passive.
	public static final int BMI_GPS_ACTIVE_ANT = 0xC;
	public static final int BMI_GPS_PASSIVE_ANT = 0xD;	
	
	/**
	 * Initializes the serial port the GPS device is on.
	 * @param fd the file descriptor of the GPS device
	 * @return
	 */
	public native int init();
	
	static {
		System.loadLibrary("GPS");
	}
}
