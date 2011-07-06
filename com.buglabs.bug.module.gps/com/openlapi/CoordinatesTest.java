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


/**
 *
 */
class CoordinatesTest extends ALAPITest {

	public void runTests() throws LAPIException {
		testConstructor();
		testDistance();
		testAzimuthTo();
		testConvertToStringType1();
		testConvertToStringType2();
		testConvertFromString();
	}

	public void testAzimuthTo() throws LAPIException {
		// test exception handling
		boolean caught = false;
		Coordinates test = new Coordinates(0.0, 0.0, Float.NaN);
		try {
			test.azimuthTo(null);
		} catch (NullPointerException e) {
			caught = true;
		}
		assertion(caught);

		// test correct behaviour when coordinates are the same
		testAzimuthTo2(0.0, 0.0, 0.0, 0.0, 0.0);
		testAzimuthTo2(90.0, 0.0, 90.0, 0.0, 0.0);
		testAzimuthTo2(-90.0, 0.0, -90.0, 0.0, 0.0);

		/*
		 * When the origin is the North pole and the destination is not the North pole,
		 * this method returns 180.0.
		 */
		testAzimuthTo2(90.0, 0.0, 0.0, 0.0, 180.0);
		testAzimuthTo2(90.0, 0.0, -90.0, 0.0, 180.0);
		testAzimuthTo2(90.0, 0.0, -33.966667, 18.4166667, 180.0);

		/*
		 * When the origin is the South pole and the destination is not the South pole,
		 * this method returns 0.0.
		 */
		testAzimuthTo2(-90.0, 0.0, 0.0, 0.0, 0.0);
		testAzimuthTo2(-90.0, 0.0, 90.0, 0.0, 0.0);
		testAzimuthTo2(-90.0, 0.0, -33.966667, 18.4166667, 0.0);

		// Table Mountain, South Africa (Mountain, Western Cape)
		// Edinburgh Castle, United Kingdom (Castle, United Kingdom)
		testAzimuthTo2(-33.966667, 18.4166667, 55.95, -3.2, 348.0);

		// Crude Glasgow <-> London calculation to sanity check
		testAzimuthTo2(55, -4, 51, 0, 147.35);

		// Gibraltar, Gibraltar (Mountain, Gibraltar)
		// Auckland Island, New Zealand (Island, New Zealand)
		testAzimuthTo2(36.11666666667, -5.31666666667, -50.71666666667,
				166.11666666667, 160.0);

		// this tests behaviour on two close locations
		// 4.6km apart
		testAzimuthTo2(-33, 18, -33, 18.05, 90);
		// 9cm apart
		testAzimuthTo2(-33, 18, -33, 18.000001, 90);
		// 1cm apart
		testAzimuthTo2(-33, 18, -33, 18.0000001, 90);
		// 1mm apart
		testAzimuthTo2(-33, 18, -33, 18.00000001, 90);

		// this tests equatorial behaviour
		testAzimuthTo2(0.0, 0.0, 0.0, 45.0, 90);
		testAzimuthTo2(0.0, 0.0, 0.0, -45.0, 270);

		// ?? more Coordinates.azimuthTo() tests
		// Especially to test correct quadrant
	}

	public void testConstructor() throws LAPIException {
		// check if the bounds are being checked on init
		assertion(testConstructor2(90.000001, 0));
		assertion(testConstructor2(-90.000001, 0));
		assertion(!testConstructor2(90, 0));
		assertion(!testConstructor2(-90, 0));
		assertion(testConstructor2(0, 180));
		assertion(!testConstructor2(0, -180));
		assertion(testConstructor2(0, 180.000001));
	}

	public void testConvertFromString() throws LAPIException {
		// some Strings that should throw exceptions
		boolean caught = false;
		try {
			Coordinates.convert(null);
		} catch (NullPointerException e) {
			caught = true;
		}
		assertion(caught);

		caught = false;
		try {
			Coordinates.convert("blah");
		} catch (IllegalArgumentException e) {
			caught = true;
		}
		assertion(caught);

		caught = false;
		try {
			Coordinates.convert("61:30::");
		} catch (IllegalArgumentException e) {
			caught = true;
		}
		assertion(caught);

		caught = false;
		try {
			Coordinates.convert("61.30:6");
		} catch (IllegalArgumentException e) {
			caught = true;
		}
		assertion(caught);

		assertEquals(61.51d, Coordinates.convert("61:30.6"), 0.001);
		assertEquals(-61.51d, Coordinates.convert("-61:30.6"), 0.001);
		assertEquals(61.51d, Coordinates.convert("61:30:36"), 0.001);
		assertEquals(10.83535, Coordinates.convert("10:50:07.2"), 0.0001);
		assertEquals(10.83535, Coordinates.convert("10:50.120"),0.0001);
		assertEquals(32 + 0.0006/60, Coordinates.convert("32:00.0006"), 0.0001);

		// ?? more Coordinates.convert() tests
	}

	public void testConvertToStringType1() throws LAPIException {
		assertEquals("61:30:36", Coordinates.convert(61.51, Coordinates.DD_MM_SS));
		assertEquals("-61:30:36", Coordinates.convert(-61.51, Coordinates.DD_MM_SS));
		assertEquals("10:50:07.2", Coordinates.convert(10.8353333, Coordinates.DD_MM_SS));
	}

