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
package freemarker.template.instruction;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;

/**
 * <p>
 * Functions in a template data model must implement this interface. Performs a
 * function on the given data model, and writes output to the given writer. Any
 * errors are handled using the given {@link TemplateRuntimeHandler}. The
 * supplied argument values list may be used as parameters to the function call.
 * </p>
 * 
 * <p>
 * <b>For experts only</b><br>
 * This model is similar in principle to the
 * {@link freemarker.template.TemplateTransformModel} and
 * {@link freemarker.template.TemplateTransformModel2} interfaces. The key
 * differences in using this model are:
 * <ul>
 * <li>It doesn't take an input stream in the form of a Reader
 * <li>It takes a parameter list, similar to
 * {@link freemarker.template.TemplateMethodModel2}
 * <li>Unlike all other models, it provides an event handler for error reporting
 * <li>Unlike all other models, it provides the model root as context
 * </ul>
 * <p>
 * This makes this model very powerful, in many ways similar to an
 * {@link EmptyInstruction} element. In particular, having access to the global
 * data model gives you more than enough rope to hang yourself with. For this
 * reason, <em>only use this model if your only other
 * option would be to create a new FM-Classic tag.</em> Normally either a
 * <code>TemplateTransformModel</code> and/or a
 * <code>TemplateMethodModel2</code> model would be sufficient.
 * </p>
 * 
 * @version $Id: TemplateFunctionModel.java 1123 2005-10-04 10:48:25Z run2000 $
 * @since 1.9
 */
public interface TemplateFunctionModel extends TemplateModel {

	/**
	 * Performs a function on the given data model.
	 * 
	 * @param modelRoot
	 *            the template model root, provides full access to the data
	 *            model
	 * @param out
	 *            the Writer to which output should be sent
	 * @param eventHandler
	 *            handles any events such as template exceptions
	 * @param argValues
	 *            a <code>List</code> of <code>TemplateModel</code>s
	 *            representing the arguments to the function
	 */
	void callFunction(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler, List argValues) throws IOException, TemplateModelException;
}
