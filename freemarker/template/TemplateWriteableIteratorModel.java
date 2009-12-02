/*
 * FreeMarker: a tool that allows Java programs to generate HTML
 * output using templates.
 * Copyright (C) 1998-2005 Benjamin Geer
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
 * Writeable template iterator models that that iterate over a
 * {@link TemplateListModel2} must implement this interface. The interface is
 * similar to the <code>java.util.ListIterator</code> interface, so that such
 * iterators can be transparently wrapped in a
 * <code>TemplateWriteableIteratorModel</code> implementation.
 * 
 * @version $Id: TemplateWriteableIteratorModel.java 1151 2005-10-09 07:59:05Z
 *          run2000 $
 * @since 1.9
 */
public interface TemplateWriteableIteratorModel extends TemplateIteratorModel {

	/**
	 * Inserts the specified element into the list. The element is inserted
	 * immediately before the element that would be returned by
	 * <code>next()</code>, if any.
	 * 
	 * @param model
	 *            the model to insert into the list
	 * @throws TemplateModelException
	 *             the model could not be assigned to the current item of the
	 *             list, or <code>next()</code> has not be called yet
	 */
	void set(TemplateModel model) throws TemplateModelException;

}
