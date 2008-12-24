package com.buglabs.application;

/**
 * Extends RunnableWithServices and provides more granular service events.
 * @author kgilmer
 *
 */
public interface ServiceChangeListener extends RunnableWithServices {
	/**
	 * Is called when a tracked service becomes available.  
	 * Not guaranteed to be unique.  
	 * Client must handle logic to track multiple instances of service.
	 * 
	 * @param service
	 */
	public void serviceAvailable(Object service);
}
