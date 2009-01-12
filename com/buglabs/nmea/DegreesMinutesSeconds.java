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
package com.buglabs.nmea;

public class DegreesMinutesSeconds {

	private String dms;

	public DegreesMinutesSeconds(String dms) {
		int i = dms.indexOf(",");
		this.dms = dms.substring(0, i);
	}

	public double getDegrees() {
		int i = dms.indexOf(".");

		if (dms.charAt(0) == '0') {
			return Double.parseDouble("-" + dms.substring(1, i - 2));
		}

		return Double.parseDouble(dms.substring(0, i - 2));
	}

	public double getMinutes() {
		int i = dms.indexOf(".");

		return Double.parseDouble(dms.substring(i - 2, i));
	}

	public double getSeconds() {
		int i = dms.indexOf(".");

		String seconds = dms.substring(i);

		return Double.parseDouble(seconds) * 60;
	}

	public double toDecimalDegrees() {
		double decimal = getSeconds() / 60.0;
		double result = (getMinutes() + decimal) / 60.0;

		double degrees = getDegrees();

		if (degrees < 0.0) {
			return degrees - result;
		}

		return degrees + result;
	}
}
