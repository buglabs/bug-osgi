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

/**
 * Template model classes should throw this exception if requested data cannot
 * be retrieved. Its detail message will be used to build an error message in
 * the output of a template using the model.
 * 
 * @version $Id: TemplateModelException.java 1051 2004-10-24 09:14:44Z run2000 $
 */
public class TemplateModelException extends TemplateException {

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -1707011064187135336L;

	/**
	 * Constructs a <code>TemplateModelException</code> with no specified detail
	 * message or underlying cause.
	 */
	public TemplateModelException() {
	}

	/**
	 * Constructs a <code>TemplateModelException</code> with the specified
	 * detail message, but no underlying cause Exception.
	 * 
	 * @param description
	 *            the detail message.
	 */
	public TemplateModelException(String description) {
		super(description);
	}

	/**
	 * Constructs a <code>TemplateModelException</code> with the given
	 * underlying <code>Exception</code>, but no detail message.
	 * 
	 * @param cause
	 *            the underlying <code>Exception</code> that caused this
	 *            exception to be raised
	 */
	public TemplateModelException(Exception cause) {
		super(cause);
	}

	/**
	 * Constructs a <code>TemplateModelException</code> with both a description
	 * of the error that occurred and the underlying <code>Exception</code> that
	 * caused this exception to be raised.
	 * 
	 * @param description
	 *            the description of the error that occurred
	 * @param cause
	 *            the underlying <code>Exception</code> that caused this
	 *            exception to be raised
	 */
	public TemplateModelException(String description, Exception cause) {
		super(description, cause);
	}
}
