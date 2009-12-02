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

import freemarker.template.TemplateException;

/**
 * Exception thrown by the compiler whenever a parse exception occurs.
 * 
 * @author Nicholas Cull
 * @version $Id: ParseException.java 1051 2004-10-24 09:14:44Z run2000 $
 */
public class ParseException extends TemplateException {

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 7493810628186924946L;

	/**
	 * Default constructor for the ParseException.
	 */
	public ParseException() {
	}

	/**
	 * Constructs a <code>ParseException</code> along with a reason for the
	 * exception.
	 * 
	 * @param reason
	 *            the reason the exception was thrown
	 */
	public ParseException(String reason) {
		super(reason);
	}

	/**
	 * Constructs a <code>ParseException</code> with the given underlying
	 * Exception, but no detail message.
	 * 
	 * @param cause
	 *            the underlying <code>Exception</code> that caused this
	 *            exception to be raised
	 */
	public ParseException(Exception cause) {
		super(cause);
	}

	/**
	 * Constructs a <code>ParseException</code> with both a description of the
	 * error that occurred and the underlying Exception that caused this
	 * exception to be raised.
	 * 
	 * @param description
	 *            the description of the error that occurred
	 * @param cause
	 *            the underlying <code>Exception</code> that caused this
	 *            exception to be raised
	 */
	public ParseException(String description, Exception cause) {
		super(description, cause);
	}
}
