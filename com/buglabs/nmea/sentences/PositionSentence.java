package com.buglabs.nmea.sentences;

import com.buglabs.nmea.DegreesMinutesSeconds;

public abstract class PositionSentence extends NMEASentence {

	String latitude;
	String longitude;
	
	public PositionSentence(String type) {
		super(type);
	}
	
	public Object parse(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLatitude() {
		return latitude;
	}

	protected void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public DegreesMinutesSeconds getLatitudeAsDMS() {
		return new DegreesMinutesSeconds(getLatitude());
	}
	
	public DegreesMinutesSeconds getLongitudeAsDMS() {
		return new DegreesMinutesSeconds(getLongitude());
	}
	
	protected void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
