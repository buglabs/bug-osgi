package com.buglabs.bug.accelerometer.pub;

/**
 * Accelerometer Configuration
 * 
 * @author Angel Roman
 */
public class AccelerometerConfiguration {
	
	public int read_queue_size;
	public int read_queue_threshold;
	public short delay; 
	public byte delay_resolution; 
	public byte delay_mode;
	public byte run;
	public byte sensitivity;
	
	public int getReadQueueSize() {
		return read_queue_size;
	}

	
	public void setReadQueueSize(int read_queue_size) {
		this.read_queue_size = read_queue_size;
	}

	/**
	 * Number of 6-byte sample sets to queue
	 */
	public int getReadQueueThreshold() {
		return read_queue_threshold;
	}

	/**
	 * Number of 6-byte sample sets to queue
	 */
	public void setReadQueueThreshold(int read_queue_threshold) {
		this.read_queue_threshold = read_queue_threshold;
	}

	/**
	 * Timer ticks between the start of 2 sucessive sample sets.
	 */
	public short getDelay() {
		return delay;
	}

	/**
	 * Timer ticks between the start of 2 sucessive sample sets.
	 */
	public void setDelay(short delay) {
		this.delay = delay;
	}

	/**
	 * Timer tick resolution
	 * 
	 * 1 = 1   uSec
	 * 2 = 8   uSec
	 * 3 = 64  uSec
	 * 4 = 256 uSec
	 * 5 = 1024 uSec
	 */
	public byte getDelayResolution() {
		return delay_resolution;
	}

	/**
	 * Timer tick resolution
	 * 
	 * @param delay_resolution
	 * 1 = 1   uSec
	 * 2 = 8   uSec
	 * 3 = 64  uSec
	 * 4 = 256 uSec
	 * 5 = 1024 uSec
	 */
	public void setDelayResolution(byte delay_resolution) {
		this.delay_resolution = delay_resolution;
	}

	/**
	 * 0 = default delay = 5 millisecond
	 * 1 = configured delay
	 */
	public byte getDelayMode() {
		return delay_mode;
	}


	/**
	 * 0 = default delay = 5 millisecond
	 * 1 = configured delay
	 */
	public void setDelayMode(byte delay_mode) {
		this.delay_mode = delay_mode;
	}

	/**
	 * 0 = sampling disabled
	 * 1 = sampling enabled
	 */
	public byte getRun() {
		return run;
	}

	/**
	 * 0 = sampling disabled
	 * 1 = sampling enabled
	 */
	public void setRun(byte run) {
		this.run = run;
	}

	/**
	 * 0 = 2.5G, 421 mV/G
	 * 1 = 3.3G, 316 mV/G
	 * 2 = 6.7G, 158 mV/G
	 * 3 = 10G,  105 mV/G
	 */
	public byte getSensitivity() {
		return sensitivity;
	}

	/**
	 * 0 = 2.5G, 421 mV/G
	 * 1 = 3.3G, 316 mV/G
	 * 2 = 6.7G, 158 mV/G
	 * 3 = 10G,  105 mV/G
	 */
	public void setSensitivity(byte sensitivity) {
		this.sensitivity = sensitivity;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof AccelerometerConfiguration) {
			AccelerometerConfiguration config = (AccelerometerConfiguration) obj;
			if(config.getDelay() != delay)
				return false;
			if(config.getDelayMode() != delay_mode)
				return false;
			if(config.getDelayResolution() != delay_resolution)
				return false;
			if(config.getReadQueueSize() != read_queue_size)
				return false;
			if(config.getReadQueueThreshold() != read_queue_threshold)
				return false;
			if(config.getRun() != run)
				return false;
			if(config.getSensitivity() != sensitivity)
				return false;
			return true;
		}
		
		return super.equals(obj);
	}
}
