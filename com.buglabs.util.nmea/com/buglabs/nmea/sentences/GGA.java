/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package com.buglabs.nmea.sentences;

import com.buglabs.nmea.Time;

public class GGA extends PositionSentence {

	private Time time;
	private int fixQuality;
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

		String[] splitSentence = sentence.split("*");

		if (splitSentence.length > 1) {
			setChecksum(splitSentence[1]);
		}

		try {
			String[] fields = splitSentence[0].split(",");

			for (int i = 0; i < fields.length; ++i) {
				switch (i) {
				case 1:
					time = new Time(fields[i]);
					break;
				case 2:
				case 3:
					setLatitude(fields[i] + "," + fields[i + 1]);
					i++;
					break;
				case 4:
				case 5:
					setLongitude(fields[i] + "," + fields[i + 1]);
					i++;
					break;
				case 6:
					fixQuality = Integer.parseInt(fields[i]);
					break;
				case 7:
					numOfSatellites = Integer.parseInt(fields[i]);
					break;
				case 8:
					if (fields[i].length() == 0) {
						precision = -1;
					} else {
						precision = Float.parseFloat(fields[i]);
					}
					break;
				case 9:
				case 10:
					altitude = Double.parseDouble(fields[i]);
					unitOfAltitude = fields[i + 1].charAt(0);
					++i;
					break;
				case 11:
				case 12:
					if (fields[i].length() == 0) {
						++i;
						break;
					}
					geoidHeight = Double.parseDouble(fields[i]);
					unitOfGeoidHeight = fields[i + 1].charAt(0);
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
