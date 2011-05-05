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

import java.io.Serializable;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Abstract class that implements most of the surrounding machinery needed for
 * binary expressions. This turns out to be identical for all binary operators,
 * so is consolidated into this one abstract class.
 * 
 * @version $Id: AbstractBinary.java 1051 2004-10-24 09:14:44Z run2000 $
 */
public abstract class AbstractBinary implements Binary, Serializable {

	/**
	 * The left-hand side of the expression to be evaluated.
	 * 
	 * @serial an Expression object representing the left-hand side of the
	 *         expression to be evaluated.
	 */
	protected Expression left;
	/**
	 * The right-hand side of the expression to be evaluated.
	 * 
	 * @serial an Expression object representing the right-hand side of the
	 *         expression to be evaluated.
	 */
	protected Expression right;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -8853390178844168110L;

	/** Default constructor. */
	public AbstractBinary() {
	}

	/**
	 * Sets the left-hand side of the expression.
	 * 
	 * @param left
	 *            the left-hand side of the expression
	 * @throws NullPointerException
	 *             the argument was null
	 */
	public void setLeft(Expression left) {
		if (left == null) {
			throw new NullPointerException("Cannot set expression argument to null");
		}
		this.left = left;
	}

	/**
	 * Sets the right-hand side of the expression.
	 * 
	 * @param right
	 *            the right-hand side of the expression
	 * @throws NullPointerException
	 *             the argument was null
	 */
	public void setRight(Expression right) {
		if (left == null) {
			throw new NullPointerException("Cannot set expression argument to null");
		}
		this.right = right;
	}

	/**
	 * Is the <code>Expression</code> complete?
	 * 
	 * @return <code>true</code> if both left and right sides of the expression
	 *         are specified, otherwise <code>false</code>
	 */
	public boolean isComplete() {
		return ((left != null) && (right != null));
	}

	/**
	 * Resolves the current expression, possibly into a different expression
	 * object. Situations where this may be used are:
	 * <ul>
	 * <li>Caching frequently-used expression objects</li>
	 * <li>Evaluating constant expressions, and returning a constant reference</li>
	 * </ul>
	 */
	public Expression resolveExpression() throws TemplateException {
		Expression expr = this;

		if (left.isConstant() && right.isConstant()) {
			TemplateModel outputModel = expr.getAsTemplateModel(ExpressionBuilder.emptyModel);
			expr = new Constant(outputModel);
		}

		return ExpressionCache.cacheExpression(expr);
	}
}
