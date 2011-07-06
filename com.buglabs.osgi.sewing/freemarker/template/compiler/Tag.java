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

import freemarker.template.instruction.Instruction;

/**
 * Represents an unparsed tag. Subclasses can call back the parser to have
 * themselves parsed.
 * 
 * @version $Id: Tag.java 987 2004-10-05 10:13:24Z run2000 $
 */
abstract class Tag {
	/**
	 * Returns a new parsed object, or calls a parser method that returns one,
	 * advancing parsePos.
	 * 
	 * @param parser
	 *            the <code>StandardTemplateParser</code> to be called
	 * @return the newly parsed <code>Instruction</code>
	 * @throws ParseException
	 *             something went wrong while parsing the
	 *             <code>Instruction</code>
	 */
	abstract Instruction parse(StandardTemplateParser parser) throws ParseException;
}
