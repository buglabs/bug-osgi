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
 * Simple interface for extracting the underlying object from a given template
 * model. Implement this interface when you want to:
 * </p>
 * <ul>
 * <li>Expose the underlying object to the reflection mechanism in the
 * <code>freemarker.ext.beans</code> package; or</li>
 * <li>Use the underlying object from a <code>TemplateMethodModel2</code>
 * implementation</li>
 * </ul>
 * 
 * <p>
 * For some object models, this could be a potential security hazard. In these
 * cases, the <code>getAsObject</code> method should return a delegate object
 * instead of the underlying object. The delegate would pass method calls to the
 * underlying object, possibly performing security checks before forwarding the
 * call.
 * </p>
 * 
 * @author Nicholas Cull
 * @version $Id: TemplateObjectModel.java 997 2004-10-15 04:07:21Z run2000 $
 */
public interface TemplateObjectModel extends TemplateModel {

	/**
	 * Return the underlying object to the reflection mechanism in the
	 * <code>freemarker.ext.beans</code> package. Any variables, methods or
	 * properties can be called directly via reflection.</p>
	 * 
	 * @return the underlying object for this template model
	 * @throws freemarker.template.TemplateModelException
	 *             the object could not be returned
	 */
	public Object getAsObject() throws TemplateModelException;
}
