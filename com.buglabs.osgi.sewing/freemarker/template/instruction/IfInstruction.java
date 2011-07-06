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

import freemarker.template.TemplateException;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.expression.Expression;
import freemarker.template.expression.ExpressionUtils;

/**
 * <p>
 * An instruction representing an if-else structure. The "if" part of the
 * structure will be executed if the condition expression evaluates to
 * <code>true</code> value.
 * </p>
 * 
 * <p>
 * Unexpectedly, this is a subclass of the {@link ElseInstruction} class.
 * </p>
 * 
 * @version $Id: IfInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 * @see IfElseInstruction
 * @see ElseInstruction
 */
public final class IfInstruction extends ElseInstruction implements Serializable {

	/** @serial the condition to be evaluated */
	private final Expression condition;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 3493189816503962845L;

	/**
	 * Constructor that takes an
	 * {@link freemarker.template.expression.Expression} to be tested when
	 * evaluating the "if" part of the instruction.
	 * 
	 * @param condition
	 *            the condition for the if statement.
	 * @throws NullPointerException
	 *             condition is null
	 */
	public IfInstruction(Expression condition) {
		if (condition == null) {
			throw new NullPointerException("If condition cannot be null");
		}
		this.condition = condition;
	}

	/**
	 * Tests the condition for which this "if" statement should match.
	 * 
	 * @return the condition to be tested
	 */
	public boolean conditionMatches(TemplateWriteableHashModel modelRoot) throws TemplateException {
		return ExpressionUtils.isTrue(condition.getAsTemplateModel(modelRoot));
	}

	/**
	 * Determine what type of end instruction this is, if any.
	 * 
	 * @return <code>ELSEIF</code>, indicating that this is an elseif
	 *         instruction
	 */
	public int getEndType() {
		return Instruction.ELSEIF;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representing this instruction subtree
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ifclause ");
		buffer.append(condition);
		buffer.append(' ');
		buffer.append(body);
		return buffer.toString();
	}

	/**
	 * For serialization, read this object normally, then check whether the if
	 * condition is null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (condition == null) {
			throw new InvalidObjectException("Cannot create an IfInstruction with a null condition");
		}
	}
}
