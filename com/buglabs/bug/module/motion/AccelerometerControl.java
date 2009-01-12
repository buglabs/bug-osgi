/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
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
			while (iter.hasNext()) {
				IAccelerometerConfigurationListener cl = (IAccelerometerConfigurationListener) iter.next();
				cl.configurationChanged(config);
			}
		}
	}

	public void registerListener(IAccelerometerConfigurationListener cl) {
		synchronized (listeners) {
			listeners.add(cl);
		}
	}

	public void unregisterListener(IAccelerometerConfigurationListener cl) {
		synchronized (listeners) {
			listeners.remove(cl);
		}
	}
}