	public void testConvertToStringType2() throws LAPIException {
		assertEquals("61:30.6", Coordinates.convert(61.51, Coordinates.DD_MM));
		assertEquals("-61:30.6", Coordinates.convert(-61.51, Coordinates.DD_MM));
		assertEquals("10:50.12", Coordinates.convert(10.8353333, Coordinates.DD_MM));
		assertEquals("32:00.0006", Coordinates.convert(32 + 0.0006/60, Coordinates.DD_MM));
	}

	/**
	 * Test the accuracy of the distance() method, using several extreme situations known
	 * to break most algorithms.
	 * <p>
	 * Note that all my test distances have been gathered by comparing website calculators
	 * and the Nokia implementation... none of these 'measured' distances are actually
	 * empirical.
	 *
	 * @throws LAPIException
	 */
	public void testDistance() throws LAPIException {
		// test exception handling
		boolean caught = false;
		Coordinates test = new Coordinates(0.0, 0.0, Float.NaN);
		try {
			test.distance(null);
		} catch (NullPointerException e) {
			caught = true;
		}
		assertion(caught);

		// Table Mountain, South Africa (Mountain, Western Cape)
		// Edinburgh Castle, United Kingdom (Castle, United Kingdom)
		testDistance2(-33.966667, 18.4166667, 55.95, -3.2, 10172500.0);

		// Cramond to ThinkTank offices
		testDistance2(55.973894, -3.305984, 55.923648, -3.172259, 10050);
		// same latitude
		testDistance2(55.973894, -3.305984, 55.973894, -3.172259, 8329);
		testDistance2(55.923648, -3.305984, 55.923648, -3.172259, 8349);
		testDistance2(55.923648, 0, 55.923648, -0.133725, 8349);
		testDistance2(55.973894, 0, 55.973894, -0.133725, 8329);

		// this tests behaviour on two close locations
		// 4.6km apart
		testDistance2(-33, 18, -33, 18.05, 4672.661);
		// 9cm apart
		testDistance2(-33, 18, -33, 18.000001, 0.0932);
		// 1cm apart
		testDistance2(-33, 18, -33, 18.0000001, 0.00932);
		// 1mm apart
		testDistance2(-33, 18, -33, 18.00000001, 0.000932);

		// this tests behaviour on the same two locations
		testDistance2(0.0, 0.0, 0.0, 0.0, 0.0);

		// this tests equatorial behaviour
		testDistance2(0.0, 0.0, 0.0, 45.0, 5009377.086);

		// near-antipode behaviour
		// Gibraltar, Gibraltar (Mountain, Gibraltar)
		// Auckland Island, New Zealand (Island, New Zealand)
		testDistance2(36.11666666667, -5.31666666667, -50.71666666667,
				166.11666666667, 18247900.0);

		// this tests actual anti-podal behaviour
		testDistance2(90.0, 0.0, -90.0, 0.0, 20020000);
		testDistance2(0.0, 0.0, 0.0, -180.0, 20037502);
	}

	private void testAzimuthTo2(double latitude1, double longitude1,
			double latitude2, double longitude2, double measured)
			throws LAPIException {
		// Specification says 1 degree precision is required, small bit added
		// for same-point checks
		double error = 1.0;
		Coordinates a = new Coordinates(latitude1, longitude1, Float.NaN);
		Coordinates b = new Coordinates(latitude2, longitude2, Float.NaN);
		double azimuth = a.azimuthTo(b);
		assertion((azimuth < (measured + error))
				&& (azimuth > (measured - error)));
	}

	/**
	 * @param latitude
	 * @param longitude
	 * @return true if the arguments were out of bounds
	 */
	private boolean testConstructor2(double latitude, double longitude) {
		boolean caught = false;
		try {
			new Coordinates(latitude, longitude, Float.NaN);
		} catch (IllegalArgumentException e) {
			caught = true;
		}
		return caught;
	}

	/**
	 * Convenience method for testing the distance calculated between two coordinates is
	 * correct within 0.36%.
	 *
	 * @param latitude1
	 *            of first coordinate.
	 * @param longitude1
	 *            of first coordinate.
	 * @param latitude2
	 *            of second coordinate.
	 * @param longitude2
	 *            of second coordinate.
	 * @param measured
	 *            distance between the coordinates, across the Earth's surface ignoring
	 *            altitude.
	 * @throws LAPIException
	 */
	private void testDistance2(double latitude1, double longitude1,
			double latitude2, double longitude2, double measured)
			throws LAPIException {
		// Specification says 0.36% precision is required, small bit added for
		// same-point checks
		double error = measured * 0.0036 + 0.000000001;
		Coordinates a = new Coordinates(latitude1, longitude1, Float.NaN);
		Coordinates b = new Coordinates(latitude2, longitude2, Float.NaN);
		double distance = a.distance(b);
		assertion((distance < (measured + error))
				&& (distance > (measured - error)),
				"Distance test failed, should be " + measured + ", but was "
						+ distance);
	}

}
