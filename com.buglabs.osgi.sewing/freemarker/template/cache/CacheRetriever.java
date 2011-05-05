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

import java.util.List;

import freemarker.template.TemplateException;

/**
 * Interface for retrieving objects to be stored in a <code>Cache</code>. This
 * interface may be implemented in order to retrieve templates from a different
 * data store, such as a relational database.
 * 
 * @version $Id: CacheRetriever.java 987 2004-10-05 10:13:24Z run2000 $
 */
public interface CacheRetriever {

	/**
	 * Retrieves data of the appropriate type to be stored in the cache.
	 * 
	 * @param location
	 *            the location of the data to be retrieved
	 * @param type
	 *            the type of object to be returned
	 * @return a <code>Cacheable</code> object loaded from the data source
	 * @throws TemplateException
	 *             the object could not be loaded
	 */
	public Cacheable loadData(String location, String type) throws TemplateException;

	/**
	 * Tests whether the object still exists in the template repository. This
	 * may be redundant. Instead, lastModified could throw an appropriate
	 * exception.
	 * 
	 * @param location
	 *            the location of the object to be tested
	 * @return <code>true</code> if the object still exists in the repository,
	 *         otherwise <code>false</code>
	 * @see #lastModified
	 */
	public boolean exists(String location);

	/**
	 * <p>
	 * Determines when the object in the template repository was last modified.
	 * </p>
	 * 
	 * @param location
	 *            the location of the object to be tested
	 * @return milliseconds since 1970 of the time the item was last modified
	 * @throws TemplateException
	 *             is thrown whenever the item:
	 *             <ul>
	 *             <li>does not exist</li>
	 *             <li>is the wrong type (eg. a directory, not a file)</li>
	 *             </ul>
	 */
	public long lastModified(String location) throws TemplateException;

	/**
	 * Returns a list of objects (<code>String</code>s) to pre-load the cache
	 * with.
	 * 
	 * @return a <code>List</code> of <code>String</code>s to preload the cache
	 *         with
	 */
	public List getPreloadData() throws TemplateException;

	/**
	 * Corresponds to checkCacheDir for file-system implementations.
	 * 
	 * @return <code>true</code> if the connection is ok, otherwise
	 *         <code>false</code>
	 * @throws TemplateException
	 *             whenever the connection has failed, and cannot be
	 *             re-established
	 */
	public boolean connectionOk() throws TemplateException;

	/**
	 * Sets the connection for this retriever. Corresponds to setPath for
	 * file-system implementations.
	 * 
	 * @param connection
	 *            the connection string for this retriever
	 */
	public void setConnection(String connection);

	/**
	 * Gets the connection for this retriever. Corresponds to getPath for
	 * file-system implementations.
	 * 
	 * @return the connection string used to connect to this retriever
	 */
	public String getConnection();
}
