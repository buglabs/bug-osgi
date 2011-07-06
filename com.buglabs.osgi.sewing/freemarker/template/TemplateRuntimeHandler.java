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

import java.io.Writer;

/**
 * Interface for handling events that occur during FM-Classic template runtime.
 * This can be implemented by adapter classes, or can be handled directly.
 * 
 * @version $Id: TemplateRuntimeHandler.java 987 2004-10-05 10:13:24Z run2000 $
 * @since 1.8
 * @see TemplateExceptionEvent
 * @see TemplateExceptionListener
 */
public interface TemplateRuntimeHandler {

	/**
	 * A severe error has occurred, that may prevent FM-Classic from processing
	 * the template.
	 */
	public static final int SEVERITY_ERROR = 1;
	/** An error that is non-critical to the continuation of processing. */
	public static final int SEVERITY_WARNING = 2;
	/** Used whenever a deprecated construct is encountered. */
	public static final int SEVERITY_DEPRECATION = 3;

	/**
	 * Fires a <code>TemplateExceptionEvent</code> to indicate that an exception
	 * has occurred in the FM-Classic runtime. Implementations can use this
	 * method to pass the event to {@link TemplateExceptionListener} objects.
	 * 
	 * @param source
	 *            the source object of the event
	 * @param exception
	 *            the exception that caused the event to be fired
	 * @param output
	 *            the current Template output stream
	 * @param sourceName
	 *            the name of the source class and method that fired the event
	 * @param severity
	 *            the severity of the exception
	 */
	void fireExceptionThrown(Object source, Exception exception, Writer output, String sourceName, int severity);
}
