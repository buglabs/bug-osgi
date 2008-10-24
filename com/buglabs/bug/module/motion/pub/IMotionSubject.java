package com.buglabs.bug.module.motion.pub;

public interface IMotionSubject {
	public void register(IMotionObserver obs);
	public void unregister(IMotionObserver obs);
	public void notifyObservers();
}
