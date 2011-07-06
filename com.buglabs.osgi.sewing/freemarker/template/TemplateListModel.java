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
 * List values in a template data model must implement this interface, which is
 * essentially the <code>Iterator</code> interface plus a {@link #rewind} method
 * that allows the list to be read again. As of 1.5.3, there is also a
 * {@link #get} method that allows the list to be accessed in a random-access
 * fashion.
 * </p>
 * 
 * <p>
 * The detail messages of any {@link TemplateModelException}s thrown will be
 * included as HTML comments in the output.
 * </p>
 * 
 * @version $Id: TemplateListModel.java 987 2004-10-05 10:13:24Z run2000 $
 * @deprecated This interface is not multi-thread safe, and also, in some cases,
 *             not single-thread safe either. Use the {@link TemplateListModel2}
 *             interface instead.
 * @see TemplateListModel2
 * @see TemplateIndexedModel
 */
public interface TemplateListModel extends TemplateModel {

	/**
	 * Resets the cursor to the beginning of the list.
	 */
	public void rewind() throws TemplateModelException;

	/**
	 * Is the cursor at the beginning of the list?
	 * 
	 * @return <code>true</code> if the cursor is at the beginning of the list,
	 *         otherwise <code>false</code>.
	 */
	public boolean isRewound() throws TemplateModelException;

	/**
	 * Is there a next item in the list?
	 * 
	 * @return <code>true</code> if there is a next element, otherwise
	 *         <code>false</code>.
	 */
	public boolean hasNext() throws TemplateModelException;

	/**
	 * Retrieves the next item in the list.
	 * 
	 * @return the next element in the list.
	 * @throws TemplateModelException
	 *             the next item in the list can't be retrieved, or no next item
	 *             exists.
	 */
	public TemplateModel next() throws TemplateModelException;

	/**
	 * Retrieves the specified item from the list.
	 * 
	 * @param index
	 *            the index of the item to be retrieved.
	 * @return the specified index in the list.
	 * @throws TemplateModelException
	 *             the specified item in the list can't be retrieved, or the
	 *             index is out of bounds.
	 * @since 1.5.3
	 */
	public TemplateModel get(int index) throws TemplateModelException;

}
