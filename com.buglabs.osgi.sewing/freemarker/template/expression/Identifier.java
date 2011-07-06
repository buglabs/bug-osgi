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
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateWriteableHashModel;

/**
 * An identifer in a variable. Can be used stand-alone to represent a variable
 * in the root model, or combined with the {@link Dot} operator to represent
 * models contained within hash models.
 * 
 * @version $Id: Identifier.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class Identifier implements Variable, Serializable {
	/**
	 * The name for this identifier.
	 * 
	 * @serial the name of the identifier
	 */
	private final String name;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 1053885113751957584L;

	/**
	 * Constructs a new <code>Identifier</code> with the given name.
	 * 
	 * @param name
	 *            the name of the identifier
	 */
	public Identifier(String name) {

		if (name == null) {
			throw new IllegalArgumentException("Identifier cannot be null");
		}

		// Due to the vagaries of Strings in Java, we make an explicit copy.
		// This is so that we don't inadvertantly hold a reference to a
		// substring of the entire unparsed template.
		this.name = new String(name);
	}

	/**
	 * Retrieve the name of the identifier.
	 * 
	 * @return the name of the identifier
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieve the name of this <code>Identifier</code>.
	 * 
	 * @param modelRoot
	 *            the <code>TemplateModelRoot</code> used to evaluate the name
	 *            of the identifier
	 * @return the name of the identifier
	 */
	public String getName(TemplateWriteableHashModel modelRoot) {
		return name;
	}

	/**
	 * Has the identifier's name been assigned?
	 * 
	 * @return <code>true</code> if the identifier is named, otherwise
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
	 * Gets the identifier's referent in modelRoot.
	 * 
	 * @param modelRoot
	 *            the root node of the data model.
	 * @return the <code>TemplateModel</code> that the variable refers to.
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {

		try {
			return modelRoot.get(name);
		} catch (TemplateModelException e) {
			throw new TemplateException("Couldn't get referent of " + name, e);
		}
	}

	/**
	 * Sets the identifier's referent in modelRoot.
	 * 
	 * @param modelRoot
	 *            the root node of the data model.
	 * @param value
	 *            the <code>TemplateModel</code> that the identifier will be set
	 *            to
	 */
	public void setTemplateModel(TemplateWriteableHashModel modelRoot, TemplateModel value) throws TemplateException {

		String variableName = name;
		modelRoot.put(variableName, value);
	}

	/**
	 * Returns the name of the identifier as a <code>String</code>.
	 * 
	 * @return the name of the identifier.
	 */
	public String toString() {
		return name;
	}

	/**
	 * Tests the equality of two <code>Identifier</code>s.
	 * 
	 * @return <code>true</code> if the identifiers are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Identifier)) {
			return false;
		}

		final Identifier id = (Identifier) o;
		if (name == null) {
			return (id.name == null);
		}
		return name.equals(id.name);
	}

	/**
	 * Returns a hash code value for the <code>Identifier</code>.
	 * 
	 * @return a hash code value for this Identifier.
	 */
	public int hashCode() {
		if (name == null) {
			return 0;
		}
		return name.hashCode() + 17;
	}

	/**
	 * For serialization purposes, resolve a deserialized instance to an
	 * instance in the expression cache.
	 */
	private Object readResolve() throws ObjectStreamException {
		return ExpressionCache.cacheExpression(this);
	}

	/**
	 * For serialization, read this object normally, then check whether the name
	 * has been deserialized to null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (name == null) {
			throw new InvalidObjectException("Cannot create an Identifier with a null name");
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
		return ExpressionCache.cacheExpression(this);
	}
}
