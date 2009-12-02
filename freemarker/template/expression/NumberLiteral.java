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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;

import freemarker.template.FastNumber;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateWriteableHashModel;

/**
 * Represents a number literal in a FM-Classic template.
 * 
 * @version $Id: NumberLiteral.java 1123 2005-10-04 10:48:25Z run2000 $
 * @since 1.8
 */
public final class NumberLiteral implements Expression, Serializable {
	private TemplateNumberModel value;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -7645724468773759205L;

	/**
	 * Serialized form is a single long value. Since a number literal cannot be
	 * empty, we don't attempt a special case for it.
	 * 
	 * @serialField numberValue long the long value that this
	 *              <code>NumberLiteral</code> represents
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("numberValue", Long.TYPE) };

	/**
	 * Constructor that takes a numeric value as a <code>String</code>.
	 * 
	 * @param value
	 *            the value to be held by this <code>NumberLiteral</code>
	 * @throws NullPointerException
	 *             the value is null
	 * @throws NumberFormatException
	 *             the value could not be parsed as a long value
	 */
	public NumberLiteral(String value) {
		this.value = new FastNumber(value);
	}

	/**
	 * Retrieve the value of this <code>NumberLiteral</code> as a
	 * {@link freemarker.template.TemplateModel}.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @return a <code>FastScalar</code> containing the number
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) {
		return value;
	}

	/**
	 * Does the <code>NumberLiteral</code> have a value?
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
		return ExpressionUtils.EXPRESSION_TYPE_NUMBER;
	}

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant() {
		return true;
	}

	/**
	 * Override the <code>Object.toString</code> method.
	 * 
	 * @return a <code>String</code> representation of this expression
	 */
	public String toString() {
		return value.toString();
	}

	/**
	 * Override the <code>Object.equals</code> method.
	 * 
	 * @param o
	 *            the object we're comparing against
	 * @return <code>true</code> if the two objects are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof NumberLiteral)) {
			return false;
		}

		final NumberLiteral numberLiteral = (NumberLiteral) o;

		if (value == null) {
			return numberLiteral.value == null;
		} else {
			return value.equals(numberLiteral.value);
		}
	}

	/**
	 * Override the <code>Object.hashCode</code> method.
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
	 * For serialization, read this object as a single long value.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();
		long value = fields.get("numberValue", 0L);

		// Recreate the original fields
		this.value = new FastNumber(value);
	}

	/**
	 * For serialization, write this object as a single long value.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();
		long value = 0;

		try {
			value = this.value.getAsNumber();
		} catch (TemplateModelException e) {
			// Shouldn't ever happen since FastScalar doesn't throw this
			// exception
		}

		// Synthesize for compactness
		fields.put("numberValue", value);
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
