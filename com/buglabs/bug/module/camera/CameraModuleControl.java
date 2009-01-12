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
package com.buglabs.bug.module.camera;

import java.io.IOException;

import com.buglabs.bug.jni.camera.CameraControl;
import com.buglabs.bug.module.camera.pub.ICameraModuleControl;
import com.buglabs.module.IModuleLEDController;

public class CameraModuleControl implements ICameraModuleControl, IModuleLEDController {

	private CameraControl cc;

	public CameraModuleControl(CameraControl cameraControl) {
		this.cc = cameraControl;
	}

	public int getSelectedCamera() throws IOException {
		int result = cc.ioctl_BMI_CAM_GET_SELECTED();

		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_GET_SELECTED failed");
		}

		return result;
	}

	public int setSelectedCamera(int slot) throws IOException {
		int result = cc.ioctl_BMI_CAM_SELECT(slot);

		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_SELECT failed");
		}

		return result;
	}

	public int setFlashBeamIntensity(int intensity) throws IOException {

		int result;

		if (intensity > 0) {
			result = cc.ioctl_BMI_CAM_FLASH_HIGH_BEAM();
		} else {
			result = cc.ioctl_BMI_CAM_FLASH_LOW_BEAM();
		}

		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_GET_SELECTED failed");
		}

		return result;
	}

	public int setLEDFlashOff() throws IOException {
		int result = cc.ioctl_BMI_CAM_FLASH_LED_OFF();

		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_FLASH_LED_OFF failed");
		}

		return result;
	}

	public int setLEDFlashOn() throws IOException {
		int result = cc.ioctl_BMI_CAM_FLASH_LED_ON();

		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_FLASH_LED_OFF failed");
		}

		return result;
	}

	public int setLEDFlash(boolean state) throws IOException {
		int result = -1;
		if (state) {
			result = cc.ioctl_BMI_CAM_FLASH_LED_ON();
			if (result < 0) {
				throw new IOException("ioctl BMI_CAM_FLASH_LED_ON failed");
			}
		} else {
			result = cc.ioctl_BMI_CAM_FLASH_LED_OFF();
			if (result < 0) {
				throw new IOException("ioctl BMI_CAM_FLASH_LED_OFF failed");
			}
		}

		return result;
	}

	public int LEDGreenOff() throws IOException {
		int result = -1;

		result = cc.ioctl_BMI_CAM_GREEN_LED_OFF();

		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_GREEN_LED_OFF failed");
		}

		return result;

	}

	public int LEDGreenOn() throws IOException {
		int result = -1;

		result = cc.ioctl_BMI_CAM_GREEN_LED_ON();

		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_GREEN_LED_ON failed");
		}

		return result;
	}

	public int LEDRedOff() throws IOException {
		int result = -1;

		result = cc.ioctl_BMI_CAM_RED_LED_OFF();

		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_RED_LED_OFF failed");
		}

		return result;
	}

	public int LEDRedOn() throws IOException {
		int result = -1;

		result = cc.ioctl_BMI_CAM_RED_LED_ON();

		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_GREEN_LED_ON failed");
		}

		return result;
	}

	public int setLEDGreen(boolean state) throws IOException {
		if (state) {
			return LEDGreenOn();
		} else {
			return LEDGreenOff();
		}
	}

	public int setLEDRed(boolean state) throws IOException {
		if (state) {
			return LEDRedOn();
		} else {
			return LEDRedOff();
		}
	}
}
