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
import freemarker.template.compiler.ParseException;
import freemarker.template.compiler.TemplateBuilder;

/**
 * <p>
 * An instruction for commenting out a block of text or adding a remark.
 * </p>
 * 
 * <p>
 * <b>Note:</b><br />
 * <code>CommentInstruction</code> is a singleton instance. Use the
 * {@link #getInstance()} method to retrieve instances of this instruction.
 * </p>
 * 
 * @version $Id: CommentInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class CommentInstruction implements UnparsedInstruction, Serializable {

	/** Singleton instance of this class. */
	private static final CommentInstruction commentInstance = new CommentInstruction();

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 2631633394072538840L;

	/**
	 * Default constructor. Private so that it can't be instantiated outsidde
	 * this class.
	 */
	private CommentInstruction() {
	}

	/**
	 * Return a singleton instance of a comment instruction.
	 * 
	 * @return a singleton <code>CommentInstruction</code> object
	 */
	public static CommentInstruction getInstance() {
		return commentInstance;
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
	 * Determine what type of end instruction this is, if any.
	 * 
	 * @return <code>NONE</code>, indicating that this is not an end instruction
	 */
	public int getEndType() {
		return Instruction.NONE;
	}

	/**
	 * Call the {@link freemarker.template.compiler.TemplateBuilder} with this
	 * comment instruction.
	 * 
	 * @param builder
	 *            the <code>TemplateBuilder</code> to be called back
	 */
	public TemplateProcessor callBuilder(TemplateBuilder builder) throws ParseException {
		return builder.buildStatement(this);
	}

	/**
	 * Sets the text of the comment.
	 * 
	 * @param text
	 *            the text of the comment
	 */
	public void setText(String text) {
		// Do nothing, throw away comment
	}

	/**
	 * Is this the right kind of instruction for the given
	 * {@link EndInstruction}?
	 * 
	 * @param endInstruction
	 *            the end instruction we're testing
	 * @return <code>true</code> if the <code>EndInstruction</code> is a comment
	 *         end instruction, otherwise <code>false</code>
	 */
	public boolean testEndInstruction(Instruction endInstruction) {
		return (endInstruction.getEndType() == COMMENT_END);
	}

	/**
	 * Process this <code>&lt;comment&gt;</code> instruction.
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
		// do nothing, skip the body and return immediately
		return TemplateProcessor.OK;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return The <code>String</code> "<code>comment</code>"
	 */
	public String toString() {
		return "comment";
	}

	/**
	 * For serialization purposes, always resolve a de-serialized object back to
	 * the singleton instance. This ensures that any strict equality tests
	 * remain valid after serialization.
	 */
	private Object readResolve() throws ObjectStreamException {
		return commentInstance;
	}
}
