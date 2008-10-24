package com.buglabs.nmea.sentences;

import com.buglabs.nmea.StringUtil;

public class RMC extends PositionSentence {

	private String timeOfFix;
	private String dataStatus;
	private String groundSpeed;
	private String dateStamp;
	private String magneticVariation;
	private String trackMadeGood;

	public RMC() {
		super("$GPRMC");
	}

	public Object parse(String sentence) {
		magneticVariation = "";

		String[] splitSentence =  StringUtil.split(sentence, "*");

		if(splitSentence.length > 1){
			setChecksum(splitSentence[1]);
		}

		String[] fields = StringUtil.split(splitSentence[0], ",");

		for(int i = 0; i < fields.length; ++i) {
			switch(i) {
			case 1:
				timeOfFix = fields[i];
				break;
			case 2:
				dataStatus = fields[i];
				break;
			case 3:
			case 4:
				setLatitude(fields[i] + "," + fields[i+1]);
				i++;
				break;
			case 5:
			case 6:
				setLongitude(fields[i] + "," + fields[i+1]);
				i++;
				break;
			case 7:
				groundSpeed = fields[i];
				break;
			case 8:
				trackMadeGood = fields[i];
				break;
			case 9:
				dateStamp = fields[i];
				break;
			case 10:
			case 11:
				if(fields.length > 11) {
					magneticVariation = fields[i] + "," + fields[i+1];
					if(magneticVariation.length() == 1) {
						magneticVariation = "";
					}
				}
				i++;
				break;
			default:
				break;
			}
		}

		return this;
	}

	public String getTimeOfFix() {
		return timeOfFix;
	}

	public String getDataStatus() {
		return dataStatus;
	}

	public String toDegrees(String measure) {
		int index = measure.indexOf(".");
		return measure.substring(0, index - 2);
	}

	public String getGroundSpeed() {
		return groundSpeed;
	}

	public String getTrackMadeGood() {
		return trackMadeGood;
	}

	public String getDateStamp() {
		return dateStamp;
	}

	public String getMagneticVaration() {
		return magneticVariation;
	}
}
