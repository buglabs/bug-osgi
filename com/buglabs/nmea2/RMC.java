package com.buglabs.nmea2;


/**
 * Contains RMC type NMEA sentences.
 * @author kgilmer
 *
 */
public final class RMC extends AbstractNMEASentence {

	private String magneticVariation;
	private String timeOfFix;
	private String dataStatus;
	private String latitude;
	private String longitude;
	private String groundSpeed;
	private String trackMadeGood;
	private String dateStamp;

	protected RMC(String sentence) {
		super(sentence);
	}
	
	protected void initialize() {
		magneticVariation = "";
		timeOfFix = "";
		dataStatus = "";
		latitude = "";
		longitude = "";
		groundSpeed = "";
		trackMadeGood = "";
		dateStamp = "";
	}

	protected void parseField(int index, String field, String fields[]) {
			switch (index) {
			case 1:
				timeOfFix = field;
				break;
			case 2:
				dataStatus = field;
				break;
			case 3:
				latitude = field + "," + fields[index + 1];
				break;
			case 5:
				longitude = field + "," + fields[index + 1];
				break;
			case 7:
				groundSpeed = field;
				break;
			case 8:
				trackMadeGood = field;
				break;
			case 9:
				dateStamp = field;
				break;
			case 10:
				if (fields.length > 11) {
					magneticVariation = field + "," + fields[index + 1];
					if (magneticVariation.length() == 1) {
						magneticVariation = "";
					}
				}
				break;
			default:
				break;
			}
		
	}

	protected void validate() {
		
	}

	public String getMagneticVariation() {
		return magneticVariation;
	}

	public String getTimeOfFix() {
		return timeOfFix;
	}

	public String getDataStatus() {
		return dataStatus;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
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

}
