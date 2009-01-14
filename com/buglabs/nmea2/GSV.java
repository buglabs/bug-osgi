package com.buglabs.nmea2;

import java.util.ArrayList;
import java.util.List;

import com.buglabs.nmea.SV;

/**
 * Contains GSV type NMEA sentence.
 * @author kgilmer
 *
 */
public final class GSV extends AbstractNMEASentence {

	private int numberOfMessages;
	private int messageNumber;
	private int numberOfSVs;
	private List satellites;

	public GSV(String sentence) {
		super(sentence);
	}

	protected void initialize() {
		numberOfMessages = -1;
		messageNumber = -1;
		numberOfSVs = -1;
		satellites = new ArrayList(3);
	}

	protected void parseField(int index, String field, String fields[]) {
		switch (index) {
		case 1:
			numberOfMessages = Integer.parseInt(field);
			break;
		case 2:
			messageNumber = Integer.parseInt(field);
			break;
		case 3:
			numberOfSVs = Integer.parseInt(field);
			break;
		case 4:
			if (fields.length > index + 3) {
				satellites.add(new SV(field, fields[index + 1], fields[index + 2], fields[index + 3]));
			}
		case 5:
		case 6:
		case 7:
			break;
		case 8:
			if (fields.length > index + 3) {
				satellites.add(new SV(field, fields[index + 1], fields[index + 2], fields[index + 3]));
			} else if (fields.length == index + 3) {
				satellites.add(new SV(field, fields[index + 1], fields[index + 2], ""));
			}
		case 9:
		case 10:
		case 11:
			break;
		case 12:
			if (fields.length > index + 3) {
				satellites.add(new SV(field, fields[index + 1], fields[index + 2], fields[index + 3]));
			} else if (fields.length == index + 3) {
				satellites.add(new SV(field, fields[index + 1], fields[index + 2], ""));
			}
		case 13:
		case 14:
		case 15:
			break;
		case 16:
			if (fields.length > index + 3) {
				satellites.add(new SV(field, fields[index + 1], fields[index + 2], fields[index + 3]));
			} else if (fields.length == index + 3) {
				satellites.add(new SV(field, fields[index + 1], fields[index + 2], ""));
			}
		default:
			break;
		}
	}

	protected void validate() {

	}

	public int getNumberOfMessages() {
		return numberOfMessages;
	}

	public int getMessageNumber() {
		return messageNumber;
	}

	public int getNumberOfSVs() {
		return numberOfSVs;
	}

	public List getSatellites() {
		return satellites;
	}
}
