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

package freemarker.template.instruction;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import freemarker.template.expression.Expression;
import freemarker.template.expression.ExpressionUtils;

/**
 * Represents a case in a switch statement. Unexpectedly, this is subclassed
 * from the {@link DefaultCaseInstruction} class.
 * 
 * @version $Id: CaseInstruction.java 987 2004-10-05 10:13:24Z run2000 $
 * @see DefaultCaseInstruction
 * @see SwitchInstruction
 */
public final class CaseInstruction extends DefaultCaseInstruction implements Serializable {

	/**
	 * @serial the expression to be matched by the switch expression
	 */
	private final Expression expression;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -1614527073195929705L;

	/**
	 * Constructor that takes an
	 * {@link freemarker.template.expression.Expression} to be evaluated when
	 * the <code>case</code> instruction is encountered.
	 * 
	 * @param expression
	 *            the <code>Expression</code> associated with this
	 *            <code>Case</code>.
	 * @throws NullPointerException
	 *             the expression was null
	 * @throws IllegalArgumentException
	 *             the expression was not a string or number
	 */
	public CaseInstruction(Expression expression) {
		if ((expression.getType() & (ExpressionUtils.EXPRESSION_TYPE_STRING | ExpressionUtils.EXPRESSION_TYPE_NUMBER)) == 0) {
			throw new IllegalArgumentException("Case instruction must be numeric or scalar");
		}
		this.expression = expression;
	}

	/**
	 * Retrieves the {@link freemarker.template.expression.Expression} to be
	 * evaluated when the <code>case</code> instruction is encountered.
	 * 
	 * @return the <code>Expression</code> associated with this
	 *         <code>CaseInstruction</code>
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * Is this the default case?
	 * 
	 * @return <code>true</code> if this is the default case, otherwise
	 *         <code>false</code>
	 */
	public boolean isDefault() {
		return false;
	}

	/**
	 * Retrieve the type of end instruction, if any.
	 * 
	 * @return <code>CASE</code>
	 */
	public int getEndType() {
		return Instruction.CASE;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representing this instruction subtree
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("case ");
		buffer.append(expression);
		buffer.append(' ');
		buffer.append(body);
		return buffer.toString();
	}

	/**
	 * For serialization, read this object normally, then check whether the case
	 * expression is null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (expression == null) {
			throw new InvalidObjectException("Cannot create a CaseInstruction with a null expression");
		}
	}
}
