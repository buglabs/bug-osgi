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

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 * A generic event multicaster class. The client owns an object of this class,
 * and calls the {@link #fireEvent} method to fire events.
 * 
 * @version $Id: GenericEventMulticaster.java 1056 2004-10-28 02:16:01Z run2000
 *          $
 */
public final class GenericEventMulticaster {

	/** The registered event listeners. */
	private volatile List listenerList;

	/** Default constructor. */
	public GenericEventMulticaster() {
		listenerList = new ArrayList();
	}

	/**
	 * Adds an event listener. The order in which the listeners are added
	 * determines the order that the listeners receive each event.
	 * 
	 * @param listener
	 *            the event listener to be registered
	 */
	public void addEventListener(EventListener listener) {
		List listClone;

		// To avoid possible thread deadlocks that could occur if a
		// listener attempts to modify listenerList while we're sending
		// the event to that listener, clone the list, then add the
		// listener to the cloned list
		listClone = (List) ((ArrayList) listenerList).clone();
		listClone.add(listener);
		listenerList = listClone;
	}

	/**
	 * Removes an event listener that was previously added. If a listener was
	 * added more than once, only the first instance is removed.
	 * 
	 * @param listener
	 *            the event listener to be unregistered
	 */
	public void removeEventListener(EventListener listener) {
		List listClone;

		// To avoid possible thread deadlocks that could occur if a
		// listener attempts to modify listenerList while we're sending
		// the event to that listener, clone the list, then remove the
		// listener from the cloned list
		listClone = (List) ((ArrayList) listenerList).clone();

		Iterator iterator = listClone.listIterator();
		while (iterator.hasNext()) {
			if (iterator.next() == listener) {
				iterator.remove();
				break;
			}
		}

		listenerList = listClone;
	}

	/**
	 * Gets all the registered event listeners. This method was added for
	 * compatibility with JavaBeans as of JDK 1.4.
	 */
	public EventListener[] getEventListeners() {
		return (EventListener[]) listenerList.toArray();
	}

	/**
	 * To shortcut event firing: if there's nothing listening, don't bother
	 * creating an <code>EventObject</code>.
	 * 
	 * @return <code>true</code> if there are no registered listeners, otherwise
	 *         <code>false</code>
	 */
	public boolean isEmpty() {
		return listenerList.isEmpty();
	}

	/**
	 * Fires an event to all the listeners of this multicaster, using a
	 * <code>ListenerAdapter</code>. If any listener throws an exception, we
	 * immediately abort sending the event to any other listeners.
	 * 
	 * @param event
	 *            the event to be sent to each of the listeners
	 * @param adapter
	 *            adapts the event listeners to this multicaster
	 * @see ListenerAdapter
	 */
	public void fireEvent(EventObject event, ListenerAdapter adapter) {
		// This is non-obvious, but tries to make the listener list
		// non-volatile for the purposes of iterating through it.
		// This is a vague area of "volatile" semantics.
		List listCopy = listenerList;
		Iterator iterator = listCopy.iterator();

		try {
			while (iterator.hasNext()) {
				EventListener listener = (EventListener) iterator.next();
				adapter.fireEvent(event, listener);
			}
		} catch (Exception e) {
			// Do nothing, except to abort the firing of any further events
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(50);

		buffer.append("GenericEventMulticaster, ");
		buffer.append(listenerList.size());
		buffer.append(" event listeners.");
		return buffer.toString();
	}
}
