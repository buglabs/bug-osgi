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

import java.io.PrintWriter;
import java.io.Writer;

/**
 * A basic handler that matches FreeMarker template legacy behaviour: any
 * exceptions thrown by FM-Classic are output inside an HTML comment within the
 * body of the output.
 * 
 * @author Nicholas Cull
 * @version $Id: HtmlExceptionListener.java 1087 2005-08-28 12:37:29Z run2000 $
 */
public final class HtmlExceptionListener implements TemplateExceptionListener {

	/** The beginning of an HTML, SGML, or XML style comment tag. */
	private static final String COMMENT_START = "<!-- ";
	/** The end of an HTML, SGML, or XML style comment tag. */
	private static final String COMMENT_END = " -->";
	/** The singleton instance of this class. */
	private static final HtmlExceptionListener instance = new HtmlExceptionListener();

	/**
	 * Creates new <code>HtmlExceptionListener</code>.
	 * 
	 * @deprecated use the {@link #getInstance} method instead
	 */
	public HtmlExceptionListener() {
	}

	/**
	 * Retrieves a singleton instance of this HTML exception listener
	 * 
	 * @return a singleton instance of this class
	 */
	public static HtmlExceptionListener getInstance() {
		return instance;
	}

	/**
	 * Handles events that are created whenever an exception is thrown. This
	 * handler turns the event into an HTML comment, and is inserted into the
	 * current output stream of the Template.
	 */
	public void exceptionThrown(TemplateExceptionEvent e) {
		Writer cWriter = e.getWriter();
		PrintWriter cOut = new PrintWriter(cWriter);
		Exception ex = e.getException();
		int severity = e.getSeverity();

		cOut.print(COMMENT_START);
		if (severity == TemplateRuntimeHandler.SEVERITY_DEPRECATION) {
			cOut.print("Note: ");
		}
		cOut.print(ex.getMessage());

		if (ex instanceof TemplateException) {
			Exception cause = ((TemplateException) ex).getCauseException();
			if (cause != null) {
				cOut.print(": ");
				cOut.println(cause.getMessage());
				ex = cause;
			}
		}

		if (severity == TemplateRuntimeHandler.SEVERITY_ERROR) {
			ex.printStackTrace(cOut);
		} else {
			cOut.print('.');
		}

		cOut.println(COMMENT_END);
	}
}
