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

import freemarker.template.instruction.ContainerInstruction;
import freemarker.template.instruction.Instruction;

/**
 * <p>
 * An interface for classes that parse templates. This defines the minimum
 * contract that {@link LinkedListTemplateBuilder} expects.
 * </p>
 * 
 * <p>
 * The typical scenario is text blocks with one or more instructions between
 * each one. Text blocks are simply Instructions of type
 * {@link freemarker.template.instruction.TextBlockInstruction}.
 * </p>
 * 
 * @author Nicholas Cull
 * @version $Id: TemplateParser.java 987 2004-10-05 10:13:24Z run2000 $
 */
public interface TemplateParser {

	/**
	 * Searches the text for an instruction, starting at the current parse
	 * position. If one is found, parses it into an
	 * {@link freemarker.template.instruction.Instruction}.
	 * 
	 * @return an <code>Instruction</code>, or <code>null</code> if no more
	 *         instructions exist in the template representing the next
	 *         instruction following the current parse position.
	 */
	public Instruction getNextInstruction() throws ParseException;

	/**
	 * Are there any more instructions left to be parsed?
	 * 
	 * @return <code>true</code> if there is more text to parse, otherwise
	 *         <code>false</code>
	 */
	public boolean isMoreInstructions();

	/**
	 * Searches the text for a matching end instruction, starting at the current
	 * parse position. If we find it, parse it and return.
	 * 
	 * @return a <code>String</code> of the intermediate text if we find the end
	 *         instruction we're after, otherwise <code>null</code>.
	 */
	public String skipToEndInstruction(ContainerInstruction beginInstruction);

	/**
	 * Adds text to an error message indicating the line number where the error
	 * was found.
	 * 
	 * @return a <tt>String</tt> containing the message.
	 */
	public String atChar();

}
