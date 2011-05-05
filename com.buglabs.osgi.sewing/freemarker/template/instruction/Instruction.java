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

import freemarker.template.TemplateProcessor;
import freemarker.template.compiler.ParseException;
import freemarker.template.compiler.TemplateBuilder;

/**
 * An interface that parsed instructions must implement.
 * 
 * @version $Id: Instruction.java 1081 2005-08-28 10:51:16Z run2000 $
 */
public interface Instruction {
	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is
	 * not an end instruction.
	 */
	public static final int NONE = -1;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * break instruction.
	 */
	public static final int BREAK = 0;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * case instruction.
	 */
	public static final int CASE = 1;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * compress end instruction.
	 */
	public static final int COMPRESS_END = 2;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is an
	 * else instruction.
	 */
	public static final int ELSE = 3;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * function end instruction.
	 */
	public static final int FUNCTION_END = 4;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is an
	 * if end instruction.
	 */
	public static final int IF_END = 5;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * list end instruction.
	 */
	public static final int LIST_END = 6;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * switch end instruction.
	 */
	public static final int SWITCH_END = 7;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * comment end instruction.
	 */
	public static final int COMMENT_END = 8;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * foreach end instruction.
	 */
	public static final int FOREACH_END = LIST_END;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * noparse end instruction.
	 */
	public static final int NOPARSE_END = 9;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * transform end instruction.
	 */
	public static final int TRANSFORM_END = 10;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is an
	 * elseif instruction.
	 */
	public static final int ELSEIF = 11;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is a
	 * default instruction.
	 */
	public static final int DEFAULT = 12;

	/**
	 * Constant returned by <code>getEndType()</code> indicating that this is an
	 * assign end instruction.
	 */
	public static final int ASSIGN_END = 13;

	/**
	 * Is this an end instruction?
	 * 
	 * @return <code>true</code> if this instruction marks the end of a
	 *         statement, otherwise <code>false</code>
	 */
	public boolean isEndInstruction();

	/**
	 * Retrieve the type of end instruction, if any.
	 * 
	 * @return the type of this instruction if it is an end instruction,
	 *         otherwise <code>NONE</code>.
	 */
	public int getEndType();

	/**
	 * A {@link freemarker.template.compiler.TemplateBuilder} can call this
	 * method to have an <code>Instruction</code> call it back to be built. The
	 * <code>Instruction</code> will call the appropriate
	 * <code>TemplateBuilder.buildStatement()</code> method for its subclass,
	 * passing back a reference to itself. This approach is intended to make
	 * type-checking of <code>Instruction</code> objects unnecessary.
	 * 
	 * @param builder
	 *            the builder to be called back by this method
	 */
	public TemplateProcessor callBuilder(TemplateBuilder builder) throws ParseException;
}
