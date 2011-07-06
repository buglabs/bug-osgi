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

import thinktank.j2me.TTUtils;

/**
 * Test the LocationProvider (note this will depend on the mode selected)
 */
class LocationProviderTest extends ALAPITest {

	private String getInfo(Location location) {
		String info =
				location.getExtraInfo("application/X-jsr179-location-nmea");
		if (info == null)
			info = location.getExtraInfo("text/plain");
		return info;
	}

	private class SimpleLocationListener implements LocationListener {
		protected boolean gotLocation = false;

		public void locationUpdated(LocationProvider provider, Location location) {
			if ((location != null) && location.isValid()) {
				gotLocation = true;
				TTUtils.log("received location with: " + getInfo(location));
			}
		}

		public void providerStateChanged(LocationProvider provider, int newState) {
		}
	}

	private class SimpleProximityListener implements ProximityListener {

		protected boolean gotProximity = false;

		public void monitoringStateChanged(boolean isMonitoringActive) {
		}

		public void proximityEvent(Coordinates coordinates, Location location) {
			gotProximity = true;
			TTUtils.log("Proximity: " + getInfo(location));
		}
	}

	public void runTests() throws LAPIException {
		testProvider();
	}

	public void testProvider() throws LAPIException {
		LocationProvider provider = null;
		try {
			provider = LocationProvider.getInstance(null);
		} catch (LocationException e) {
			throw new LAPIException("Failed to obtain provider");
		}
		assertion(provider != null, "Provider was null");

		Location location = getLocation(provider);
		assertion(location != null,
			"Didn't receive a valid Location. Check the GPS device.");

		// set up a LocationListener
		SimpleLocationListener locListener = new SimpleLocationListener();
		provider.setLocationListener(locListener, 1000, -1, -1);
		// set up a ProximityListener
		// this should be alerted by any Edinburgh-area Locations
		Coordinates cramond = new Coordinates(55.973894, -3.305984, Float.NaN);
		// half the radius of the earth
		int radius = 6378137 / 2;
		SimpleProximityListener proxListener = new SimpleProximityListener();
		try {
			LocationProvider.addProximityListener(proxListener, cramond, radius);
		} catch (Exception e) {
		}

		// pause for 30 seconds, the location should have changed allowing us to
		// test that the location changed, and both types of listeners worked
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
		}

		Location newLocation = getLocation(provider);
		assertion(newLocation != null, " didn't get a location");

		// Location should exist
		assertion(newLocation != location, " location did not change");

		// LocationListener should have worked
		assertion(locListener.gotLocation, "Didn't receive a location");
		// remove the listener (a test in itself)
		provider.setLocationListener(null, -1, -1, -1);

		// ProximityListener should have worked
		assertion(
			proxListener.gotProximity,
			"ProximityListener not successful."
					+ " If you're not in Edinburgh then don't worry about this failing!");
	}

	/**
	 * Convenience method that swallows any Exceptions into a null return.
	 * 
	 * @param provider
	 * @return
	 */
	private Location getLocation(LocationProvider provider) {
		Location location = null;
		try {
			// 10 second timeout
			location = provider.getLocation(10);
		} catch (Exception e) {
			TTUtils.log("hmm, no Location: " + e.getMessage());
		}
		return location;
	}
}
