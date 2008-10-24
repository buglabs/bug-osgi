package com.buglabs.bug.module.lcd.accelerometer;

import java.io.InputStream;

import com.buglabs.bug.accelerometer.pub.AccelerometerSampleStream;
import com.buglabs.bug.accelerometer.pub.IAccelerometerRawFeed;
import com.buglabs.bug.accelerometer.pub.IAccelerometerSampleFeed;
import com.buglabs.bug.module.lcd.Activator;
import com.buglabs.util.LogServiceUtil;
import com.buglabs.util.StreamMultiplexer;

public class LCDAccelerometerInputStreamProvider extends StreamMultiplexer implements IAccelerometerSampleFeed, IAccelerometerRawFeed {

	public LCDAccelerometerInputStreamProvider(InputStream is) {
		super(is, 6, 50);
		setName("LCDAccelerometer");
		setLogService(LogServiceUtil.getLogService(Activator.getInstance().getBundleContext()));
	}

	public AccelerometerSampleStream getSampleInputStream() {
		return new LCDAccelerometerSampleInputStream(getInputStream());
	}
}
