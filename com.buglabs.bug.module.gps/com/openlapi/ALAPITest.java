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
 * Abstract class with some convenience methods for unit tests of the JSR-179
 * implementation. Basically trying to give us JUnit functionaility within the phone JVM.
 */
abstract class ALAPITest {

	/**
	 * Keep track of the test number as some informative debugging info. (be much better
	 * if we could use reflection to get the method name as well).
	 */
	private int testNum = 1;

	/**
	 * Run the tests contained in the class, and throw an exception if a test fails.
	 * <p>
	 * Unfortunately this is needed as J2ME does not support Class.getMethods()
	 *
	 * @throws LAPIException
	 */
	public abstract void runTests() throws LAPIException;

	/**
	 * Assertion with default fail string
	 *
	 * @see #assertion(boolean, String)
	 */
	protected void assertion(boolean test) throws LAPIException {
		assertion(test, "test #" + testNum + " failed");
	}

	/**
	 * Convenience method that can be used where JUnit tests would have an 'assert'.
	 * Obviously not as convenient though.
	 *
	 * @param test
	 * @param failMessage
	 * @throws LAPIException
	 */
	protected void assertion(boolean test, String failMessage)
			throws LAPIException {
		if (!test)
			throw new LAPIException(getClass().getName() + ": " + failMessage);
		// increment the test count
		testNum++;
	}

	/**
	 * @param expected
	 * @param received
	 * @throws LAPIException
	 */
	protected void assertEquals(Object expected, Object received) throws LAPIException {
		assertion(expected.equals(received), "test #" + testNum
				+ " failed. Expected " + expected + " received " + received);
	}

	/**
	 * @param expected
	 * @param received
	 * @throws LAPIException
	 */
	protected void assertEquals(double expected, double received, double epsilon) throws LAPIException {
		assertion(expected - epsilon <= received && expected + epsilon >= received,
				"test #" + testNum + " failed. Expected " + expected + " received " + received);
	}
}
