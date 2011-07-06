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
 * Event object that is thrown at runtime whenever an exceptional situation
 * occurs.
 * 
 * @version $Id: TemplateExceptionEvent.java 1087 2005-08-28 12:37:29Z run2000 $
 * @see TemplateExceptionListener
 * @see TemplateRuntimeHandler
 */
public final class TemplateExceptionEvent extends java.util.EventObject {
	/** @serial the name of the source object that caused this event. */
	private final String sourceName;
	/** @serial the severity of the error. */
	private final int severity;
	/** @serial a writer to write any error message to. */
	private final Writer output;
	/** @serial the exception containing details of the event. */
	private final Exception exception;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -6235933433102342378L;

	/**
	 * Creates new TemplateExceptionEvent.
	 * 
	 * @param source
	 *            the source of the event
	 * @param e
	 *            the exception that caused the event
	 * @param output
	 *            the output stream of the <code>Template</code>
	 * @param sourceName
	 *            the name of the event source
	 * @param severity
	 *            the severity of the message
	 */
	public TemplateExceptionEvent(Object source, Exception e, Writer output, String sourceName, int severity) {
		super(source);
		this.sourceName = sourceName;
		this.severity = severity;
		this.exception = e;
		this.output = output;
	}

	/**
	 * Get the name of the source, such as
	 * <code>freemarker.template.Template.process</code>
	 * 
	 * @return the name of the source
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * Retrieve the severity, such as
	 * {@link TemplateRuntimeHandler#SEVERITY_ERROR}.
	 * 
	 * @return the severity of the error
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * Get the {@link Template}'s current output stream. Used whenever we want
	 * to send messages to the template's output as a result of this event.
	 * 
	 * @return the output stream of the <code>Template</code>
	 * @deprecated use the {@link #getWriter} method for maximum efficiency,
	 *             since this method now has to wrap the underlying
	 *             <code>Writer</code> in a <code>PrintWriter</code> object
	 */
	public PrintWriter getOutput() {
		return new PrintWriter(output);
	}

	/**
	 * Get the {@link Template}'s current <code>Writer</code>. Used whenever we
	 * want to send messages to the template's output as a result of this event.
	 * 
	 * @return the <code>Writer</code> for this <code>Template</code>
	 */
	public Writer getWriter() {
		return output;
	}

	/**
	 * The exception that caused this event to be fired.
	 * 
	 * @return the underlying exception
	 */
	public Exception getException() {
		return exception;
	}
}
