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
 * Writeable values that can be indexed by a numeric value must implement this
 * interface.
 * </p>
 * 
 * <p>
 * The contract of this interface is general enough that an index need not start
 * at any particular place, be sequential, or be limited in size, other than the
 * limits given by {@link TemplateNumberModel} (a 64-bit signed integer). It
 * also doesn't specify what should happen if the index is outside the existing
 * range. The list can be extended, or an exception thrown.
 * </p>
 * 
 * @version $Id: TemplateWriteableIndexedModel.java 1151 2005-10-09 07:59:05Z
 *          run2000 $
 * @since 1.9
 * @see TemplateIndexedModel
 */
public interface TemplateWriteableIndexedModel extends TemplateIndexedModel {

	/**
	 * Set the value corresponding to the given index. Traditionally this would
	 * correspond to an index into an array, or similar structure, such as a
	 * <code>java.util.Vector</code>.
	 * 
	 * @param index
	 *            the index of the underlying value we're interested in
	 * @param model
	 *            the <code>TemplateModel</code> to be added to the list for the
	 *            given index
	 * @throws TemplateModelException
	 *             the value could not be set, possibly due to an index
	 *             out-of-bounds, or some other error
	 */
	public void putAtIndex(long index, TemplateModel model) throws TemplateModelException;
}
