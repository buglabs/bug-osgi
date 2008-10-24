package com.buglabs.bug.module.lcd.accelerometer;

import java.io.IOException;

import com.buglabs.bug.accelerometer.pub.AccelerometerSample;
import com.buglabs.bug.accelerometer.pub.AccelerometerSampleStream;
import com.buglabs.bug.accelerometer.pub.IAccelerometerSampleFeed;
import com.buglabs.bug.accelerometer.pub.IAccelerometerSampleProvider;

public class LCDAccelerometerSampleProvider implements
		IAccelerometerSampleProvider {

	private IAccelerometerSampleFeed prov;

	public LCDAccelerometerSampleProvider(IAccelerometerSampleFeed prov) {
		this.prov = prov;
	}
	
	public AccelerometerSample readSample() throws IOException {
		AccelerometerSampleStream is = prov.getSampleInputStream();
		AccelerometerSample sample = is.readSample();
		is.close();
		
		return sample;
	}
}
