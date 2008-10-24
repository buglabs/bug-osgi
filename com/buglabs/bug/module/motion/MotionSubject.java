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
			while(!isInterrupted() && (read = (byte) motionIs.read()) != -1) {
				byte expected = Motion.BMI_MOTION_DETECT_ENABLED | 
								Motion.BMI_MOTION_DETECT_DELTA | 
								Motion.BMI_MOTION_DETECT_LATCHED_STATUS | 
								Motion.BMI_MOTION_DETECT_STATUS;
				
				if( read == expected) {
					notifyObservers();
				}
			}
		} catch (IOException e) {
			// TODO Log this
			//e.printStackTrace();
		} finally {
			if(motionIs != null) {
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
		synchronized(observers) {
			Iterator iter = observers.iterator();
			while(iter.hasNext()) {
				IMotionObserver obs = (IMotionObserver) iter.next();
				obs.motionDetected();
			}
		}
	}

	public void register(IMotionObserver obs) {
		synchronized(observers) {
			observers.add(obs);
		}
	}

	public void unregister(IMotionObserver obs) {
		synchronized(observers) {
			observers.remove(obs);
		}
	}
}
