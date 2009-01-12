package com.buglabs.bug.module.gps.pub;

import org.osgi.util.position.Position;

/**
 * This interface describes location services.
 * 
 * @author kgilmer
 * 
 */
public interface IPositionProvider {
	/**
	 * @return current position as Position object or null if unable to determine position.
	 */
	public Position getPosition();

	/**
	 * @return current position as LatLon object or null if unable to determine Lat/Long.
	 */
	public LatLon getLatitudeLongitude();
}
