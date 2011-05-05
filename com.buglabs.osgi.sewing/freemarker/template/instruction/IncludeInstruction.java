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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.Writer;

import freemarker.template.TemplateException;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.cache.Cache;
import freemarker.template.cache.Cacheable;
import freemarker.template.expression.Expression;
import freemarker.template.expression.ExpressionUtils;

/**
 * An instruction that gets another template from a
 * {@link freemarker.template.cache.Cache}, and processes it within the current
 * template.
 * 
 * @version $Id: IncludeInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class IncludeInstruction extends EmptyInstruction implements Serializable {

	/**
	 * @serial the template to which this include instruction belongs
	 */
	private final TemplateProcessor template;
	/**
	 * @serial an expression that determines name of the template to be included
	 */
	private final Expression name;
	/**
	 * @serial an expression that determines the type of template to be included
	 */
	private final Expression type;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 8423827110332040952L;

	/**
	 * Constructor that takes the originating template and the name of the
	 * template to be included.
	 * 
	 * @param template
	 *            the template that this <code>Include</code> is a part of.
	 *            Provided so that the instruction can locate a
	 *            {@link freemarker.template.cache.Cache} instance.
	 * @param name
	 *            the name, in the <code>TemplateCache</code>, of the template
	 *            to be included.
	 * @throws NullPointerException
	 *             template or template name are null
	 * @throws IllegalArgumentException
	 *             template name is not a string or number
	 */
	public IncludeInstruction(TemplateProcessor template, Expression name) {
		this(template, name, null);
	}

	/**
	 * Constructor that takes the originating template, the name of the template
	 * to be included and the type of template.
	 * 
	 * @param template
	 *            the template that this <code>Include</code> is a part of.
	 * @param name
	 *            the name, in the <code>TemplateCache</code>, of the template
	 *            to be included.
	 * @param type
	 *            the type of the template, as known by the
	 *            <code>TemplateRegistry</code>, or <code>null</code>
	 * @throws NullPointerException
	 *             template or template name are null
	 * @throws IllegalArgumentException
	 *             template name is not a string or number, or template type is
	 *             not null, a string, or a number
	 */
	public IncludeInstruction(TemplateProcessor template, Expression name, Expression type) {
		if (template == null) {
			throw new NullPointerException("Template for Include instruction cannot be null");
		}
		if ((name.getType() & (ExpressionUtils.EXPRESSION_TYPE_STRING | ExpressionUtils.EXPRESSION_TYPE_NUMBER)) == 0) {
			throw new IllegalArgumentException("Template name for Include instruction must be scalar or numeric");
		}
		if ((type != null) && ((type.getType() & (ExpressionUtils.EXPRESSION_TYPE_STRING | ExpressionUtils.EXPRESSION_TYPE_NUMBER)) == 0)) {
			throw new IllegalArgumentException("Template type for Include instruction must be scalar or numeric");
		}
		this.template = template;
		this.name = name;
		this.type = type;
	}

	/**
	 * Evaluate the template being included by this instruction.
	 * 
	 * @param modelRoot
	 *            the root node of the data model.
	 * @param out
	 *            a <code>Writer</code> to send the output to.
	 * @param eventHandler
	 *            a <code>TemplateEventAdapter</code> for handling any events
	 *            that occur during processing.
	 */
	public short process(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler) throws IOException {
		Cache cache = null;

		if (template instanceof Cacheable) {
			cache = ((Cacheable) template).getCache();
		}
		if (cache == null) {
			eventHandler.fireExceptionThrown(this, new TemplateException("The current template wasn't given a reference to a Cache"), out,
					"freemarker.template.instruction.IncludeInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		String itemName;
		try {
			itemName = ExpressionUtils.getAsString(name.getAsTemplateModel(modelRoot));
		} catch (TemplateException e) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Error in template name expression", e), out,
					"freemarker.template.instruction.IncludeInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		if (itemName == null) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Error in template name expression: " + "expression evaluates to null"), out,
					"freemarker.template.instruction.IncludeInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		String itemType = null;
		try {
			if (type != null) {
				itemType = ExpressionUtils.getAsString(type.getAsTemplateModel(modelRoot));
			}
		} catch (TemplateException e) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Error in template type expression", e), out,
					"freemarker.template.instruction.IncludeInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		TemplateProcessor includedTemplate;
		if (itemType == null) {
			includedTemplate = (TemplateProcessor) cache.getItem(itemName);
		} else {
			includedTemplate = (TemplateProcessor) cache.getItem(itemName, itemType);
		}
		if (includedTemplate == null) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Template \"" + itemName + "\" not found in cache"), out,
					"freemarker.template.instruction.IncludeInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		includedTemplate.process(modelRoot, out, eventHandler);
		return TemplateProcessor.OK;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representing this instruction subtree
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("include ");
		buffer.append(name);
		if (type != null) {
			buffer.append(" of type ");
			buffer.append(type);
		}
		return buffer.toString();
	}

	/**
	 * For serialization, read this object normally, then check whether the
	 * parent template or the name are null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if ((template == null) || (name == null)) {
			throw new InvalidObjectException("Cannot create an IncludeInstruction with a null template or name");
		}
	}
}
