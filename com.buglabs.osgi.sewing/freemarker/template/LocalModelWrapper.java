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
package freemarker.template;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * Wraps a template hash model with a template model root. The hash model can be
 * accessed transparently from the root of this model. Any local variables are
 * stored within the supplied <code>Map</code>. Anything that doesn't exist
 * within the <code>Map</code> is automatically forwarded to the wrapped global
 * model. Local variables are counted as being all variables contained in the
 * <code>localModel</code> parameter of the constructor method.
 * </p>
 * 
 * <p>
 * Use this class when you want a well-defined distinction between global and
 * local variables.
 * </p>
 * 
 * @version $Id: LocalModelWrapper.java 1189 2005-10-16 01:53:54Z run2000 $
 * @since 1.9
 * @see RootModelWrapper
 */
public class LocalModelWrapper implements TemplateWriteableHashModel, Serializable {
	/**
	 * The map containing local values for the root model.
	 * 
	 * @serial a <code>Map</code> containing any values added to the template
	 *         model at run time.
	 */
	protected Map localModel;
	/**
	 * The underlying root model being wrapped.
	 * 
	 * @serial the underlying <code>TemplateModelRoot</code> being wrapped by
	 *         this wrapper object
	 */
	protected TemplateWriteableHashModel globalModel;

	/** Class UUID for serialization. */
	private static final long serialVersionUID = 3937849863527347324L;

	/**
	 * Create a new <code>RootModelWrapper</code> with the given hash model as
	 * the model to be wrapped.
	 * 
	 * @param globalModel
	 *            the root model to be wrapped
	 * @param localModel
	 *            a Map containing the pre-populated local variables
	 */
	public LocalModelWrapper(TemplateWriteableHashModel globalModel, Map localModel) {
		this.globalModel = globalModel;
		this.localModel = localModel;
	}

	/**
	 * Retrieve a template model for the given key, if one exists. First, look
	 * at the local storage to see if we have an entry. If so, return the local
	 * entry. Otherwise, forward the call to the wrapped hash model.
	 * 
	 * @param key
	 *            the name of the value to be returned
	 * @return a TemplateModel for the corresponding key, if one exists,
	 *         otherwise <code>null</code>
	 * @throws freemarker.template.TemplateModelException
	 *             there was a problem with the underlying hash model
	 */
	public TemplateModel get(String key) throws TemplateModelException {
		if (localModel.containsKey(key)) {
			return (TemplateModel) localModel.get(key);
		}
		return globalModel.get(key);
	}

	/**
	 * Returns whether we have a completely empty model. If the local storage is
	 * non-empty, return <code>false</code>. Otherwise, forward the call to the
	 * global model.
	 * 
	 * @return <code>true</code> if the model is empty, otherwise
	 *         <code>false</code>
	 * @throws freemarker.template.TemplateModelException
	 *             there was a problem with underlying hash model
	 */
	public boolean isEmpty() throws TemplateModelException {
		if (localModel.isEmpty()) {
			return globalModel.isEmpty();
		}
		return false;
	}

	/**
	 * Put the given template model into storage with the given key. The storage
	 * may be either the local or global model, depending on what was declared
	 * as local at construction time.
	 * 
	 * @param key
	 *            the name of the model to be stored
	 * @param model
	 *            the model being stored
	 */
	public void put(String key, TemplateModel model) throws TemplateModelException {
		if (localModel.containsKey(key)) {
			localModel.put(key, model);
		} else {
			globalModel.put(key, model);
		}
	}

	/**
	 * Clear all the local variables from the local storage, and just provide
	 * pass-through access to the wrapped hash model.
	 */
	public void reset() {
		localModel.clear();
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("LocalModelWrapper, ");
		buffer.append(localModel.size());
		buffer.append(" local items.");
		return buffer.toString();
	}
}
