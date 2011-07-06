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
 * Performs an inequality test on two template models.
 * 
 * @version $Id: NotEquals.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class NotEquals extends AbstractBinary implements Serializable {

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 3598906373248105071L;

	/** Default constructor. */
	public NotEquals() {
	}

	/**
	 * Sets the left-hand side of the expression.
	 * 
	 * @param left
	 *            the left-hand side of the expression
	 * @throws NullPointerException
	 *             the argument was null
	 * @throws IllegalArgumentException
	 *             the argument was neither a String nor a number
	 */
	public void setLeft(Expression left) {
		if ((left.getType() & (ExpressionUtils.EXPRESSION_TYPE_STRING | ExpressionUtils.EXPRESSION_TYPE_NUMBER)) == 0) {
			throw new IllegalArgumentException("Scalar or numeric required for notequals expression");
		}
		super.setLeft(left);
	}

	/**
	 * Sets the right-hand side of the expression.
	 * 
	 * @param right
	 *            the right-hand side of the expression
	 * @throws NullPointerException
	 *             the argument was null
	 * @throws IllegalArgumentException
	 *             the argument was neither a String nor a number
	 */
	public void setRight(Expression right) {
		if ((right.getType() & (ExpressionUtils.EXPRESSION_TYPE_STRING | ExpressionUtils.EXPRESSION_TYPE_NUMBER)) == 0) {
			throw new IllegalArgumentException("Scalar or numeric required for notequals expression");
		}
		super.setRight(right);
	}

	/**
	 * Returns the inequality of two expressions previously set.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @return A <code>FastBoolean</code> of the "not-equals" expression
	 * @throws TemplateException
	 *             the expression could not be evaluated for some reason
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		return FastBoolean.getInstance(!ExpressionUtils.isEqual(left.getAsTemplateModel(modelRoot), right.getAsTemplateModel(modelRoot)));
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
	 * Return the precedence for this operator to the caller. Used for
	 * associating operators according to precedence.
	 * 
	 * @return an integer indicating the precedence of this operator
	 */
	public int getPrecedence() {
		return ExpressionBuilder.PRECEDENCE_EQUALITY;
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
		buffer.append('(');
		buffer.append(left);
		buffer.append(" ne ");
		buffer.append(right);
		buffer.append(')');
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
		if (!(o instanceof NotEquals)) {
			return false;
		}

		final AbstractBinary abstractBinary = (AbstractBinary) o;

		if (left == null ? abstractBinary.left != null : !left.equals(abstractBinary.left))
			return false;
		if (right == null ? abstractBinary.right != null : !right.equals(abstractBinary.right))
			return false;

		return true;
	}

	/**
	 * Returns the hash code for this operator.
	 * 
	 * @return the hash code of this object
	 */
	public int hashCode() {
		int result;
		result = (left != null ? left.hashCode() : 0);
		result = 29 * result + (right != null ? right.hashCode() : 0);
		result = 29 * result + 41;
		return result;
	}
}
