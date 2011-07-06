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

package org.osgi.util.measurement;

/**
 * Groups a state name, value and timestamp.
 * 
 * <p>
 * The state itself is represented as an integer and the time is measured in
 * milliseconds since midnight, January 1, 1970 UTC.
 * 
 * <p>
 * A <tt>State</tt> object is immutable so that it may be easily shared.
 * 
 * @version $Revision: 1.1 $
 * @author Open Services Gateway Initiative
 */

public class State {
	final int value;
	final long time;
	final String name;

	/**
	 * Create a new <tt>State</tt> object.
	 * 
	 * @param value
	 *            The value of the state.
	 * @param name
	 *            The name of the state.
	 * @param time
	 *            The time measured in milliseconds since midnight, January 1,
	 *            1970 UTC.
	 */
	public State(int value, String name, long time) {
		this.value = value;
		this.name = name;
		this.time = time;
	}

	/**
	 * Create a new <tt>State</tt> object with a time of 0.
	 * 
	 * @param value
	 *            The value of the state.
	 * @param name
	 *            The name of the state.
	 */
	public State(int value, String name) {
		this(value, name, 0);
	}

	/**
	 * Returns the value of this <tt>State</tt>.
	 * 
	 * @return The value of this <tt>State</tt> object.
	 */
	public final int getValue() {
		return value;
	}

	/**
	 * Returns the time with which this <tt>State</tt> was created.
	 * 
	 * @return The time with which this <tt>State</tt> was created. The time
	 *         is measured in milliseconds since midnight, January 1, 1970 UTC.
	 */
	public final long getTime() {
		return time;
	}

	/**
	 * Returns the name of this <tt>State</tt>.
	 * 
	 * @return The name of this <tt>State</tt> object.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns a <tt>String</tt> object representing this object.
	 * 
	 * @return a <tt>String</tt> object representing this object.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(value);

		if (name != null) {
			sb.append(" \"");
			sb.append(name);
			sb.append("\"");
		}

		return (sb.toString());
	}

	/**
	 * Returns a hash code value for this object.
	 * 
	 * @return A hash code value for this object.
	 */
	public int hashCode() {
		int hash = value;

		if (name != null) {
			hash ^= name.hashCode();
		}

		return hash;
	}

	/**
	 * Return whether the specified object is equal to this object. Two
	 * <tt>State</tt> objects are equal if they have same value and name.
	 * 
	 * @param obj
	 *            The object to compare with this object.
	 * @return <tt>true</tt> if this object is equal to the specified object;
	 *         <tt>false</tt> otherwise.
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof State)) {
			return false;
		}

		State that = (State) obj;

		if (value != that.value) {
			return false;
		}

		if (name == that.name) {
			return true;
		}

		if (name == null) {
			return false;
		}

		return name.equals(that.name);
	}
}
