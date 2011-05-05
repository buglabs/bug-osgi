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
import java.util.EventListener;
import java.util.EventObject;

/**
 * <p>
 * Adapter class for firing events that could happen at {@link Template}
 * runtime. Different listeners can be registered for different events that can
 * occur. So far only one listener class is defined:
 * {@link TemplateExceptionEvent}.
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
 * TemplateEventAdapter adapter = new TemplateEventAdapter();
 * adapter.addTemplateExceptionListener( new Log4jExceptionListener() );
 * 
 * // Add more listeners as required
 * adapter.addTemplateExceptionListener( ... );
 * 
 * // Call the template
 * template.process( modelRoot, writer, adapter );
 * </pre>
 * 
 * @version $Id: TemplateEventAdapter.java 1087 2005-08-28 12:37:29Z run2000 $
 * @see SimpleEventAdapter
 */
public class TemplateEventAdapter implements TemplateRuntimeHandler {

	/**
	 * A severe error has occurred, that may prevent FM-Classic from processing
	 * the template.
	 * 
	 * @deprecated this constant has moved to the
	 *             <code>TemplateRuntimeHandler</code> interface
	 */
	public static final int SEVERITY_ERROR = 1;
	/**
	 * An error that is non-critical to the continuation of processing.
	 * 
	 * @deprecated this constant has moved to the
	 *             <code>TemplateRuntimeHandler</code> interface
	 */
	public static final int SEVERITY_WARNING = 2;
	/**
	 * Used whenever a deprecated construct is encountered.
	 * 
	 * @deprecated this constant has moved to the
	 *             <code>TemplateRuntimeHandler</code> interface
	 */
	public static final int SEVERITY_DEPRECATION = 3;

	/**
	 * The default <code>TemplateRuntimeHandler</code> instance when no other
	 * one is specified.
	 */
	public static final TemplateRuntimeHandler DefaultEventAdapter = new SimpleEventAdapter(HtmlExceptionListener.getInstance());

	/**
	 * The trivial <code>TemplateRuntimeHandler</code> instance where no events
	 * are fired.
	 */
	public static final TemplateRuntimeHandler NullEventAdapter = new SimpleEventAdapter();

	/**
	 * The multicaster that notifies all event listeners when an exception
	 * occurs.
	 */
	protected GenericEventMulticaster templateExceptionListeners = new GenericEventMulticaster();

	/** A ListenerAdapter for exceptionThrown events. */
	protected ListenerAdapter exceptionThrownAdapter = new ListenerAdapter() {
		public void fireEvent(EventObject event, EventListener listener) throws Exception {
			((TemplateExceptionListener) listener).exceptionThrown((TemplateExceptionEvent) event);
		}
	};

	/** Creates new TemplateEventAdapter. */
	public TemplateEventAdapter() {
	}

	/**
	 * Fires a <code>TemplateExceptionEvent</code> to all the current listeners.
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

		if (!templateExceptionListeners.isEmpty()) {
			TemplateExceptionEvent event = new TemplateExceptionEvent(source, exception, output, sourceName, severity);
			templateExceptionListeners.fireEvent(event, exceptionThrownAdapter);
		}
	}

	/**
	 * Adds a listener for {@link TemplateExceptionEvent} events.
	 * 
	 * @param el
	 *            the event listener to be added
	 */
	public void addTemplateExceptionListener(TemplateExceptionListener el) {
		templateExceptionListeners.addEventListener(el);
	}

	/**
	 * Removes the specified listener.
	 * 
	 * @param el
	 *            the event listener to be removed
	 */
	public void removeTemplateExceptionListener(TemplateExceptionListener el) {
		templateExceptionListeners.removeEventListener(el);
	}

	/**
	 * Retrieves all the current <code>TemplateExceptionListener</code>s that
	 * are listening for events.
	 * 
	 * @return an array of <code>TemplateExceptionListener</code>s
	 */
	public TemplateExceptionListener[] getTemplateExceptionListeners() {
		return (TemplateExceptionListener[]) templateExceptionListeners.getEventListeners();
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("TemplateEventAdapter, ");
		buffer.append(templateExceptionListeners);
		return buffer.toString();
	}
}
