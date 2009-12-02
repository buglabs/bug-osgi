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
 * Writeable hashes in a template data model must implement this interface. In
 * addition, the root node of a template data model must implement this
 * interface.
 * </p>
 * 
 * <p>
 * The detail messages of any <code>TemplateModelException</code>s thrown will
 * be included as HTML comments in the output.
 * </p>
 * 
 * @version $Id: TemplateWriteableHashModel.java 1177 2005-10-10 13:24:39Z
 *          run2000 $
 * @since 1.9
 * @see TemplateHashModel
 * @see TemplateModelRoot
 */
public interface TemplateWriteableHashModel extends TemplateHashModel {

	/**
	 * Sets a value in the hash model.
	 * 
	 * @param key
	 *            the name by which the TemplateModel is identified in the
	 *            template
	 * @param model
	 *            the value to be set with the given key for this hash model
	 * @throws TemplateModelException
	 *             there was a problem setting the value for the given key
	 */
	void put(String key, TemplateModel model) throws TemplateModelException;
}
