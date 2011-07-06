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
import java.util.Set;

import freemarker.template.TemplateException;

/**
 * Implements a load-on-demand caching strategy.
 * 
 * @author Nicholas Cull
 * @version $Id: LoadOnDemandCachingStrategy.java 995 2004-10-15 04:05:41Z
 *          run2000 $
 */
public final class LoadOnDemandCachingStrategy extends BaseCachingStrategy {
	private HashMap cache = new HashMap();
	private UpdateTimer timer;
	private long delay = 5000; // five seconds
	private int maximumAge = 0; // Default to no expiry
	private String defaultTemplate;

	/** Creates new LoadOnDemandCachingStrategy. */
	public LoadOnDemandCachingStrategy() {
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
	public synchronized void setMaximumAge(int age) {
		maximumAge = age;
	}

	/**
	 * Retrieves the maximum age a cache item can be before it is evicted from
	 * the cache. The age is determined as the number of cache updates since the
	 * item was last accessed. This is meaningful only if the cache policy is a
	 * load-on-demand type.
	 * 
	 * @return the maximum age before an item is evicted from the cache
	 */
	public synchronized int getMaximumAge() {
		return maximumAge;
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
	 * Retrieves an item from the cache, according to the loading policy
	 * implemented.
	 * 
	 * @param name
	 *            the name of the item to retrieve
	 * @param type
	 *            the type of item to be retrieved
	 * @return the corresponding <code>Cacheable</code> object, or
	 *         <code>null</code> if not found or an error has occurred
	 */
	public Cacheable getItem(String name, String type) {
		CacheElement element;
		Cacheable item;
		long lastModified;

		if (!connectionOk()) {
			return null;
		}

		element = (CacheElement) cache.get(name);

		try {
			lastModified = retriever.lastModified(name);

			if (element == null) {
				HashMap newCache = (HashMap) cache.clone();
				item = retriever.loadData(name, type);
				if (item != null) {
					item.setCache(this);
				}
				newCache.put(name, new CacheElement(name, type, item, lastModified));
				eventHandler.fireElementUpdated(this, name, lastModified);
				synchronized (this) {
					cache = newCache;
				}
			} else if (lastModified > element.lastModified()) {
				item = retriever.loadData(name, type);
				if (item != null) {
					item.setCache(this);
				}
				synchronized (this) {
					cache.put(name, new CacheElement(name, type, item, lastModified));
				}
				eventHandler.fireElementUpdated(this, name, lastModified);
			} else if ((type != null) && (!type.equals(element.getType()))) {
				item = retriever.loadData(name, type);
				if (item != null) {
					item.setCache(this);
				}
				synchronized (this) {
					cache.put(name, new CacheElement(name, type, item, lastModified));
				}
				eventHandler.fireElementUpdated(this, name, lastModified);
			} else {
				// Return the item we already have.
				item = element.getObject();
			}
		} catch (TemplateException e) {
			eventHandler.fireElementUpdateFailed(this, name, e);
			return null;
		}
		return item;
	}

	/**
	 * Retrieves a list of objects currently in the cache.
	 * 
	 * @return an iterator that can recurse over the cached objects. May return
	 *         <code>null</code> if there are no items in the cache, or the
	 *         strategy does not implement a cache.
	 */
	public Iterator listCachedFiles() {
		return Collections.unmodifiableCollection(cache.values()).iterator();
	}

	/**
	 * Asks for a "blank" update. It is up to the implementation to determine
	 * what has to be updated.
	 */
	public void update() throws InterruptedException {
		if (connectionOk()) {
			removeDeletedItems();
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
	 * Stops automatically updating the cache.
	 */
	public synchronized void stopAutoUpdate() {
		if (timer != null) {
			timer.stopTiming();
			timer = null;
		}
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
			// Propagate the interrupted flag to the caller
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Removes from the cache objects that correspond to deleted items.
	 */
	private void removeDeletedItems() throws InterruptedException {
		HashMap newCache = (HashMap) cache.clone();
		Set keySet = newCache.keySet();
		Iterator keyIterator = keySet.iterator();

		// Iterate through existing items, and check whether they're
		// still in the main data store
		while (keyIterator.hasNext()) {
			String name = (String) keyIterator.next();
			CacheElement element = (CacheElement) newCache.get(name);
			element.age();

			if ((!retriever.exists(name)) || (element.isExpired(maximumAge))) {
				keyIterator.remove();
				eventHandler.fireElementRemoved(this, name);
			}

			// Check whether the thread has been interrupted in the mean time.
			// If so, exit the loop immediately.
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
		}

		// Finally, update the cache with the new contents
		synchronized (this) {
			cache = newCache;
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
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("LoadOnDemandCachingStrategy, ");
		buffer.append(cache.size());
		buffer.append(" cached items. ");
		if (retriever != null) {
			buffer.append(retriever.toString());
		}
		return buffer.toString();
	}
}
