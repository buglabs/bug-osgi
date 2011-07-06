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

import java.io.ObjectStreamException;
import java.io.Serializable;

import freemarker.template.FastBoolean;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateWriteableHashModel;

/**
 * Represents a boolean literal in a FM-Classic template.
 * 
 * @version $Id: BooleanLiteral.java 1123 2005-10-04 10:48:25Z run2000 $
 * @since 1.8
 */
public final class BooleanLiteral implements Expression, Serializable {
	/** The <code>true</code> instance of this class. */
	public static final BooleanLiteral TRUE = new BooleanLiteral(true);
	/** The <code>false</code> instance of this class. */
	public static final BooleanLiteral FALSE = new BooleanLiteral(false);

	/** @serial the boolean value represented by this boolean literal. */
	private final boolean booleanValue;

	/** The serialization UUID for this class. */
	private static final long serialVersionUID = -4820468165653838133L;

	/**
	 * Constructor that takes a boolean value as a <code>boolean</code>.
	 * 
	 * @param value
	 *            the value to be held by this <code>BooleanLiteral</code>
	 */
	private BooleanLiteral(boolean value) {
		this.booleanValue = value;
	}

	/**
	 * Retrieve the value of this <code>BooleanLiteral</code> as a
	 * {@link freemarker.template.TemplateModel}.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @throws TemplateException
	 *             the expression could not be evaluated for some reason
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		return booleanValue ? FastBoolean.TRUE : FastBoolean.FALSE;
	}

	/**
	 * Does the <code>BooleanLiteral</code> have a value?
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
		return ExpressionUtils.EXPRESSION_TYPE_NUMBER | ExpressionUtils.EXPRESSION_TYPE_STRING;
	}

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant() {
		return true;
	}

	/**
	 * Factory method for retrieving instances of a <code>BooleanLiteral</code>.
	 * 
	 * @param isTrue
	 *            <code>true</code> if we want a True instance, otherwise
	 *            <code>false</code> to retrieve a False instance
	 * @return a True instance if <code>isTrue</code> is set, otherwise a False
	 *         instance
	 */
	public static BooleanLiteral getInstance(boolean isTrue) {
		return isTrue ? TRUE : FALSE;
	}

	/**
	 * Retrieve the value of this object as a String.
	 * 
	 * @return a <code>String</code> representation of this expression
	 */
	public String toString() {
		return booleanValue ? "true" : "false";
	}

	/**
	 * For serialization purposes, always resolve a de-serialized object back to
	 * the singleton instance. This ensures that any strict equality tests
	 * remain valid after serialization.
	 */
	private Object readResolve() throws ObjectStreamException {
		if (booleanValue) {
			return TRUE;
		} else {
			return FALSE;
		}
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
		return this;
	}
}
