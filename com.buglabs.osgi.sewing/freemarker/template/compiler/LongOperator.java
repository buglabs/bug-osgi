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

import freemarker.template.expression.Expression;

/**
 * Represents an unparsed long operator (an operator that's more than one
 * character long). Subclasses return new
 * {@link freemarker.template.expression.Expression} objects.
 * 
 * @version $Id: LongOperator.java 987 2004-10-05 10:13:24Z run2000 $
 */
abstract class LongOperator {

	/**
	 * Returns a new Expression object corresponding to the operator, and
	 * advances parsePos.
	 */
	abstract Expression parse() throws ParseException;
}
