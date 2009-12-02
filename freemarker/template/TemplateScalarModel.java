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
 * Scalar values in a template data model must implement this interface.
 * </p>
 * 
 * <p>
 * The detail messages of any {@link TemplateModelException}s thrown will be
 * included as HTML comments in the output.
 * </p>
 * 
 * @version $Id: TemplateScalarModel.java 987 2004-10-05 10:13:24Z run2000 $
 * @see TemplateNumberModel
 */
public interface TemplateScalarModel extends TemplateModel {

	/**
	 * Returns the scalar's value as a <code>String</code>.
	 * 
	 * @return the <code>String</code> value of this scalar.
	 */
	public String getAsString() throws TemplateModelException;
}
