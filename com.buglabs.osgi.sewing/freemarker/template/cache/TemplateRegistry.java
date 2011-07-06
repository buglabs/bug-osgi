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

package freemarker.template.cache;

import java.util.HashMap;
import java.util.Map;

import freemarker.template.BinaryData;
import freemarker.template.Template;
import freemarker.template.UnparsedTemplate;

/**
 * <p>
 * Stores a register of prototype templates, which can be retrieved by the
 * template cache whenever it needs to compile a template. This allows the cache
 * to store and retrieve different template implementations as required.
 * </p>
 * 
 * <p>
 * This is an implementation of the Parameterized Factory Method pattern.
 * </p>
 * 
 * @author Nicholas Cull
 * @version $Id: TemplateRegistry.java 987 2004-10-05 10:13:24Z run2000 $
 */
public class TemplateRegistry implements Cloneable {
	/** A map of template types that can be instantiated by this object. */
	protected Map m_cTemplates = new HashMap();

	/** Creates new <code>TemplateRegistry</code>. */
	public TemplateRegistry() {
		registerDefaultTemplates();
	}

	/**
	 * Creates a new <code>TemplateRegistry</code> as a clone of an existing
	 * one.
	 * 
	 * @param cOriginal
	 *            the original <code>TemplateRegistry</code> to be cloned
	 */
	public TemplateRegistry(TemplateRegistry cOriginal) {
		m_cTemplates = (Map) ((HashMap) cOriginal.m_cTemplates).clone();
	}

	/**
	 * Registers the templates that will be held in this template registry. This
	 * method may be subclassed to provide alternate template sets.
	 */
	protected void registerDefaultTemplates() {
		// Register the templates in the map
		m_cTemplates.put("template", new Template());
		m_cTemplates.put("unparsed", new UnparsedTemplate());
		m_cTemplates.put("binary", new BinaryData());
	}

	/**
	 * Retrieve a cloned template from the registry. The returned template can
	 * then be used to create a compiled template from a given source.
	 * 
	 * @param aKey
	 *            the type of template to retrieve
	 */
	public Cacheable getTemplate(String aKey) {
		return (Cacheable) ((Cacheable) m_cTemplates.get(aKey)).clone();
	}

	/**
	 * Adds a prototype template to the registry. The template can then be
	 * cloned and retrieved from the registry on demand.
	 * 
	 * @param aName
	 *            the name of the prototype template
	 * @param cTemplate
	 *            the prototype template itself
	 */
	public void addTemplate(String aName, Cacheable cTemplate) {
		Map cNewTemplates = (Map) ((HashMap) m_cTemplates).clone();

		// For thread-safety, we clone the old map before adding the
		// new template.
		cNewTemplates.put(aName, cTemplate);
		m_cTemplates = cNewTemplates;
	}

	/**
	 * Clones the current registry, and returns the clone back to the caller.
	 * The clone operation performed here is a shallow clone.
	 * 
	 * @return a clone of the current <code>TemplateRegistry</code>
	 */
	public Object clone() {
		return new TemplateRegistry(this);
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(50);

		buffer.append("TemplateRegistry, ");
		buffer.append(m_cTemplates.size());
		buffer.append(" registered templates.");
		return buffer.toString();
	}
}
