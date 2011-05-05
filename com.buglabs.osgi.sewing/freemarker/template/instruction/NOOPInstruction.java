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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.Writer;

import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.compiler.TemplateBuilder;

/**
 * <p>
 * An instruction that does nothing. This is useful when we want to substitute
 * one instruction for another. The best example of this is the way
 * {@link freemarker.template.compiler.LinkedListTemplateBuilder} deals with
 * {@link FunctionInstruction}s.
 * </p>
 * 
 * <p>
 * <b>Note:</b><br />
 * <code>NOOPInstruction</code> is a singleton instance. Use the
 * {@link #getInstance()} method to retrieve instances of this instruction.
 * </p>
 * 
 * @version $Id: NOOPInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class NOOPInstruction implements Instruction, TemplateProcessor, Serializable {

	private static final NOOPInstruction noopInstance = new NOOPInstruction();

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 2509145854653059701L;

	/** Default constructor. */
	private NOOPInstruction() {
	}

	/**
	 * Retrieves a singleton instance of <code>NOOPInstruction</code>.
	 * 
	 * @return a singleton <code>NOOPInstruction</code>
	 */
	public static NOOPInstruction getInstance() {
		return noopInstance;
	}

	/**
	 * Is this an end instruction?
	 * 
	 * @return <code>false</code>, indicating that this is not an end
	 *         instruction
	 */
	public boolean isEndInstruction() {
		return false;
	}

	/**
	 * Retrieve the type of end instruction, if any.
	 * 
	 * @return <code>NONE</code>, indicating that this is not an end instruction
	 */
	public int getEndType() {
		return Instruction.NONE;
	}

	/**
	 * A {@link freemarker.template.compiler.TemplateBuilder} can call this
	 * method to have an <code>Instruction</code> call it back to be built. This
	 * implementation returns immediately, since there is nothing additional to
	 * be parsed within this instruction.
	 * 
	 * @param builder
	 *            the builder to be called back by this method
	 */
	public TemplateProcessor callBuilder(TemplateBuilder builder) {
		return this;
	}

	/**
	 * Processes the contents of this <code>TemplateProcessor</code> and outputs
	 * the resulting text to a <code>Writer</code>. This implementation returns
	 * immediately, since there is nothing to output.
	 * 
	 * @param modelRoot
	 *            the root node of the data model.
	 * @param out
	 *            a <code>Writer</code> to send the output to.
	 * @param eventHandler
	 *            a <code>TemplateEventAdapter</code> for handling any events
	 *            that occur during processing.
	 */
	public short process(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler) {
		return TemplateProcessor.OK;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return the <code>String</code> "<code>NOOP</code>"
	 */
	public String toString() {
		return "NOOP";
	}

	/**
	 * For serialization purposes, always resolve a de-serialized object back to
	 * the singleton instance. This ensures that any strict equality tests
	 * remain valid after serialization.
	 */
	private Object readResolve() throws ObjectStreamException {
		return noopInstance;
	}
}
