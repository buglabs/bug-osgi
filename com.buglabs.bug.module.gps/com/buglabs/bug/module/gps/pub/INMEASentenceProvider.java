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
package com.buglabs.bug.module.gps.pub;

import com.buglabs.nmea.sentences.RMC;

/**
 * Provides access methods to NMEA data from GPS devices.
 * 
 * If a GPS device is attached, a INMEASentenceProvider service will be accessible to clients via
 * the OSGi service registry.  This service is appropriate for clients wishing to poll for NEMA
 * sentences at specific times.
 * 
 * @author Angel Roman
 */
public interface INMEASentenceProvider {
	/**
	 * Provides the latest RMC sentence read from the GPS Device, or null if no information is available.
	 * @deprecated
	 * @return RMC sentence object
	 */
	public RMC getRMC();
	
	/**
	 * @return Last parsed RMC NMEA sentence, or null if no sentence with location information has been received.
	 */
	public com.buglabs.nmea2.RMC getLastRMC();
	
	/**
	 * @return the index of the RMC value currently available.
	 * Useful for determining if new RMC is available.
	 */
	public int getIndex();
}
