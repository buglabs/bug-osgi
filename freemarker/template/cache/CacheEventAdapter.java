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

package freemarker.template.cache;

import java.util.EventListener;
import java.util.EventObject;

import freemarker.template.CacheEvent;
import freemarker.template.CacheListener;
import freemarker.template.GenericEventMulticaster;
import freemarker.template.ListenerAdapter;

/**
 * <p>
 * Adapter class responsible for firing cache events. Four different cache
 * events are supported:
 * </p>
 * <ul>
 * <li>cacheUnavailable</li>
 * <li>elementUpdated</li>
 * <li>elementUpdateFailed</li>
 * <li>elementRemoved</li>
 * </ul>
 * 
 * @author Nicholas Cull
 * @version $Id: CacheEventAdapter.java 1051 2004-10-24 09:14:44Z run2000 $
 * @see freemarker.template.CacheEvent
 * @see freemarker.template.CacheListener
 */
public class CacheEventAdapter {

	/** Holds any event listeners wanting to receive cache events. */
	protected GenericEventMulticaster multicaster = new GenericEventMulticaster();

	/** A ListenerAdapter for cacheUnavailable events. */
	protected ListenerAdapter unavailableAdapter = new ListenerAdapter() {
		public void fireEvent(EventObject event, EventListener listener) {
			((CacheListener) listener).cacheUnavailable((CacheEvent) event);
		}
	};

	/** A ListenerAdapter for elementUpdated events. */
	protected ListenerAdapter updatedAdapter = new ListenerAdapter() {
		public void fireEvent(EventObject event, EventListener listener) {
			((CacheListener) listener).elementUpdated((CacheEvent) event);
		}
	};

	/** A ListenerAdapter for elementUpdateFailed events. */
	protected ListenerAdapter updateFailedAdapter = new ListenerAdapter() {
		public void fireEvent(EventObject event, EventListener listener) {
			((CacheListener) listener).elementUpdateFailed((CacheEvent) event);
		}
	};

	/** A ListenerAdapter for elementRemoved events. */
	protected ListenerAdapter removedAdapter = new ListenerAdapter() {
		public void fireEvent(EventObject event, EventListener listener) {
			((CacheListener) listener).elementRemoved((CacheEvent) event);
		}
	};

	/** Creates new CacheEventAdapter. */
	public CacheEventAdapter() {
	}

	/**
	 * Fires a cacheUnavailable event to all registered listeners.
	 * 
	 * @param source
	 *            the source of the event
	 * @param e
	 *            the <code>Exception</code> that caused the event to be fired
	 */
	public void fireCacheUnavailable(Cache source, Exception e) {
		fireCacheEvent(source, unavailableAdapter, null, e);
	}

	/**
	 * Fires an elementUpdated event to all registered listeners.
	 * 
	 * @param source
	 *            the source of the event
	 * @param elementName
	 *            the cache element that was updated
	 */
	public void fireElementUpdated(Cache source, String elementName, long lastModified) {
		fireCacheEvent(source, updatedAdapter, elementName, null);
	}

	/**
	 * Fires an elementUpdateFailed event to all registered listeners.
	 * 
	 * @param source
	 *            the source of the event
	 * @param elementName
	 *            the cache element that failed to be updated
	 * @param e
	 *            the <code>Exception</code> that caused the event to be fired
	 */
	public void fireElementUpdateFailed(Cache source, String elementName, Exception e) {
		fireCacheEvent(source, updateFailedAdapter, elementName, e);
	}

	/**
	 * Fires an elementRemoved event to all registered listeners.
	 * 
	 * @param source
	 *            the source of the event
	 * @param elementName
	 *            the cache element that was updated
	 */
	public void fireElementRemoved(Cache source, String elementName) {
		fireCacheEvent(source, removedAdapter, elementName, null);
	}

	/**
	 * A convenience method for firing a CacheEvent.
	 * 
	 * @param source
	 *            the source of the event
	 * @param adapter
	 *            a <code>ListenerAdapter</code>.
	 * @param elementName
	 *            the name of the cache element in question, or null.
	 * @param e
	 *            an <code>Exception</code> to be included with the event, or
	 *            null.
	 */
	protected void fireCacheEvent(Cache source, ListenerAdapter adapter, String elementName, Exception e) {
		if (!multicaster.isEmpty()) {
			CacheEvent event = new CacheEvent(source, elementName, e);
			multicaster.fireEvent(event, adapter);
		}
	}

	/**
	 * A convenience method for firing a CacheEvent.
	 * 
	 * @param source
	 *            the source of the event
	 * @param adapter
	 *            a <code>ListenerAdapter</code>.
	 * @param elementName
	 *            the name of the cache element in question, or null.
	 * @param lastModified
	 *            the time that the element was last modified
	 */
	protected void fireCacheEvent(Cache source, ListenerAdapter adapter, String elementName, long lastModified) {
		if (!multicaster.isEmpty()) {
			CacheEvent event = new CacheEvent(source, elementName, lastModified);
			multicaster.fireEvent(event, adapter);
		}
	}

	/**
	 * Registers a {@link freemarker.template.CacheListener} for a {@link Cache}
	 * .
	 * 
	 * @param listener
	 *            the <code>CacheListener</code> to be registered.
	 */
	public void addCacheListener(CacheListener listener) {
		multicaster.addEventListener(listener);
	}

	/**
	 * Unregisters a {@link freemarker.template.CacheListener} for a
	 * {@link Cache}.
	 * 
	 * @param listener
	 *            the <code>CacheListener</code> to be unregistered.
	 */
	public void removeCacheListener(CacheListener listener) {
		multicaster.removeEventListener(listener);
	}

	/**
	 * Retrieves all the listeners associated with this {@link Cache}.
	 * 
	 * @return an array of <tt>CacheListener</tt>s
	 */
	public CacheListener[] getCacheListeners() {
		return (CacheListener[]) multicaster.getEventListeners();
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("CacheEventAdapter, ");
		buffer.append(multicaster);
		return buffer.toString();
	}
}
