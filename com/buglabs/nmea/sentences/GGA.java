package com.buglabs.nmea.sentences;

import com.buglabs.nmea.StringUtil;
import com.buglabs.nmea.Time;

public class GGA extends PositionSentence {

	private Time time;
	private int	fixQuality;
	private int numOfSatellites;
	private float precision;
	private double altitude;
	private char unitOfAltitude;
	private char unitOfGeoidHeight;
	private double geoidHeight;
	private String timeLastUpdate;
	private String referenceStationID;

	public GGA() {
		super("$GPGGA");
	}

	public Object parse(String sentence) {

		String[] splitSentence =  StringUtil.split(sentence, "*");

		if(splitSentence.length > 1){
			setChecksum(splitSentence[1]);
		}

		try {
			String[] fields = StringUtil.split(splitSentence[0], ",");

			for(int i = 0; i < fields.length; ++i) {
				switch(i) {
				case 1:
					time = new Time(fields[i]);
					break;
				case 2:
				case 3:
					setLatitude(fields[i] + "," + fields[i+1]);
					i++;
					break;
				case 4:
				case 5:
					setLongitude(fields[i] + "," + fields[i+1]);
					i++;
					break;
				case 6:
					fixQuality = Integer.parseInt(fields[i]);
					break;
				case 7:
					numOfSatellites = Integer.parseInt(fields[i]);
					break;
				case 8:
					if(fields[i].length() == 0) {
						precision = -1;
					} else {
						precision = Float.parseFloat(fields[i]);
					}
					break;
				case 9:
				case 10:
					altitude = Double.parseDouble(fields[i]);
					unitOfAltitude = fields[i+1].charAt(0);
					++i;
					break;
				case 11:
				case 12:
					if(fields[i].length() == 0)
					{
						++i;
						break;
					}
					geoidHeight = Double.parseDouble(fields[i]);
					unitOfGeoidHeight = fields[i+1].charAt(0);
					++i;
					break;
				case 13:
					i++;
					break;
				case 14:
					i++;
					break;
				default:
					break;
				}
			}
		} catch (NumberFormatException e) {
			
			NMEAParserException exception = new NMEAParserException("Unable to parse sentence: " + sentence, e);
			
			throw exception;
		}
		
		return this;
	}

	public double getAltitude() {
		return altitude;
	}

	public int getFixQuality() {
		return fixQuality;
	}

	public double getGeoidHeight() {
		return geoidHeight;
	}

	public int getNumOfSatellites() {
		return numOfSatellites;
	}

	public float getPrecision() {
		return precision;
	}

	public String getReferenceStationID() {
		return referenceStationID;
	}

	public Time getTime() {
		return time;
	}

	public String getTimeLastUpdate() {
		return timeLastUpdate;
	}

	public char getUnitOfAltitude() {
		return unitOfAltitude;
	}

	public char getUnitOfGeoidHeight() {
		return unitOfGeoidHeight;
	}
}
