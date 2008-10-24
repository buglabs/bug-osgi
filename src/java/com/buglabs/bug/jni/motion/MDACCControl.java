package com.buglabs.bug.jni.motion;

import com.buglabs.bug.jni.common.CharDevice;

public class MDACCControl extends CharDevice {
	public native int ioctl_BMI_MDACC_CTL_RED_LED_OFF();
	public native int ioctl_BMI_MDACC_CTL_RED_LED_ON();
	public native int ioctl_BMI_MDACC_CTL_GREEN_LED_OFF();
	public native int ioctl_BMI_MDACC_CTL_GREEN_LED_ON();
}
