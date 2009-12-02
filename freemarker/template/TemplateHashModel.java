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
 * Hashes in a template data model must implement this interface.
 * </p>
 * 
 * <p>
 * The detail messages of any <code>TemplateModelException</code>s thrown will
 * be included as HTML comments in the output.
 * </p>
 * 
 * @version $Id: TemplateHashModel.java 1151 2005-10-09 07:59:05Z run2000 $
 * @see TemplateWriteableHashModel
 */
public interface TemplateHashModel extends TemplateModel {

	/**
	 * Gets a {@link TemplateModel} from the hash.
	 * 
	 * @param key
	 *            the name by which the <code>TemplateModel</code> is identified
	 *            in the template.
	 * @return the <code>TemplateModel</code> referred to by the key, or
	 *         <code>null</code> if not found.
	 * @throws TemplateModelException
	 *             there was a problem getting the value for the given key
	 */
	public TemplateModel get(String key) throws TemplateModelException;
}
