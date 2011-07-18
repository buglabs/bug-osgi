/*******************************************************************************
 * Copyright (c) 2010 Bug Labs, Inc.
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
package com.buglabs.bug.module.video.pub;

//import java.io.IOException;

import com.buglabs.bug.dragonfly.module.IModuleControl;

/**
 * The interface that controls functions of the Video module.
 */
public interface IVideoModuleControl extends IModuleControl {
	/**
	 * Get the currently selected video resolution.
	 * @return resolution, e.g. "1280x1024"
	 * @deprecated use getDisplaySize();
	 */
	public String getResolution();

	/**
	 * @returns a string of valid monitor resolutions.
	 */
	public String[] getMonitorResolutions();
	
	/**
	 * @return true if video module is currently in VGA mode
	 */
	public boolean isVGA();
	
	/**
	 * @return true if video module is currently in DVI mode
	 */
	public boolean isDVI();


	/**
	 * @return true if video module is currently disabled
	 */
	public boolean isOff();
	
	/**
	 * Switch video mode to VGA.
	 * @return true if request was successful.
	 */
	public boolean setVGA();
	
	/**
	 * Switch video mode to DVI.
	 * @return true if request was successful.
	 */
	public boolean setDVI();

	/**
	 * Switch video mode off
	 * @return true if request was successful.
	 */
	public boolean setOff();

	/**
	* Cause the video module to scan for a monitor, and select the best
	* match monitor for it's output mode.
	* @return true of there was no error during scanning and setting
	*/
	public boolean setAutoDetect();

}
