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
 * Represent a single entry in the cache.
 * 
 * @version $Id: CacheElement.java 987 2004-10-05 10:13:24Z run2000 $
 */
public final class CacheElement implements Comparable, Cloneable {
	private final Cacheable object;
	private final long lastModified;
	private final String name;
	private final String type;
	private int age;

	/**
	 * Create a new <code>CacheElement</code>.
	 * 
	 * @param name
	 *            the canonical name of the element
	 * @param type
	 *            the type of object
	 * @param object
	 *            the object to be stored
	 * @param lastModified
	 *            the time the object was last modified in the data store
	 */
	public CacheElement(String name, String type, Cacheable object, long lastModified) {
		this.name = name;
		this.type = type;
		this.object = object;
		this.lastModified = lastModified;
		this.age = 0;
	}

	/**
	 * Create a new shallow clone of a given <code>CacheElement</code>.
	 * 
	 * @param ce
	 *            the cache element to be cloned
	 */
	public CacheElement(CacheElement ce) {
		this.object = ce.object;
		this.lastModified = ce.lastModified;
		this.name = ce.name;
		this.type = ce.type;
		this.age = ce.age;
	}

	/**
	 * Retrieve the name of the element being cached.
	 * 
	 * @return the name of the cache element
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieve the time the element was last modified, according to the cache.
	 * 
	 * @return the last modified time of the element in the cache
	 */
	public long lastModified() {
		return lastModified;
	}

	/**
	 * Retrieve the object being cached.
	 * 
	 * @return the cached object
	 */
	public Cacheable getObject() {
		age = 0;
		return object;
	}

	/**
	 * Retrieve the type of object being cached.
	 * 
	 * @return the type of the object being cached
	 */
	public String getType() {
		return type;
	}

	/**
	 * Age this element in the cache.
	 */
	public void age() {
		age++;
	}

	/**
	 * Has the object in this cache element expired?
	 * 
	 * @param age
	 *            the age at which this element would expire
	 * @return <code>true</code> if the element has expired, otherwise
	 *         <code>false</code>
	 */
	public boolean isExpired(int age) {
		return (age > 0) && (this.age >= age);
	}

	/**
	 * Compares an object against this <code>CacheElement</code>.
	 * 
	 * @param obj
	 *            the object to be compared
	 * @return whether the object is less than, equal to, or greater than the
	 *         current object
	 * @throws ClassCastException
	 *             the object cannot be compared
	 */
	public int compareTo(Object obj) {
		return compareTo((CacheElement) obj);
	}

	/**
	 * Compares another <code>CacheElement</code> against this one.
	 * 
	 * @param target
	 *            the <code>CacheElement</code> to be compared
	 * @return whether the object is less than, equal to, or greather than the
	 *         current object
	 */
	public int compareTo(CacheElement target) {
		int nResult;

		// Compare not only the name, but the type as well
		nResult = name.compareTo(target.name);
		if (nResult == 0) {
			nResult = type.compareTo(target.type);
		}
		return nResult;
	}

	/**
	 * Performs a <em>shallow</em> clone of the <code>CacheElement</code>. The
	 * object being cached is not cloned, only a reference to it.
	 * 
	 * @return a clone of the current <code>CacheElement</code>
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Cached copy of ");
		buffer.append(name);
		return buffer.toString();
	}
}
