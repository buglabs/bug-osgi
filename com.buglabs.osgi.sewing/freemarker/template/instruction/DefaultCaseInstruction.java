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

import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.compiler.TemplateBuilder;
import freemarker.template.expression.Expression;

/**
 * Represents a case in a switch statement. Unexpectedly, this is a superclass
 * for the regular {@link CaseInstruction} class.
 * 
 * @version $Id: DefaultCaseInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 * @see CaseInstruction
 * @see SwitchInstruction
 */
public class DefaultCaseInstruction implements TemplateProcessor, Instruction, Serializable {

	/**
	 * The template body to process if the default case is reached.
	 * 
	 * @serial the template body to be processed if the case matches
	 */
	protected TemplateProcessor body;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -7534875993650505782L;

	/** Default constructor. */
	public DefaultCaseInstruction() {
	}

	/**
	 * Retrieves the {@link Expression} to be evaluated when the
	 * <code>case</code> instruction is encountered.
	 * 
	 * @return the <code>Expression</code> associated with this
	 *         <code>CaseInstruction</code>
	 */
	public Expression getExpression() {
		// The actual expression returned is irrelevant, since it is
		// never tested for default cases.
		return null;
	}

	/**
	 * Is this the default case?
	 * 
	 * @return <code>true</code> if this is the default case, otherwise
	 *         <code>false</code>
	 */
	public boolean isDefault() {
		return true;
	}

	/**
	 * Sets the body to be processed if the case expression evaluated to
	 * <code>true</code>.
	 * 
	 * @param body
	 *            the <code>TemplateProcessor</code> to be processed if this
	 *            <code>Case</code> passes the test.
	 */
	public final void setBody(TemplateProcessor body) {
		this.body = body;
	}

	/**
	 * Is this an end instruction?
	 * 
	 * @return <code>true</code>, to indicate this is an end instruction
	 */
	public final boolean isEndInstruction() {
		return true;
	}

	/**
	 * Retrieve the type of end instruction, if any.
	 * 
	 * @return <code>DEFAULT</code>
	 */
	public int getEndType() {
		return Instruction.DEFAULT;
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
	 * Process this <code>&lt;default&gt;</code> instruction.
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
		buffer.append("default ");
		buffer.append(body);
		return buffer.toString();
	}
}
