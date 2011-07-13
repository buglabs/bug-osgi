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
package com.buglabs.bug.module.gps;

import java.io.IOException;
import java.util.TimerTask;

import org.osgi.service.log.LogService;

import com.buglabs.bug.module.gps.pub.IGPSModuleControl;

/**
 * A class to express GPS state with GPS module LED.
 * @author aroman
 *
 */
public class GPSFIXLEDStatusTask extends TimerTask {
	/**
	 * Amount of time the GPS LED should blink.
	 */
	private static final int LED_BLINK_INTERVAL_MILLIS = 10;
	private IGPSModuleControl control;
	private final LogService log;

	/**
	 * @param control control
	 * @param log LogService
	 */
	public GPSFIXLEDStatusTask(IGPSModuleControl control, LogService log) {
		this.control = control;
		this.log = log;
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		int status = 0;
		int delay = LED_BLINK_INTERVAL_MILLIS;
		try {
			status = control.getStatus();

			if ((status & 0x01) == 0) {
				control.setLEDGreen(true);
				control.setLEDRed(false);
				Thread.sleep(delay);
				control.setLEDGreen(false);
				control.setLEDGreen(false);
			} else {
				control.setLEDGreen(false);
				control.setLEDRed(true);
				Thread.sleep(delay);
				control.setLEDGreen(false);
				control.setLEDRed(false);
			}
		} catch (IOException e) {
			log.log(LogService.LOG_ERROR, "FIXLEDStatusTask: Unable to query gps control on slot " + control.getSlotId(), e);
		} catch (InterruptedException e) {
			// Ignore interruption
		}
	}
}
