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
 * <p>
 * List values in a template data model must implement either this interface or
 * the deprecated {@link TemplateListModel} interface. This interface acts
 * effectively as a factory for {@link TemplateIteratorModel} objects.
 * TemplateIteratorModel objects are the objects that actually iterate over the
 * underlying list represented by this interface. The relationship is almost
 * identical to that between <code>java.util.List</code> implementations and the
 * <code>java.util.Iterator</code> interface.
 * </p>
 * 
 * <p>
 * This interface also supports the notion of recycling iterators. This is not
 * required, but may improve efficiency in cases where creating a new iterator
 * is expensive.
 * </p>
 * 
 * <p>
 * This interface replaces the now deprecated {@link TemplateListModel}
 * interface.
 * </p>
 * 
 * <p>
 * The detail messages of any {@link TemplateModelException}s thrown will be
 * included as HTML comments in the output.
 * </p>
 * 
 * @version $Id: TemplateListModel2.java 987 2004-10-05 10:13:24Z run2000 $
 * @since 1.8
 * @see TemplateListModel
 * @see TemplateIteratorModel
 */
public interface TemplateListModel2 extends TemplateModel {

	/**
	 * Retrieves an iterator to iterate over this list. Can choose to return
	 * <code>null</code> if there are no elements in the list.
	 * 
	 * @return an iterator to iterate over the current list.
	 * @throws TemplateModelException
	 *             the next item in the list can't be retrieved, or no next item
	 *             exists.
	 */
	public TemplateIteratorModel templateIterator() throws TemplateModelException;

	/**
	 * <p>
	 * Returns the used iterator to the list model. Implement this method when
	 * you want to use an object pool of <code>TemplateIterator</code> objects.
	 * Otherwise, leave the implementation of this method blank.
	 * </p>
	 * 
	 * <p>
	 * Note that if the iterator returned in {@link #templateIterator} is
	 * <code>null</code>, this method will not be called for the
	 * <code>null</code> iterator.
	 * </p>
	 * 
	 * @param iterator
	 *            the iterator to be returned to the object pool, if any
	 */
	public void releaseIterator(TemplateIteratorModel iterator);
}
