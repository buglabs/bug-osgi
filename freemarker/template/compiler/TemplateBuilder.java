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

import freemarker.template.TemplateProcessor;
import freemarker.template.instruction.BreakInstruction;
import freemarker.template.instruction.EmptyInstruction;
import freemarker.template.instruction.ExitInstruction;
import freemarker.template.instruction.FunctionInstruction;
import freemarker.template.instruction.GenericStartInstruction;
import freemarker.template.instruction.IfElseInstruction;
import freemarker.template.instruction.ListInstruction;
import freemarker.template.instruction.SwitchInstruction;
import freemarker.template.instruction.UnparsedInstruction;

/**
 * An interface for objects that build the compiled form of a template.
 * 
 * @version $Id: TemplateBuilder.java 987 2004-10-05 10:13:24Z run2000 $
 */
public interface TemplateBuilder {

	/**
	 * Builds a new template.
	 * 
	 * @return a <code>TemplateProcessor</code> representing the compiled form
	 *         of the template.
	 */
	public TemplateProcessor build() throws ParseException;

	/**
	 * When an implementation of <code>TemplateBuilder</code> calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is an {@link freemarker.template.instruction.EmptyInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(EmptyInstruction instruction) throws ParseException;

	/**
	 * When an implementation of <code>TemplateBuilder</code> calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it should be built as a
	 * {@link freemarker.template.instruction.GenericStartInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(GenericStartInstruction instruction) throws ParseException;

	/**
	 * When an implementation of <code>TemplateBuilder</code> calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is a {@link freemarker.template.instruction.ListInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(ListInstruction instruction) throws ParseException;

	/**
	 * When an implementation of <code>TemplateBuilder</code> calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is a {@link freemarker.template.instruction.FunctionInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(FunctionInstruction instruction) throws ParseException;

	/**
	 * When an implementation of <code>TemplateBuilder</code> calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is an {@link freemarker.template.instruction.IfElseInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(IfElseInstruction instruction) throws ParseException;

	/**
	 * When an implementation of <code>TemplateBuilder</code> calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is a {@link freemarker.template.instruction.SwitchInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(SwitchInstruction instruction) throws ParseException;

	/**
	 * When an implementation of <code>TemplateBuilder</code> calls
	 * {@link freemarker.template.instruction.Instruction#callBuilder}, the
	 * {@link freemarker.template.instruction.Instruction} will call this method
	 * if it is an {@link freemarker.template.instruction.UnparsedInstruction}.
	 * 
	 * @param instruction
	 *            the <code>Instruction</code> on which
	 *            <code>callBuilder()</code> was called.
	 */
	public TemplateProcessor buildStatement(UnparsedInstruction instruction) throws ParseException;

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
	public TemplateProcessor buildStatement(BreakInstruction instruction) throws ParseException;

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
	public TemplateProcessor buildStatement(ExitInstruction instruction) throws ParseException;
}
