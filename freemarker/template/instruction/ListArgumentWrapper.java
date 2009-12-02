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
package freemarker.template.instruction;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateWriteableHashModel;

/**
 * @author Nicholas Cull
 * @version $Id: ListArgumentWrapper.java 1123 2005-10-04 10:48:25Z run2000 $
 * @since 1.9
 */
interface ListArgumentWrapper extends TemplateWriteableHashModel {

	boolean next() throws TemplateModelException;

	void reset();

}
