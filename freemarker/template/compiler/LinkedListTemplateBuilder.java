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

package freemarker.template.compiler;

import java.util.ArrayList;
import java.util.List;

import freemarker.template.FunctionTemplateProcessor;
import freemarker.template.TemplateProcessor;
import freemarker.template.instruction.BreakInstruction;
import freemarker.template.instruction.DefaultCaseInstruction;
import freemarker.template.instruction.ElseInstruction;
import freemarker.template.instruction.EmptyInstruction;
import freemarker.template.instruction.ExitInstruction;
import freemarker.template.instruction.FunctionInstruction;
import freemarker.template.instruction.FunctionModel;
import freemarker.template.instruction.GenericStartInstruction;
import freemarker.template.instruction.IfElseInstruction;
import freemarker.template.instruction.Instruction;
import freemarker.template.instruction.ListInstruction;
import freemarker.template.instruction.NOOPInstruction;
import freemarker.template.instruction.SwitchInstruction;
import freemarker.template.instruction.UnparsedInstruction;

/**
 * <p>
 * Builds a template as a tree structure in which child nodes are stored in
 * {@link TemplateArrayList}s. Each instance can be used to compile one
 * template.
 * </p>
 * 
 * <p>
 * This class is now misnamed, since it originally built a
 * <code>TemplateLinkedList</code>.
 * </p>
 * 
 * @version $Id: LinkedListTemplateBuilder.java 1081 2005-08-28 10:51:16Z
 *          run2000 $
 */
public final class LinkedListTemplateBuilder implements TemplateBuilder {
	private final FunctionTemplateProcessor template;
	private final TemplateParser parser;
	private short listInstructions;
	private short switchInstructions;
	private short functionInstructions;

	/**
	 * Constructs a new <code>LinkedListTemplateBuilder</code> with a
	 * {@link freemarker.template.FunctionTemplateProcessor} and a
	 * {@link TemplateParser}.
	 * 
	 * @param template
	 *            the template to be built
	 * @param parser
	 *            the parser to parse the input stream
	 */
	public LinkedListTemplateBuilder(FunctionTemplateProcessor template, TemplateParser parser) {
		this.template = template;
		this.parser = parser;
	}

	/**
	 * Builds the template.
	 * 
	 * @return the head of the built template.
	 * @throws ParseException
	 *             the template could not be built
	 */
	public TemplateProcessor build() throws ParseException {
		List accumulator = new ArrayList();

		listInstructions = 0;
		switchInstructions = 0;
		functionInstructions = 0;
		Instruction endInstruction = buildInstructions(accumulator);
		TemplateArrayList list = new TemplateArrayList(accumulator);

		if (endInstruction != null) {
			throw new ParseException("Unexpected instruction" + parser.atChar());
		}
		return list;
	}

	/**
	 * Builds a list of {@link freemarker.template.instruction.Instruction}s.
	 * 
	 * @param list
	 *            the current <code>List</code> to which links are to be added.
	 * @return an end instruction, if the current branch ends with one,
	 *         otherwise <code>null</code>.
	 */
	private Instruction buildInstructions(List list) throws ParseException {
		Instruction instruction;

		while (true) {
			// Search for an instruction.
			instruction = parser.getNextInstruction();

			// If there aren't any more instructions, put the rest of the
			// file in a TextBlockInstruction, use that as the statement
			// for the current link, and return.
			if (instruction == null) {
				return null;
			}

			// If we've been given an end instruction, return it.
			if (instruction.isEndInstruction()) {
				return instruction;
			}

			// Have the instruction call us back to be built and inserted
			// into the link.
			list.add(instruction.callBuilder(this));

			// If there's any text after the instruction, continue building
			// links.
			if (!parser.isMoreInstructions()) {
				return null;
			}
		}
	}

	/**
	 * When this {@link TemplateBuilder} implementation calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is an {@link freemarker.template.instruction.EmptyInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(EmptyInstruction instruction) throws ParseException {
		return instruction;
	}

	/**
	 * When this {@link TemplateBuilder} implementation calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it should be built as a
	 * {@link freemarker.template.instruction.GenericStartInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(GenericStartInstruction instruction) throws ParseException {
		buildGenericBody(instruction);
		return instruction;
	}

	/**
	 * When this {@link TemplateBuilder} implementation calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it should be built as a
	 * {@link freemarker.template.instruction.ListInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(ListInstruction instruction) throws ParseException {
		try {
			listInstructions++;
			buildGenericBody(instruction);
		} finally {
			listInstructions--;
		}
		return instruction;
	}

	/**
	 * When this {@link TemplateBuilder} implementation calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is a {@link freemarker.template.instruction.FunctionInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(FunctionInstruction instruction) throws ParseException {
		TemplateProcessor tp;

		try {
			functionInstructions++;
			tp = NOOPInstruction.getInstance();
			buildGenericBody(instruction);
			template.addFunction(instruction.getName(), new FunctionModel(instruction));
		} finally {
			functionInstructions--;
		}
		return tp;
	}

	/**
	 * Builds a GenericStartInstruction's body.
	 * 
	 * @param instruction
	 *            the instruction to be built
	 */
	private void buildGenericBody(GenericStartInstruction instruction) throws ParseException {
		List accumulator = new ArrayList();
		Instruction endInstruction = buildInstructions(accumulator);
		TemplateArrayList body = new TemplateArrayList(accumulator);
		instruction.setBody(body);

		// Make sure buildLinks() returned the right end instruction.
		if (endInstruction == null || !instruction.testEndInstruction(endInstruction)) {

			throw new ParseException("Expected end instruction for " + instruction.toString() + parser.atChar());
		}
	}

