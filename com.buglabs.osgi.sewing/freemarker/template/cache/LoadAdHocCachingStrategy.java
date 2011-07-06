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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import freemarker.template.TemplateException;

/**
 * Implements a load-ad-hoc caching strategy.
 * 
 * @author Nicholas Cull
 * @version $Id: LoadAdHocCachingStrategy.java 987 2004-10-05 10:13:24Z run2000
 *          $
 */
public final class LoadAdHocCachingStrategy extends BaseCachingStrategy {
	private HashMap cache = new HashMap();
	private String defaultTemplate;

	/** Creates new LoadAdHocCachingStrategy. */
	public LoadAdHocCachingStrategy() {
	}

	/**
	 * Sets the interval between two cache updates. This is meaningful only if
	 * the cache policy is a load-on-demand or preload type.
	 * 
	 * @param delay
	 *            the number of seconds between cache updates
	 */
	public void setDelay(long delay) {
		// Do nothing -- ad-hoc doesn't do auto-updates
	}

	/**
	 * Returns the interval between two cache updates. This is meaningful only
	 * if the cache policy is a load-on-demand or preload type.
	 * 
	 * @return the number of seconds between cache updates
	 */
	public long getDelay() {
		return 0;
	}

	/**
	 * Sets the maximum age a cache item can be before it is evicted from the
	 * cache. The age is determined as the number of cache updates since the
	 * item was last accessed. This is meaningful only if the cache policy is a
	 * load-on-demand type.
	 * 
	 * @param age
	 *            the maximum age before an item is evicted from the cache
	 */
	public void setMaximumAge(int age) {
		// Do nothing -- ad-hoc doesn't auto-expire cache items
	}

	/**
	 * Retrieves the maximum age a cache item can be before it is evicted from
	 * the cache. The age is determined as the number of cache updates since the
	 * item was last accessed. This is meaningful only if the cache policy is a
	 * load-on-demand type.
	 * 
	 * @return the maximum age before an item is evicted from the cache
	 */
	public int getMaximumAge() {
		return 0;
	}

	/**
	 * Retrieves an item from the cache, according to the loading policy
	 * implemented.
	 * 
	 * @param name
	 *            the name of the item to retrieve
	 * @return the corresponding <code>Cacheable</code> object, or
	 *         <code>null</code> if not found or an error has occurred
	 */
	public Cacheable getItem(String name) {
		return getItem(name, defaultTemplate);
	}

	/**
	 * Gets an item of the specified type from the cache.
	 * 
	 * @param name
	 *            a string uniquely identifying the item.
	 * @param type
	 *            the type of item to be retrieved
	 * @return the item corresponding to the name, or <code>null</code> if not
	 *         found.
	 */
	public Cacheable getItem(String name, String type) {
		CacheElement element = (CacheElement) cache.get(name);

		if (element == null) {
			return null;
		}

		// If we have an element, but of the wrong type, reget the element.
		if ((type != null) && (!type.equals(element.getType()))) {
			update(name, type);
			element = (CacheElement) cache.get(name);
			if ((element == null) || (!type.equals(element.getType()))) {
				eventHandler.fireElementUpdateFailed(this, name, new TemplateException("Couldn't update element to new type"));
				return null;
			}
		}
		return element.getObject();
	}

	/**
	 * Returns an iterator over a list of {@link CacheElement} instances.
	 * 
	 * @return the iterator over a list of <code>CacheElement</code> instances
	 *         that correspond to templates in the cache
	 */
	public Iterator listCachedFiles() {
		return Collections.unmodifiableCollection(cache.values()).iterator();
	}

