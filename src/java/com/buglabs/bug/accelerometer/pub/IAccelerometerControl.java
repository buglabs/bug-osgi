package com.buglabs.bug.accelerometer.pub;


public interface IAccelerometerControl {
	public void setConfiguration(AccelerometerConfiguration config);
	public AccelerometerConfiguration  getConfiguration();
	public void registerListener(IAccelerometerConfigurationListener cl);
	public void unregisterListener(IAccelerometerConfigurationListener cl);
}
