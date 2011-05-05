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

/**
 * An iterator that operates over a {@link TemplateIndexedModel} implementation
 * that also implements {@link TemplateWriteableIndexedModel}. The assumption
 * here is that we have constant-time random access to the underlying
 * implementation.
 * 
 * @version $Id: FastIndexedWriteableIterator.java 1189 2005-10-16 01:53:54Z
 *          run2000 $
 * @since 1.9
 * @see FastIndexedIterator
 */
public class FastIndexedWriteableIterator implements TemplateWriteableIteratorModel {
	/** The model that we will index into. */
	protected final TemplateWriteableIndexedModel indexModel;
	/** The beginning index of the model. */
	protected final long startIndex;
	/** The end index of the model. */
	protected final long endIndex;
	/** The current index into the model. */
	protected long currentIndex;

	/**
	 * Constructor that takes the object we're iterating over as an argument
	 * 
	 * @param model
	 *            the indexed list model to iterate over
	 */
	public FastIndexedWriteableIterator(TemplateWriteableIndexedModel model, long startIndex, long endIndex) {

		if (model == null) {
			throw new NullPointerException("Model to indexed iterator cannot be null");
		}
		this.indexModel = model;
		this.currentIndex = startIndex;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	/**
	 * Is the iterator empty?
	 * 
	 * @return <code>false</code>, indicating the iterator is not empty
	 */
	public boolean isEmpty() throws TemplateModelException {
		return startIndex > endIndex;
	}

	/**
	 * Do we have a next value?
	 * 
	 * @return <code>true</code> if more values exist in the list, otherwise
	 *         <code>false</code>
	 */
	public boolean hasNext() {
		return currentIndex <= endIndex;
	}

	/**
	 * Return the next value in the indexModel, and increment the counter to
	 * point to the next value.
	 * 
	 * @return a <code>TemplateModel</code> representing the next value in the
	 *         list
	 */
	public TemplateModel next() throws TemplateModelException {
		return indexModel.getAtIndex(currentIndex++);
	}

	/**
	 * Inserts the specified element into the list. The element is inserted
	 * immediately before the next element that would be returned by next, if
	 * any.
	 * 
	 * @param model
	 *            the model to insert into the list
	 * @throws TemplateModelException
	 *             the model could not be assigned to the list
	 */
	public void set(TemplateModel model) throws TemplateModelException {
		indexModel.putAtIndex(currentIndex - 1, model);
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(50);
		buffer.append("Writeable indexed iterator from ");
		buffer.append(startIndex);
		buffer.append(" to ");
		buffer.append(endIndex);
		return buffer.toString();
	}
}
