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
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import freemarker.template.TemplateException;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.compiler.ParseException;
import freemarker.template.compiler.TemplateBuilder;
import freemarker.template.expression.Expression;

/**
 * <p>
 * A instruction that handles if-elseif-else functionality. The initial "if"
 * clause is held in a single variable. If any else or elseif clauses are
 * encountered, they are stored in a <code>List</code> of
 * <code>IfInstructions</code>. The <code>List</code> is not constructed until
 * the first else or elseif is encountered.
 * </p>
 * 
 * @author <a href="mailto:jon@revusky.com">Jonathan Revusky</a>
 * @version $Id: IfElseInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class IfElseInstruction implements Instruction, TemplateProcessor, Serializable {

	private IfInstruction firstIf;
	private List ifInstructions;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -8166829185543651466L;

	/**
	 * Serialized text block as an array of one or more if instructions,
	 * followed by an optional else instruction.
	 * 
	 * @serialField
	 *                  ifInstructions IfInstruction[] an array of all if and
	 *                  elseif instructions
	 * @serialField
	 *                  elseInstruction ElseInstruction a single, optional else
	 *                  instruction
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("ifInstructions", IfInstruction[].class),
			new ObjectStreamField("elseInstruction", ElseInstruction.class) };

	/**
	 * Constructs a new if/else instruction without the first block.
	 * 
	 * @param condition
	 *            the condition for the first if
	 * @throws NullPointerException
	 *             condition is null
	 */
	public IfElseInstruction(Expression condition) {
		firstIf = new IfInstruction(condition);
	}

	/**
	 * Adds the main block to the first "if" statement. For if/else, this is a
	 * special case.
	 * 
	 * @param block
	 *            the block to be processed if the first "if" statement is
	 *            <code>true</code>
	 */
	public void setIfBlock(TemplateProcessor block) {
		firstIf.setBody(block);
	}

	/**
	 * Adds a new test to the if/else instruction. Each test is evaluated in the
	 * order they are added using this method.
	 * 
	 * @param instruction
	 *            the "if" instruction to be evaluated and executed
	 * @throws NullPointerException
	 *             instruction is null
	 */
	public void addTest(ElseInstruction instruction) {
		if (instruction == null) {
			throw new NullPointerException("If instruction cannot be null");
		}
		if (ifInstructions == null) {
			ifInstructions = new ArrayList(5);
		}
		ifInstructions.add(instruction);
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
	 * implementation throws an <code>UnsupportedOperationException</code>
	 * indicating that this is not yet production code.
	 * 
	 * @param builder
	 *            the builder to be called back by this method
	 */
	public TemplateProcessor callBuilder(TemplateBuilder builder) throws ParseException {
		return builder.buildStatement(this);
	}

	/**
	 * Evaluate the <code>&lt;if ... &gt;</code> instruction.
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

		try {
			if (firstIf.conditionMatches(modelRoot)) {
				return firstIf.process(modelRoot, out, eventHandler);
			}

			if (ifInstructions != null) {
				int conditionsLength = ifInstructions.size();
				for (int i = 0; i < conditionsLength; i++) {
					ElseInstruction instruction = (ElseInstruction) ifInstructions.get(i);
					if (instruction.conditionMatches(modelRoot)) {
						return instruction.process(modelRoot, out, eventHandler);
					}
				}
			}
		} catch (TemplateException te) {
			eventHandler.fireExceptionThrown(this, te, out, "freemarker.template.instruction.IfElseInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
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
		List clauses = new ArrayList();

		if (firstIf != null) {
			clauses.add(firstIf);
		}
		if (ifInstructions != null) {
			clauses.addAll(ifInstructions);
		}
		buffer.append("if ");
		buffer.append(clauses);

		return buffer.toString();
	}

	/**
	 * For serialization, write this object as an array of IfExpression objects,
	 * followed by an optional ElseExpression.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();
		ElseInstruction elseInstruction = null;
		int ifSize = 1;

		if (ifInstructions != null) {
			ifSize += ifInstructions.size();
		}

		List ifList = new ArrayList(ifSize);

		ifList.add(firstIf);
		if (ifInstructions != null) {
			ifList.addAll(ifInstructions);
		}

		if (!ifList.isEmpty()) {
			int lastIf = ifList.size() - 1;
			elseInstruction = (ElseInstruction) ifList.get(lastIf);
			if (elseInstruction.getEndType() == Instruction.ELSE) {
				ifList.remove(lastIf);
			} else {
				elseInstruction = null;
			}
		}

		IfInstruction[] ifArray = new IfInstruction[ifList.size()];
		ifList.toArray(ifArray);

		fields.put("ifInstructions", ifArray);
		fields.put("elseInstruction", elseInstruction);
		stream.writeFields();
	}

	/**
	 * For serialization, read this object as an array of IfExpression objects,
	 * followed by an optional ElseExpression.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();
		IfInstruction[] ifArray;
		ElseInstruction elseInstruction;

		ifArray = (IfInstruction[]) fields.get("ifInstructions", null);
		elseInstruction = (ElseInstruction) fields.get("elseInstruction", null);

		if ((ifArray == null) || (ifArray.length == 0)) {
			throw new InvalidObjectException("Cannot create an IfElseInstruction with an empty if-list");
		}
		firstIf = ifArray[0];
		List ifList = Arrays.asList(ifArray).subList(1, ifArray.length);

		ifInstructions = new ArrayList(ifArray.length);
		ifInstructions.addAll(ifList);
		if (elseInstruction != null) {
			ifInstructions.add(elseInstruction);
		}
	}
}
