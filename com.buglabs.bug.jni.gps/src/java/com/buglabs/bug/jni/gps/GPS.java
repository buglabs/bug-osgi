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
package com.buglabs.bug.jni.gps;

import com.buglabs.bug.jni.common.CharDevice;

public class GPS extends CharDevice {

	// Control LEDs
	public static final int BMI_GPS_RLEDOFF = 0x1;
	public static final int BMI_GPS_RLEDON = 0x2;
	public static final int BMI_GPS_GLEDOFF = 0x3;
	public static final int BMI_GPS_GLEDON = 0x4;

	// Don't care about these
	public static final int BMI_GPS_SETBOOT = 0x5;
	public static final int BMI_GPS_CLRBOOT = 0x6;
	public static final int BMI_GPS_SETWAKE = 0x7;
	public static final int BMI_GPS_CLRWAKE = 0x8;
	public static final int BMI_GPS_SETRST = 0xA;
	public static final int BMI_GPS_CLRRST = 0xB;

	// Passive is the internal antenna
	// Active is the external antenna
	// Default is passive.
	public static final int BMI_GPS_ACTIVE_ANT = 0xC;
	public static final int BMI_GPS_PASSIVE_ANT = 0xD;

	/**
	 * Initializes the serial port the GPS device is on.
	 * 
	 * @param fd
	 *            the file descriptor of the GPS device
	 * @return
	 */
	public native int init();

	static {
		System.loadLibrary("GPS");
	}
}
