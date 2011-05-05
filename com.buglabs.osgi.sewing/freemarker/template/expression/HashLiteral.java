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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import freemarker.template.FastHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateWriteableHashModel;

/**
 * Represents a literal Hash model in a FM-Classic template. The hash model is
 * not evaluated until run time, since the model may contain variables or other
 * more complex expressions that can't be determined at compile time.
 * 
 * @version $Id: HashLiteral.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class HashLiteral implements Expression, Serializable {
	private Map values;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 7012055010641913273L;

	/**
	 * Serialized form is two arrays, both the same size. One contains
	 * expressions to be evaluated as names, the other contains expressions to
	 * be evaluated as values. This is done primarily for type correctness, and
	 * avoids serializing a Map object.
	 * 
	 * @serialField
	 *                  names Expression[] an array of name expressions
	 * @serialField
	 *                  values Expression[] an array of value expressions
	 *                  associated with the names
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("names", Expression[].class), new ObjectStreamField("values", Expression[].class) };

	/**
	 * Constructor that takes a list of {@link Expression} elements to be
	 * evaluated as a hash model at run time.
	 * 
	 * @param values
	 *            the values to be added to the <code>HashLiteral</code>
	 * @throws NullPointerException
	 *             the value list is null
	 * @throws IllegalArgumentException
	 *             there are an odd number of arguments in the value list
	 */
	public HashLiteral(List values) {
		Map cHash = new HashMap();
		int iList;

		if ((values.size() % 2) != 0) {
			throw new IllegalArgumentException("Incorrect number of parameters supplied for a hash literal");
		}

		for (iList = 0; iList < values.size(); iList += 2) {
			cHash.put(values.get(iList), values.get(iList + 1));
		}

		this.values = cHash;
	}

	/**
	 * The {@link freemarker.template.TemplateModel} value of this
	 * <code>Expression</code>.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @return a <code>FastHash</code> containing the values in the hash model
	 * @throws TemplateException
	 *             the expression could not be evaluated for some reason
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		Map hash = new HashMap();
		Expression cKey, cValue;
		Iterator iItem;

		if (values != null) {
			iItem = values.keySet().iterator();

			while (iItem.hasNext()) {
				cKey = (Expression) iItem.next();
				cValue = (Expression) values.get(cKey);
				hash.put(ExpressionUtils.getAsString(cKey.getAsTemplateModel(modelRoot)), cValue.getAsTemplateModel(modelRoot));
			}
		}

		return new FastHash(hash);
	}

	/**
	 * Has the <code>HashLiteral</code> been populated?
	 * 
	 * @return <code>true</code> if the <code>HashLiteral</code> is populated,
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
		return ExpressionUtils.EXPRESSION_TYPE_HASH;
	}

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant() {
		return false;
	}

	/**
	 * For serialization, write this object as two arrays of Expressions. The
	 * first array contains the expressions that will be evaluated as names of
	 * the hash. The seconds array contains the expressions that will be
	 * evaluated as values of the hash.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();
		int size = values.size();
		Expression[] expressionNames = new Expression[size];
		Expression[] expressionValues = new Expression[size];

		Iterator iValues = values.keySet().iterator();
		int i = 0;

		while (iValues.hasNext()) {
			Expression name = (Expression) iValues.next();
			expressionNames[i] = name;
			expressionValues[i] = (Expression) values.get(name);
			i++;
		}

		fields.put("names", expressionNames);
		fields.put("values", expressionValues);
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
	 * For serialization, read this object as two arrays of Expressions. The
	 * first array contains the expressions that will be evaluated as names of
	 * the hash. The seconds array contains the expressions that will be
	 * evaluated as values of the hash. Test whether the arrays are the same
	 * size, and whether they are null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();

		Expression[] expressionNames = (Expression[]) fields.get("names", null);
		Expression[] expressionValues = (Expression[]) fields.get("values", null);

		if ((expressionNames == null) || (expressionValues == null)) {
			throw new InvalidObjectException("Cannot create a HashLiteral with a null map");
		}
		if (expressionNames.length != expressionValues.length) {
			throw new InvalidObjectException("Cannot create a HashLiteral with an unbalanced map");
		}
		values = new HashMap();

		int size = expressionNames.length;
		for (int i = 0; i < size; i++) {
			values.put(expressionNames[i], expressionValues[i]);
		}
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
		if (!(o instanceof HashLiteral)) {
			return false;
		}

		final HashLiteral hashLiteral = (HashLiteral) o;

		return values.equals(hashLiteral.values);
	}

	/**
	 * Returns the hash code for this operator.
	 * 
	 * @return the hash code of this object
	 */
	public int hashCode() {
		return values.hashCode() + 23;
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
		Map hash = new HashMap();
		Expression cKey, cValue;
		Iterator iItem;

		// See if we can intelligently determine whether all the elements
		// in this hash are constant. If yes, return a constant expression.
		if (values != null) {
			iItem = values.keySet().iterator();

			while (iItem.hasNext()) {
				cKey = (Expression) iItem.next();
				cValue = (Expression) values.get(cKey);

				if (cKey.isConstant() && cValue.isConstant()) {
					hash.put(ExpressionUtils.getAsString(cKey.getAsTemplateModel(ExpressionBuilder.emptyModel)), cValue.getAsTemplateModel(ExpressionBuilder.emptyModel));
				} else {
					hash = null;
					break;
				}
			}
		}

		Expression expr = this;
		if (hash != null) {
			expr = new Constant(new FastHash(hash));
		}

		return ExpressionCache.cacheExpression(expr);
	}
}
