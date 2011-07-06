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

import freemarker.template.CacheListener;
import freemarker.template.TemplateException;

/**
 * Base class for implementing common elements of the caching strategies
 * included with FM-Classic.
 * 
 * @author Nicholas Cull
 * @version $Id: BaseCachingStrategy.java 987 2004-10-05 10:13:24Z run2000 $
 */
public abstract class BaseCachingStrategy implements CachingStrategy {
	/** A proxy object for firing cache events to any listening event handlers. */
	protected CacheEventAdapter eventHandler;
	/**
	 * An object from which a caching strategy can retrieve templates. Typically
	 * proxies for a file system.
	 */
	protected CacheRetriever retriever;

	/** Default constructor. */
	public BaseCachingStrategy() {
	}

	/**
	 * Creates new <code>BaseCachingStrategy</code> with a cache retriever.
	 * 
	 * @param retriever
	 *            the retriever to use to pull items from the data store
	 */
	public BaseCachingStrategy(CacheRetriever retriever) {
		this.retriever = retriever;
	}

	/**
	 * Sets up a retriever to retrieve cacheable objects.
	 * 
	 * @param retriever
	 *            the new retriever for the cache
	 */
	public void setCacheRetriever(CacheRetriever retriever) {
		this.retriever = retriever;
	}

	/**
	 * Returns the current retriever.
	 * 
	 * @return the current cache retriever
	 */
	public CacheRetriever getCacheRetriever() {
		return retriever;
	}

	/**
	 * Registers a {@link freemarker.template.CacheListener} for this
	 * {@link Cache}.
	 * 
	 * @param listener
	 *            the <code>CacheListener</code> to be registered.
	 */
	public void addCacheListener(CacheListener listener) {
		eventHandler.addCacheListener(listener);
	}

	/**
	 * Returns all the {@link freemarker.template.CacheListener}s registered
	 * with this strategy.
	 * 
	 * @return an array of <code>CacheListener</code>s that have been
	 *         registered.
	 */
	public CacheListener[] getCacheListeners() {
		return eventHandler.getCacheListeners();
	}

	/**
	 * Unregisters a {@link freemarker.template.CacheListener} for this
	 * {@link Cache}.
	 * 
	 * @param listener
	 *            the <code>CacheListener</code> to be unregistered.
	 */
	public void removeCacheListener(CacheListener listener) {
		eventHandler.removeCacheListener(listener);
	}

	/**
	 * Determines whether our data source is still available. If not, fires a
	 * cache event and stops auto-updates, since this is a fatal condition for
	 * the cache to encounter.
	 * 
	 * @return <code>true</code> if the data source is ok, otherwise
	 *         <code>false</code>
	 */
	protected boolean connectionOk() {
		try {
			return retriever.connectionOk();
		} catch (TemplateException e) {
			eventHandler.fireCacheUnavailable(this, e);
			stopAutoUpdate();
			return false;
		}
	}

	/**
	 * Sets the object to be used for firing cache events.
	 * 
	 * @param cHandler
	 *            the event handler to use for firing events
	 */
	public void setEventHandler(CacheEventAdapter cHandler) {
		eventHandler = cHandler;
	}
}
