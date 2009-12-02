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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.Writer;

import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.compiler.ParseException;
import freemarker.template.compiler.TemplateBuilder;

/**
 * An instruction for containing an arbitrary block of text that is not parsed
 * any further by FM-Classic.
 * 
 * @version $Id: NoParseInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class NoParseInstruction implements UnparsedInstruction, Serializable {

	private char[] text;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 4149079532356939048L;

	/**
	 * Serialized noparse block as a String object. This means that for the 1.2
	 * series of JVMs, the serialized noparse block cannot be larger than 64kB.
	 * 
	 * @serialField textValue String the <code>String</code> value of this text
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("textValue", String.class) };

	/** Default constructor. */
	public NoParseInstruction() {
	}

	/**
	 * Sets the text to be output when evaluating this instruction.
	 * 
	 * @param text
	 *            the text to be output
	 */
	public void setText(String text) {
		this.text = (text == null ? null : text.toCharArray());
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
	 * A {@link freemarker.template.compiler.TemplateBuilder} can call this
	 * method to have an <code>Instruction</code> call it back to be built. This
	 * implementation calls the
	 * {@link freemarker.template.compiler.TemplateBuilder#buildStatement(UnparsedInstruction)}
	 * method, passing back a reference to itself. This approach is intended to
	 * make type-checking of {@link Instruction} objects unnecessary.
	 * 
	 * @param builder
	 *            the builder to be called back by this method
	 */
	public TemplateProcessor callBuilder(TemplateBuilder builder) throws ParseException {
		return builder.buildStatement(this);
	}

	/**
	 * Is this the right kind of instruction for the given
	 * {@link EndInstruction}?
	 * 
	 * @param endInstruction
	 *            the end instruction we're testing
	 * @return <code>true</code> if the <code>EndInstruction</code> is a noparse
	 *         end instruction, otherwise <code>false</code>
	 */
	public boolean testEndInstruction(Instruction endInstruction) {
		return (endInstruction.getEndType() == NOPARSE_END);
	}

	/**
	 * Process this <code>&lt;noparse&gt;</code> instruction.
	 * 
	 * @param modelRoot
	 *            the root node of the data model.
	 * @param out
	 *            a <code>Writer</code> to send the output to.
	 * @param eventHandler
	 *            a <code>TemplateEventAdapter</code> for handling any events
	 *            that occur during processing.
	 */
	public short process(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler) throws IOException {
		if (text != null) {
			out.write(text);
		}
		return TemplateProcessor.OK;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representing this instruction subtree
	 */
	public String toString() {
		return "noparse (...)";
	}

	/**
	 * For serialization, write this object as a String.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();

		if (text == null) {
			fields.put("textValue", null);
		} else {
			fields.put("textValue", new String(text));
		}
		stream.writeFields();
	}

	/**
	 * For serialization, read this object as a String.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();
		String value = (String) fields.get("textValue", null);

		// Recreate the original value
		if (value != null) {
			text = value.toCharArray();
		}
	}
}
