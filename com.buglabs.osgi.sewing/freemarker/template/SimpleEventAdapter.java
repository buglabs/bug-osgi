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
 * <p>
 * Adapter class for firing events that could happen at {@link Template}
 * runtime. At most one listener can be registered for different events that can
 * occur. This event adapter is an immutable class so that singleton instances
 * can't have their behaviour changed.
 * </p>
 * 
 * <p>
 * <b>Usage example:</b>
 * </p>
 * 
 * <pre>
 * // Configure Log4j
 * ...
 * 
 * // Set up the template
 * Template template = new Template(filename);
 * 
 * // Set up the model
 * TemplateModelRoot modelRoot = (...)
 * 
 * // Set up the listener
 * SimpleEventAdapter adapter = new SimpleEventAdapter( new Log4jExceptionListener() );
 * 
 * // Call the template
 * template.process( modelRoot, writer, adapter );
 * </pre>
 * 
 * @version $Id: SimpleEventAdapter.java 987 2004-10-05 10:13:24Z run2000 $
 * @since 1.8
 * @see TemplateEventAdapter
 */
public final class SimpleEventAdapter implements TemplateRuntimeHandler {

	private final TemplateExceptionListener exceptionListener;

	/** Creates a new SimpleEventAdapter. */
	public SimpleEventAdapter() {
		exceptionListener = null;
	}

	/**
	 * Creates a new SimpleEventAdapter with the given exception listener as the
	 * target of the exception events generated.
	 * 
	 * @param exceptionListener
	 *            the single event listener to received exception events
	 */
	public SimpleEventAdapter(TemplateExceptionListener exceptionListener) {
		this.exceptionListener = exceptionListener;
	}

	/**
	 * Fires a <code>TemplateExceptionEvent</code> to the current listener.
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
	public void fireExceptionThrown(Object source, Exception exception, Writer output, String sourceName, int severity) {

		if (exceptionListener != null) {
			TemplateExceptionEvent event = new TemplateExceptionEvent(source, exception, output, sourceName, severity);
			try {
				exceptionListener.exceptionThrown(event);
			} catch (TemplateException e) {
				// Do nothing, since we can't report it
			}
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("SimpleEventAdapter, ");
		buffer.append(exceptionListener);
		buffer.append(" listening.");
		return buffer.toString();
	}
}
