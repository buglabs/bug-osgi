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
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TemplateTransformModel2;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.expression.Variable;

/**
 * An instruction that processes a TemplateTransformModel.
 * 
 * @version $Id: TransformInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class TransformInstruction extends GenericStartInstruction implements Serializable {

	/** @serial the name of the variable containing the transformation */
	private final Variable transformVariable;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 6158167624358601586L;

	/**
	 * Creates new <code>TransformInstruction</code>, with a given
	 * transformation variable.
	 * 
	 * @param variable
	 *            the variable representing the transformation to use
	 * @throws NullPointerException
	 *             the variable was null
	 */
	public TransformInstruction(Variable variable) {
		if (variable == null) {
			throw new NullPointerException("Variable for transform instruction cannot be null");
		}
		transformVariable = variable;
	}

	/**
	 * <p>
	 * Performs a <code>&lt;transform ... &gt;</code> operation on any child
	 * instructions. In the event that a transformation cannot be found or is
	 * empty, processing of the given block proceeds without transformation.
	 * </p>
	 * 
	 * <p>
	 * <b>Note:</b><br />
	 * This implementation is time-efficient rather than space efficient. For a
	 * space-efficient tradeoff, use <code>PipeReader</code> and
	 * <code>PipeWriter</code> instead. <code>PipeWriter</code> would need to be
	 * in a separate thread.
	 * </p>
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

		// If the body is null, skip all processing here
		if (body == null) {
			return TemplateProcessor.OK;
		}

		String transformName;
		try {
			transformName = transformVariable.getName(modelRoot);
		} catch (TemplateException te) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't resolve name of transform variable", te), out,
					"freemarker.template.instruction.TransformInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		TemplateModel baseModel;
		try {
			baseModel = transformVariable.getAsTemplateModel(modelRoot);
		} catch (TemplateException te) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't get referent of " + transformName, te), out,
					"freemarker.template.instruction.TransformInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		// If we can't find a transformer, process directly
		try {
			if ((baseModel == null) || (baseModel.isEmpty())) {
				body.process(modelRoot, out, eventHandler);
				return TemplateProcessor.OK;
			}
		} catch (TemplateModelException e) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't perform transformation " + transformName, e), out,
					"freemarker.template.instruction.TransformInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
		}

		// Process the body into a string buffer.
		StringWriter sw = new StringWriter();

		// Get the content we have so far
		short result;
		result = body.process(modelRoot, sw, eventHandler);

		if (baseModel instanceof TemplateTransformModel2) {
			// Now read from the string buffer
			Reader in = new StringReader(sw.toString());

			try {
				((TemplateTransformModel2) baseModel).transform(in, out);

			} catch (TemplateModelException e) {
				eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't perform transformation " + transformName, e), out,
						"freemarker.template.instruction.TransformInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			}
		} else if (baseModel instanceof TemplateTransformModel) {
			// Now read from the string buffer
			Reader inData = new StringReader(sw.toString());
			PrintWriter outData = new PrintWriter(out);

			try {
				((TemplateTransformModel) baseModel).transform(inData, outData);

			} catch (TemplateModelException e) {
				eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't perform transformation " + transformName, e), out,
						"freemarker.template.instruction.TransformInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			}
		} else {
			eventHandler.fireExceptionThrown(this, new TemplateException(transformName + " is not a TemplateTransformModel or TemplateTransformModel2"), out,
					"freemarker.template.instruction.TransformInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
		}
		return result;
	}

	/**
	 * Is this the right kind of instruction for the given
	 * {@link EndInstruction}?
	 * 
	 * @param endInstruction
	 *            the end instruction we're testing
	 * @return <code>true</code> if the <code>EndInstruction</code> is a
	 *         transform end instruction, otherwise <code>false</code>
	 */
	public boolean testEndInstruction(Instruction endInstruction) {
		return (endInstruction.getEndType() == TRANSFORM_END);
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representing this instruction subtree
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("transform ");
		buffer.append(transformVariable);
		buffer.append(' ');
		buffer.append(body);
		return buffer.toString();
	}

	/**
	 * For serialization, read this object normally, then check whether the
	 * transform variable is null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (transformVariable == null) {
			throw new InvalidObjectException("Cannot create a TransformInstruction with a null variable");
		}
	}
}
