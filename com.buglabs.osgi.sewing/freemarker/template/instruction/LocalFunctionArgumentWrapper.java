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

import java.util.Iterator;
import java.util.List;

import freemarker.template.RootModelWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.expression.Identifier;

/**
 * @author run2000
 * @version $Id: LocalFunctionArgumentWrapper.java 1123 2005-10-04 10:48:25Z
 *          run2000 $
 */
class LocalFunctionArgumentWrapper implements FunctionArgumentWrapper {
	private List argNames;
	private List argValues;

	public LocalFunctionArgumentWrapper(List argNames, List argValues) {
		this.argNames = argNames;
		this.argValues = argValues;
	}

	public TemplateWriteableHashModel getWrapper(TemplateWriteableHashModel globalModel) {
		RootModelWrapper localModelRoot = new RootModelWrapper(globalModel);
		Iterator argNamesIterator = argNames.iterator();
		Iterator argIterator = argValues.iterator();

		while (argNamesIterator.hasNext()) {
			Identifier argName = (Identifier) argNamesIterator.next();

			// Put the argument's value in the data model.
			TemplateModel argModel = null;
			if (argIterator.hasNext()) {
				argModel = (TemplateModel) argIterator.next();
			}
			localModelRoot.put(argName.getName(), argModel);
		}

		return localModelRoot;
	}

	public void cleanUp(TemplateWriteableHashModel localModel) {
		if (localModel != null) {
			((RootModelWrapper) localModel).reset();
		}
	}
}