	/**
	 * When this {@link TemplateBuilder} implementation calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is an {@link freemarker.template.instruction.IfInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(IfElseInstruction instruction) throws ParseException {

		// Recursively build the if block.
		List accumulator = new ArrayList();
		boolean elseFound = false;

		Instruction nextInstruction = buildInstructions(accumulator);
		TemplateArrayList body = new TemplateArrayList(accumulator);

		// Add the first "if" block. The "if" test is already inserted.
		instruction.setIfBlock(body);

		// Recursively build the elseif and else blocks, if there are any.
		while (true) {
			if (nextInstruction == null) {
				throw new ParseException("Expected end of if statement" + parser.atChar());
			}

			int endType = nextInstruction.getEndType();
			if (elseFound && (endType != Instruction.IF_END)) {
				throw new ParseException("Expected end of if statement" + parser.atChar());
			}

			switch (endType) {
			case Instruction.IF_END:
				return instruction;

			case Instruction.ELSE:
				elseFound = true;
				// Intentional drop-through here...

			case Instruction.ELSEIF:
				ElseInstruction ifInstruction = (ElseInstruction) nextInstruction;
				accumulator.clear();
				nextInstruction = buildInstructions(accumulator);
				body = new TemplateArrayList(accumulator);
				ifInstruction.setBody(body);
				instruction.addTest(ifInstruction);
				break;

			default:
				throw new ParseException("Expected end of if statement" + parser.atChar());
			}
		}
	}

	/**
	 * When this {@link TemplateBuilder} implementation calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is a {@link freemarker.template.instruction.SwitchInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(SwitchInstruction instruction) throws ParseException {
		DefaultCaseInstruction caseInstruction = null;
		int lastType = Instruction.NONE;
		boolean defaultFound = false;
		switchInstructions++;

		// Get blocks followed by end instructions, and check for end
		// instructions that are meaningful to us.
		while (true) {
			List accumulator = new ArrayList();
			Instruction endInstruction = buildInstructions(accumulator);
			TemplateArrayList body = new TemplateArrayList(accumulator);

			if (endInstruction == null) {
				switchInstructions--;
				throw new ParseException("Expected end of switch structure" + parser.atChar());
			}

			if ((lastType == Instruction.CASE) || (lastType == Instruction.DEFAULT)) {
				caseInstruction.setBody(body);
				instruction.addCase(caseInstruction);
			}

			int endType = endInstruction.getEndType();

			// Make sure we don't have more case or default instructions after
			// we find the first default instruction.
			if (defaultFound && (endType != Instruction.SWITCH_END)) {
				switchInstructions--;
				throw new ParseException("Expected end switch instruction following default" + parser.atChar());
			}

			switch (endType) {
			case Instruction.DEFAULT:
				defaultFound = true;
				// Intentionally drop-through here...

			case Instruction.CASE:
				caseInstruction = (DefaultCaseInstruction) endInstruction;
				break;

			case Instruction.SWITCH_END:
				switchInstructions--;
				return instruction;

			default:
				switchInstructions--;
				throw new ParseException("Unexpected instruction" + parser.atChar());
			}
			lastType = endType;
		}
	}

	/**
	 * When this {@link TemplateBuilder} implementation calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is an {@link freemarker.template.instruction.UnparsedInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(UnparsedInstruction instruction) throws ParseException {
		String text = parser.skipToEndInstruction(instruction);

		if (text != null) {
			instruction.setText(text);
			return instruction;
		}

		throw new ParseException("Expected end instruction for " + instruction.toString() + parser.atChar());
	}

	/**
	 * When an implementation of <code>TemplateBuilder</code> calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is a {@link freemarker.template.instruction.BreakInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(BreakInstruction instruction) throws ParseException {
		if ((switchInstructions == 0) && (listInstructions == 0)) {
			throw new ParseException("Unexpected break instruction" + parser.atChar());
		}
		return instruction;
	}

	/**
	 * When an implementation of <code>TemplateBuilder</code> calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is an {@link freemarker.template.instruction.ExitInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(ExitInstruction instruction) throws ParseException {
		if (functionInstructions == 0) {
			throw new ParseException("Unexpected exit instruction" + parser.atChar());
		}
		return instruction;
	}
}
