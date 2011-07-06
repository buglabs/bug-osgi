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
 * An unsynchronized, immutable variation of the {@link SimpleScalar} class.
 * This avoids the need for synchronization, since we no longer have any
 * situations where the underlying value changes. This means that
 * <code>FastScalar</code> should be significantly faster in some cases.
 * </p>
 * 
 * @version $Id: FastScalar.java 987 2004-10-05 10:13:24Z run2000 $
 * @see FastBoolean
 * @see FastHash
 * @see FastList
 * @see FastNumber
 * @since 1.7.5
 */
public final class FastScalar implements TemplateScalarModel, Serializable {

	/**
	 * The value of this <code>FastScalar</code>.
	 */
	private final String stringValue;

	/** Serialization id, for future compatibility. */
	private static final long serialVersionUID = -246961910931252863L;

	/**
	 * Constructs an instance of this object with an initial value.
	 * 
	 * @param stringValue
	 *            the value to store
	 */
	public FastScalar(String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * Returns the scalar's value as a <code>String</code>.
	 * 
	 * @return the String value of this scalar.
	 */
	public String getAsString() throws TemplateModelException {
		return stringValue;
	}

	/**
	 * Is the scalar value empty?
	 * 
	 * @return <code>true</code> if this <code>String</code> is empty or
	 *         <code>null</code>, otherwise <code>false</code>.
	 */
	public boolean isEmpty() throws TemplateModelException {
		return ((stringValue == null) || (stringValue.length() == 0));
	}

	/**
	 * Return the value of this object as a <code>String</code>.
	 */
	public String toString() {
		if (stringValue == null) {
			return "null";
		}
		return stringValue;
	}

	/**
	 * Tests this object for equality with the given object.
	 * 
	 * @param o
	 *            the object to compare against
	 * @return <code>true</code> if the objects are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof FastScalar)) {
			return false;
		}

		final FastScalar fastScalar = (FastScalar) o;

		if (stringValue == null) {
			return fastScalar.stringValue == null;
		} else {
			return stringValue.equals(fastScalar.stringValue);
		}
	}

	/**
	 * Return the hash value for this object.
	 * 
	 * @return a hash code corresponding to the object's value
	 */
	public int hashCode() {
		if (stringValue == null) {
			return 0;
		}
		return stringValue.hashCode() + 17;
	}
}
