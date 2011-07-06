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

package freemarker.template;

import java.io.IOException;
import java.io.Writer;

/**
 * Objects representing compiled templates must implement this interface.
 * 
 * @version $Id: TemplateProcessor.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public interface TemplateProcessor {

	/**
	 * Constant returned by <code>process()</code> indicating that this is
	 * normal control flow.
	 */
	public static final short OK = 0;

	/**
	 * Constant returned by <code>process()</code> indicating that this is the
	 * result of a break instruction.
	 */
	public static final short BREAK = 1;

	/**
	 * Constant returned by <code>process()</code> indicating that this is the
	 * result of an exit instruction.
	 */
	public static final short EXIT = 2;

	/**
	 * Constant returned by <code>process()</code> indicating that this is the
	 * result of a break instruction.
	 */
	public static final short UNCOMPILED_TEMPLATE = -1;

	/**
	 * Processes the contents of this <code>TemplateProcessor</code> and outputs
	 * the resulting text to a <code>Writer</code>.
	 * 
	 * @param modelRoot
	 *            the root node of the data model.
	 * @param out
	 *            a <code>Writer</code> to send the output to.
	 * @param eventHandler
	 *            a <code>TemplateEventAdapter</code> for handling any events
	 *            that occur during processing.
	 * @return an exit code indicating how the process terminated, typically
	 *         used for short-circuiting template processing
	 * @throws IOException
	 *             an IO error occurred during processing
	 */
	public short process(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler) throws IOException;
}
