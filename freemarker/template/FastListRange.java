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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * A simple list model that represents a range of values. For memory
 * conservation, we store the from and to values only. For a range that iterates
 * backwards, set the <code>toValue</code> to be lower than the
 * <code>fromValue</code>.
 * 
 * @version $Id: FastListRange.java 1051 2004-10-24 09:14:44Z run2000 $
 * @since 1.8
 */
public final class FastListRange implements TemplateListModel2, TemplateIndexedModel, Serializable {
	/** The beginning of the list range. */
	private final long fromValue;
	/**
	 * The end of the list range, inclusive. Can be lower than the
	 * <code>fromValue</code> field, to indicate that the list should be stepped
	 * through backwards. This also implies that the list cannot be empty.
	 */
	private final long toValue;
	/** Whether we step forwards or backwards in the list. */
	private transient int step;
	/**
	 * The difference between the <code>fromValue</code> and
	 * <code>toValue</code> values.
	 */
	private transient long range;

	/** Serialization id, for future compatibility. */
	static final long serialVersionUID = -3029960574903790084L;

	/**
	 * Constructor that takes from and to values to represent the indexModel of
	 * values. Both from and to values are inclusive.
	 * 
	 * @param fromValue
	 *            the start of the range
	 * @param toValue
	 *            the end of the range
	 */
	public FastListRange(long fromValue, long toValue) {
		this.fromValue = fromValue;
		this.toValue = toValue;
		step = (fromValue > toValue) ? -1 : 1;
		range = Math.abs(toValue - fromValue);
	}

	/**
	 * Is the range empty?
	 * 
	 * @return <code>false</code>, because there is always at least one value in
	 *         the range
	 */
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Get the value at the specified index.
	 * 
	 * @param index
	 *            the index of the item we're after
	 * @return a new <code>FastNumber</code> containing the value at the given
	 *         index
	 * @throws freemarker.template.TemplateModelException
	 *             the index was out of bounds
	 */
	public TemplateModel getAtIndex(long index) throws TemplateModelException {

		if ((index < 0) || (index > range)) {
			throw new TemplateModelException("Index out of bounds for given indexModel");
		}

		return new FastNumber(fromValue + (index * step));
	}

	/**
	 * Get a new iterator for this template model.
	 * 
	 * @return an iterator to use over the values in this range
	 */
	public TemplateIteratorModel templateIterator() {
		return new FastIndexedIterator(this, 0, range);
	}

	/**
	 * Reclaim the iterator. In this case we don't do anything, since we don't
	 * pool iterators in this implementation.
	 * 
	 * @param iterator
	 *            the iterator to be reclaimed
	 */
	public void releaseIterator(TemplateIteratorModel iterator) {
		// Do nothing
	}

	/**
	 * Serialized form consists of only the from and to values. Make sure
	 * transient fields are repopulated when we deserialize this object.
	 * 
	 * @param stream
	 *            the stream from which to deserialize
	 * @throws IOException
	 *             there was an IO problem with the stream
	 * @throws ClassNotFoundException
	 *             the classes being deserialized could not be found
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		step = (fromValue > toValue) ? -1 : 1;
		range = Math.abs(toValue - fromValue);
	}

	/**
	 * <p>
	 * Return the string value of this list range. The values typically look
	 * like this:
	 * </p>
	 * 
	 * <pre>
	 * [ {fromValue} .. {toValue} ]
	 * </pre>
	 * 
	 * @return a <code>String</code> representing this range
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(16);

		buffer.append("[ ");
		buffer.append(fromValue);
		buffer.append(" .. ");
		buffer.append(toValue);
		buffer.append(" ]");

		return buffer.toString();
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
		if (!(o instanceof FastListRange)) {
			return false;
		}

		final FastListRange fastListRange = (FastListRange) o;

		if (fromValue != fastListRange.fromValue) {
			return false;
		}
		return (toValue == fastListRange.toValue);
	}

	/**
	 * Return the hash value for this object.
	 * 
	 * @return a hash code corresponding to the object's value
	 */
	public int hashCode() {
		int result;
		result = (int) (fromValue ^ (fromValue >>> 32));
		result = 29 * result + (int) (toValue ^ (toValue >>> 32));
		return result;
	}
}
