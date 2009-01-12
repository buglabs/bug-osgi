package com.buglabs.bug.module.gps.pub;

import java.io.IOException;
import java.io.InputStream;

/**
 * NMEA raw data feed from GPS device.
 * @author armoan
 *
 */
public interface INMEARawFeed {
	/**
	 * @return NMEA data as a stream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException;
}
