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

import java.util.Set;

import freemarker.template.instruction.TemplateFunctionModel;

/**
 * Adds the ability to retrieve Template functions from a TemplateProcessor.
 * Used by IncludeInstruction to retrieve a list of callable Functions.
 * 
 * @version $Id: FunctionTemplateProcessor.java 1098 2005-09-06 14:01:53Z
 *          run2000 $
 */
public interface FunctionTemplateProcessor extends TemplateProcessor {

	/**
	 * Retrieves a function from the template. Called by
	 * <code>CallInstruction</code>s and <code>IncludeInstruction</code>s at
	 * run-time.
	 * 
	 * @param name
	 *            the name of the function to be retrieved
	 */
	public TemplateFunctionModel getFunction(String name);

	/**
	 * Retrieve a <code>Set</code> of function names for this template.
	 * 
	 * @return a <code>Set</code> of function names (<code>String</code>
	 *         objects) that have been defined for this template.
	 */
	public Set getFunctionNames();

	/**
	 * Adds a function to the template. Called by the
	 * {@link freemarker.template.compiler.TemplateBuilder} at compile-time.
	 * 
	 * @param name
	 *            the name of the function to be stored
	 * @param function
	 *            the function to be stored by the template
	 */
	public void addFunction(String name, TemplateFunctionModel function);
}
