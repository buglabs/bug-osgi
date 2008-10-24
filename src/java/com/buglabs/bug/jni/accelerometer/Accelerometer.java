package com.buglabs.bug.jni.accelerometer;

import com.buglabs.bug.accelerometer.pub.AccelerometerConfiguration;
import com.buglabs.bug.jni.common.CharDevice;

public class Accelerometer extends CharDevice {
	
	static {
		System.loadLibrary("Accelerometer");
	}

	public native int ioctl_BMI_MDACC_ACCELEROMETER_RUN();
	public native int ioctl_BMI_MDACC_ACCELEROMETER_STOP();
	public native int ioctl_BMI_MDACC_ACCELEROMETER_SET_CONFIG(AccelerometerConfiguration config);
	public native AccelerometerConfiguration ioctl_BMI_MDACC_ACCELEROMETER_GET_CONFIG();
}
