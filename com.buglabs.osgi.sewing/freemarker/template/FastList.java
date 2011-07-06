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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * An unsynchronized variation of the {@link SimpleList} class. This allows
 * <code>FastList</code> to be significantly faster than {@link SimpleList} in
 * some cases.
 * </p>
 * 
 * <p>
 * Note that every template process is performed synchronously, so within a
 * single-threaded application instances of this class will always be safe.
 * </p>
 * 
 * <p>
 * Note also that this class was retrofitted with the {@link TemplateListModel2}
 * interface as of 1.8.
 * </p>
 * 
 * @version $Id: FastList.java 1098 2005-09-06 14:01:53Z run2000 $
 * @see FastBoolean
 * @see FastHash
 * @see FastNumber
 * @see FastScalar
 * @since 1.7.5
 */
public final class FastList implements TemplateListModel2, TemplateIndexedModel, TemplateWriteableIndexedModel, TemplateObjectModel, Serializable {

	/**
	 * The <code>List</code> that this <code>FastList</code> wraps.
	 */
	private final List list;

	/** Serialization id, for future compatibility */
	private static final long serialVersionUID = -6793356950285945537L;

	/**
	 * Constructs an empty <code>FastList</code>.
	 */
	public FastList() {
		list = new ArrayList();
	}

	/**
	 * Constructs a <code>FastList</code> from the given <code>List</code>.
	 * 
	 * @throws NullPointerException
	 *             the list value is null
	 */
	public FastList(List list) {
		if (list == null) {
			throw new NullPointerException("FastList list value cannot be null");
		}
		this.list = list;
	}

	/**
	 * Is the underlying <code>List</code> empty?
	 * 
	 * @return <code>true</code> if the list is empty, otherwise
	 *         <code>false</code>
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Retrieves an iterator to iterate over this list.
	 * 
	 * @return an iterator to iterate over the current list.
	 * @throws TemplateModelException
	 *             the next item in the list can't be retrieved, or no next item
	 *             exists.
	 */
	public TemplateIteratorModel templateIterator() throws TemplateModelException {
		return new FastListIterator(list);
	}

	/**
	 * Returns the used iterator to the list model.
	 * 
	 * @param iterator
	 *            the iterator to be returned to the object pool, if any
	 */
	public void releaseIterator(TemplateIteratorModel iterator) {
		// Do nothing
	}

	/**
	 * Retrieves the specified item from the list.
	 * 
	 * @param index
	 *            the index of the item to be retrieved.
	 * @return the specified index in the list.
	 * @throws TemplateModelException
	 *             the specified item in the list can't be retrieved, or the
	 *             index is out of bounds.
	 */
	public TemplateModel getAtIndex(long index) throws TemplateModelException {
		try {
			return (TemplateModel) list.get((int) index);
		} catch (IndexOutOfBoundsException e) {
			throw new TemplateModelException("Index out of bounds", e);
		} catch (ClassCastException e) {
			throw new TemplateModelException("Element is not a TemplateModel", e);
		}
	}

	/**
	 * Set the value corresponding to the given index. Traditionally this would
	 * correspond to an index into an array, or similar structure, such as a
	 * <code>java.util.Vector</code>.
	 * 
	 * @param index
	 *            the index of the underlying value we're interested in
	 * @param model
	 *            the model to be added to the list
	 * @throws TemplateModelException
	 *             the value could not be determined, possibly due to an index
	 *             out-of-bounds, or an otherwise undefined value
	 */
	public void putAtIndex(long index, TemplateModel model) throws TemplateModelException {

		try {
			list.set((int) index, model);
		} catch (IndexOutOfBoundsException e) {
			throw new TemplateModelException("Could not assign model to index: " + e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Return an unmodifiable view of the underlying <code>List</code> object
	 * for manipulation by the <code>freemarker.ext.beans</code> package.
	 * </p>
	 * 
	 * @return an unmodifiable view of the underlying <code>List</code> object
	 */
	public Object getAsObject() {
		return Collections.unmodifiableList(list);
	}

	/**
	 * Returns a <code>String</code> representation of the object.
	 */
	public String toString() {
		return list.toString();
	}

	/**
	 * Tests this object for equality with the given object.
	 * 
	 * @param o
	 *            the object to be compared with
	 * @return <code>true</code> if the objects are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof FastList)) {
			return false;
		}

		final FastList fastList = (FastList) o;

		return list.equals(fastList.list);
	}

	/**
	 * Return the hash value for this object.
	 * 
	 * @return a hash code corresponding to the value of this object
	 */
	public int hashCode() {
		return list.hashCode() + 23;
	}
}
