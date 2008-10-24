package com.buglabs.bug.accelerometer.pub;

import java.io.IOException;


public interface IAccelerometerSampleProvider {
	/**
	 * Reads a single sample from the accelerometer.
	 *
	 * @return The latest sample from the accelerometer. 
	 */
	public AccelerometerSample readSample() throws IOException;
}
