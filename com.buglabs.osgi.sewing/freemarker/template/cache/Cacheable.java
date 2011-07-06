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
 * <p>
 * Abstracts information relating to the caching of a particular object. Any
 * object that needs to be held in a {@link Cache} needs to implement this
 * interface.
 * </p>
 * 
 * @see Cache
 * @version $Id: Cacheable.java 987 2004-10-05 10:13:24Z run2000 $
 */
public interface Cacheable extends Cloneable {

	/**
	 * Sets the {@link Cache} that this object is stored in.
	 * 
	 * @param cache
	 *            the <code>Cache</code> that this template belongs to.
	 */
	public void setCache(Cache cache);

	/**
	 * Retrieve the {@link Cache} that this object is stored in.
	 * 
	 * @return the <code>Cache</code> that this template belongs to.
	 */
	public Cache getCache();

	/**
	 * Retrieve a clone of the current object.
	 */
	public Object clone();
}
