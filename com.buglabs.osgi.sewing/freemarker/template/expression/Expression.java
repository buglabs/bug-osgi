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

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateWriteableHashModel;

/**
 * <p>
 * An <tt>Expression</tt> can return a TemplateModel value. An expression is
 * used whenever we want to interact with the template models. An expression can
 * be simple, such as an identifier, or more complex, such as a dynamic key name
 * or a concatenation operator.
 * </p>
 * <p>
 * Expressions can be (in fact, usually are) nested. For instance, a comparison
 * of two concatenation operations may be represented as follows:
 * </p>
 * 
 * <pre>
 *                         Equals
 *                           |
 *          +----------------+---------------+
 *          |                                |
 *          |Plus                            | Plus
 *     +----+----+                     +-----+-----+
 *     |         |                     |           |
 *     |         |                     |           |
 * identifier identifier           identifier      + dynamic-key-name
 *                                                 |
 *                                                 |
 *                                                 + identifier
 * </pre>
 * <p>
 * Each node on the tree represents a different expression object.
 * </p>
 * 
 * <p>
 * Once complete (i.e. parsed), expressions should be considered immutable.
 * </p>
 * 
 * @version $Id: Expression.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public interface Expression {

	/**
	 * The {@link freemarker.template.TemplateModel} value of this
	 * <code>Expression</code>.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @throws TemplateException
	 *             the expression could not be evaluated for some reason
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException;

	/**
	 * Is the <code>Expression</code> complete?
	 * 
	 * @return <code>true</code> if this <code>Expression</code> is complete,
	 *         otherwise <code>false</code>
	 */
	public boolean isComplete();

	/**
	 * Determine the type of result that can be calculated by this expression.
	 * This is in the form of an integer constant ored together from values in
	 * the {@link ExpressionUtils} class.
	 */
	public int getType();

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant();

	/**
	 * Resolves the current expression, possibly into a different expression
	 * object. This is loosely equivalent to the serialization protocol's
	 * <code>readResolve</code> method. Situations where this may be used are:
	 * <ul>
	 * <li>Caching frequently-used expression objects</li>
	 * <li>Evaluating constant expressions, and returning a constant reference</li>
	 * </ul>
	 */
	public Expression resolveExpression() throws TemplateException;

}
