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
import java.io.Writer;

import freemarker.template.TemplateException;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.expression.Expression;
import freemarker.template.expression.Identifier;
import freemarker.template.expression.Variable;

/**
 * An instruction that assigns a literal or reference to a single-identifier
 * variable.
 * 
 * @version $Id: AssignInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class AssignInstruction extends EmptyInstruction implements Serializable {

	/**
	 * @serial the variable to which the value will be assigned
	 */
	private final Variable variable;
	/**
	 * @serial an expression which, when evaluated, will be assigned to the
	 *         variable
	 */
	private final Expression value;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 6116263906768944170L;

	/**
	 * Constructor that takes a vairable to be assigned and the expression whose
	 * value should be assigned.
	 * 
	 * @param variable
	 *            the variable to assign to.
	 * @param value
	 *            the expression to assign.
	 * @throws NullPointerException
	 *             the variable or value is null
	 * @throws IllegalArgumentException
	 *             attempt to assign variable to an iterator
	 */
	public AssignInstruction(Variable variable, Expression value) {
		if (variable == null) {
			throw new NullPointerException("Variable in assign instruction cannot be null");
		}
		if (value == null) {
			throw new NullPointerException("Value expression in assign instruction cannot be null");
		}

		// This test is non-obvious, but the idea here is to avoid breaking the
		// ability to recycle iterators back to their list parent objects. The
		// releaseIterator method assumes we can't create a reference to the
		// iterator that can escape the current <list> or <foreach>
		// instruction. Of course there are other ways of getting around this
		// requirement, but are non-obvious and require intentionally breaking
		// the model. <assign> statements look more innocent.

		if (value instanceof Identifier) {
			String name = ((Identifier) value).getName();
			if (name.endsWith("#")) {
				throw new IllegalArgumentException("Cannot assign variable to transient iterator variable");
			}
		}

		this.variable = variable;
		this.value = value;
	}

	/**
	 * Process this <code>&lt;assign ... &gt;</code> instruction.
	 * 
	 * @param modelRoot
	 *            the root node of the data model
	 * @param out
	 *            a <code>Writer</code> to send the output to
	 * @param eventHandler
	 *            a <code>TemplateEventAdapter</code> for handling any events
	 *            that occur during processing
	 */
	public short process(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler) throws IOException {

		try {
			variable.setTemplateModel(modelRoot, value.getAsTemplateModel(modelRoot));
		} catch (TemplateException e) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't perform assignment", e), out, "freemarker.template.instruction.AssignInstruction.process",
					TemplateRuntimeHandler.SEVERITY_WARNING);
		}
		return TemplateProcessor.OK;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representing this instruction subtree
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("assign ");
		buffer.append(variable);
		buffer.append(" to ");
		buffer.append(value);
		return buffer.toString();
	}

	/**
	 * For serialization, read this object normally, then check whether both
	 * sides of the statement are non-null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if ((variable == null) || (value == null)) {
			throw new InvalidObjectException("Cannot create an AssignInstruction with a null variable or value");
		}
	}
}
