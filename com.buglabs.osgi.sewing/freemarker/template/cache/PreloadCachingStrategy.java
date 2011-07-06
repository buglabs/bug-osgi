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
import java.util.Map;
import java.util.Set;

import freemarker.template.TemplateException;

/**
 * Implements a preload caching strategy.
 * 
 * @author Nicholas Cull
 * @version $Id: PreloadCachingStrategy.java 1051 2004-10-24 09:14:44Z run2000 $
 */
public final class PreloadCachingStrategy extends BaseCachingStrategy {
	private Map cache = new HashMap();
	private UpdateTimer timer;
	private long delay = 5000; // five seconds
	private String defaultTemplate;

	/** Creates new PreloadCachingStrategy. */
	public PreloadCachingStrategy() {
	}

	/**
	 * Sets the interval between two cache updates. This is meaningful only if
	 * the cache policy is a load-on-demand or preload type.
	 * 
	 * @param delay
	 *            the number of seconds between cache updates
	 */
	public synchronized void setDelay(long delay) {
		this.delay = delay * 1000;
	}

	/**
	 * Returns the interval between two cache updates. This is meaningful only
	 * if the cache policy is a load-on-demand or preload type.
	 * 
	 * @return the number of seconds between cache updates
	 */
	public synchronized long getDelay() {
		return delay / 1000;
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
		// Do nothing -- preload doesn't perform cache expiry
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
	 * implemented. We're preloading, so we can just return the template from
	 * the cache if we have it -- it will be updated the next time our
	 * <code>update()</code> method is called.
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
	 * Retrieves an item from the cache, according to the loading policy
	 * implemented. We're preloading, so we can just return the template from
	 * the cache if we have it -- it will be updated the next time our
	 * <code>update()</code> method is called.
	 * 
	 * @param name
	 *            the name of the item to retrieve
	 * @return the corresponding <code>Cacheable</code> object, or
	 *         <code>null</code> if not found or an error has occurred
	 */
	public Cacheable getItem(String name, String type) {
		CacheElement element = (CacheElement) cache.get(name);

		if (element == null) {
			return null;
		}

		// If we have an element, but of the wrong type, reget the element.
		if ((type != null) && (!type.equals(element.getType()))) {
			Cacheable item;
			long lastModified;

			try {
				// Create a new element of the correct type
				lastModified = retriever.lastModified(name);
				item = retriever.loadData(name, type);
				if (item != null) {
					item.setCache(this);
				}
				element = new CacheElement(name, type, item, lastModified);

				// Replace the existing element -- should be atomic operation
				synchronized (this) {
					cache.put(name, element);
				}
				eventHandler.fireElementUpdated(this, name, lastModified);

			} catch (TemplateException e) {
				eventHandler.fireElementUpdateFailed(this, name, e);
				return null;
			}
		}
		return element.getObject();
	}

	/**
	 * Returns an iterator over a list of CacheElement instances.
	 * 
	 * @return the iterator over a list of CacheElement instances that
	 *         correspond to templates in the cache
	 */
	public Iterator listCachedFiles() {
		return Collections.unmodifiableCollection(cache.values()).iterator();
	}

	/**
	 * Begins automatic updates of the cache.
	 */
	public synchronized void startAutoUpdate() {
		stopAutoUpdate();

		try {
			update();
			timer = new UpdateTimer(this, delay);
			timer.startTiming(1);
		} catch (InterruptedException e) {
			// Propagate the interrupted status up to the caller
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Stops automatically updating the cache.
	 */
	public synchronized void stopAutoUpdate() {
		if (timer != null) {
			timer.stopTiming();
			timer = null;
		}
	}

	/**
	 * Asks for a "blank" update. It is up to the implementation to determine
	 * what has to be updated.
	 */
	public void update() throws InterruptedException {
		if (connectionOk()) {
			loadItems();
		}
	}

	/**
	 * Asks for the named object to be updated.
	 * 
	 * @param name
	 *            the name of the object to update
	 */
	public void update(String name) {
		// Do nothing
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
		// Do nothing
	}

	/**
	 * Load all items in the data store and remove the ones in the cache that we
	 * don't know about.
	 */
	private void loadItems() throws InterruptedException {

		try {
			List visitedFiles = retriever.getPreloadData();
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			loadItems(visitedFiles);
		} catch (TemplateException e) {
			stopAutoUpdate();
			eventHandler.fireCacheUnavailable(this, e);
			return;
		}
	}

	/**
	 * Load all items in a given list into the cache. If an item already exists,
	 * it is checked for currency, and updated if necessary.
	 * 
	 * @param items
	 *            a list of strings representing an item to load in the cache.
	 */
	private void loadItems(List items) throws InterruptedException {
		Map newCache = new HashMap((int) (items.size() * 1.4) + 1, (float) 0.75);
		Iterator iName = items.iterator();
		String name;
		Cacheable item;
		long lastModified;
		CacheElement element;

		while (iName.hasNext()) {
			name = (String) iName.next();
			element = (CacheElement) cache.get(name);

			try {
				lastModified = retriever.lastModified(name);
			} catch (TemplateException e) {
				eventHandler.fireElementUpdateFailed(this, name, e);
				continue;
			}

			if (element == null) {
				try {
					item = retriever.loadData(name, defaultTemplate);
				} catch (TemplateException e) {
					eventHandler.fireElementUpdateFailed(this, name, e);
					item = null;
				}
				if (item != null) {
					item.setCache(this);
					newCache.put(name, new CacheElement(name, defaultTemplate, item, lastModified));
					eventHandler.fireElementUpdated(this, name, lastModified);
				}

			} else if (lastModified > element.lastModified()) {
				String type = element.getType();
				try {
					item = retriever.loadData(name, type);
				} catch (TemplateException e) {
					eventHandler.fireElementUpdateFailed(this, name, e);
					item = null;
				}
				if (item != null) {
					item.setCache(this);
					newCache.put(name, new CacheElement(name, type, item, lastModified));
					eventHandler.fireElementUpdated(this, name, lastModified);
				}
			} else {
				newCache.put(name, element);
			}

			// Check whether the thread has been interrupted in the mean time.
			// If so, exit the loop immediately.
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
		}
		removeUnvisitedItems(newCache);
		synchronized (this) {
			cache = newCache;
		}
	}

	/**
	 * Fire the eventRemoved event for any items corresponding to those we
	 * didn't just visit.
	 * 
	 * @param newFiles
	 *            list of visited items
	 */
	private void removeUnvisitedItems(final Map newFiles) {
		Set keySet = cache.keySet();
		Iterator keyIterator = keySet.iterator();

		while (keyIterator.hasNext()) {
			String elementName = (String) keyIterator.next();
			if (!newFiles.containsKey(elementName)) {
				eventHandler.fireElementRemoved(this, elementName);
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
	 * 
	 * @param template
	 *            the type of template to be used by default when retrieving
	 *            objects from the repository
	 */
	public void setDefaultTemplate(String template) {
		defaultTemplate = template;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("PreloadCachingStrategy, ");
		buffer.append(cache.size());
		buffer.append(" cached items. ");
		if (retriever != null) {
			buffer.append(retriever.toString());
		}
		return buffer.toString();
	}
}
