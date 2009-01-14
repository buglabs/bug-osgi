package com.buglabs.bug.module.gps.pub;

import com.buglabs.nmea2.AbstractNMEASentence;

/**
 * Register this service to recieve NMEA sentence events.
 * @author kgilmer
 *
 */
public interface INMEASentenceSubscriber {
	/**
	 * @param sentence
	 */
	public void sentenceReceived(AbstractNMEASentence sentence);
}
