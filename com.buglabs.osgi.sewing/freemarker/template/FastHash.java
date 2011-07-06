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
 * An unsynchronized variation of the {@link SimpleHash} class to handle
 * key-pair values. This means that <code>FastHash</code> should be
 * significantly faster than {@link SimpleHash} in some cases.
 * </p>
 * 
 * <p>
 * Instances of this class should be used in situations where either:
 * </p>
 * <ul>
 * <li>The underlying values never change, such as when the object is not used
 * as a model root or when the backing <code>Map</code> never changes, <b>or</b>
 * </li>
 * <li>The model is used in a single-threaded situation</li>
 * </ul>
 * <p>
 * Note that every template process is performed synchronously, so within a
 * single-threaded application instances of this class will always be safe.
 * </p>
 * 
 * @version $Id: FastHash.java 987 2004-10-05 10:13:24Z run2000 $
 * @see FastBoolean
 * @see FastList
 * @see FastNumber
 * @see FastScalar
 * @since 1.7.5
 */
public final class FastHash implements TemplateModelRoot, TemplateObjectModel, Serializable {
	/**
	 * The <code>Map</code> that this <code>FastHash</code> wraps.
	 */
	private final Map hash;

	/** Serialization id, for future compatibility */
	private static final long serialVersionUID = -472268658486232109L;

	/** Default constructor. */
	public FastHash() {
		hash = new HashMap();
	}

	/**
	 * Constructs an <code>FastHash</code> given the backing <code>Map</code>.
	 * 
	 * @param hash
	 *            The Map to use as the backing for this FastHash.
	 * @throws NullPointerException
	 *             the map is null
	 */
	public FastHash(Map hash) {
		if (hash == null) {
			throw new NullPointerException("FastHash hash map cannot be null");
		}
		this.hash = hash;
	}

	/**
	 * Is the model empty?
	 * 
	 * @return <code>true</code> if this object is empty, otherwise
	 *         <code>false</code>
	 */
	public boolean isEmpty() {
		return hash.isEmpty();
	}

	/**
	 * Gets a {@link TemplateModel} from the hash.
	 * 
	 * @param key
	 *            the name by which the <code>TemplateModel</code> is identified
	 *            in the template.
	 * @return the <code>TemplateModel</code> referred to by the key, or
	 *         <code>null</code> if not found.
	 */
	public TemplateModel get(String key) {
		return (TemplateModel) hash.get(key);
	}

	/**
	 * Sets a value in the hash model.
	 * 
	 * @param key
	 *            the hash key.
	 * @param model
	 *            the hash value.
	 */
	public void put(String key, TemplateModel model) {
		hash.put(key, model);
	}

	/**
	 * Removes a key from the hash model.
	 * 
	 * @param key
	 *            the key to be removed.
	 */
	public void remove(String key) {
		hash.remove(key);
	}

	/**
	 * <p>
	 * Return an unmodifiable view of the underlying <code>Map</code> object for
	 * manipulation by the <code>freemarker.ext.beans</code> package.
	 * </p>
	 * 
	 * @return an unmodifiable view of the underlying <code>Map</code> object
	 */
	public Object getAsObject() throws TemplateModelException {
		return Collections.unmodifiableMap(hash);
	}

	/**
	 * Returns a <code>String</code> representation of the object.
	 */
	public String toString() {
		return hash.toString();
	}

	/**
	 * Tests this object for equality with the given object.
	 * 
	 * @param o
	 *            the object to be compared with
	 * @return <code>true</code> if the object is equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof FastHash)) {
			return false;
		}

		final FastHash fastHash = (FastHash) o;

		return hash.equals(fastHash.hash);
	}

	/**
	 * Return the hash value for this object.
	 * 
	 * @return a hash code corresponding to the value of this object
	 */
	public int hashCode() {
		return hash.hashCode() + 23;
	}
}
