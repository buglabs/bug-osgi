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
import java.io.ObjectStreamException;
import java.io.Serializable;

import freemarker.template.FastListRange;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateWriteableHashModel;

/**
 * An expression that represents a indexModel of list values. The from and to
 * expressions are held until evaluation time, then a
 * {@link freemarker.template.FastListRange} is created. A simple
 * {@link freemarker.template.FastIndexedIterator} is used to iterate over the
 * values.
 * 
 * @version $Id: ListRange.java 1123 2005-10-04 10:48:25Z run2000 $
 * @since 1.8
 */
public final class ListRange implements Expression, Serializable {
	/**
	 * @serial An expression that evaluates to the start of the list range.
	 */
	private final Expression fromRange;
	/**
	 * @serial An expression that evaluates to the end of the list range.
	 */
	private final Expression toRange;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 9096757710628448671L;

	/**
	 * Constructor that takes from and to values in the form of expressions. The
	 * expressions are evaluated at template run time and converted into values
	 * usable by the {@link freemarker.template.FastListRange}.
	 * 
	 * @param fromRange
	 *            the start of the range, as an expression
	 * @param toRange
	 *            the end of the range, as an expression
	 * @throws NullPointerException
	 *             fromRange or toRange are null
	 * @throws IllegalArgumentException
	 *             the expressions cannot be evaluated as numbers
	 */
	public ListRange(Expression fromRange, Expression toRange) {

		if ((fromRange.getType() & ExpressionUtils.EXPRESSION_TYPE_NUMBER) == 0 || (toRange.getType() & ExpressionUtils.EXPRESSION_TYPE_NUMBER) == 0) {
			throw new IllegalArgumentException("Range cannot be created from non-numeric arguments");
		}

		this.fromRange = fromRange;
		this.toRange = toRange;
	}

	/**
	 * Retrieve the list range as a template model.
	 * 
	 * @param modelRoot
	 *            the model from which to evaluate the range of the list
	 * @return a new <code>FastListRange</code> representing the range list
	 * @throws TemplateException
	 *             the new range could not be created
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		long fromValue = ExpressionUtils.getAsNumber(fromRange.getAsTemplateModel(modelRoot));
		long toValue = ExpressionUtils.getAsNumber(toRange.getAsTemplateModel(modelRoot));
		return new FastListRange(fromValue, toValue);
	}

	/**
	 * Do we have from and to values?
	 * 
	 * @return <code>true</code> if both from and to values are specified,
	 *         otherwise <code>false</code>
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
		return ExpressionUtils.EXPRESSION_TYPE_LIST;
	}

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant() {
		return false;
	}

	/**
	 * For serialization purposes, resolve a deserialized instance to an
	 * instance in the expression cache.
	 */
	private Object readResolve() throws ObjectStreamException {
		return ExpressionCache.cacheExpression(this);
	}

	/**
	 * For serialization, read this object normally, then check whether either
	 * fromRange or toRange has been deserialized to null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if ((fromRange == null) || (toRange == null)) {
			throw new InvalidObjectException("Cannot create a ListRange with null expressions");
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of this expression
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[ ");
		buffer.append(fromRange);
		buffer.append(" .. ");
		buffer.append(toRange);
		buffer.append(" ]");
		return buffer.toString();
	}

	/**
	 * Determines whether this object is equal to the given object.
	 * 
	 * @param o
	 *            the object to be compared with
	 * @return <code>true</code> if the objects are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ListRange)) {
			return false;
		}

		final ListRange listRange = (ListRange) o;

		if (!fromRange.equals(listRange.fromRange))
			return false;
		return toRange.equals(listRange.toRange);
	}

	/**
	 * Returns the hash code for this operator.
	 * 
	 * @return the hash code of this object
	 */
	public int hashCode() {
		int result;
		result = fromRange.hashCode();
		result = 29 * result + toRange.hashCode();
		return result;
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
	public Expression resolveExpression() throws TemplateException {
		Expression expr = this;

		// If the from- and to-range are constants, make this expression
		// a constant.
		if (fromRange.isConstant() && toRange.isConstant()) {

			long fromValue = ExpressionUtils.getAsNumber(fromRange.getAsTemplateModel(ExpressionBuilder.emptyModel));
			long toValue = ExpressionUtils.getAsNumber(toRange.getAsTemplateModel(ExpressionBuilder.emptyModel));
			expr = new Constant(new FastListRange(fromValue, toValue));
		}

		return ExpressionCache.cacheExpression(expr);
	}
}
