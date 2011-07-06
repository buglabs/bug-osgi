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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

/**
 * <p>
 * Transformations in a template data model must implement either this interface
 * or the {@link TemplateTransformModel2} interface. Input to the transformation
 * is reader from a <code>Reader</code>, and written out to a
 * <code>PrintWriter</code>.
 * </p>
 * 
 * <p>
 * The detail messages of any {@link TemplateModelException}s thrown will be
 * included as HTML comments in the output. Any <code>IOException</code>s are
 * passed up to the caller of the template.
 * </p>
 * 
 * @version $Id: TemplateTransformModel.java 987 2004-10-05 10:13:24Z run2000 $
 * @see TemplateTransformModel2
 */
public interface TemplateTransformModel extends TemplateModel {

	/**
	 * Performs a transformation/filter on FM-Classic output.
	 * 
	 * @param source
	 *            the input to be transformed
	 * @param output
	 *            the destination of the transformation
	 */
	public void transform(Reader source, PrintWriter output) throws IOException, TemplateModelException;
}
