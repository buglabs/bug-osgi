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
 * The FM-Classic classes use this exception internally.
 * 
 * @version $Id: TemplateException.java 1051 2004-10-24 09:14:44Z run2000 $
 */
public class TemplateException extends Exception {

	/** The underlying cause of this exception, if any. */
	private final Exception causeException;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -390036580929011875L;

	/**
	 * Constructs a <code>TemplateException</code> with no specified detail
	 * message or underlying cause.
	 */
	public TemplateException() {
		causeException = null;
	}

	/**
	 * Constructs a <code>TemplateException</code> with the given detail
	 * message, but no underlying cause exception.
	 * 
	 * @param description
	 *            the description of the error that occurred
	 */
	public TemplateException(String description) {
		super(description);
		causeException = null;
	}

	/**
	 * Constructs a <code>TemplateException</code> with the given underlying
	 * <code>Exception</code>, but no detail message.
	 * 
	 * @param cause
	 *            the underlying <code>Exception</code> that caused this
	 *            exception to be raised
	 */
	public TemplateException(Exception cause) {
		causeException = cause;
	}

	/**
	 * Constructs a <code>TemplateException</code> with both a description of
	 * the error that occurred and the underlying <code>Exception</code> that
	 * caused this exception to be raised.
	 * 
	 * @param description
	 *            the description of the error that occurred
	 * @param cause
	 *            the underlying <code>Exception</code> that caused this
	 *            exception to be raised
	 */
	public TemplateException(String description, Exception cause) {
		super(description);
		causeException = cause;
	}

	/**
	 * <p>
	 * Returns the underlying exception that caused this exception to be
	 * generated.
	 * </p>
	 * <p>
	 * <b>Note:</b><br />
	 * avoided calling it <code>getCause</code> to avoid name clash with JDK 1.4
	 * method. This would be problematic because the JDK 1.4 method returns a
	 * <code>Throwable</code> rather than an <code>Exception</code>.
	 * </p>
	 * 
	 * @return the underlying <code>Exception</code>, if any, that caused this
	 *         exception to be raised
	 */
	public Exception getCauseException() {
		return causeException;
	}

	/**
	 * <p>
	 * Returns the underlying exception that caused this exception to be
	 * generated. This method is intended as a compatibility method for JDK 1.4
	 * and later.
	 * </p>
	 * 
	 * @return the underlying <code>Throwable</code>, if any, that caused this
	 *         exception to be raised
	 */
	public Throwable getCause() {
		return causeException;
	}
}
