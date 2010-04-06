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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;

import thinktank.j2me.BluetoothDeviceDiscover;
import thinktank.j2me.BluetoothServiceDiscover;
import thinktank.j2me.TTUtils;

/**
 * An implementation of the LocationProvider that accesses a Bluetooth GPS device. Device
 * discovery typically takes about 10 seconds.
 * <p>
 * Note that a connection to a running GPS device takes about 30 seconds before valid data
 * starts coming through.
 */
final class LocationProviderBTGPS extends LocationProviderSimplified {
	private volatile NMEADaemon daemon = null;

	private volatile boolean starting = false;

	/**
	 * @param criteria
	 * @param output
	 *            if NMEA logging is required
	 * @throws LocationException 
	 */
	LocationProviderBTGPS(Criteria criteria) throws LocationException {
		TTUtils.log("OpenLAPI GPS mode");
		startBackend();
	}

	protected void startBackend() throws LocationException {
		// this is always called from a synchronized block
		if (starting || ((daemon != null) && daemon.isRunning())) {
			return;
		}
		starting = true;

		// TODO: cache URLs of trusted devices in the RecordStore
		// TODO: cache bad device numbers in memory for this session
		// TODO: allow user to add trusted device URLs in config file

		Runnable runnable = new Runnable() {
			public void run() {
				// first of all, try all known valid URIS
				Enumeration en = goodURIs.elements();
				while (en.hasMoreElements()) {
					String uri = (String) en.nextElement();
					if (connect(uri)) {
						TTUtils.log("Using known source " + uri);
						return;
					}
				}

				BluetoothDeviceDiscover discover =
					new BluetoothDeviceDiscover();
				// this will block for about 10 to 20 seconds
				// so we do this in a new thread to avoid blocking beyond timeouts
				// this has the disadvantage that we can't report failures
				Hashtable devices = discover.discover();
				// send the devices to a helper method which will prioritise
				Vector ranked = rankDevices(devices.keys());
				// attempt to connect to the devices in the given order
				connect(devices, ranked);
			}
		};
		Thread thread = new Thread(runnable);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	/**
	 * given a vector of device names that have been detected,
	 * create a ranked list with the most likely GPS devices at the start.
	 * 
	 * @param devices
	 * @return the ranked list of devices
	 */
	private Vector rankDevices(Enumeration names) {
		Vector good = new Vector();
		Vector bad = new Vector();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (name.indexOf("GPS") != -1 || name.equals("AD800")) {
				good.addElement(name);
			} else {
				bad.addElement(name);
			}
		}
		Enumeration en = bad.elements();
		while (en.hasMoreElements()) {
			good.addElement(en.nextElement());
		}
		return good;
	}

	/** <String> of device numbers that do not work */
	private final Vector badDevices = new Vector();

	/** <String> of device URIs that do not work */
	private final Vector badURIs = new Vector();

	/** <String> of device URIs that have produced valid NMEA in the past */
	private final Vector goodURIs = new Vector();

	/**
	 * attempt to connect to the devices using the given ordering, exiting
	 * early on a successful connection.
	 * @param devices <String, RemoteDevice>
	 * @param ranked <String>
	 */
	private void connect(Hashtable devices, Vector ranked) {
		Enumeration en = ranked.elements();
		while (en.hasMoreElements()) {
			String name = (String) en.nextElement();
			RemoteDevice device = (RemoteDevice) devices.get(name);
			TTUtils.log("connect seen " + name);
			String address = device.getBluetoothAddress();
			if (badDevices.contains(address)) {
				TTUtils.log("Skipping " + name +
					" because it recently failed");
				continue;
			}

			// check that the device supports SPP
			// 0x1101 is SPP
			BluetoothServiceDiscover serviceDiscover =
				new BluetoothServiceDiscover(device, 0x1101);
			Vector recs = serviceDiscover.discover();
			Enumeration rEn = recs.elements();
			while (rEn.hasMoreElements()) {
				ServiceRecord rec = (ServiceRecord) rEn.nextElement();
				String uri = rec.getConnectionURL(
					ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
				TTUtils.log("Found SPP on " + uri);
				// check the bad list
				if (badURIs.contains(uri)) {
					TTUtils.log("Skipping " + uri +
						" because it recently failed");
					continue;
				}

				if (connect(uri))
					return;
			}
			badDevices.addElement(address);
		}
	}

	/**
	 * @param uri
	 * @return true if a connection (which may be invalid) was created, or
	 * false if it failed.
	 */
	private boolean connect(final String uri) {
		final InputStream stream;
		try {
			stream = Connector.openInputStream(uri);
		} catch (IOException e) {
			badURIs.addElement(uri);
			TTUtils.log("IOException " + e.getMessage());
			// try other eligible devices
			return false;
		}
		daemon = new NMEADaemon(LocationProviderBTGPS.this, stream,
			new NMEADaemon.ValidStreamCallback() {
				public void onSuccess() {
					if (!goodURIs.contains(uri))
						goodURIs.addElement(uri);
					TTUtils.log("valid NMEA on " + uri);
				}

				public void onFailure(String failed) {
					TTUtils.log("invalid NMEA on " + uri + ": " + failed);
					badURIs.addElement(uri);
					// make sure we don't keep using this input
					try {
						stream.close();
					} catch (IOException ex) {
					}
				}
			});
		// start in a separate thread to avoid race condition
		// with 'starting' field
		new Thread(daemon).start();
		starting = false;
		return true;
	}

	protected void stopBackend() {
		if (daemon == null || !daemon.isRunning())
			return;

		daemon.end();
		daemon = null;
	}
}
