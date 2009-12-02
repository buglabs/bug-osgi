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
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateWriteableHashModel;

/**
 * The dot operator. Used to reference items inside a
 * {@link freemarker.template.TemplateHashModel}. It associates with other
 * {@link Variable} expressions to its left.
 * 
 * @version $Id: Dot.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class Dot implements Unary, Variable, Serializable {
	/**
	 * @serial The identifier following the dot
	 */
	private final Identifier identifier;
	/**
	 * @serial The variable preceding the dot
	 */
	private Variable target;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -5994615948590817642L;

	/**
	 * Create a new <code>Dot</code> instruction.
	 * 
	 * @param id
	 *            the identifier to the right of the dot.
	 * @throws NullPointerException
	 *             the identifier is null
	 */
	public Dot(Identifier id) {
		if (id == null) {
			throw new NullPointerException("Identifier for Dot operator cannot be null");
		}
		identifier = id;
	}

	/**
	 * Retrieve the name of this portion of the variable.
	 * 
	 * @param modelRoot
	 *            the model to be used in cases where the variable is the result
	 *            of an {@link freemarker.template.expression.Expression}.
	 */
	public String getName(TemplateWriteableHashModel modelRoot) {
		return identifier.getName();
	}

	/**
	 * Are both the target and the identifier specified?
	 * 
	 * @return <code>true</code> if the target and identifier are both
	 *         specified, otherwise <code>false</code>
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
		return ExpressionUtils.EXPRESSION_TYPE_VARIABLE;
	}

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant() {
		return false;
	}

	/**
	 * The {@link freemarker.template.TemplateModel} value of this
	 * <code>Expression</code>.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @throws TemplateException
	 *             the expression could not be evaluated for some reason
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		TemplateModel leftModel = target.getAsTemplateModel(modelRoot);

		if ((leftModel == null) || (leftModel.isEmpty())) {
			return null;
		}

		TemplateModel referent;
		if (leftModel instanceof TemplateHashModel) {
			TemplateHashModel leftHash = (TemplateHashModel) leftModel;

			try {
				referent = leftHash.get(identifier.getName());
			} catch (TemplateModelException e) {
				throw new TemplateException("Couldn't get referent of " + identifier.getName(), e);
			}
		} else {

			throw new TemplateException(target.getName(modelRoot) + " is not a TemplateHashModel, it's a " + leftModel.getClass().getName());

		}

		return referent;
	}

	/**
	 * Implements a write of the given value to a writeable hash model.
	 * 
	 * @param modelRoot
	 *            the root model, for determining context
	 * @param value
	 *            the value to be assigned
	 * @throws TemplateException
	 *             there was a problem performing the assignment
	 */
	public void setTemplateModel(TemplateWriteableHashModel modelRoot, TemplateModel value) throws TemplateException {

		TemplateModel leftModel = target.getAsTemplateModel(modelRoot);

		if (leftModel == null) {
			throw new TemplateException(target.getName(modelRoot) + " is a null model");
		}

		if (leftModel instanceof TemplateWriteableHashModel) {
			TemplateWriteableHashModel leftHash = (TemplateWriteableHashModel) leftModel;

			try {
				leftHash.put(identifier.getName(), value);
			} catch (TemplateModelException e) {
				throw new TemplateException("Couldn't get referent of " + identifier.getName(), e);
			}
		} else {
			throw new TemplateException(target.getName(modelRoot) + " is not a TemplateHashModel, it's a " + leftModel.getClass().getName());
		}

	}

	/**
	 * Retrieve the operator association for this operator.
	 * 
	 * @return <code>POSTFIX</code>.
	 */
	public int getAssociationType() {
		return POSTFIX;
	}

	/**
	 * Sets the target of this <code>Dot</code> operator.
	 * 
	 * @throws IllegalArgumentException
	 *             the target is of the wrong type for this operator.
	 */
	public void setTarget(Expression target) {
		if (!(target instanceof Variable) || !(target.isComplete())) {
			throw new IllegalArgumentException("Syntax error in expression");
		}
		this.target = (Variable) target;
	}

	/**
	 * Return the precedence for this operator to the caller. Used for
	 * associating operators according to precedence.
	 * 
	 * @return an integer indicating the precedence of this operator
	 */
	public int getPrecedence() {
		return ExpressionBuilder.PRECEDENCE_VARIABLE;
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
	 * For serialization, read this object normally, then check whether the
	 * identifier has been deserialized to null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (identifier == null) {
			throw new InvalidObjectException("Cannot create an Dot with a null identifier");
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of this expression
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (target == null) {
			buffer.append("???");
		} else {
			buffer.append(target);
		}
		buffer.append('.');
		buffer.append(identifier);
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
		if (!(o instanceof Dot)) {
			return false;
		}

		final Dot dot = (Dot) o;

		if (!identifier.equals(dot.identifier)) {
			return false;
		}
		if (target == null) {
			return dot.target == null;
		}
		return target.equals(dot.target);
	}

	/**
	 * Returns the hash code for this operator.
	 * 
	 * @return the hash code of this object
	 */
	public int hashCode() {
		int result;
		result = identifier.hashCode();
		result = 29 * result + (target != null ? target.hashCode() : 0);
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
	public Expression resolveExpression() {
		return ExpressionCache.cacheExpression(this);
	}
}
