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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import freemarker.template.FastList;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateWriteableHashModel;

/**
 * Represents a literal List model in a FM-Classic template. The list model is
 * not evaluated until run time, since the model may contain variables or other
 * more complex expressions that can't be determined at compile time.
 * 
 * @version $Id: ListLiteral.java 1149 2005-10-09 07:41:19Z run2000 $
 */
public final class ListLiteral implements Expression, Serializable {
	private List values;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -470184301148036096L;

	/**
	 * Serialized form is an array of zero or more Expression objects. This is
	 * primarily for type correctness and avoids having to serialize a List
	 * object.
	 * 
	 * @serialField
	 *                  values Expression[] an array of Expression objects that
	 *                  will be evaluated as template models at run time
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("values", Expression[].class) };

	/**
	 * Constructor that takes a list of {@link Expression} elements to be
	 * evaluated as a list model at run time.
	 * 
	 * @param values
	 *            the values to be added to the <code>ListLiteral</code>
	 * @throws NullPointerException
	 *             the value list is null
	 */
	public ListLiteral(List values) {
		if (values == null) {
			throw new NullPointerException("List literal cannot be null");
		}
		this.values = values;
	}

	/**
	 * The {@link freemarker.template.TemplateModel} value of this
	 * <code>Expression</code>.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @return a <code>FastList</code> containing the values in the list model
	 * @throws TemplateException
	 *             the expression could not be evaluated for some reason
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		return new FastList(getModelList(modelRoot));
	}

	/**
	 * For the benefit of method calls, return the list of arguments as a list
	 * of <code>TemplateModel</code> values.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @return a <code>List</code> of <code>TemplateModel</code>s contained in
	 *         the <code>ListLiteral</code>
	 * @throws TemplateException
	 *             the literal list could not be returned
	 */
	public List getModelList(TemplateWriteableHashModel modelRoot) throws TemplateException {
		List list = new ArrayList(values.size());
		Iterator iterator = values.iterator();

		while (iterator.hasNext()) {
			Expression cItem = (Expression) iterator.next();
			list.add(cItem.getAsTemplateModel(modelRoot));
		}

		return list;
	}

	/**
	 * For the benefit of method calls, return the list of arguments as a list
	 * of <code>String</code> values.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @return a <code>List</code> of <code>TemplateModel</code>s contained in
	 *         the <code>ListLiteral</code>
	 * @throws TemplateException
	 *             the literal list could not be returned
	 */
	public List getValueList(TemplateWriteableHashModel modelRoot) throws TemplateException {
		List list = new ArrayList(values.size());
		Iterator iterator = values.iterator();

		while (iterator.hasNext()) {
			Expression cItem = (Expression) iterator.next();
			list.add(ExpressionUtils.getAsStringOrEmpty(cItem.getAsTemplateModel(modelRoot)));
		}

		return list;
	}

	/**
	 * Has the <code>ListLiteral</code> been populated?
	 * 
	 * @return <code>true</code> if the <code>ListLiteral</code> is populated,
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
	 * For serialization, write this object as an array of Expressions.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();

		Expression[] expressionArray = new Expression[values.size()];
		values.toArray(expressionArray);

		fields.put("values", expressionArray);
		stream.writeFields();
	}

	/**
	 * For serialization purposes, resolve a deserialized instance to an
	 * instance in the expression cache.
	 */
	private Object readResolve() throws ObjectStreamException {
		return ExpressionCache.cacheExpression(this);
	}

	/**
	 * For serialization, read this object as an array of Expressions, then
	 * check whether the list has been deserialized to null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();
		Expression[] expressionArray = (Expression[]) fields.get("values", null);

		if (expressionArray == null) {
			throw new InvalidObjectException("Cannot create a ListLiteral with a null value list");
		}
		values = new ArrayList(Arrays.asList(expressionArray));
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of this expression
	 */
	public String toString() {
		return values.toString();
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
		if (!(o instanceof ListLiteral)) {
			return false;
		}

		final ListLiteral listLiteral = (ListLiteral) o;

		return values.equals(listLiteral.values);
	}

	/**
	 * Returns the hash code for this operator.
	 * 
	 * @return the hash code of this object
	 */
	public int hashCode() {
		return values.hashCode() + 13;
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
		// See if we can intelligently determine whether all the elements
		// in this list are constant
		List list = new ArrayList(values.size());
		Iterator iterator = values.iterator();

		while (iterator.hasNext()) {
			Expression cItem = (Expression) iterator.next();

			if (cItem.isConstant()) {
				list.add(cItem.getAsTemplateModel(ExpressionBuilder.emptyModel));
			} else {
				list = null;
				break;
			}
		}

		Expression expr = this;

		if (list != null) {
			expr = new Constant(new FastList(list));
		}
		return ExpressionCache.cacheExpression(expr);
	}
}
