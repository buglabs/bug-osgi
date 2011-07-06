/*
 * FreeMarker: a tool that allows Java programs to generate HTML
 * output using templates.
 * Copyright (C) 1998-2005 Benjamin Geer
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

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateWriteableHashModel;

/**
 * Represents an empty literal in a FM-Classic template. Typically used to
 * support the "#empty" identifer.
 * 
 * @version $Id: EmptyLiteral.java 1123 2005-10-04 10:48:25Z run2000 $
 * @since 1.9
 */
public class EmptyLiteral implements Expression, Serializable {

	/** The canonical instance of this class. */
	public static final EmptyLiteral EMPTY = new EmptyLiteral();

	/** The serialization UUID for this class. */
	private static final long serialVersionUID = 379509679679977545L;

	/**
	 * Constructor for the canonical representation of the empty literal.
	 */
	private EmptyLiteral() {
	}

	/**
	 * Retrieve the value of this <code>EmptyLiteral</code> as a
	 * {@link freemarker.template.TemplateModel}.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @throws TemplateException
	 *             the expression could not be evaluated for some reason
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		return null;
	}

	/**
	 * Is the <code>Expression</code> complete?
	 * 
	 * @return <code>true</code> if this <code>Expression</code> is complete,
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
		return ExpressionUtils.EXPRESSION_TYPE_VARIABLE;
	}

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant() {
		return true;
	}

	/**
	 * Factory method for retrieving instances of a <code>EmptyLiteral</code>.
	 */
	public EmptyLiteral getInstance() {
		return EMPTY;
	}

	/**
	 * Retrieve the value of this object as a String.
	 * 
	 * @return a <code>String</code> representation of this expression
	 */
	public String toString() {
		return "(empty)";
	}

	/**
	 * For serialization purposes, always resolve a de-serialized object back to
	 * the singleton instance. This ensures that any strict equality tests
	 * remain valid after serialization.
	 */
	private Object readResolve() throws ObjectStreamException {
		return EMPTY;
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
		return this;
	}
}
