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

import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateListModel;
import freemarker.template.TemplateListModel2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateWriteableHashModel;

/**
 * Represents a constant value within an expression. This is calculated by the
 * {@link ExpressionBuilder} and stored within the parse tree in place of a more
 * complex expression. The value is stored as a TemplateModel, which is returned
 * on demand.
 * 
 * @version $Id: Constant.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class Constant implements Expression, Serializable {
	/**
	 * @serial the constant value to be returned
	 */
	private final TemplateModel constantValue;
	private transient int constantType;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -2725667819446857448L;

	/**
	 * Creates a new constant expression with the given model as its value.
	 * 
	 * @param constantValue
	 *            the value to be returned when a template model is requested
	 * @throws NullPointerException
	 *             the value is null
	 */
	public Constant(TemplateModel constantValue) {
		if (constantValue == null) {
			throw new NullPointerException("A constant value cannot be null");
		}
		this.constantValue = constantValue;
		calculateType();
	}

	/**
	 * Calculate the type of this constant value. For now, only detect the
	 * simple type values.
	 */
	private void calculateType() {
		int type = 0;

		if (constantValue instanceof TemplateScalarModel) {
			type |= ExpressionUtils.EXPRESSION_TYPE_STRING;
		}
		if (constantValue instanceof TemplateNumberModel) {
			type |= ExpressionUtils.EXPRESSION_TYPE_NUMBER;
		}
		if ((constantValue instanceof TemplateListModel) || (constantValue instanceof TemplateListModel2)) {
			type |= ExpressionUtils.EXPRESSION_TYPE_LIST;
		}
		if (constantValue instanceof TemplateHashModel) {
			type |= ExpressionUtils.EXPRESSION_TYPE_HASH;
		}

		// For now, we don't store these types of constant expressions,
		// since we have no specific need for them. More specifically,
		// we currently have no operators that return a method or
		// transformer. Uncomment this code if we ever implement any.

		/*
		 * if( constantValue instanceof TemplateMethodModel ) { type |=
		 * ExpressionUtils.EXPRESSION_TYPE_METHOD; } if(( constantValue
		 * instanceof TemplateTransformModel ) || ( constantValue instanceof
		 * TemplateTransformModel2 )) { type |=
		 * ExpressionUtils.EXPRESSION_TYPE_TRANSFORM; }
		 */

		constantType = type;
	}

	/**
	 * Is the <code>Expression</code> complete?
	 * 
	 * @return <code>true</code> since the constant <code>Expression</code> is
	 *         always complete
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
		return constantType;
	}

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant() {
		return true;
	}

	/**
	 * The {@link TemplateModel} value of this constant <code>Expression</code>.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @throws TemplateException
	 *             the expression could not be evaluated for some reason
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		return constantValue;
	}

	/**
	 * For serialization purposes, resolve a deserialized instance to an
	 * instance in the expression cache.
	 */
	private Object readResolve() throws ObjectStreamException {
		return ExpressionCache.cacheExpression(this);
	}

	/**
	 * For serialization, read this object normally, check for null, and
	 * recalculate the expression type.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		stream.defaultReadObject();
		if (constantValue == null) {
			throw new InvalidObjectException("Cannot deserialize constant expression to null value");
		}
		calculateType();
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of this constant expression
	 */
	public String toString() {
		return constantValue.toString();
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
		if (!(o instanceof Constant)) {
			return false;
		}

		final Constant constant = (Constant) o;
		return constantValue.equals(constant.constantValue);
	}

	/**
	 * Returns the hash code for this constant expression.
	 * 
	 * @return the hash code of this object
	 */
	public int hashCode() {
		return constantValue.hashCode();
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
