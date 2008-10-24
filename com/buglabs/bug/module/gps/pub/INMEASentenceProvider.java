package com.buglabs.bug.module.gps.pub;

import com.buglabs.nmea.sentences.RMC;

/**
 * Provides access methods to NMEA data from
 * gps devices.
 * 
 * @author Angel Roman
 */
public interface INMEASentenceProvider {
	/**
	 * Provides the latest RMC sentence read from the GPS Device.
	 * 
	 * @return RMC sentence object
	 */
	public RMC getRMC();
}
