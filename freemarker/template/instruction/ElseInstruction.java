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
import java.io.Serializable;
import java.io.Writer;

import freemarker.template.TemplateException;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.compiler.TemplateBuilder;

/**
 * <p>
 * An instruction representing an if-else structure. The "else" part of the
 * structure will be executed if the condition expression evaluates to
 * <code>true</code> value.
 * </p>
 * 
 * <p>
 * As a special case, any "else" structures are performed the same way as "if"
 * structures, but with the test always returning <code>true</code>.
 * </p>
 * 
 * <p>
 * Unexpectedly, this is a superclass of the regular {@link IfInstruction}
 * class.
 * </p>
 * 
 * @version $Id: ElseInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 * @see IfElseInstruction
 * @see IfInstruction
 */
public class ElseInstruction implements Instruction, TemplateProcessor, Serializable {

	/**
	 * The template body to process if the else clause is reached.
	 * 
	 * @serial the template body to process if the clause evaluates to true
	 */
	protected TemplateProcessor body;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 1635834628241107145L;

	/**
	 * Constructor to be used when evaluating the "else" part of the
	 * instruction.
	 */
	public ElseInstruction() {
	}

	/**
	 * Tests the condition for which this "else" statement should match.
	 * 
	 * @return the condition to be tested
	 */
	public boolean conditionMatches(TemplateWriteableHashModel modelRoot) throws TemplateException {
		return true;
	}

	/**
	 * Sets the body to be executed if the "if" expression is <code>true</code>.
	 * 
	 * @param body
	 *            a <code>TemplateProcessor</code> to be processed if the
	 *            condition is <code>true</code>.
	 */
	public final void setBody(TemplateProcessor body) {
		this.body = body;
	}

	/**
	 * Is this an end instruction?
	 * 
	 * @return <code>true</code>, indicating that this is not an end instruction
	 */
	public final boolean isEndInstruction() {
		return true;
	}

	/**
	 * Determine what type of end instruction this is, if any.
	 * 
	 * @return <code>ELSE</code>, indicating that this is an else instruction
	 */
	public int getEndType() {
		return Instruction.ELSE;
	}

	/**
	 * A {@link TemplateBuilder} can call this method to have an
	 * <code>Instruction</code> call it back to be built. For end instructions,
	 * there is nothing more to be build, so return immediately.
	 * 
	 * @param builder
	 *            the builder to be called back by this method
	 */
	public final TemplateProcessor callBuilder(TemplateBuilder builder) {
		return this;
	}

	/**
	 * Evaluate the <code>&lt;else&gt;</code> instruction.
	 * 
	 * @param modelRoot
	 *            the root node of the data model.
	 * @param out
	 *            a <code>Writer</code> to send the output to.
	 * @param eventHandler
	 *            a <code>TemplateEventAdapter</code> for handling any events
	 *            that occur during processing.
	 */
	public final short process(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler) throws IOException {

		if (body != null) {
			return body.process(modelRoot, out, eventHandler);
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
		buffer.append("else ");
		buffer.append(body);
		return buffer.toString();
	}
}
