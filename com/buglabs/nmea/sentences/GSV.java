package com.buglabs.nmea.sentences;

import java.util.ArrayList;
import java.util.List;

import com.buglabs.nmea.SV;
import com.buglabs.nmea.StringUtil;

public class GSV extends NMEASentence {

	private int numberOfMessages;
	private int messageNumber;
	private int numberOfSVs;
	private List satellites;
	
	public GSV() {
		super("$GPGSV");
		satellites = new ArrayList(3);
	}
	
	public int getMessageNumber() {
		return messageNumber;
	}
	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}
	public int getNumberOfMessages() {
		return numberOfMessages;
	}
	public void setNumberOfMessages(int numberOfMessages) {
		this.numberOfMessages = numberOfMessages;
	}
	public int getNumberOfSVs() {
		return numberOfSVs;
	}
	public void setNumberOfSVs(int numberOfSVs) {
		this.numberOfSVs = numberOfSVs;
	}
	public List getSatellites() {
		return satellites;
	}
	public void setSatellites(List satellites) {
		this.satellites = satellites;
	}

	public Object parse(String sentence) {
		String[] fields = StringUtil.split(sentence, ",");
		satellites.clear();
		
		for(int i = 0; i < fields.length; ++i) {
			switch(i) {
			case 1:
				try {
					setNumberOfMessages(Integer.parseInt(fields[i]));
				} catch (NumberFormatException e) {
					setNumberOfMessages(-1);
				}
				break;
			case 2:
				try {
					setMessageNumber(Integer.parseInt(fields[i]));
				} catch (NumberFormatException e) {
					setMessageNumber(-1);
				}
				break;
			case 3:
				try {
					setNumberOfSVs(Integer.parseInt(fields[i]));
				} catch (NumberFormatException e) {
					setNumberOfSVs(-1);
				}
				break;
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
				String[] lastfield = StringUtil.split(fields[i+3], "*");
				if(lastfield.length > 1) {
					setChecksum(lastfield[1]);
				}
				satellites.add(new SV(fields[i], fields[i+1], fields[i+2], lastfield[0]));
				i += 3;
				break;
			default:
				break;
			}
		}

		return this;
	}	
}
