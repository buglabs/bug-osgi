package com.buglabs.bug.module.motion.pub;


/**
 * Implementors will register an ImotionObserver with <code>IMotionSubject</code> to be
 * notified when motion events occur.
 * 
 * @author aroman
 * 
 */
public interface IMotionObserver {

	public void motionDetected();
}
