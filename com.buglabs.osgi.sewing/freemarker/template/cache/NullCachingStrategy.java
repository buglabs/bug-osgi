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

import java.util.Iterator;

import freemarker.template.TemplateException;

/**
 * Implements the most simple caching strategy possible: a null cache. That is,
 * no caching takes place at all, the strategy simply forwards all requests onto
 * the cache retriever.
 * 
 * @author Nicholas Cull
 * @version $Id: NullCachingStrategy.java 987 2004-10-05 10:13:24Z run2000 $
 */
public final class NullCachingStrategy extends BaseCachingStrategy {
	private String defaultTemplate;

	/** Creates new NullCachingStrategy. */
	public NullCachingStrategy() {
	}

	/**
	 * Creates new NullCachingStrategy with a cache retriever.
	 * 
	 * @param retriever
	 *            the retriever to use to pull items from the data store
	 */
	public NullCachingStrategy(CacheRetriever retriever) {
		this.retriever = retriever;
	}

	/**
	 * Sets the interval between two cache updates. This is meaningful only if
	 * the cache policy is a load-on-demand or preload type.
	 * 
	 * @param delay
	 *            the number of seconds between cache updates
	 */
	public void setDelay(long delay) {
		// Do nothing here
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
		// Do nothing here
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
		Cacheable item;

		try {
			item = retriever.loadData(name, defaultTemplate);
			item.setCache(this);
			return item;
		} catch (TemplateException e) {
			eventHandler.fireElementUpdateFailed(this, name, e);
			return null;
		}
	}

	/**
	 * Retrieves an item from the cache, according to the loading policy
	 * implemented.
	 * 
	 * @param name
	 *            the name of the item to retrieve
	 * @param type
	 *            the type of the item to retrieve
	 * @return the corresponding <code>Cacheable</code> object, or
	 *         <code>null</code> if not found or an error has occurred
	 */
	public Cacheable getItem(String name, String type) {
		Cacheable item;

		try {
			item = retriever.loadData(name, type);
			item.setCache(this);
			return item;
		} catch (TemplateException e) {
			eventHandler.fireElementUpdateFailed(this, name, e);
			return null;
		}
	}

	/**
	 * Retrieves a list of objects currently in the cache.
	 * 
	 * @return <code>null</code>, indicating that no caching is used
	 */
	public Iterator listCachedFiles() {
		return null;
	}

	/**
	 * Asks for a "blank" update. It is up to the implementation to determine
	 * what has to be updated.
	 */
	public void update() {
		// Do nothing
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
	 * Begins automatic updates of the cache.
	 */
	public void startAutoUpdate() {
		// Do nothing
	}

	/**
	 * Stops automatically updating the cache.
	 */
	public void stopAutoUpdate() {
		// Do nothing
	}

	/**
	 * Clears all the elements in the cache.
	 */
	public void clearCache() {
		// Do nothing -- nothing is cached
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
		buffer.append("NullCachingStrategy, no cached items. ");
		if (retriever != null) {
			buffer.append(retriever.toString());
		}
		return buffer.toString();
	}
}
