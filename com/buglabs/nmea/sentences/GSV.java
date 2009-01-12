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

		for (int i = 0; i < fields.length; ++i) {
			switch (i) {
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
				String[] lastfield = StringUtil.split(fields[i + 3], "*");
				if (lastfield.length > 1) {
					setChecksum(lastfield[1]);
				}
				satellites.add(new SV(fields[i], fields[i + 1], fields[i + 2], lastfield[0]));
				i += 3;
				break;
			default:
				break;
			}
		}

		return this;
	}
}
