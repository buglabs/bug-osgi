package com.buglabs.bug.program.pub;

import java.io.IOException;

import org.osgi.framework.Bundle;

/**
 * This manager starts user applications. It's designed to be tolerant of
 * applications that fail to start or have other serious problems. The state for
 * each user application is stored in the Configuration Manager.
 * 
 * @deprecated This service will become internal to com.buglabs.bug.program.
 *             Clients should not implement.
 * 
 * @author kgilmer
 * 
 */
public interface IUserAppManager {

	public abstract void addApplication(String filePath) throws IOException;

	public abstract void run();

	/**
	 * Returns an array of bundles that have been installed since the last time
	 * <code>getInstalledBundles()</code> was run. Clears the list afterwards.
	 * 
	 * @return
	 */
	public abstract Bundle[] getInstalledBundles();

	public abstract void shutdown();

	/**
	 * Clears any knowledge of existing user applications.
	 */
	public abstract void clearState();

	/**
	 * Removes an application from the User App Storage location.
	 * 
	 * @param location
	 * @throws IOException
	 */
	public abstract void removeApplication(String location) throws IOException;
}