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

import freemarker.template.CacheListener;

/**
 * An interface for self-updating caches consisting of any single type of
 * object. The class of objects to be held in the cache is not specified by this
 * interface.
 * 
 * @see freemarker.template.cache.Updateable
 * @version $Id: Cache.java 987 2004-10-05 10:13:24Z run2000 $
 */
public interface Cache {
	/**
	 * Registers a {@link freemarker.template.CacheListener} for this
	 * <code>Cache</code>.
	 * 
	 * @param listener
	 *            the <code>CacheListener</code> to be registered.
	 */
	public void addCacheListener(CacheListener listener);

	/**
	 * Unregisters a {@link freemarker.template.CacheListener} for this
	 * <code>Cache</code>.
	 * 
	 * @param listener
	 *            the <code>CacheListener</code> to be unregistered.
	 */
	public void removeCacheListener(CacheListener listener);

	/**
	 * Retrieves all the listeners associated with this <code>Cache</code>.
	 * 
	 * @return an array of <code>CacheListener</code>s
	 */
	public CacheListener[] getCacheListeners();

	/**
	 * Stops automatically updating the cache. Normally only call this prior to
	 * cache shutdown.
	 */
	public void stopAutoUpdate();

	/**
	 * Returns an iterator over a list of {@link CacheElement} instances.
	 * 
	 * @return the iterator over a list of <code>CacheElement</code> instances
	 *         that correspond to templates in the cache
	 */
	public Iterator listCachedFiles();

	/**
	 * Gets an item from the cache. Assumes a default type of object will be
	 * returned.
	 * 
	 * @param name
	 *            a string uniquely identifying the item.
	 * @return the item corresponding to the name, or <code>null</code> if not
	 *         found.
	 */
	public Cacheable getItem(String name);

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
	public Cacheable getItem(String name, String type);
}
