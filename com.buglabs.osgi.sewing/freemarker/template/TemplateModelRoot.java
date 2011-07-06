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
 * The root node of a template data model may implement this interface. This
 * interface is now just a marker interface. All functionality has either been
 * moved to {@link TemplateWriteableHashModel} or made redundant by changes to
 * the engine.
 * 
 * @version $Id: TemplateModelRoot.java 1177 2005-10-10 13:24:39Z run2000 $
 * @see TemplateWriteableHashModel
 */
public interface TemplateModelRoot extends TemplateWriteableHashModel {

	/**
	 * Sets a value in the hash model.
	 * 
	 * @param key
	 *            the hash key.
	 * @param model
	 *            the hash value to be added.
	 */
	public void put(String key, TemplateModel model);

	/**
	 * Removes a key from the hash model.
	 * 
	 * @param key
	 *            the key to be removed.
	 */
	public void remove(String key);
}
