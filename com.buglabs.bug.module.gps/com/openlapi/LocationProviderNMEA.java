/*
 * Copyright ThinkTank Maths Limited 2006 - 2008
 *
 * This file is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 */
package com.openlapi;

import java.io.IOException;
import java.io.InputStream;

import thinktank.j2me.TTUtils;

/**
 * Implementation of {@link LocationProvider} that uses an NMEA log file as input.
 * 
 * @author Samuel Halliday, ThinkTank Maths Limited
 * @see http://en.wikipedia.org/wiki/NMEA_0183
 */
public class LocationProviderNMEA extends LocationProviderSimplified {

	/**
	 * Takes in an input of NMEA data that is instantly accessible and adds in a delay in
	 * the recovery of the data. This is to allow a log file to emulate a GPS device.
	 * <p>
	 * This implementation simply delays every sentence by 1 second (or so) and ignores
	 * the timestamp in the NMEA sentences.
	 * 
	 * @author Samuel Halliday, ThinkTank Maths Limited
	 */
	static class NMEAReader extends InputStream {
		private final InputStream input;

		/**
		 * @param input
		 */
		public NMEAReader(InputStream input) {
			this.input = input;
		}

		public int read() throws IOException {
			int i = input.read();
			if (i == '\n')
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			return i;
		}

		public void close() throws IOException {
			input.close();
		}
	}

	private volatile NMEADaemon daemon = null;

	private final String source;

	/**
	 * @param criteria
	 * @param source
	 * @throws LocationException
	 */
	LocationProviderNMEA(Criteria criteria, String source)
			throws LocationException {
		TTUtils.log("OpenLAPI NMEA mode");
		this.source = source;
		startBackend();
	}

	protected void startBackend() throws LocationException {
		if (daemon != null)
			return;

		InputStream input = OpenLAPICommon.getOpenLAPIResource(source);
		NMEAReader nmea = new NMEAReader(input);
		daemon = new NMEADaemon(this, nmea, new NMEADaemon.ValidStreamCallback() {
			public void onSuccess() {
			}

			public void onFailure(String failed) {
				TTUtils.log("INVALID NMEA: " + failed);
				daemon.end();
			}
		});
		new Thread(daemon).start();
	}

	protected void stopBackend() {
		if (daemon == null)
			return;

		daemon.end();
		daemon = null;
	}

}
