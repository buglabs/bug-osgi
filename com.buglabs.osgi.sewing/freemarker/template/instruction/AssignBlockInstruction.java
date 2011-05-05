/*
 * FreeMarker: a tool that allows Java programs to generate HTML
 * output using templates.
 * Copyright (C) 1998-2005 Benjamin Geer
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
import java.io.StringWriter;
import java.io.Writer;

import freemarker.template.FastScalar;
import freemarker.template.RootModelWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.expression.Variable;

/**
 * An instruction that assigns a template block to a single-identifier variable.
 * 
 * @version $Id: AssignBlockInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 * @since 1.9
 */
public final class AssignBlockInstruction extends GenericStartInstruction implements Serializable {

	/**
	 * @serial the variable to which the value will be assigned
	 */
	private final Variable variable;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -3359865056378079818L;

	/**
	 * Constructor that takes a vairable to be assigned and the expression whose
	 * value should be assigned.
	 * 
	 * @param variable
	 *            the variable to assign to.
	 * @throws NullPointerException
	 *             the variable or value is null
	 * @throws IllegalArgumentException
	 *             attempt to assign variable to an iterator
	 */
	public AssignBlockInstruction(Variable variable) {
		if (variable == null) {
			throw new NullPointerException("Variable in assign instruction cannot be null");
		}

		this.variable = variable;
	}

	/**
	 * Process this <code>&lt;assign ... &gt;</code> block instruction.
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

		// Process the body into a string buffer.
		StringWriter sw = new StringWriter();
		RootModelWrapper localModel = new RootModelWrapper(modelRoot);

		// Get the content we have so far
		short result = TemplateProcessor.OK;
		try {
			result = body.process(localModel, sw, eventHandler);
			variable.setTemplateModel(modelRoot, new FastScalar(sw.toString()));
		} catch (TemplateException e) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't perform assignment", e), out, "freemarker.template.instruction.AssignInstruction.process",
					TemplateRuntimeHandler.SEVERITY_WARNING);
		} finally {
			// Clear the local context to avoid any circular references
			localModel.reset();
		}

		return result;
	}

	/**
	 * Is this the right kind of instruction for the given
	 * {@link EndInstruction}?
	 * 
	 * @param endInstruction
	 *            the end instruction we're testing
	 * @return <code>true</code> if the <code>EndInstruction</code> is a
	 *         transform end instruction, otherwise <code>false</code>
	 */
	public boolean testEndInstruction(Instruction endInstruction) {
		return (endInstruction.getEndType() == ASSIGN_END);
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
		buffer.append(" to body ");
		buffer.append(body);
		return buffer.toString();
	}

	/**
	 * For serialization, read this object normally, then check whether the
	 * variable is non-null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (variable == null) {
			throw new InvalidObjectException("Cannot create an AssignBlockInstruction with a null variable or value");
		}
	}
}
