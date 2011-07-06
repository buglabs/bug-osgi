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
 * A simple implementation of the {@link TemplateListModel2} interface, using an
 * underlying <code>List</code> implementation.
 * </p>
 * 
 * <p>
 * A <code>SimpleList</code> can act as a cache for another
 * <code>TemplateListModel</code>, e.g. one that gets data from a database. When
 * passed another {@link TemplateListModel} as an argument to its constructor or
 * to its {@link #copy(TemplateListModel)} method, the <code>SimpleList</code>
 * immediately copies all the elements and discards the
 * <code>TemplateListModel</code>.
 * </p>
 * 
 * <p>
 * All the public methods in this implementation are synchronized.
 * </p>
 * 
 * <p>
 * Note that as of 1.8, this model has been retrofitted to the
 * {@link TemplateListModel2} interface.
 * </p>
 * 
 * @version $Id: SimpleList.java 1080 2005-08-28 10:18:09Z run2000 $
 * @see SimpleHash
 * @see SimpleScalar
 * @see SimpleNumber
 */
public class SimpleList implements TemplateListModel2, TemplateIndexedModel, TemplateObjectModel, Serializable {

	/**
	 * The contents of this <code>SimpleList</code> are stored in this
	 * <code>List</code> object.
	 * 
	 * @serial The <code>List</code> that this <code>SimpleList</code> wraps.
	 */
	protected List list;

	/** Serialization id, for future compatibility. */
	private static final long serialVersionUID = 2831161847625458436L;

	/**
	 * Constructs an empty <code>SimpleList</code>.
	 */
	public SimpleList() {
		list = new ArrayList();
	}

	/**
	 * Constructs a <code>SimpleList</code> from the given <code>List</code>. A
	 * defensive copy of the list is made.
	 * 
	 * @param list
	 *            the list of values to be copied into this
	 *            <code>SimpleList</code>
	 * @throws NullPointerException
	 *             the list value is null
	 */
	public SimpleList(List list) {
		this.list = new ArrayList(list);
	}

	/**
	 * Constructs a <code>SimpleList</code> from the given <code>Array</code> of
	 * {@link TemplateModel}s.
	 * 
	 * @param arr
	 *            the array to be copied into the underlying <code>List</code>.
	 * @throws NullPointerException
	 *             the array to be copied is null
	 */
	public SimpleList(TemplateModel[] arr) {
		copy(arr);
	}

	/**
	 * Constructs a <code>SimpleList</code>, copying into it the values from
	 * another {@link TemplateListModel}.
	 * 
	 * @param listToCopy
	 *            the list to be copied into this one.
	 * @throws TemplateModelException
	 *             something went wrong while copying the given list into the
	 *             <code>SimpleList</code>.
	 * @throws NullPointerException
	 *             the list to be copied is null
	 */
	public SimpleList(TemplateListModel listToCopy) throws TemplateModelException {
		copy(listToCopy);
	}

	/**
	 * Constructs a <code>SimpleList</code>, copying into it the values from
	 * another {@link TemplateListModel2}.
	 * 
	 * @param listToCopy
	 *            the list to be copied into this one.
	 * @throws TemplateModelException
	 *             something went wrong while copying the given list into the
	 *             <code>SimpleList</code>.
	 * @throws NullPointerException
	 *             the list to be copied is null
	 */
	public SimpleList(TemplateListModel2 listToCopy) throws TemplateModelException {
		copy(listToCopy);
	}

	/**
	 * Removes all the elements from this <code>SimpleList</code>.
	 */
	public synchronized void clear() {
		list.clear();
	}

	/**
	 * Discards the contents of this <code>SimpleList</code>, and copies into it
	 * the values from a {@link TemplateListModel} object.
	 * 
	 * @param listToCopy
	 *            the list to be copied into this one.
	 * @throws TemplateModelException
	 *             something went wrong while copying the given list into the
	 *             <code>SimpleList</code>.
	 * @throws NullPointerException
	 *             the list to be copied is null
	 */
	public synchronized void copy(TemplateListModel listToCopy) throws TemplateModelException {
		List newList = new ArrayList();

		if (!listToCopy.isRewound()) {
			listToCopy.rewind();
		}

		while (listToCopy.hasNext()) {
			newList.add(listToCopy.next());
		}

		this.list = newList;
	}

	/**
	 * Discards the contents of this <code>SimpleList</code>, and copies into it
	 * the values from another {@link TemplateListModel2}.
	 * 
	 * @param listToCopy
	 *            the list to be copied into this one.
	 * @throws TemplateModelException
	 *             something went wrong while copying the given list into the
	 *             <code>SimpleList</code>.
	 * @throws NullPointerException
	 *             the list to be copied is null
	 */
	public synchronized void copy(TemplateListModel2 listToCopy) throws TemplateModelException {
		TemplateIteratorModel iterator = listToCopy.templateIterator();
		List newList = new ArrayList();

		while (iterator.hasNext()) {
			newList.add(iterator.next());
		}

		this.list = newList;
	}

	/**
	 * Discards the contents of this <code>SimpleList</code>, and copies into it
	 * the values from the given <code>Array</code> of {@link TemplateModel}s.
	 * 
	 * @param arr
	 *            the array to be copied into the underlying list.
	 * @throws NullPointerException
	 *             the array to be copied is null
	 */
	public synchronized void copy(TemplateModel[] arr) {
		List newList = new ArrayList(arr.length);

		for (int i = 0; i < arr.length; i++) {
			newList.add(arr[i]);
		}
		this.list = newList;
	}

	/**
	 * Is the underlying <code>List</code> empty?
	 * 
	 * @return <code>true</code> if the list is empty, otherwise
	 *         <code>false</code>
	 */
	public synchronized boolean isEmpty() throws TemplateModelException {
		return list.isEmpty();
	}

	/**
	 * Adds a {@link TemplateModel} to the end of this <code>SimpleList</code>.
	 * 
	 * @param element
	 *            the <code>TemplateModel</code> to be added.
	 */
	public synchronized void add(TemplateModel element) {
		list.add(element);
	}

	/**
	 * Adds a string to the end of this <code>SimpleList</code>, by wrapping the
	 * string in a {@link FastScalar}.
	 * 
	 * @param s
	 *            the <code>String</code> to be added.
	 */
	public synchronized void add(String s) {
		add(new FastScalar(s));
	}

	/**
	 * Adds a number to the end of this <code>SimpleList</code>, by wrapping the
	 * string in a {@link FastNumber}.
	 * 
	 * @param n
	 *            the <code>Number</code> to be added.
	 */
	public synchronized void add(Number n) {
		add(new FastNumber(n));
	}

	/**
	 * Adds a number to the end of this <code>SimpleList</code>, by wrapping the
	 * string in a {@link FastNumber}.
	 * 
	 * @param n
	 *            the <code>Number</code> to be added.
	 */
	public synchronized void add(long n) {
		add(new FastNumber(n));
	}

	/**
	 * Adds a boolean to the end of this <code>SimpleList</code>, by wrapping
	 * the boolean in a {@link FastBoolean}.
	 * 
	 * @param bool
	 *            the boolean to be added.
	 */
	public synchronized void add(boolean bool) {
		add(FastBoolean.getInstance(bool));
	}

	/**
	 * Retrieves an iterator to iterate over this list.
	 * 
	 * @return an iterator to iterate over the current list.
	 * @throws TemplateModelException
	 *             the next item in the list can't be retrieved, or no next item
	 *             exists.
	 */
	public synchronized TemplateIteratorModel templateIterator() throws TemplateModelException {
		return new FastIterator(list);
	}

	/**
	 * Returns the used iterator to the list model.
	 * 
	 * @param iterator
	 *            the iterator to be returned to the object pool, if any
	 */
	public void releaseIterator(TemplateIteratorModel iterator) {
		// Do nothing.
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
	public synchronized TemplateModel getAtIndex(long index) throws TemplateModelException {
		if (index >= list.size())
			throw new TemplateModelException("IndexOutOfBounds(" + index + ')');
		return (TemplateModel) list.get((int) index);
	}

	/**
	 * Return an unmodifiable copy of the underlying <code>List</code> object
	 * for manipulation by the <code>freemarker.ext.beans</code> package.
	 * 
	 * @return an unmodifiable view of the underlying <code>List</code> object
	 */
	public synchronized Object getAsObject() throws TemplateModelException {
		return Collections.unmodifiableList(list);
	}

	/**
	 * Retrieve the value of this object as a <code>String</code>.
	 */
	public synchronized String toString() {
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
	public synchronized boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SimpleList)) {
			return false;
		}

		final SimpleList simpleList = (SimpleList) o;

		return list.equals(simpleList.list);
	}

	/**
	 * Return the hash value for this object.
	 * 
	 * @return the hash code corresponding to the value of this object
	 */
	public synchronized int hashCode() {
		return list.hashCode() + 17;
	}
}
