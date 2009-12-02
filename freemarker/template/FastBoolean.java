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
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;

/**
 * <p>
 * An unsynchronised, immutable variation of the {@link SimpleScalar} class to
 * handle boolean values. This avoids the need for synchronization, since we no
 * longer have any situations where the underlying value changes. This means
 * that <code>FastBoolean</code> should be significantly faster in some cases.
 * </p>
 * 
 * <p>
 * As of 1.8, this class also implements the {@link TemplateNumberModel}
 * interface, in order to simplify casting of boolean literals to numbers.
 * </p>
 * 
 * <p>
 * <b>Note:</b><br />
 * Unlike the other Fast classes, <code>FastBoolean</code> uses a factory method
 * to create instances. This can dramatically reduce the amount of garbage
 * generated by reusing the same objects when possible.
 * </p>
 * 
 * @version $Id: FastBoolean.java 1080 2005-08-28 10:18:09Z run2000 $
 * @see FastHash
 * @see FastList
 * @see FastScalar
 * @see FastNumber
 * @since 1.7.5
 */
public final class FastBoolean implements TemplateScalarModel, TemplateNumberModel, TemplateObjectModel, Serializable {

	/** Represents a true boolean expression. */
	public static final FastBoolean TRUE = new FastBoolean(true);
	/** Represents a false boolean expression. */
	public static final FastBoolean FALSE = new FastBoolean(false);

	private boolean isEmpty;
	private short numberValue;
	private String stringValue;

	/** Serialization id, for future compatibility. */
	private static final long serialVersionUID = 3879422004795437198L;

	/**
	 * Serialize as a single boolean value, for compactness.
	 * 
	 * @serialField booleanValue boolean the boolean value that this
	 *              <code>FastBoolean</code> represents
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("booleanValue", Boolean.TYPE) };

	/**
	 * Constructs an instance of this object with an initial value.
	 * 
	 * @param value
	 *            the value of the boolean expression
	 */
	private FastBoolean(boolean value) {
		isEmpty = !value;

		if (value) {
			stringValue = "true";
			numberValue = 1;
		} else {
			stringValue = null;
			numberValue = 0;
		}
	}

	/**
	 * Returns the boolean value as a <code>String</code>.
	 * 
	 * @return the <code>String</code> value of this scalar.
	 */
	public String getAsString() throws TemplateModelException {
		return stringValue;
	}

	/**
	 * Returns the boolean value as a <code>long</code>.
	 * 
	 * @return the <code>long</code> value of this scalar.
	 * @since 1.8
	 */
	public long getAsNumber() throws TemplateModelException {
		return numberValue;
	}

	/**
	 * Is the model empty?
	 * 
	 * @return <code>true</code> if this object is empty, otherwise
	 *         <code>false</code>
	 */
	public boolean isEmpty() throws TemplateModelException {
		return isEmpty;
	}

	/**
	 * Return the model as a Boolean object. This is for the benefit of the
	 * reflection library.
	 * 
	 * @return <code>Boolean.TRUE</code> if this object is true, otherwise
	 *         <code>Boolean.FALSE</code>
	 */
	public Object getAsObject() throws TemplateModelException {
		return isEmpty ? Boolean.FALSE : Boolean.TRUE;
	}

	/**
	 * Factory method for retrieving instances of a <code>FastBoolean</code>.
	 * 
	 * @param isTrue
	 *            <code>true</code> if we want a True instance, otherwise
	 *            <code>false</code> to retrieve a False instance
	 * @return a True instance if <code>isTrue</code> is set, otherwise a False
	 *         instance
	 */
	public static FastBoolean getInstance(boolean isTrue) {
		return isTrue ? TRUE : FALSE;
	}

	/**
	 * Returns true if the passed object is the TRUE instance.
	 * 
	 * @param value
	 *            the value to compare against the TRUE instance
	 * @return <code>true</code> if this is the TRUE instance, otherwise
	 *         <code>false</code>
	 */
	public static boolean getBoolean(Object value) {
		return value == TRUE;
	}

	/**
	 * For serialization purposes, always resolve a de-serialized object back to
	 * the singleton instance. This ensures that any strict equality tests
	 * remain valid after serialization.
	 */
	private Object readResolve() throws ObjectStreamException {
		if (isEmpty) {
			return FALSE;
		} else {
			return TRUE;
		}
	}

	/**
	 * For serialization, read this object as a single boolean value.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();
		boolean value = fields.get("booleanValue", false);

		// Recreate the original fields
		isEmpty = !value;
		if (value) {
			stringValue = "true";
			numberValue = 1;
		} else {
			stringValue = null;
			numberValue = 0;
		}
	}

	/**
	 * For serialization, write this object as a single boolean value.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();

		// Synthesize for compactness
		fields.put("booleanValue", !isEmpty);
		stream.writeFields();
	}

	/**
	 * Retrieve the value of this object as a <code>String</code>.
	 */
	public String toString() {
		return isEmpty ? "false" : "true";
	}
}