	/**
	 * Begins automatic updates of the cache.
	 */
	public void startAutoUpdate() {
		try {
			loadItems();
		} catch (InterruptedException e) {
			// Propagate the interrupted status back up to the caller
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Stops automatically updating the cache.
	 */
	public void stopAutoUpdate() {
		// Do nothing
	}

	/**
	 * Asks for a "blank" update. It is up to the implementation to determine
	 * what has to be updated.
	 */
	public void update() {
		// Do nothing for ad-hoc loading
	}

	/**
	 * Asks for the named object to be updated.
	 * 
	 * @param name
	 *            the name of the object to update
	 */
	public void update(String name) {
		update(name, defaultTemplate);
	}

	/**
	 * Asks for the named object to be updated.
	 * 
	 * @param name
	 *            the name of the object to update
	 * @param type
	 *            the type of the object to update
	 */
	public void update(String name, String type) {
		Cacheable item;
		long lastModified;
		CacheElement element;

		if (!connectionOk()) {
			return;
		}

		element = (CacheElement) cache.get(name);
		try {
			lastModified = retriever.lastModified(name);
		} catch (TemplateException e) {
			eventHandler.fireElementUpdateFailed(this, name, e);
			return;
		}

		if (element == null) {
			// To avoid synchronisation issues, clone the existing map,
			// add the new element, then replace with the new cache
			HashMap newCache = (HashMap) cache.clone();
			try {
				item = retriever.loadData(name, type);
			} catch (TemplateException e) {
				eventHandler.fireElementUpdateFailed(this, name, e);
				return;
			}
			if (item != null) {
				item.setCache(this);
				newCache.put(name, new CacheElement(name, type, item, lastModified));
				synchronized (this) {
					cache = newCache;
				}
				eventHandler.fireElementUpdated(this, name, lastModified);
			}
		} else if (lastModified > element.lastModified()) {
			// Create a new element with the updated contents
			CacheElement newElement;
			try {
				item = retriever.loadData(name, type);
			} catch (TemplateException e) {
				eventHandler.fireElementUpdateFailed(this, name, e);
				return;
			}
			if (item != null) {
				item.setCache(this);
				newElement = new CacheElement(name, type, item, lastModified);

				// Replace the existing element -- should be atomic operation
				synchronized (this) {
					cache.put(name, newElement);
				}
				eventHandler.fireElementUpdated(this, name, lastModified);
			}

			// If we have an element, but of the wrong type, reget the element.
		} else if ((type != null) && (!type.equals(element.getType()))) {

			// Create a new element of the correct type
			try {
				item = retriever.loadData(name, type);
			} catch (TemplateException e) {
				eventHandler.fireElementUpdateFailed(this, name, e);
				return;
			}
			if (item != null) {
				item.setCache(this);
				element = new CacheElement(name, type, item, lastModified);

				// Replace the existing element -- should be atomic operation
				synchronized (this) {
					cache.put(name, element);
				}
				eventHandler.fireElementUpdated(this, name, lastModified);
			}
		}
	}

	/**
	 * Clears all the elements in the cache.
	 */
	public void clearCache() {
		cache = new HashMap();
	}

	/**
	 * Sets the default template to use when retrieving.
	 */
	public void setDefaultTemplate(String aTemplate) {
		defaultTemplate = aTemplate;
	}

	/**
	 * Load all items in the data store.
	 */
	private void loadItems() throws InterruptedException {

		try {
			List visitedFiles = retriever.getPreloadData();
			if (Thread.currentThread().interrupted()) {
				// Abort the update
				throw new InterruptedException();
			}
			loadItems(visitedFiles);
		} catch (TemplateException e) {
			eventHandler.fireCacheUnavailable(this, e);
			return;
		}
	}

	/**
	 * Load all items in a given list into the cache.
	 * 
	 * @param items
	 *            a list of strings representing an item to load in the cache.
	 */
	private void loadItems(List items) throws InterruptedException {
		HashMap newCache = new HashMap((int) (items.size() * 1.4), (float) 0.75);
		Iterator iName = items.iterator();
		String name;
		Cacheable item = null;
		long lastModified;
		Thread currentThread = Thread.currentThread();

		while (iName.hasNext()) {
			name = (String) iName.next();
			try {
				lastModified = retriever.lastModified(name);
			} catch (TemplateException e) {
				eventHandler.fireElementUpdateFailed(this, name, e);
				continue;
			}

			try {
				item = retriever.loadData(name, defaultTemplate);
			} catch (TemplateException e) {
				eventHandler.fireElementUpdateFailed(this, name, e);
			}

			if (item != null) {
				item.setCache(this);
			}
			newCache.put(name, new CacheElement(name, defaultTemplate, item, lastModified));
			eventHandler.fireElementUpdated(this, name, lastModified);

			// Check whether the thread has been interrupted in the mean time.
			// If so, exit the loop immediately.
			if (currentThread.interrupted()) {
				throw new InterruptedException();
			}
		}
		synchronized (this) {
			cache = newCache;
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("LoadAdHocCachingStrategy, ");
		buffer.append(cache.size());
		buffer.append(" cached items. ");
		if (retriever != null) {
			buffer.append(retriever.toString());
		}
		return buffer.toString();
	}
}
