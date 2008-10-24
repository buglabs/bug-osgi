package com.buglabs.bug.jni.camera;

import com.buglabs.bug.jni.common.CharDevice;

public class CameraControl extends CharDevice {
	
	public native int ioctl_BMI_CAM_FLASH_HIGH_BEAM();
	public native int ioctl_BMI_CAM_FLASH_LOW_BEAM();
	public native int ioctl_BMI_CAM_FLASH_LED_OFF();
	public native int ioctl_BMI_CAM_FLASH_LED_ON();
	public native int ioctl_BMI_CAM_RED_LED_OFF();
	public native int ioctl_BMI_CAM_RED_LED_ON();
	public native int ioctl_BMI_CAM_GREEN_LED_OFF();
	public native int ioctl_BMI_CAM_GREEN_LED_ON();
	
	/**
	 * Selects a camera.
	 * 
	 * @param slot The slot of the camera to be selected.
	 * @return
	 */
	public native int ioctl_BMI_CAM_SELECT(int slot);
	public native int ioctl_BMI_CAM_GET_SELECTED();

}
