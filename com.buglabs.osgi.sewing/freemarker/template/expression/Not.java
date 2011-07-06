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
 * Performs a logical "not" operation on a given template model.
 * 
 * @version $Id: Not.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class Not implements Unary, Serializable {

	/** @serial the expression to be negated. */
	private Expression target;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -2236703423795583489L;

	/** Default constructor. */
	public Not() {
	}

	/**
	 * Returns a binary "not" of the expression previously set.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @return A <code>FastBoolean</code> of the "not" expression
	 * @throws TemplateException
	 *             the expression could not be evaluated for some reason
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		return FastBoolean.getInstance(!ExpressionUtils.isTrue(target.getAsTemplateModel(modelRoot)));
	}

	/**
	 * Is the target specified?
	 * 
	 * @return <code>true</code> if the target is specified, otherwise
	 *         <code>false</code>
	 */
	public boolean isComplete() {
		return (target != null);
	}

	/**
	 * Determine the type of result that can be calculated by this expression.
	 * This is in the form of an integer constant ored together from values in
	 * the {@link ExpressionUtils} class.
	 */
	public int getType() {
		return ExpressionUtils.EXPRESSION_TYPE_STRING | ExpressionUtils.EXPRESSION_TYPE_NUMBER;
	}

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant() {
		return false;
	}

	/**
	 * Retrieve the operator association for this operator.
	 * 
	 * @return <code>PREFIX</code>.
	 */
	public int getAssociationType() {
		return PREFIX;
	}

	/**
	 * Sets the target of this <code>Not</code> operator.
	 * 
	 * @param target
	 *            the target of this operator
	 * @throws NullPointerException
	 *             the expression is null
	 */
	public void setTarget(Expression target) {
		if (target == null) {
			throw new NullPointerException("Not expression needs to be non-null");
		}
		this.target = target;
	}

	/**
	 * Return the precedence for this operator to the caller. Used for
	 * associating operators according to precedence.
	 * 
	 * @return an integer indicating the precedence of this operator
	 */
	public int getPrecedence() {
		return ExpressionBuilder.PRECEDENCE_NEGATION;
	}

	/**
	 * For serialization purposes, resolve a deserialized instance to an
	 * instance in the expression cache.
	 */
	private Object readResolve() throws ObjectStreamException {
		if (isComplete()) {
			return ExpressionCache.cacheExpression(this);
		}
		return this;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of this expression
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("! ");
		if (target == null) {
			buffer.append("???");
		} else {
			buffer.append(target);
		}
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
		if (!(o instanceof Not)) {
			return false;
		}

		final Not not = (Not) o;

		if (target == null) {
			return not.target == null;
		}
		return target.equals(not.target);
	}

	/**
	 * Returns the hash code for this operator.
	 * 
	 * @return the hash code of this object
	 */
	public int hashCode() {
		return (target != null ? target.hashCode() + 13 : 0);
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

		if (target.isConstant()) {
			TemplateModel outputModel = getAsTemplateModel(ExpressionBuilder.emptyModel);
			expr = new Constant(outputModel);
		}

		return ExpressionCache.cacheExpression(expr);
	}
}
