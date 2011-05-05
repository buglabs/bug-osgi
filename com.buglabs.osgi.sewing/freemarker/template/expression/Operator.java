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
 * <p>
 * Interface that represents an operator expression. An operator is associated
 * with operands. For FM-Classic, an operator can be either a unary operator
 * (takes one operand), or a binary operator (takes two operands).
 * </p>
 * 
 * <p>
 * Operators have precedence. Precedence between operators is determined by the
 * {@link #getPrecedence} method. This provides a way for
 * <code>ExpressionBuilder</code> to determine what precedence each operator
 * has.
 * </p>
 * 
 * @version $Id: Operator.java 990 2004-10-15 03:22:06Z run2000 $
 */
public interface Operator extends Expression {

	/**
	 * Return the precedence for this operator to the caller. Used for
	 * associating operators according to precedence.
	 * 
	 * @return an integer indicating the precedence of this operator
	 */
	public int getPrecedence();
}
