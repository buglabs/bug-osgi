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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.buglabs.bug.jni.motion.Motion;
import com.buglabs.bug.module.motion.pub.IMotionObserver;
import com.buglabs.bug.module.motion.pub.IMotionSubject;

public class MotionSubject extends Thread implements IMotionSubject {

	InputStream motionIs;
	ArrayList observers;

	MotionSubject(InputStream gpsIs) {
		this.motionIs = gpsIs;
		observers = new ArrayList();
	}

	public void run() {
		byte read = 0;

		try {
			while (!isInterrupted() && (read = (byte) motionIs.read()) != -1) {
				byte expected = Motion.BMI_MOTION_DETECT_ENABLED | Motion.BMI_MOTION_DETECT_DELTA | Motion.BMI_MOTION_DETECT_LATCHED_STATUS | Motion.BMI_MOTION_DETECT_STATUS;

				if (read == expected) {
					notifyObservers();
				}
			}
		} catch (IOException e) {
			// TODO Log this
			// e.printStackTrace();
		} finally {
			if (motionIs != null) {
				try {
					motionIs.close();
				} catch (IOException e) {
					// TODO Log this
					e.printStackTrace();
				}
			}
		}
	}

	public void notifyObservers() {
		synchronized (observers) {
			Iterator iter = observers.iterator();
			while (iter.hasNext()) {
				IMotionObserver obs = (IMotionObserver) iter.next();
				obs.motionDetected();
			}
		}
	}

	public void register(IMotionObserver obs) {
		synchronized (observers) {
			observers.add(obs);
		}
	}

	public void unregister(IMotionObserver obs) {
		synchronized (observers) {
			observers.remove(obs);
		}
	}
}
