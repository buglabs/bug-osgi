package com.buglabs.bug.module.motion;

import java.util.ArrayList;
import java.util.Iterator;

import com.buglabs.bug.accelerometer.pub.AccelerometerConfiguration;
import com.buglabs.bug.accelerometer.pub.IAccelerometerConfigurationListener;
import com.buglabs.bug.accelerometer.pub.IAccelerometerControl;
import com.buglabs.bug.jni.accelerometer.Accelerometer;

public class AccelerometerControl implements IAccelerometerControl {

	private Accelerometer acc;
	private ArrayList listeners;

	public AccelerometerControl(Accelerometer acc) {
		this.acc = acc;
		listeners = new ArrayList();
	}

	public AccelerometerConfiguration getConfiguration() {
		return acc.ioctl_BMI_MDACC_ACCELEROMETER_GET_CONFIG();
	}

	public void setConfiguration(AccelerometerConfiguration config) {
		acc.ioctl_BMI_MDACC_ACCELEROMETER_SET_CONFIG(config);

		synchronized (listeners) {
			Iterator iter = listeners.iterator();
			while(iter.hasNext()) {
				IAccelerometerConfigurationListener cl = (IAccelerometerConfigurationListener) iter.next();
				cl.configurationChanged(config);
			}
		}
	}

	public void registerListener(IAccelerometerConfigurationListener cl) {
		synchronized(listeners) {
			listeners.add(cl);	
		}
	}

	public void unregisterListener(IAccelerometerConfigurationListener cl) {
		synchronized (listeners) {
			listeners.remove(cl);	
		}
	}
}
