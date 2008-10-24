package com.buglabs.bug.module.motion;

import java.io.InputStream;

import com.buglabs.bug.accelerometer.pub.AccelerometerSampleStream;
import com.buglabs.bug.accelerometer.pub.IAccelerometerRawFeed;
import com.buglabs.bug.accelerometer.pub.IAccelerometerSampleFeed;
import com.buglabs.util.LogServiceUtil;
import com.buglabs.util.StreamMultiplexer;

public class AccelerometerRawFeed extends StreamMultiplexer implements IAccelerometerRawFeed, IAccelerometerSampleFeed {
	
	private AccelerometerControl control;

	public AccelerometerRawFeed(InputStream is, AccelerometerControl control) {
		super(is, 6, 50);
		setName("AccelerometerRawFeed");
		setLogService(LogServiceUtil.getLogService(Activator.getInstance().getBundleContext()));
		this.control = control;
	}

	public AccelerometerSampleStream getSampleInputStream() {
		return new MotionAccelerometerSampleStream(getInputStream(), control);
	}
}
