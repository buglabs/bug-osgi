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
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Wraps a template hash model with a template model root. The hash model can be
 * accessed transparently from the root of this model. Any local variables are
 * stored within a <code>HashMap</code>. Anything that doesn't exist within the
 * <code>HashMap</code> is automatically forwarded to the wrapped hash model.
 * Local variables are counted as being any variables added using the
 * {@link #put put()} method.
 * </p>
 * 
 * <p>
 * Use this class when your template model (the hash model) is shared across
 * several template calls, possibly multithreaded, and you want to be sure that
 * the model isn't inadvertantly changed between calls.
 * </p>
 * 
 * <p>
 * To do this for multithreaded calls, create a separate
 * <code>RootModelWrapper</code> object for each template call, passing in the
 * data model you want to be left unchanged. Then, call your templates with each
 * <code>RootModelWrapper</code> object as your root data model.
 * </p>
 * 
 * <p>
 * To do this for synchronous calls, it is sufficient to create one
 * <code>RootModelWrapper</code> object, passing in the data model you want to
 * be left unchanged. Then, call your templates with the
 * <code>RootModelWrapper</code> object as your root data model, and call the
 * {@link #reset} method between each template call.
 * </p>
 * 
 * <p>
 * This class was previously in the <code>freemarker.ext.misc</code> package.
 * </p>
 * 
 * @version $Id: RootModelWrapper.java 1189 2005-10-16 01:53:54Z run2000 $
 * @since 1.8
 * @see LocalModelWrapper
 */
public class RootModelWrapper implements TemplateModelRoot, Serializable {
	/**
	 * The map containing temporary values for the root model.
	 * 
	 * @serial a <code>Map</code> containing any values added to the template
	 *         model at run time.
	 */
	protected Map rootModel;
	/**
	 * The underlying template hash being wrapped.
	 * 
	 * @serial the underlying <code>TemplateHashModel</code> being wrapped by
	 *         this wrapper object
	 */
	protected TemplateHashModel hashModel;

	/** Class UUID for serialization. */
	private static final long serialVersionUID = 5887349023424507785L;

	/**
	 * Create a new <code>RootModelWrapper</code> with the given hash model as
	 * the model to be wrapped.
	 * 
	 * @param hashModel
	 *            the hash model to be wrapped
	 */
	public RootModelWrapper(TemplateHashModel hashModel) {
		this.hashModel = hashModel;
		rootModel = new HashMap();
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
	 * @throws TemplateModelException
	 *             there was a problem with the underlying hash model
	 */
	public TemplateModel get(String key) throws TemplateModelException {
		if (rootModel.containsKey(key)) {
			return (TemplateModel) rootModel.get(key);
		}
		return hashModel.get(key);
	}

	/**
	 * Returns whether we have a completely empty model. If the local storage is
	 * non-empty, return <code>false</code>. Otherwise, forward the call to the
	 * wrapped hash model.
	 * 
	 * @return <code>true</code> if the model is empty, otherwise
	 *         <code>false</code>
	 * @throws TemplateModelException
	 *             there was a problem with underlying hash model
	 */
	public boolean isEmpty() throws TemplateModelException {
		if (rootModel.isEmpty()) {
			return hashModel.isEmpty();
		}
		return false;
	}

	/**
	 * Put the given template model into local storage with the given key.
	 * 
	 * @param key
	 *            the name of the model to be stored
	 * @param model
	 *            the model being stored locally
	 */
	public void put(String key, TemplateModel model) {
		rootModel.put(key, model);
	}

	/**
	 * <p>
	 * Remove the named model from local storage.
	 * </p>
	 * 
	 * <p>
	 * Note that we don't attempt to block access to the hash model when we
	 * remove a key. This is because the only time a "remove" call is performed
	 * is when the template engine is sure that there was no underlying data for
	 * that key to begin with.
	 * </p>
	 * 
	 * @param key
	 *            the name of the model to be removed
	 */
	public void remove(String key) {
		rootModel.remove(key);
	}

	/**
	 * Clear all the local variables from the local storage, and just provide
	 * pass-through access to the wrapped hash model.
	 */
	public void reset() {
		rootModel.clear();
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("RootModelWrapper, ");
		buffer.append(rootModel.size());
		buffer.append(" local items.");
		return buffer.toString();
	}
}
