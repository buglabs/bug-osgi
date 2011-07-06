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

import java.util.EventObject;

/**
 * An event fired by self-updating caches.
 * 
 * @version $Id: CacheEvent.java 1051 2004-10-24 09:14:44Z run2000 $
 * @see CacheListener
 * @see FileTemplateCache
 */
public final class CacheEvent extends EventObject {

	/**
	 * @serial an <code>Exception</code> associated with this event.
	 */
	private final Exception exception;

	/**
	 * @serial the name of an element associated with this event.
	 */
	private final String elementName;

	/**
	 * @serial the modification date of the element, if applicable
	 */
	private final long lastModified;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -4153598084703911616L;

	/**
	 * Constructor that takes the object where the event originated.
	 * 
	 * @param source
	 *            the source of this event
	 */
	public CacheEvent(Object source) {
		super(source);
		exception = null;
		elementName = null;
		lastModified = 0;
	}

	/**
	 * Constructor that takes an element name, and the exception that caused the
	 * event.
	 * 
	 * @param source
	 *            the source of this event
	 * @param elementName
	 *            the name of the cache element
	 * @param lastModified
	 *            the time that the element was last modified
	 */
	public CacheEvent(Object source, String elementName, long lastModified) {
		super(source);
		this.elementName = elementName;
		this.lastModified = lastModified;
		this.exception = null;
	}

	/**
	 * Constructor that takes an element name, and the exception that caused the
	 * event.
	 * 
	 * @param source
	 *            the source of this event
	 * @param elementName
	 *            the name of the cache element
	 * @param exception
	 *            the exception that caused the event
	 */
	public CacheEvent(Object source, String elementName, Exception exception) {
		super(source);
		this.elementName = elementName;
		this.exception = exception;
		this.lastModified = 0;
	}

	/**
	 * Retrieves the exception that caused the event.
	 * 
	 * @return the <code>Exception</code>, if any, associated with this event.
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Retrieves the name of the element that caused the event.
	 * 
	 * @return the name of the cache element, if any, associated with this
	 *         event.
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * Retrieves the last modification date of the named element. Applicable
	 * only for elementUpdated events.
	 * 
	 * @return a <code>long</code> representing the time in milliseconds that
	 *         the element was last modified, or <code>0</code> if no time is
	 *         specified
	 */
	public long getLastModified() {
		return lastModified;
	}
}
