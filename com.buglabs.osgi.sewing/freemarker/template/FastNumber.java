/*
 * FreeMarker: a tool that allows Java programs to generate HTML
 * output using templates.
 * Copyright (C) 1998-2004 Benjamin Geer
 * Email: beroul@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */

package freemarker.template;

import java.io.Serializable;

/**
 * <p>
 * An unsynchronized, immutable implementation of the
 * {@link TemplateNumberModel} interface. This avoids the need for
 * synchronization, since we don't have any situations where the underlying
 * value changes.
 * </p>
 * 
 * @version $Id: FastNumber.java 987 2004-10-05 10:13:24Z run2000 $
 * @see FastBoolean
 * @see FastHash
 * @see FastList
 * @see FastScalar
 * @since 1.8
 */
public final class FastNumber implements TemplateNumberModel, Serializable {

	/**
	 * The value of this <code>FastNumber</code>.
	 */
	private final long numberValue;

	/** Serialization id, for future compatibility */
	private static final long serialVersionUID = -5922840888258397822L;

	/**
	 * Constructs an instance of this object with an initial value.
	 * 
	 * @param stringValue
	 *            the value to store
	 * @throws NullPointerException
	 *             the value is null
	 * @throws NumberFormatException
	 *             the value could not be parsed as a long
	 */
	public FastNumber(String stringValue) {
		this.numberValue = Long.parseLong(stringValue);
	}

	/**
	 * Constructs an instance of this object with an initial value.
	 * 
	 * @param longValue
	 *            the value to store
	 */
	public FastNumber(long longValue) {
		this.numberValue = longValue;
	}

	/**
	 * Constructs an instance of this object with an initial value.
	 * 
	 * @param numberValue
	 *            the value to store
	 */
	public FastNumber(Number numberValue) {
		this.numberValue = numberValue.longValue();
	}

	/**
	 * Returns the scalar's value as a <code>String</code>.
	 * 
	 * @return the String value of this scalar.
	 */
	public long getAsNumber() throws TemplateModelException {
		return numberValue;
	}

	/**
	 * Is the scalar value empty?
	 * 
	 * @return <code>true</code> if this <code>String</code> is empty or
	 *         <code>null</code>, otherwise <code>false</code>.
	 */
	public boolean isEmpty() throws TemplateModelException {
		return false;
	}

	/**
	 * Return the value of this object as a <code>String</code>.
	 */
	public String toString() {
		return Long.toString(numberValue);
	}

	/**
	 * Tests this object for equality with the given object.
	 * 
	 * @param o
	 *            the object to be compared against
	 * @return <code>true</code> if the objects are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof FastNumber)) {
			return false;
		}

		final FastNumber fastNumber = (FastNumber) o;
		return (numberValue == fastNumber.numberValue);
	}

	/**
	 * Return the hash value for this object.
	 * 
	 * @return a hash code corresponding to the value of this object
	 */
	public int hashCode() {
		return (int) (numberValue ^ (numberValue >>> 32));
	}
}
