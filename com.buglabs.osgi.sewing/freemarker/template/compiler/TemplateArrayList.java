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

package freemarker.template.compiler;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;

/**
 * Encapsulates an array of {@link freemarker.template.TemplateProcessor}
 * objects. At run time, the contents of each item are processed in order.
 * 
 * @version $Id: TemplateArrayList.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class TemplateArrayList implements TemplateProcessor, Serializable {
	/**
	 * @serial an array of template processors, to be processed in sequence
	 */
	private final TemplateProcessor[] processors;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -6417959979633304920L;

	/**
	 * Create a new <code>TemplateArrayList</code>, given a list of
	 * {@link freemarker.template.TemplateProcessor}s to build the internal data
	 * structure.
	 * 
	 * @param processorList
	 *            a <code>List</code> of <code>TemplateProcessor</code> objects
	 *            to be added to the internal list.
	 * @throws NullPointerException
	 *             processorList is null
	 */
	public TemplateArrayList(List processorList) {
		processors = new TemplateProcessor[processorList.size()];
		processorList.toArray(processors);
	}

	/**
	 * Processes the contents of the internal
	 * {@link freemarker.template.TemplateProcessor} list, and outputs the
	 * resulting text to a <code>Writer</code>.
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
		short result;

		for (int i = 0; i < processors.length; i++) {
			result = processors[i].process(modelRoot, out, eventHandler);
			if (result != TemplateProcessor.OK) {
				return result;
			}
		}
		return TemplateProcessor.OK;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object
	 */
	public String toString() {
		return Arrays.asList(processors).toString();
	}

	/**
	 * For serialization, read this object normally, then check the array for
	 * validity.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (processors == null) {
			throw new InvalidObjectException("Cannot create a TemplateArrayList with a null array");
		}
	}
}
