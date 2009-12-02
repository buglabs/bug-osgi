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

import freemarker.template.TemplateIteratorModel;
import freemarker.template.TemplateListModel2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.TemplateWriteableIteratorModel;

/**
 * @author Nicholas Cull
 * @version $Id: List2ArgumentWrapper.java 1123 2005-10-04 10:48:25Z run2000 $
 * @since 1.9
 */
class List2ArgumentWrapper implements ListArgumentWrapper {

	private TemplateWriteableHashModel globalModel;
	private TemplateListModel2 listModel;
	private TemplateIteratorModel iterator;
	private TemplateModel variable;
	private String iteratorName;
	private String variableName;
	private boolean writeable;

	public List2ArgumentWrapper(TemplateWriteableHashModel modelRoot, TemplateListModel2 listModel, String name) throws TemplateModelException {
		this.globalModel = modelRoot;
		this.listModel = listModel;
		this.iterator = listModel.templateIterator();
		this.variableName = name;
		this.iteratorName = name + "#";
		this.writeable = (this.iterator instanceof TemplateWriteableIteratorModel);
	}

	/**
	 * Is the object empty?
	 * 
	 * @return <code>false</code>, to indicate the model is not empty
	 */
	public boolean isEmpty() throws TemplateModelException {
		return false;
	}

	public boolean next() throws TemplateModelException {
		if (!iterator.hasNext()) {
			return false;
		}
		variable = iterator.next();
		return true;
	}

	/**
	 * Gets a {@link freemarker.template.TemplateModel} from the hash.
	 * 
	 * @param key
	 *            the name by which the <code>TemplateModel</code> is identified
	 *            in the template.
	 * @return the <code>TemplateModel</code> referred to by the key, or
	 *         <code>null</code> if not found.
	 */
	public TemplateModel get(String key) throws TemplateModelException {
		if (variableName.equals(key)) {
			return variable;
		}
		if (iteratorName.equals(key)) {
			return iterator;
		}
		return globalModel.get(key);
	}

	/**
	 * Sets a value in the hash model.
	 * 
	 * @param key
	 *            the hash key
	 * @param model
	 *            the value to be added to the hash model
	 */
	public void put(String key, TemplateModel model) throws TemplateModelException {

		if (variableName.equals(key)) {
			if (writeable) {
				((TemplateWriteableIteratorModel) iterator).set(model);
				return;
			} else {
				throw new TemplateModelException("Cannot assign to variable " + key + " within a list operator");
			}
		} else if (iteratorName.equals(key)) {
			throw new TemplateModelException("Cannot assign to iterator " + key + " within a list operator");
		}
		globalModel.put(key, model);
	}

	/**
	 * Make sure all state is cleared, to avoid possible circular references.
	 */
	public void reset() {
		listModel.releaseIterator(iterator);
		variable = null;
		iterator = null;
	}
}
