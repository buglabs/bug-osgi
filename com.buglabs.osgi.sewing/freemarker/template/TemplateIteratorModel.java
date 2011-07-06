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
 * Iterators that iterate over a {@link TemplateListModel2} must implement this
 * interface. The interface is almost identical to the
 * <code>java.util.Iterator</code> interface, so that such iterators can be
 * transparently wrapped in a <code>TemplateIteratorModel</code> implementation.
 * </p>
 * 
 * @version $Id: TemplateIteratorModel.java 1149 2005-10-09 07:41:19Z run2000 $
 * @since 1.8
 * @see TemplateListModel2
 * @see TemplateWriteableIteratorModel
 */
public interface TemplateIteratorModel extends TemplateModel {

	/**
	 * Do we have another item in the list?
	 * 
	 * @return <code>true</code> if there are more items to be iterated over,
	 *         otherwise <code>false</code>
	 * @throws TemplateModelException
	 *             there was a problem determining the next item in the list
	 */
	public boolean hasNext() throws TemplateModelException;

	/**
	 * Retrieve the next item in the list. The item will be a
	 * <code>TemplateModel</code> containing the underlying value.
	 * 
	 * @return the next item in the list
	 * @throws TemplateModelException
	 *             the next item couldn't be retrieved, or we're at the end of
	 *             the list
	 */
	public TemplateModel next() throws TemplateModelException;
}
