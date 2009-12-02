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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

/**
 * <p>
 * A simple implementation of the {@link TemplateScalarModel} interface, using a
 * <code>String</code> or a <code>boolean</code>.
 * </p>
 * 
 * <p>
 * All the public methods in this implementation are synchronized.
 * </p>
 * 
 * @version $Id: SimpleScalar.java 987 2004-10-05 10:13:24Z run2000 $
 * @see SimpleNumber
 * @see SimpleList
 * @see SimpleHash
 */
public class SimpleScalar implements TemplateScalarModel, Serializable {

	/**
	 * The value of this <code>SimpleScalar</code> if it wraps a
	 * <code>String</code>.
	 */
	protected String stringValue;

	/**
	 * The value of this <code>SimpleScalar</code> if it wraps a
	 * <code>boolean</code>.
	 */
	protected boolean booleanValue;

	/** Serialization id, for compatibility with 1.7 */
	private static final long serialVersionUID = -1990000765027659736L;

	/**
	 * Serialized form matches the form used in FreeMarker 1.7. This is actually
	 * more verbose than required, but backward compatibility means we do it
	 * this way.
	 * 
	 * @serialField stringValue String the <code>String</code> value of this
	 *              scalar
	 * @serialField booleanValue boolean the <code>boolean</code> value of this
	 *              scalar
	 * @serialField useBoolean boolean for backward compatibility, do we use the
	 *              String or the boolean value?
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("stringValue", String.class), new ObjectStreamField("booleanValue", Boolean.TYPE),
			new ObjectStreamField("useBoolean", Boolean.TYPE) };

	/**
	 * Constructs an empty <code>SimpleScalar</code>.
	 */
	public SimpleScalar() {
	}

	/**
	 * Constructs a <code>SimpleScalar</code> containing a string value.
	 * 
	 * @param value
	 *            the string value.
	 */
	public SimpleScalar(String value) {
		setValue(value);
	}

	/**
	 * Constructs a <code>SimpleScalar</code> containing a boolean value.
	 * 
	 * @param value
	 *            the boolean value.
	 */
	public SimpleScalar(boolean value) {
		setValue(value);
	}

	/**
	 * Is this <code>SimpleScalar</code> empty?
	 * 
	 * @return <code>true</code> if this object is empty, otherwise
	 *         <code>false</code>
	 */
	public synchronized boolean isEmpty() throws TemplateModelException {
		return !booleanValue;
	}

	/**
	 * Returns the scalar's value as a <code>String</code>.
	 * 
	 * @return the <code>String</code> value of this scalar.
	 */
	public synchronized String getAsString() throws TemplateModelException {
		return stringValue;
	}

	/**
	 * Sets the <code>String</code> value of this <code>SimpleScalar</code>.
	 * 
	 * @param value
	 *            the <code>String</code> value.
	 */
	public synchronized void setValue(String value) {
		this.stringValue = value;
		this.booleanValue = (value != null) && (value.length() > 0);
	}

	/**
	 * Sets the <code>boolean</code> value of this <tt>SimpleScalar</tt>.
	 * 
	 * @param value
	 *            the <tcodet>boolean</code> value.
	 */
	public synchronized void setValue(boolean value) {
		this.booleanValue = value;
		this.stringValue = value ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
	}

	/**
	 * Retrieve the <code>String</code> value of this object.
	 */
	public synchronized String toString() {
		return stringValue;
	}

	/**
	 * For serialization, write this object in a 1.7 compatible manner. This
	 * adds the extra <code>useBoolean</code> flag that 1.7 needs. When
	 * deserialized, the <code>defaultReadObject</code> method will do the right
	 * thing.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();

		fields.put("stringValue", stringValue);
		fields.put("booleanValue", booleanValue);

		// Synthesize this property for 1.7 compatibility
		fields.put("useBoolean", (booleanValue == false));
		stream.writeFields();
	}

	/**
	 * Tests this object for equality with the given object.
	 * 
	 * @param o
	 *            the object to be compared against
	 * @return <code>true</code> if the object is equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SimpleScalar)) {
			return false;
		}

		final SimpleScalar simpleScalar = (SimpleScalar) o;

		if (booleanValue != simpleScalar.booleanValue) {
			return false;
		}

		if (stringValue == null) {
			return simpleScalar.stringValue == null;
		}

		return stringValue.equals(simpleScalar.stringValue);
	}

	/**
	 * Return the hash value for this object.
	 * 
	 * @return a hash code corresponding to the value of this SimpleScalar
	 */
	public int hashCode() {
		int result;
		result = (stringValue != null ? stringValue.hashCode() : 0);
		result = 29 * result + (booleanValue ? 1 : 0);
		return result;
	}
}
