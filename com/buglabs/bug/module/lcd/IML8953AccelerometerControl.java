package com.buglabs.bug.module.lcd;

import java.io.IOException;

import com.buglabs.bug.accelerometer.pub.AccelerometerSample;
import com.buglabs.bug.accelerometer.pub.IAccelerometerSampleProvider;
import com.buglabs.bug.module.lcd.pub.ML8953Accelerometer;

public class IML8953AccelerometerControl implements ML8953Accelerometer, IAccelerometerSampleProvider {
	
	
	
	private ML8953Device device;

	public IML8953AccelerometerControl() {
		device  = ML8953Device.getInstance();
	}
	
	public short readX() throws IOException {
		String position = device.getPosition();
		position = position.substring(position.indexOf('(')+1,position.indexOf(','));
		return Short.parseShort(position.trim());
		//return sensorDevice.accelReadX();
	}

	public short readY() throws IOException {
		String position = device.getPosition().trim();
		position = position.substring(position.indexOf(',')+1);
		position = position.substring(0,position.indexOf(','));
		return Short.parseShort(position.trim());
	}

	public short readZ() throws IOException {
		String position = device.getPosition();
		position = position.substring(position.lastIndexOf(',')+1,position.indexOf(')'));
		return Short.parseShort(position.trim());
	}

	public byte readRegister(byte address) throws IOException {
		//sysfs magic
		return -1;
		//return sensorDevice.accelReadRegister(address);
	}
	
	public void writeRegister(byte address, byte value) throws IOException {
		//sysfs magic  ???

		//sensorDevice.accelWriteRegister(address, value);
		
		// we just changed the data format, so we need
		// to update the scale factor to match
/*		if (address == IADXL134XAccelerometer.REG_DATA_FORMAT)
		{
			updateDataFormat(value);
		}
*/	}

	/**
	 * scale factor used to convert raw reading to g's
	 * Specifically this is the mg/LSB scaling factor.
	 */

	




	private float convert2gs(final short raw)
	{
		return raw / 1000F;
	}
	
	//
	// Accelerometer Sample Provider
	//
	
	// TODO: should we be doing the -1 on Y or is something else bassackwards?
	public AccelerometerSample readSample() throws IOException {
		return new AccelerometerSample(
				convert2gs(readX()),
				convert2gs(readY()) * -1F, // demotastic/puck wrong way otherwise
				convert2gs(readZ()));
	}


	public void accelInterruptWaitINT1() {
		//sensorDevice.accelInterruptWaitINT1();
		//sysfs magic?
	}	

	public void accelInterruptWaitINT2() {
		//sensorDevice.accelInterruptWaitINT2();
		//sysfs magic?
		
	}	
}
