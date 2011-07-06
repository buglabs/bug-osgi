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

/**
 * An interface for binary operators.
 * 
 * @version $Id: Binary.java 987 2004-10-05 10:13:24Z run2000 $
 */
public interface Binary extends Operator {

	/**
	 * Sets the left-hand side of the expression to be evaluated.
	 * 
	 * @param left
	 *            the {@link Expression} to be evaluated on the left-hand side
	 * @throws NullPointerException
	 *             the expression is null
	 * @throws IllegalArgumentException
	 *             the expression doesn't match the expected type or other
	 *             criteria
	 */
	public void setLeft(Expression left);

	/**
	 * Sets the right-hand side of the expression to be evaluated.
	 * 
	 * @param right
	 *            the {@link Expression} to be evaluated on the right-hand side
	 * @throws NullPointerException
	 *             the expression is null
	 * @throws IllegalArgumentException
	 *             the expression doesn't match the expected type or other
	 *             criteria
	 */
	public void setRight(Expression right);

}
