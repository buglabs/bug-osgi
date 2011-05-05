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

/**
 * Abstract interface for a cache loading strategy. Items may be loaded and
 * cached based on a variety of algorithms. This interface abstracts out the
 * loading policy from the rest of the caching machinery.
 * 
 * @author Nicholas Cull
 * @version $Id: CachingStrategy.java 987 2004-10-05 10:13:24Z run2000 $
 * @see Updateable
 */
public interface CachingStrategy extends Updateable, Cache {

	/**
	 * Sets the {@link CacheRetriever} for this caching strategy.
	 * 
	 * @param retriever
	 *            the <code>CacheRetriever</code> to be used
	 */
	public void setCacheRetriever(CacheRetriever retriever);

	/**
	 * Retrieve the {@link CacheRetriever} currently being used.
	 * 
	 * @return the current <code>CacheRetriever</code>
	 */
	public CacheRetriever getCacheRetriever();

	/**
	 * Sets the interval between two cache updates. This is meaningful only if
	 * the cache policy is a load-on-demand or preload type.
	 * 
	 * @param delay
	 *            the number of seconds between cache updates
	 */
	public void setDelay(long delay);

	/**
	 * Returns the interval between two cache updates. This is meaningful only
	 * if the cache policy is a load-on-demand or preload type.
	 * 
	 * @return the number of seconds between cache updates
	 */
	public long getDelay();

	/**
	 * Sets the maximum age a cache item can be before it is evicted from the
	 * cache. The age is determined as the number of cache updates since the
	 * item was last accessed. This is meaningful only if the cache policy is a
	 * load-on-demand type.
	 * 
	 * @param age
	 *            the maximum age before an item is evicted from the cache
	 */
	public void setMaximumAge(int age);

	/**
	 * Retrieves the maximum age a cache item can be before it is evicted from
	 * the cache. The age is determined as the number of cache updates since the
	 * item was last accessed. This is meaningful only if the cache policy is a
	 * load-on-demand type.
	 * 
	 * @return the maximum age before an item is evicted from the cache
	 */
	public int getMaximumAge();

	/**
	 * Clears all the elements in the cache.
	 */
	public void clearCache();

	/**
	 * Sets the object to be used for firing cache events.
	 * 
	 * @param handler
	 *            the event handler to use for firing events
	 */
	public void setEventHandler(CacheEventAdapter handler);

	/**
	 * Sets the default template to use when retrieving.
	 * 
	 * @param template
	 *            the type of template to be used by default when retrieving
	 *            objects from the repository
	 */
	public void setDefaultTemplate(String template);

	/**
	 * Begins automatic updates of the cache.
	 */
	public void startAutoUpdate();

}
