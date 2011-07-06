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
 * Values that can be indexed by a numeric value must implement this interface.
 * This interface replaces the {@link TemplateListModel#get get} method in
 * <code>TemplateListModel</code>.
 * </p>
 * 
 * <p>
 * The contract of this interface is general enough that an index need not start
 * at any particular place, be sequential, or be limited in size, other than the
 * limits given by {@link TemplateNumberModel} (a 64-bit signed integer).
 * </p>
 * 
 * @version $Id: TemplateIndexedModel.java 1084 2005-08-28 11:16:15Z run2000 $
 * @since 1.8
 * @see TemplateListModel
 * @see TemplateWriteableIndexedModel
 */
public interface TemplateIndexedModel extends TemplateModel {

	/**
	 * Get the value corresponding to the given index. Traditionally this would
	 * correspond to an index into an array, or similar structure, such as a
	 * <code>java.util.Vector</code>.
	 * 
	 * @param index
	 *            the index of the underlying value we're interested in
	 * @return a <code>TemplateModel</code> representing the value for the given
	 *         index
	 * @throws TemplateModelException
	 *             the value could not be determined, possibly due to an index
	 *             out-of-bounds, or an otherwise undefined value
	 */
	public TemplateModel getAtIndex(long index) throws TemplateModelException;
}
