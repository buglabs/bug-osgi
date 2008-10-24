package com.buglabs.bug.accelerometer.pub;

/**
 * Clients may implement this to receive notifications of
 *  
 * @author Angel Roman
 */
public interface IAccelerometerConfigurationListener {
	public void configurationChanged(AccelerometerConfiguration config);
}
