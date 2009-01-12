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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.buglabs.nmea.sentences.GGA;
import com.buglabs.nmea.sentences.GSV;
import com.buglabs.nmea.sentences.NMEAParserException;
import com.buglabs.nmea.sentences.NMEASentence;
import com.buglabs.nmea.sentences.PTTK;
import com.buglabs.nmea.sentences.RMC;

public class NMEAParser {
	private static List sentences;

	public NMEAParser() {
		sentences = new ArrayList();
		sentences.add(new GSV());
		sentences.add(new RMC());
		sentences.add(new GGA());
		sentences.add(new PTTK());
	}

	public Object parse(String sen) throws NMEAParserException {
		Iterator iter = sentences.iterator();

		while (iter.hasNext()) {
			NMEASentence sentence = (NMEASentence) iter.next();
			if (sentence.isValid(sen)) {
				return sentence.parse(sen);
			}
		}

		return null;
	}
}
