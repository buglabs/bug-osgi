package com.buglabs.nmea2;

import com.buglabs.nmea.Time;
import com.buglabs.nmea.sentences.NMEAParserException;

/**
 * Stores GGA type NMEA sentence data
 * @author kgilmer
 *
 */
public final class GGA extends AbstractNMEASentence {

	/**
	 * Time                                    170834          17:08:34 Z
	    Latitude                                4124.8963, N    41d 24.8963' N or 41d 24' 54" N
		Longitude                               08151.6838, W   81d 51.6838' W or 81d 51' 41" W
		Fix Quality:
		Number of Satellites                    05              5 Satellites are in view
		Horizontal Dilution of Precision (HDOP) 1.5             Relative accuracy of horizontal position
		Altitude                                280.2, M        280.2 meters above mean sea level
		Height of geoid above                   WGS84 ellipsoid         -34.0, M        -34.0 meters
		Time since last DGPS update             blank           No last update
		DGPS reference station id               blank           No station id
		Checksum                                *75             Used by program to check for transmission errors
	 */
	
	private Time time;
	private String latitude;
	private String longitude;
	private int fixQuality;
	private int numOfSatellites;
	private float precision;
	private double altitude;
	private char unitOfAltitude;
	private double geoidHeight;
	private char unitOfGeoidHeight;
	
	protected GGA(String sentence) {
		super(sentence);
	}
	
	protected void initialize() {
		precision = (float) -1.0;
	}

	protected void parseField(int index, String field, String fields[]) {
		try {
				switch (index) {
				case 1:
					time = new Time(field);
					break;
				case 2:
					latitude = field + "," + fields[index + 1];
					break;
				case 4:
					longitude = field + "," + fields[index+ 1];
					break;	
				case 6:
					fixQuality = Integer.parseInt(field);
					break;
				case 7:
					numOfSatellites = Integer.parseInt(field);
					break;
				case 8:
					precision = Float.parseFloat(field);
					break;
				case 9:
					altitude = Double.parseDouble(field);
					break;
				case 10:
					unitOfAltitude = field.charAt(0);
					break;
				case 11:
					geoidHeight = Double.parseDouble(field);
					break;
				case 12:
					unitOfGeoidHeight = field.charAt(0);
					break;
				case 14:
					break;
				default:
					break;
				}
			
		} catch (NumberFormatException e) {
			throw new NMEAParserException("Unable to parse sentence.", e);
		}
	}

	public Time getTime() {
		return time;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public int getFixQuality() {
		return fixQuality;
	}

	public int getNumOfSatellites() {
		return numOfSatellites;
	}

	public float getPrecision() {
		return precision;
	}

	public double getAltitude() {
		return altitude;
	}

	public char getUnitOfAltitude() {
		return unitOfAltitude;
	}

	public double getGeoidHeight() {
		return geoidHeight;
	}

	public char getUnitOfGeoidHeight() {
		return unitOfGeoidHeight;
	}

	protected void validate() {
		
	}

	public String toString() {
		return  checksum + "  " +  time + "  " +  latitude + "  " +  longitude + "  " +  fixQuality + "  " +  numOfSatellites + "  " +  precision + "  " +  altitude + "  " +  unitOfAltitude + "  " +  geoidHeight + "  " +  unitOfGeoidHeight;
	}
}
