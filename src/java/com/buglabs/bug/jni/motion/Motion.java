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
package com.buglabs.bug.jni.motion;

import com.buglabs.bug.jni.common.CharDevice;

public class Motion extends CharDevice {

	static {
		System.loadLibrary("Motion");
	}

	public static final byte BMI_MOTION_DETECT_STATUS = (1 << 3);
	public static final byte BMI_MOTION_DETECT_LATCHED_STATUS = (1 << 2);
	public static final byte BMI_MOTION_DETECT_DELTA = (1 << 1);
	public static final byte BMI_MOTION_DETECT_ENABLED = (1 << 0);

	/**
	 * 
	 * @return The value of the IOX register. -1 if ioctl failed.
	 */
	public native int ioctl_BMI_MDACC_MOTION_DETECTOR_GET_STATUS();

	/**
	 * Start motion capture
	 * 
	 * @return -1 if ioctl failed.
	 */
	public native int ioctl_BMI_MDACC_MOTION_DETECTOR_RUN();

	/**
	 * Stops motion capture
	 * 
	 * @return -1 if ioctl failed.
	 */
	public native int ioctl_BMI_MDACC_MOTION_DETECTOR_STOP();
}
