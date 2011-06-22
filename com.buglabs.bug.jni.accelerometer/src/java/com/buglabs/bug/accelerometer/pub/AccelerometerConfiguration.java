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
package com.buglabs.bug.accelerometer.pub;

/**
 * Accelerometer Configuration
 * 
 * @author Angel Roman
 */
public class AccelerometerConfiguration {

	public int read_queue_size;
	public int read_queue_threshold;
	public short delay;
	public byte delay_resolution;
	public byte delay_mode;
	public byte run;
	public byte sensitivity;

	public int getReadQueueSize() {
		return read_queue_size;
	}

	public void setReadQueueSize(int read_queue_size) {
		this.read_queue_size = read_queue_size;
	}

	/**
	 * Number of 6-byte sample sets to queue
	 */
	public int getReadQueueThreshold() {
		return read_queue_threshold;
	}

	/**
	 * Number of 6-byte sample sets to queue
	 */
	public void setReadQueueThreshold(int read_queue_threshold) {
		this.read_queue_threshold = read_queue_threshold;
	}

	/**
	 * Timer ticks between the start of 2 sucessive sample sets.
	 */
	public short getDelay() {
		return delay;
	}

	/**
	 * Timer ticks between the start of 2 sucessive sample sets.
	 */
	public void setDelay(short delay) {
		this.delay = delay;
	}

	/**
	 * Timer tick resolution
	 * 
	 * 1 = 1 uSec 2 = 8 uSec 3 = 64 uSec 4 = 256 uSec 5 = 1024 uSec
	 */
	public byte getDelayResolution() {
		return delay_resolution;
	}

	/**
	 * Timer tick resolution
	 * 
	 * @param delay_resolution
	 *            1 = 1 uSec 2 = 8 uSec 3 = 64 uSec 4 = 256 uSec 5 = 1024 uSec
	 */
	public void setDelayResolution(byte delay_resolution) {
		this.delay_resolution = delay_resolution;
	}

	/**
	 * 0 = default delay = 5 millisecond 1 = configured delay
	 */
	public byte getDelayMode() {
		return delay_mode;
	}

	/**
	 * 0 = default delay = 5 millisecond 1 = configured delay
	 */
	public void setDelayMode(byte delay_mode) {
		this.delay_mode = delay_mode;
	}

	/**
	 * 0 = sampling disabled 1 = sampling enabled
	 */
	public byte getRun() {
		return run;
	}

	/**
	 * 0 = sampling disabled 1 = sampling enabled
	 */
	public void setRun(byte run) {
		this.run = run;
	}

	/**
	 * 0 = 2.5G, 421 mV/G 1 = 3.3G, 316 mV/G 2 = 6.7G, 158 mV/G 3 = 10G, 105
	 * mV/G
	 */
	public byte getSensitivity() {
		return sensitivity;
	}

	/**
	 * 0 = 2.5G, 421 mV/G 1 = 3.3G, 316 mV/G 2 = 6.7G, 158 mV/G 3 = 10G, 105
	 * mV/G
	 */
	public void setSensitivity(byte sensitivity) {
		this.sensitivity = sensitivity;
	}

	public boolean equals(Object obj) {
		if (obj instanceof AccelerometerConfiguration) {
			AccelerometerConfiguration config = (AccelerometerConfiguration) obj;
			if (config.getDelay() != delay)
				return false;
			if (config.getDelayMode() != delay_mode)
				return false;
			if (config.getDelayResolution() != delay_resolution)
				return false;
			if (config.getReadQueueSize() != read_queue_size)
				return false;
			if (config.getReadQueueThreshold() != read_queue_threshold)
				return false;
			if (config.getRun() != run)
				return false;
			if (config.getSensitivity() != sensitivity)
				return false;
			return true;
		}

		return super.equals(obj);
	}
}
