package com.buglabs.bug.jni.motion;

import com.buglabs.bug.jni.common.CharDevice;

public class Motion extends CharDevice {

	static {
		System.loadLibrary("Motion");
	}

	public static final byte BMI_MOTION_DETECT_STATUS         = (1<<3);
	public static final byte BMI_MOTION_DETECT_LATCHED_STATUS = (1<<2);
	public static final byte BMI_MOTION_DETECT_DELTA          = (1<<1);
	public static final byte BMI_MOTION_DETECT_ENABLED        = (1<<0);
	
	/**
	 * 
	 * @return The value of the IOX register. -1 if ioctl failed.
	 */
	public native int ioctl_BMI_MDACC_MOTION_DETECTOR_GET_STATUS();
	
	/**
	 * Start motion capture
	 * 
	 * @return -1 if ioctl failed.
	 */
	public native int ioctl_BMI_MDACC_MOTION_DETECTOR_RUN();
	
	/**
	 * Stops motion capture
	 * 
	 * @return -1 if ioctl failed.
	 */
	public native int ioctl_BMI_MDACC_MOTION_DETECTOR_STOP();
}
