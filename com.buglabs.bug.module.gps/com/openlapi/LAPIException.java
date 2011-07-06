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
 * An Exception thrown if a test in the JSR-179 test suite fails.
 */
class LAPIException extends Exception {

	/**
	 * Constructs a JSR179Exception with no detail message.
	 */
	public LAPIException() {
	}

	/**
	 * Constructs a JSR179Exception with the specified detail message.
	 *
	 * @param s
	 *            the detailed message
	 */
	public LAPIException(String s) {
		super(s);
	}
}
