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

package freemarker.template.expression;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;

import freemarker.template.FastScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateWriteableHashModel;

/**
 * Represents a <code>String</code> literal in a FM-Classic template.
 * 
 * @version $Id: StringLiteral.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class StringLiteral implements Expression, Serializable {
	private TemplateScalarModel value;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 7561151871681860296L;

	/**
	 * Serialized form is a single String object. This is primarily for
	 * compactness and avoids having to serialize a TemplateModel object.
	 * 
	 * @serialField stringValue String the string value that this
	 *              <code>StringLiteral</code> represents
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("stringValue", String.class) };

	/**
	 * Constructor that takes the <code>String</code> value to be generated at
	 * run time.
	 * 
	 * @param value
	 *            the <code>String</code> value to be generated
	 * @throws NullPointerException
	 *             the value is null
	 */
	public StringLiteral(String value) {
		// Due to the vagaries of Strings in Java, we make an explicit copy.
		// This is so that we don't inadvertantly hold a reference to a
		// substring of the entire unparsed template.
		this.value = new FastScalar(new String(value));
	}

	/**
	 * Retrieve the value of this <code>StringLiteral</code> as a
	 * {@link freemarker.template.TemplateModel}.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @return a <code>FastScalar</code> containing the literal
	 *         <code>String</code>
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) {
		return value;
	}

	/**
	 * Does the <code>StringLiteral</code> contain a value?
	 * 
	 * @return <code>true</code> if there is a value, otherwise
	 *         <code>false</code>
	 */
	public boolean isComplete() {
		return true;
	}

	/**
	 * Determine the type of result that can be calculated by this expression.
	 * This is in the form of an integer constant ored together from values in
	 * the {@link ExpressionUtils} class.
	 */
	public int getType() {
		return ExpressionUtils.EXPRESSION_TYPE_STRING;
	}

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant() {
		return true;
	}

	/**
	 * Return the String value of this string literal.
	 * 
	 * @return the value of this literal as a <code>String</code> object
	 */
	public String toString() {
		return '"' + value.toString() + '"';
	}

	/**
	 * Override the <code>Object.equals</code> method.
	 * 
	 * @param o
	 *            the object to compare against
	 * @return <code>true</code> if the two objects are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof StringLiteral)) {
			return false;
		}

		final StringLiteral stringLiteral = (StringLiteral) o;

		if (value == null) {
			return stringLiteral.value == null;
		} else {
			return value.equals(stringLiteral.value);
		}
	}

	/**
	 * Retrieve the hash value for this object.
	 * 
	 * @return a hash code corresponding to the value of this object
	 */
	public int hashCode() {
		return value.hashCode() + 17;
	}

	/**
	 * For serialization purposes, resolve a deserialized instance to an
	 * instance in the expression cache.
	 */
	private Object readResolve() throws ObjectStreamException {
		return ExpressionCache.cacheExpression(this);
	}

	/**
	 * For serialization, read this object as a single String object.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();
		String value = (String) fields.get("stringValue", null);

		if (value == null) {
			throw new InvalidObjectException("Cannot create a StringLiteral with a null value ");
		}

		// Recreate the original fields
		this.value = new FastScalar(value);
	}

	/**
	 * For serialization, write this object as a single String object.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();
		String value = null;

		try {
			value = this.value.getAsString();
		} catch (TemplateModelException e) {
			// Shouldn't ever happen since FastScalar doesn't throw this
			// exception
		}

		// Synthesize for compactness
		fields.put("stringValue", value);
		stream.writeFields();
	}

	/**
	 * Resolves the current expression, possibly into a different expression
	 * object. This is loosely equivalent to the serialization protocol's
	 * <code>readResolve</code> method. Situations where this may be used are:
	 * <ul>
	 * <li>Caching frequently-used expression objects</li>
	 * <li>Evaluating constant expressions, and returning a constant reference</li>
	 * </ul>
	 */
	public Expression resolveExpression() {
		return ExpressionCache.cacheExpression(this);
	}
}
