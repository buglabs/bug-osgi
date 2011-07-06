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

import java.util.EventListener;

/**
 * An interface for classes that listen for <code>CacheEvent</code>s.
 * 
 * @version $Id: CacheListener.java 987 2004-10-05 10:13:24Z run2000 $
 * @see CacheEvent
 * @see FileTemplateCache
 */
public interface CacheListener extends EventListener {

	/**
	 * Called when a cache cannot be updated. The event fired contains the
	 * <code>Exception</code> object.
	 * 
	 * @param event
	 *            the {@link CacheEvent} fired.
	 */
	public void cacheUnavailable(CacheEvent event);

	/**
	 * Called when a cache element updates successfully.
	 * 
	 * @param event
	 *            the {@link CacheEvent} fired.
	 */
	public void elementUpdated(CacheEvent event);

	/**
	 * Called when an element is removed from the cache.
	 * 
	 * @param event
	 *            the {@link CacheEvent} fired.
	 */
	public void elementRemoved(CacheEvent event);

	/**
	 * Called when a cache element fails to update. The event fired contains the
	 * <code>Exception</code> object.
	 * 
	 * @param event
	 *            the {@link CacheEvent} fired.
	 */
	public void elementUpdateFailed(CacheEvent event);
}
