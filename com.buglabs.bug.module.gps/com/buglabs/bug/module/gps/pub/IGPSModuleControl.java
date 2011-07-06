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
package com.buglabs.bug.module.gps.pub;

import java.io.IOException;

import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.bug.dragonfly.module.IModuleLEDController;

/**
 * This is the module control interface for the GPS module.
 *
 */
public interface IGPSModuleControl extends IModuleControl, IModuleLEDController {

	public static final int STATUS_PASSIVE_ANTENNA = 0x40;
	public static final int STATUS_ACTIVE_ANTENNA = 0x80;

	/**
	 * bit 0: GPS FIX Active Low. 
	 * bit 1: Overcurrent condition caused by the
	 * active antenna path. Active Low. 
	 * bit 2: Output to wake up device from
	 * sleep after push_to_fix. Active High. 
	 * bit 3: Input to download firmware
	 * to flash. 
	 * bit 4: Unused 
	 * bit 5: Unused 
	 * bit 7, 6: 0, 1 Passtive Antenna
	 * (External Antenna) 1, 0 Active Antenna (Internal Antenna)
	 * 
	 * @return the value of the IOX register
	 */
	public int getStatus() throws IOException;

	/**
	 * Use the active (external) antenna of the gps device.
	 * 
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public int setActiveAntenna() throws IOException;

	/**
	 * Use the passive (internal) antenna of the gps device.
	 * 
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public int setPassiveAntenna() throws IOException;
}
