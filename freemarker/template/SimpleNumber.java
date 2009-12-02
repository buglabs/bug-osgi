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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

/**
 * <p>
 * A simple implementation of the {@link TemplateNumberModel} interface using a
 * <code>long</code>.
 * </p>
 * 
 * <p>
 * All the public methods in this implementation are synchronized.
 * </p>
 * 
 * @version $Id: SimpleNumber.java 987 2004-10-05 10:13:24Z run2000 $
 * @see SimpleHash
 * @see SimpleList
 * @see SimpleScalar
 * @since 1.8
 */
public class SimpleNumber implements TemplateNumberModel, Serializable {
	/** The number stored in this <code>SimpleNumber</code> */
	protected long numberValue;
	/** Whether this <code>SimpleNumber</code> is currently empty. */
	protected boolean empty;

	/** Serialization id, for compatibility with 1.8 */
	private static final long serialVersionUID = 8890816762797344438L;

	/**
	 * Serialized form is a single <code>Long</code> value, which may be null.
	 * This makes serialized form easy, and deals with the empty value
	 * conveniently.
	 * 
	 * @serialField numberValue Long the <code>Long</code> value of this number,
	 *              or <code>null</code> if the number is empty
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("numberValue", Long.class) };

	/**
	 * Constructs a new <code>SimpleNumber</code> with a default value of 0.
	 */
	public SimpleNumber() {
		empty = true;
	}

	/**
	 * Constructs a new <code>SimpleNumber</code> with the given
	 * <code>long</code> value.
	 * 
	 * @param numberValue
	 *            the long value
	 */
	public SimpleNumber(long numberValue) {
		this.numberValue = numberValue;
	}

	/**
	 * Constructs a new <code>SimpleNumber</code> with the given
	 * <code>Number</code> value.
	 * 
	 * @param numberValue
	 *            the Number value
	 */
	public SimpleNumber(Number numberValue) {
		if (numberValue == null) {
			empty = true;
		} else {
			this.numberValue = numberValue.longValue();
		}
	}

	/**
	 * Constructs a new <code>SimpleNumber</code> with the given
	 * <code>String</code> value.
	 * 
	 * @param stringValue
	 *            the String to be converted to a number
	 * @throws NullPointerException
	 *             the String is null
	 * @throws NumberFormatException
	 *             the String could not be parsed as a long
	 */
	public SimpleNumber(String stringValue) {
		numberValue = Long.parseLong(stringValue);
	}

	/**
	 * Is this <code>SimpleNumber</code> empty?
	 * 
	 * @return <code>true</code> if this object is empty, otherwise
	 *         <code>false</code>
	 */
	public synchronized boolean isEmpty() throws TemplateModelException {
		return empty;
	}

	/**
	 * Return the number value as a <code>long</code>.
	 * 
	 * @return the number value
	 */
	public synchronized long getAsNumber() throws TemplateModelException {
		return numberValue;
	}

	/**
	 * Sets the value of this <code>SimpleNumber</code>
	 * 
	 * @param value
	 *            the <code>long</code> value
	 */
	public synchronized void setValue(long value) {
		numberValue = value;
		empty = false;
	}

	/**
	 * Sets the value of this <code>SimpleNumber</code>
	 * 
	 * @param value
	 *            the <code>Number</code> value
	 */
	public synchronized void setValue(Number value) {
		empty = (value == null);
		if (!empty) {
			numberValue = value.longValue();
		}
	}

	/**
	 * Sets the value of this <code>SimpleNumber</code> to the given
	 * <code>String</code> value.
	 * 
	 * @param stringValue
	 *            the String to be converted to a number
	 * @throws NullPointerException
	 *             the String is null
	 * @throws NumberFormatException
	 *             the String could not be parsed as a long
	 */
	public synchronized void setValue(String stringValue) {
		numberValue = Long.parseLong(stringValue);
		empty = false;
	}

	/**
	 * Retrieve the <code>String</code> value of this object.
	 */
	public synchronized String toString() {
		if (empty) {
			return "null";
		}
		return Long.toString(numberValue);
	}

	/**
	 * For serialization, write this object as a single <code>Long</code> value.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();

		if (empty) {
			fields.put("numberValue", null);
		} else {
			fields.put("numberValue", new Long(numberValue));
		}

		// Synthesize this property for 1.8 compatibility
		stream.writeFields();
	}

	/**
	 * For serialization, read this object as a single <code>Long</code> value.
	 * If <code>null</code>, assume the object is empty.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();
		Long value = (Long) fields.get("numberValue", null);

		// Recreate the original fields
		empty = (value == null);
		if (value != null) {
			numberValue = value.longValue();
		}
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
		if (!(o instanceof SimpleNumber)) {
			return false;
		}

		final SimpleNumber simpleNumber = (SimpleNumber) o;

		if (empty != simpleNumber.empty) {
			return false;
		}
		if (empty == true) {
			return true;
		}
		return (numberValue == simpleNumber.numberValue);
	}

	/**
	 * Return the hash value for this object.
	 * 
	 * @return the hash code corresponding to the value of this object
	 */
	public int hashCode() {
		int result;
		if (empty)
			return 9;
		result = (int) (numberValue ^ (numberValue >>> 32));
		return result * 29;
	}
}
