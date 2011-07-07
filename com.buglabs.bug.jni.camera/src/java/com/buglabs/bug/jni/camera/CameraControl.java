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
package com.buglabs.bug.jni.camera;

/**
 * Controller for the BUG 2.0 Camera Module.
 *
 */
public class CameraControl {

	/**
	 * Flash ligh beam
	 * @return 0 on success
	 */
	public native int ioctl_BMI_CAM_FLASH_HIGH_BEAM();

	/**
	 * Flash low beam
	 * @return 0 on success
	 */
	public native int ioctl_BMI_CAM_FLASH_LOW_BEAM();

	/**
	 * Turn flash off
	 * @return 0 on success
	 */
	public native int ioctl_BMI_CAM_FLASH_LED_OFF();

	/**
	 * Turn flash on
	 * @return 0 on success
	 */
	public native int ioctl_BMI_CAM_FLASH_LED_ON();

	/**
	 * Turn red LED off
	 * @return 0 on success
	 */
	public native int ioctl_BMI_CAM_RED_LED_OFF();

	/**
	 * Turn red LED on
	 * @return 0 on success
	 */
	public native int ioctl_BMI_CAM_RED_LED_ON();

	public native int ioctl_BMI_CAM_GREEN_LED_OFF();

	public native int ioctl_BMI_CAM_GREEN_LED_ON();

	/**
	 * Selects a camera.
	 * 
	 * @param slot
	 *            The slot of the camera to be selected.
	 * @deprecated
	 * @return
	 */
	public native int ioctl_BMI_CAM_SELECT(int slot);

	/**
	 * 
	 * @return the number of the slot the selected camera is in
	 */
	public native int ioctl_BMI_CAM_GET_SELECTED();
	
	/**
	 * Suspend camera module
	 * @return  0 on success
	 */
	public native int ioctl_BMI_CAM_SUSPEND();
	
	/**
	 * Resume camera module
	 * @return 0 on success
	 */
	public native int ioctl_BMI_CAM_RESUME();

	// for ICamera2ModuleControl
	/**
	 * TODO: Document
	 * @return 0 on success
	 */
	public native int getTestPattern();
	
	/**
	 * TODO: Document
	 * @param testPattern
	 * @return  0 on success
	 */
	public native int setTestPattern(int testPattern);
	
	/**
	 * TODO: Document
	 * @return
	 */
	public native int getColorEffects();
	
	/**
	 * TODO: Document
	 * @param colorEffects
	 * @return
	 */
	public native int setColorEffects(int colorEffects);
	
	/**
	 * TODO: Document
	 * @return
	 */
	public native int getVerticalFlip();
	
	/**
	 * TODO: Document
	 * @param verticalFlip
	 * @return
	 */
	public native int setVerticalFlip(int verticalFlip);
	
	/**
	 * TODO: Document
	 * @return
	 */
	public native int getHorizontalMirror();
	
	/**
	 * TODO: Document
	 * @param horizontalMirror
	 * @return
	 */
	public native int setHorizontalMirror(int horizontalMirror);
	
	/**
	 * TODO: Document
	 * @return
	 */
	public native int getExposureLevel();
	
	/**
	 * TODO: Document
	 * @param exposureLevel
	 * @return
	 */
	public native int setExposureLevel(int exposureLevel);
}
