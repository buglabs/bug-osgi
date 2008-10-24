package com.buglabs.bug.module.gps.pub;

import java.io.Serializable;

/**
 * A class that describes position in longitude and latitude.
 * 
 * @author aroman
 * 
 */
public class LatLon implements Serializable {
	private static final long serialVersionUID = 6318765018123280457L;

	public LatLon(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public double latitude;

	public double longitude;
}
