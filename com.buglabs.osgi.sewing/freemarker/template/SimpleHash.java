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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * A simple implementation of the {@link TemplateHashModel} and
 * {@link TemplateModelRoot} interfaces, using an underlying <code>Map</code>.
 * </p>
 * 
 * <p>
 * All the public methods in this implementation are synchronized.
 * </p>
 * 
 * @version $Id: SimpleHash.java 1056 2004-10-28 02:16:01Z run2000 $
 * @see SimpleList
 * @see SimpleScalar
 * @see SimpleNumber
 */
public class SimpleHash implements TemplateModelRoot, TemplateObjectModel, Serializable {

	/**
	 * The contents of this <code>SimpleHash</code> are stored in this
	 * <code>Map</code> object.
	 * 
	 * @serial the <code>Map</code> that this <code>SimpleHash</code> wraps.
	 */
	protected Map hash;

	/** Serialization id, for compatibility with 1.7 */
	private static final long serialVersionUID = -4449005381456582151L;

	/**
	 * Constructs an empty <code>SimpleHash</code>.
	 */
	public SimpleHash() {
		hash = new HashMap();
	}

	/**
	 * Constructs a <code>SimpleHash</code> given a <code>Map</code> of values.
	 * A defensive copy of the map is taken.
	 * 
	 * @param hash
	 *            The <code>Map</code> of values to be copied into this
	 *            <code>SimpleHash</code>.
	 * @throws NullPointerException
	 *             the map value is null
	 */
	public SimpleHash(Map hash) {
		this.hash = new HashMap(hash);
	}

	/**
	 * Is the underlying <code>Map</code> empty?
	 * 
	 * @return <code>true</code> if the <code>Map</code> is empty, otherwise
	 *         <code>false</code>
	 */
	public synchronized boolean isEmpty() throws TemplateModelException {
		return hash.isEmpty();
	}

	/**
	 * Puts a {@link TemplateModel} in the hash.
	 * 
	 * @param key
	 *            the name by which the <code>TemplateModel</code> is identified
	 *            in the template.
	 * @param model
	 *            the <code>TemplateModel</code> to store.
	 */
	public synchronized void put(String key, TemplateModel model) {
		hash.put(key, model);
	}

	/**
	 * Puts a string in the hash, by first wrapping the string in a
	 * {@link FastScalar}.
	 * 
	 * @param key
	 *            the name by which the resulting <code>TemplateModel</code> is
	 *            identified in the template.
	 * @param value
	 *            the string to store.
	 */
	public synchronized void put(String key, String value) {
		hash.put(key, new FastScalar(value));
	}

	/**
	 * Puts a number in the hash, by first wrapping the string in a
	 * {@link FastNumber}.
	 * 
	 * @param key
	 *            the name by which the resulting <code>TemplateModel</code> is
	 *            identified in the template.
	 * @param value
	 *            the number to store.
	 */
	public synchronized void put(String key, Number value) {
		hash.put(key, new FastNumber(value));
	}

	/**
	 * Puts a number in the hash, by first wrapping the string in a
	 * {@link FastNumber}.
	 * 
	 * @param key
	 *            the name by which the resulting <code>TemplateModel</code> is
	 *            identified in the template.
	 * @param value
	 *            the number to store.
	 */
	public synchronized void put(String key, long value) {
		hash.put(key, new FastNumber(value));
	}

	/**
	 * Puts a boolean in the hash, by first wrapping the boolean in a
	 * {@link FastBoolean}.
	 * 
	 * @param key
	 *            the name by which the resulting <code>TemplateModel</code> is
	 *            identified in the template.
	 * @param value
	 *            the boolean to store.
	 */
	public synchronized void put(String key, boolean value) {
		hash.put(key, FastBoolean.getInstance(value));
	}

	/**
	 * Gets a {@link TemplateModel} from the underlying hash.
	 * 
	 * @param key
	 *            the name by which the <code>TemplateModel</code> is identified
	 *            in the template.
	 * @return the <code>TemplateModel</code> referred to by the key, or
	 *         <code>null</code> if not found.
	 */
	public synchronized TemplateModel get(String key) throws TemplateModelException {
		return (TemplateModel) hash.get(key);
	}

	/**
	 * Removes the given key from the underlying <code>Map</code>.
	 * 
	 * @param key
	 *            the key to be removed
	 */
	public synchronized void remove(String key) {
		hash.remove(key);
	}

	/**
	 * Return an unmodifiable copy of the underlying <code>Map</code> object for
	 * manipulation by the <code>freemarker.ext.beans</code> package.
	 * 
	 * @return an unmodifiable view of the underlying <code>Map</code> object
	 */
	public synchronized Object getAsObject() throws TemplateModelException {
		return Collections.unmodifiableMap(hash);
	}

	/**
	 * Convenience method for returning the <code>String</code> value of the
	 * underlying hash.
	 */
	public String toString() {
		return hash.toString();
	}

	/**
	 * Tests this object for equality with the given object.
	 * 
	 * @param o
	 *            the object to be compared against
	 * @return <code>true</code> if the objects are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SimpleHash)) {
			return false;
		}

		final SimpleHash simpleHash = (SimpleHash) o;

		return hash.equals(simpleHash.hash);
	}

	/**
	 * Return the hash value for this object.
	 * 
	 * @return a hash code corresponding to the value of the object
	 */
	public int hashCode() {
		return hash.hashCode() + 17;
	}
}
