package com.buglabs.bug.accelerometer.pub;

import java.io.IOException;
import java.io.InputStream;


/**
 * 
 * @author Angel Roman
 */
public abstract class AccelerometerSampleStream extends InputStream {

	protected InputStream is;
	
	/**
	 * AccelerometerSampleInputStream constructor.
	 * 
	 * @param is An Accelerometer Raw Feed InputStream.
	 * @param config The accelerometer configuration
	 */
	public AccelerometerSampleStream(InputStream is) {
		this.is = is;
	}
	
	public void close() throws IOException {
		super.close();
		is.close();
	}
	public int read() throws IOException {
		return is.read();
	}
	
	public int read(byte[] b) throws IOException {
		return is.read(b);
	}
	
	public abstract AccelerometerSample readSample() throws IOException;

}
