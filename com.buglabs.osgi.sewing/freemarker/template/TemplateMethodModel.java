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
 *
 * 22 October 1999: This class added by Holger Arendt.
 */

package freemarker.template;

import java.util.List;

/**
 * <p>
 * Method calls in a template data model must implement either this interface or
 * the {@link TemplateMethodModel2} interface. The calling convention from a
 * Template is identical to that of a {@link TemplateMethodModel2}, only the
 * arguments passed to the object are different.
 * </p>
 * 
 * <p>
 * The detail messages of any <code>TemplateModelException</code>s thrown will
 * be included as HTML comments in the output.
 * </p>
 * 
 * @version $Id: TemplateMethodModel.java 987 2004-10-05 10:13:24Z run2000 $
 * @see TemplateMethodModel2
 */
public interface TemplateMethodModel extends TemplateModel {

	/**
	 * Executes a method call. Arguments are passed as a <code>List</code> of
	 * <code>String</code> objects.
	 * 
	 * @param arguments
	 *            a <code>List</code> of <code>String</code> objects containing
	 *            the values of the arguments passed to the method.
	 * @return the <code>TemplateModel</code> produced by the method, or
	 *         <code>null</code>.
	 */
	public TemplateModel exec(List arguments) throws TemplateModelException;
}
