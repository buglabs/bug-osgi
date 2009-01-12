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
package com.buglabs.bug.module.lcd.accelerometer;

import java.io.IOException;
import java.io.InputStream;

import com.buglabs.bug.accelerometer.pub.AccelerometerSample;
import com.buglabs.bug.accelerometer.pub.AccelerometerSampleStream;

public class LCDAccelerometerSampleInputStream extends AccelerometerSampleStream {

	private static final int SIGN_MASK = 0x2000;

	public LCDAccelerometerSampleInputStream(InputStream is) {
		super(is);
	}

	public AccelerometerSample readSample() throws IOException {
		byte data[] = new byte[6];
		short sample[] = null;

		int result = read(data);

		if (result == data.length) {
			sample = new short[3];

			for (int i = 0; i < sample.length; ++i) {
				sample[i] = (short) ((short) (data[i * 2] << 8) + (short) data[i * 2 + 1]);
			}
		} else {
			throw new IOException("Unable to read sample from accelerometer\n Read length = " + result);
		}

		return new AccelerometerSample(convertToGs(sample[2]), convertToGs(sample[1]), convertToGs(sample[0]));

	}

	private float convertToGs(short s) {
		if ((SIGN_MASK & s) != 0) {
			return -1 * (~s) / 1024.0f;
		}

		return s / 1024.0f;
	}
}
