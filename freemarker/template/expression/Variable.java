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

package freemarker.template.expression;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateWriteableHashModel;

/**
 * An interface for expressions that get their values from the data model, and
 * for those data models that can be set to a value.
 * 
 * @version $Id: Variable.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public interface Variable extends Expression {

	/**
	 * Retrieve the name of this portion of the variable.
	 * 
	 * @param modelRoot
	 *            the model to be used in cases where the variable is the result
	 *            of an <code>Expression<code>.
	 * @throws TemplateException
	 *             the name could not be determined
	 */
	public String getName(TemplateWriteableHashModel modelRoot) throws TemplateException;

	/**
	 * Sets the specified value to the result of this expression. An exception
	 * may be thrown depending on whether the model evaluated by this expression
	 * is assignable, and whether the model itself throws an exception.
	 * 
	 * @param modelRoot
	 *            the model to be used in cases where the variable is the result
	 *            of an <code>Expression</code>
	 * @param value
	 *            the value to be assigned
	 * @throws TemplateException
	 *             the value could not be assigned to the model
	 */
	public void setTemplateModel(TemplateWriteableHashModel modelRoot, TemplateModel value) throws TemplateException;

}
