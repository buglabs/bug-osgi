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
import freemarker.template.TemplateIndexedModel;
import freemarker.template.TemplateListModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.TemplateWriteableIndexedModel;

/**
 * A unary operator that uses the string value of an expression as a hash key.
 * It associates with other {@link Variable} expressions to its left.
 * 
 * @version $Id: DynamicKeyName.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class DynamicKeyName implements Unary, Variable, Serializable {
	/**
	 * @serial the expression to be evaluated when determining the key
	 */
	private final Expression keyName;
	/**
	 * @serial The variable preceding the dynamic key name
	 */
	private Variable target;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 1475259725669639671L;

	/**
	 * Constructor that takes an {@link Expression} used to evaluate the key
	 * name at run time.
	 * 
	 * @param key
	 *            the expression to be used to evaluate the key name
	 * @throws NullPointerException
	 *             the key expression was null
	 * @throws IllegalArgumentException
	 *             the key could not be evaluated as a number or a String
	 */
	public DynamicKeyName(Expression key) {
		if ((key.getType() & (ExpressionUtils.EXPRESSION_TYPE_NUMBER | ExpressionUtils.EXPRESSION_TYPE_STRING)) == 0) {
			throw new IllegalArgumentException("Dynamic key name must be a number or a String");
		}
		keyName = key;
	}

	/**
	 * Sets the target of the dynamic key.
	 * 
	 * @param target
	 *            the target of the dynamic key
	 * @throws IllegalArgumentException
	 *             the expression was not suitable for a dynamic key name
	 *             evaluation
	 */
	public void setTarget(Expression target) {
		if (!(target instanceof Variable) || !(target.isComplete())) {
			throw new IllegalArgumentException("Variable expression required for dynamic key name");
		}
		this.target = (Variable) target;
	}

	/**
	 * Are both the key name and target specified?
	 * 
	 * @return <code>true</code> if both target and key name are specified,
	 *         otherwise <code>false</code>
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
	 * Retrieve the operator association for this operator.
	 * 
	 * @return <code>POSTFIX</code>.
	 */
	public int getAssociationType() {
		return POSTFIX;
	}

	/**
	 * Sets the target of this <code>DynamicKeyName</code> operator.
	 * 
	 * @throws TemplateException
	 *             the target is of the wrong type for this operator.
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		TemplateModel targetModel = target.getAsTemplateModel(modelRoot);

		if ((targetModel == null) || (targetModel.isEmpty())) {
			return null;
		}

		TemplateModel key = keyName.getAsTemplateModel(modelRoot);

		if (targetModel instanceof TemplateIndexedModel) {
			if (key instanceof TemplateNumberModel) {
				TemplateIndexedModel targetList = (TemplateIndexedModel) targetModel;
				long index = ExpressionUtils.getAsNumber(key);

				try {
					return targetList.getAtIndex(index);
				} catch (TemplateModelException e) {
					throw new TemplateException("Couldn't get referent of " + index, e);
				}
			}
		} else if (targetModel instanceof TemplateListModel) {

			if (key instanceof TemplateNumberModel) {
				TemplateListModel targetList = (TemplateListModel) targetModel;
				long index = ExpressionUtils.getAsNumber(key);

				try {
					return targetList.get((int) index);
				} catch (TemplateModelException e) {
					throw new TemplateException("Couldn't get referent of " + index, e);
				}
			}
		}
		if (targetModel instanceof TemplateHashModel) {
			TemplateHashModel targetHash = (TemplateHashModel) targetModel;
			String keyName1 = ExpressionUtils.getAsString(key);

			try {
				return targetHash.get(keyName1);
			} catch (TemplateModelException e) {
				throw new TemplateException("Couldn't get referent of " + keyName1, e);
			}
		}
		throw new TemplateException(target.getName(modelRoot) + " is not a TemplateListModel or a TemplateHashModel, " + "or key does not match the expected type. It's a "
				+ targetModel.getClass().getName() + ", while the key is a " + key.getClass().getName());
	}

	/**
	 * Implements a write of the given value to a writeable hash model or
	 * writeable list model.
	 * 
	 * @param modelRoot
	 *            the root model, for determining context
	 * @param value
	 *            the value to be assigned
	 * @throws TemplateException
	 *             there was a problem performing the assignment
	 */
	public void setTemplateModel(TemplateWriteableHashModel modelRoot, TemplateModel value) throws TemplateException {

		TemplateModel targetModel = target.getAsTemplateModel(modelRoot);

		if (targetModel == null) {
			return;
		}

		TemplateModel key = keyName.getAsTemplateModel(modelRoot);

		if (targetModel instanceof TemplateWriteableIndexedModel) {
			if (key instanceof TemplateNumberModel) {
				TemplateWriteableIndexedModel targetList = (TemplateWriteableIndexedModel) targetModel;
				long index = ExpressionUtils.getAsNumber(key);

				try {
					targetList.putAtIndex(index, value);
					return;
				} catch (TemplateModelException e) {
					throw new TemplateException("Couldn't put indexed value at " + index, e);
				}
			}
		} else if (targetModel instanceof TemplateWriteableHashModel) {
			TemplateWriteableHashModel targetHash = (TemplateWriteableHashModel) targetModel;
			String keyName1 = ExpressionUtils.getAsString(key);

			try {
				targetHash.put(keyName1, value);
				return;
			} catch (TemplateModelException e) {
				throw new TemplateException("Couldn't put hash value of " + keyName1, e);
			}
		}
		throw new TemplateException(target.getName(modelRoot) + " is not a TemplateWriteableListModel or a TemplateWriteableHashModel, "
				+ "or key does not match the expected type. It's a " + targetModel.getClass().getName() + ", while the key is a " + key.getClass().getName());
	}

	/**
	 * Retrieve the name of this portion of the variable.
	 * 
	 * @param modelRoot
	 *            the model to be used in cases where the variable is the result
	 *            of an {@link freemarker.template.expression.Expression}.
	 * @throws TemplateException
	 *             the name could not be determined
	 */
	public String getName(TemplateWriteableHashModel modelRoot) throws TemplateException {
		return ExpressionUtils.getAsString(keyName.getAsTemplateModel(modelRoot));
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
	 * For serialization, read this object normally, then check whether the key
	 * name has been deserialized to null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (keyName == null) {
			throw new InvalidObjectException("Cannot create an DynamicKeyName with a null key name");
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
		buffer.append('[');
		buffer.append(keyName);
		buffer.append(']');
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
		if (!(o instanceof DynamicKeyName)) {
			return false;
		}

		final DynamicKeyName dynamicKeyName = (DynamicKeyName) o;

		if (!keyName.equals(dynamicKeyName.keyName)) {
			return false;
		}
		if (target == null) {
			return dynamicKeyName.target == null;
		}
		return target.equals(dynamicKeyName.target);
	}

	/**
	 * Returns the hash code for this operator.
	 * 
	 * @return the hash code of this object
	 */
	public int hashCode() {
		int result;
		result = keyName.hashCode();
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