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
package com.buglabs.bug.module.gps;

import java.io.BufferedReader;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.osgi.service.log.LogService;

import com.buglabs.bug.module.gps.pub.INMEASentenceProvider;
import com.buglabs.nmea.NMEAParser;
import com.buglabs.nmea.sentences.NMEAParserException;
import com.buglabs.nmea.sentences.RMC;

/**
 * This class is a thread that listens for NMEA sentences on the InputStream passed in the constructor, and will present the last
 * parsed sentence to clients.
 * 
 * @author aroman
 *
 */
public class NMEASentenceProvider extends Thread implements INMEASentenceProvider {

	private InputStream nmeaStream;

	private RMC cachedRMC;

	private final LogService log;

	public NMEASentenceProvider(InputStream nmeaStream, LogService log) {
		this.nmeaStream = nmeaStream;
		this.log = log;
	}

	/* (non-Javadoc)
	 * @see com.buglabs.bug.module.gps.pub.INMEASentenceProvider#getRMC()
	 */
	public RMC getRMC() {
		return cachedRMC;
	}

	public void run() {
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(nmeaStream));
			String sentence;
			NMEAParser parser = new NMEAParser();

			do {
				try {
					sentence = br.readLine();
				} catch (CharConversionException e) {
					sentence = "";
					continue;
				}

				try {
					log.log(LogService.LOG_DEBUG, "Raw NMEA Line: " + sentence);
					Object objSentence = parser.parse(sentence);

					if (objSentence != null && objSentence instanceof RMC) {
						cachedRMC = (RMC) objSentence;
					}
				} catch (NMEAParserException e) {
					log.log(LogService.LOG_ERROR, "An error occured while parsing sentence.", e);
				}
			} while (!Thread.currentThread().isInterrupted() && (sentence != null));
		} catch (IOException e) {
			log.log(LogService.LOG_ERROR, "An IO Error occurred in reading from NMEA stream.", e);
		} finally {
			try {
				if (br != null) {
					br.close();
				} else {
					if (nmeaStream != null) {
						nmeaStream.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
