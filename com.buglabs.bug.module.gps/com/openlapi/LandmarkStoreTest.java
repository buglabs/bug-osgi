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
import java.util.Enumeration;
import java.util.Vector;

/**
 * Tests the LandmarkStore
 */
class LandmarkStoreTest extends ALAPITest {

	public static void main(String[] args) {
		LandmarkStoreTest lst = new LandmarkStoreTest();
		try {
			lst.runTests();
		} catch (LAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * A Vector of Landmark objects to use for populating stores.
	 */
	private Vector defaultLandmarks = null;

	public void runTests() throws LAPIException {
		testDefaultStore();
		testUserStore();
	}

	/**
	 * Add some Landmark objects to a store
	 *
	 * @see
	 * @param store
	 * @throws IOException
	 * @throws SecurityException
	 */
	private void populateStore(LandmarkStore store) throws SecurityException,
			IOException {
		if (defaultLandmarks == null) {
			// create Landmark objects
			defaultLandmarks = new Vector();

			// Edinburgh Castle
			AddressInfo castleAddress = new AddressInfo();
			castleAddress.setField(AddressInfo.STREET, "Royal Mile");
			castleAddress.setField(AddressInfo.POSTAL_CODE, "EH1 2NF");
			castleAddress.setField(AddressInfo.CITY, "Edinburgh");
			castleAddress.setField(AddressInfo.COUNTY, "City of Edinburgh");
			castleAddress.setField(AddressInfo.STATE, "Scotland");
			castleAddress.setField(AddressInfo.COUNTRY, "United Kingdom");
			castleAddress.setField(AddressInfo.COUNTRY_CODE, "GB");
			castleAddress.setField(AddressInfo.URL,
					"http://maps.google.co.uk/maps?q=EH1+2NF");
			QualifiedCoordinates castleCq = new QualifiedCoordinates(55.94944,
					-3.198352, Float.NaN, Float.NaN, Float.NaN);
			Landmark castle = new Landmark("Edinburgh Castle",
					"Scottish castle and wonderful tourist attraction.",
					castleCq, castleAddress);
			defaultLandmarks.addElement(castle);

			// ThinkTank's offices
			AddressInfo thinktankAddress = new AddressInfo();
			thinktankAddress.setField(AddressInfo.EXTENSION, "ThinkTank, ETTC");
			thinktankAddress.setField(AddressInfo.STREET,
					"Alrick Building, King's Buildings");
			thinktankAddress.setField(AddressInfo.POSTAL_CODE, "EH9 3JL");
			thinktankAddress.setField(AddressInfo.CITY, "Edinburgh");
			thinktankAddress.setField(AddressInfo.COUNTY, "City of Edinburgh");
			thinktankAddress.setField(AddressInfo.STATE, "Scotland");
			thinktankAddress.setField(AddressInfo.COUNTRY, "United Kingdom");
			thinktankAddress.setField(AddressInfo.COUNTRY_CODE, "GB");
			thinktankAddress.setField(AddressInfo.URL,
					"http://thinktankmaths.co.uk");
			QualifiedCoordinates thinktankCq = new QualifiedCoordinates(
					55.923648, -3.172259, Float.NaN, Float.NaN, Float.NaN);
			Landmark thinktank = new Landmark("ThinkTank Maths",
					"Technology consultancy and IP generator.", thinktankCq,
					thinktankAddress);
			defaultLandmarks.addElement(thinktank);
		}

		Enumeration en = defaultLandmarks.elements();
		for (; en.hasMoreElements();) {
			Landmark lm = (Landmark) en.nextElement();
			store.addLandmark(lm, null);
		}
	}

	private void testDefaultStore() throws LAPIException {
		try {
			LandmarkStore defaultStore = LandmarkStore.getInstance(null);
			assertion(defaultStore != null, "Default store failed to load");
			Enumeration landmarks = null;
			try {
				landmarks = defaultStore.getLandmarks();
			} catch (IOException e) {
				assertion(false, "Default store failed to load");
			}
			if (landmarks == null) {
				// the default store had nothing in it. Populate
				populateStore(defaultStore);
				landmarks = defaultStore.getLandmarks();
			}

			// the default store should have something now
			assertion(landmarks != null, "Default store doesn't hold anything");

			// add some category info if it doesn't exist already
			String category = "Scottish";
			try {
				defaultStore.addCategory(category);
			} catch (IllegalArgumentException e) {
				// harmless
			}
			for (; landmarks.hasMoreElements();) {
				Landmark lm = (Landmark) landmarks.nextElement();
				defaultStore.addLandmark(lm, category);
			}
			// ensure the landmarks are in the category
			landmarks = defaultStore.getLandmarks(category, null);
			assertion(landmarks.hasMoreElements(),
					"Categories not written to store");

			// ?? more LandmarkStore tests
			// e.g. check equality between (edited) Landmark objects

		} catch (Exception e) {
			throw new LAPIException("Unexpected exception in default store.");
		}
	}

	private void testUserStore() throws LAPIException {
		try {
			LandmarkStore.createLandmarkStore("User-defined");
		} catch (Exception e) {
		}
		LandmarkStore userStore = LandmarkStore.getInstance("User-defined");
		Enumeration landmarks = null;
		try {
			landmarks = userStore.getLandmarks();
		} catch (IOException e) {
		}
		if (landmarks == null) {
			// the default store had nothing in it. Populate
			try {
				populateStore(userStore);
				landmarks = userStore.getLandmarks();
			} catch (Exception e) {
			}
		}

		// the default store should have something now
		assertion(landmarks != null, "User store doesn't hold anything");
	}

}
