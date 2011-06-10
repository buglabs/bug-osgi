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
package com.buglabs.bug.module.motion.pub;

import java.io.IOException;
import java.io.InputStream;

public interface IMotionRawFeed {

	/**
	 * BMI_MOTION_DETECT_STATUS
	 * 
	 * This bit is the present status of the the motion detector status pin. A
	 * value of 1 indicates that motion is being detected. A value of 0
	 * indicates that motion is not being detected.
	 */
	public final static byte BMI_MOTION_DETECT_STATUS = (1 << 3);

	/**
	 * BMI_MOTION_DETECT_LATCHED_STATUS
	 * 
	 * This bit is the latched status of the motion sensor. This bit is set to 1
	 * when the BMI_MOTION_DETECT_STATUS bit changes from 0 to 1. This bit will
	 * be cleared as the result of an "ioctl(BMI_MDACC_MOTION_GET_STATUS)" or a
	 * read() system call.
	 */
	public final static byte BMI_MOTION_DETECT_LATCHED_STATUS = (1 << 2);

	/**
	 * BMI_MOTION_DETECT_DELTA
	 * 
	 * This bit indicates that the motion detector status has changed from 1 to
	 * 0 or has changed from 0 to 1. This bit will be cleared as the result of
	 * an "ioctl(BMI_MDACC_MOTION_GET_STATUS)" or read() system calls.
	 */
	public final static byte BMI_MOTION_DETECT_DELTA = (1 << 1);

	/**
	 * BMI_MOTION_DETECT_ENABLED
	 * 
	 * This bits is the state of the motion detector sampling and status
	 * reporting mechanism. A value of 1 indicates that the motion detector is
	 * enabled. A value of 0 indicates that the motion detector is disabled.
	 */
	public final static byte BMI_MOTION_DETECT_ENABLED = (1 << 0);

	/**
	 * Each byte returned by the input stream represents the status byte from
	 * the motion detector.
	 * 
	 * Use BMI_MOTION_DETECT_STATUS, BMI_MOTION_DETECT_LATCHED_STATUS,
	 * BMI_MOTION_DETECT_DELTA and BMI_MOTION_DETECT_ENABLED to bit mask the
	 * status byte.
	 * 
	 * @throws IOException
	 * @returns InputStream to the status register of the motion detector.
	 */
	public InputStream getInputStream() throws IOException;
}
