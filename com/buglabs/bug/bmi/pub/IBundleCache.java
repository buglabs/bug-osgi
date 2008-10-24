package com.buglabs.bug.bmi.pub;

import org.osgi.framework.Bundle;

/**
 * A bundle cache is responsible for providing Bundles to be executed.
 * 
 * @author ken
 * 
 */
public interface IBundleCache {

	public abstract void clear();

	/**
	 * @param bundleId
	 * @return file URLs to given bundle.
	 */
	public abstract String[] get(String bundleId);

	public abstract String put(Bundle bundle);

	public abstract void remove(Bundle bundle);

}