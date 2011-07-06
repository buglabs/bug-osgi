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

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Iterator class for {@link FastList} where the underlying Collection is a
 * List. Note that this model is <em>not</em> serializable, to match the
 * semantics of the underlying <code>java.util.ListIterator</code>.
 * 
 * @version $Id: FastListIterator.java 1087 2005-08-28 12:37:29Z run2000 $
 * @see FastList
 * @since 1.8
 */
public class FastListIterator implements TemplateWriteableIteratorModel {
	/** The iterator being wrapped by this model. */
	protected final ListIterator iterator;

	/**
	 * Constructor that takes a <code>java.util.Collection</code> implementation
	 * to iterator over.
	 * 
	 * @throws NullPointerException
	 *             the collection is null
	 */
	public FastListIterator(List list) {
		iterator = list.listIterator();
	}

	/**
	 * Do we have another item in the list?
	 * 
	 * @return <code>true</code> if there are more items to be iterated over,
	 *         otherwise <code>false</code>
	 * @throws TemplateModelException
	 *             there was a problem determining the next item in the list
	 */
	public boolean hasNext() throws TemplateModelException {
		return iterator.hasNext();
	}

	/**
	 * Retrieve the next item in the list. The item will be a
	 * <code>TemplateModel</code> containing the underlying value.
	 * 
	 * @return the next item in the list
	 * @throws TemplateModelException
	 *             the next item couldn't be retrieved, or we're at the end of
	 *             the list
	 */
	public TemplateModel next() throws TemplateModelException {
		try {
			return (TemplateModel) iterator.next();
		} catch (NoSuchElementException e) {
			throw new TemplateModelException("No more elements", e);
		} catch (ClassCastException e) {
			throw new TemplateModelException("Element is not a TemplateModel", e);
		} catch (ConcurrentModificationException e) {
			throw new TemplateModelException("List has been structurally modified", e);
		}
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
		iterator.set(model);
	}

	/**
	 * Is the object empty?
	 * 
	 * @return <code>true</code> if this object is empty, otherwise
	 *         <code>false</code>
	 */
	public boolean isEmpty() throws TemplateModelException {
		return iterator == null;
	}
}
