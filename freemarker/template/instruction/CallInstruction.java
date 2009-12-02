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
import java.util.List;

import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModel2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.expression.ExpressionUtils;
import freemarker.template.expression.ListLiteral;
import freemarker.template.expression.MethodCall;

/**
 * An instruction representing a function call.
 * 
 * @version $Id: CallInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class CallInstruction extends EmptyInstruction implements Serializable {

	/** @serial The function or method name and arguments to be called */
	private final MethodCall methodCall;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -3034404332018443588L;

	/**
	 * Constructor for a function call.
	 * 
	 * @param methodCall
	 *            the method call expression representing the arguments to be
	 *            passed to the function
	 * @throws NullPointerException
	 *             the method call expression was null
	 */
	public CallInstruction(MethodCall methodCall) {
		if (methodCall == null) {
			throw new NullPointerException("Method call cannot be null in call instruction");
		}
		this.methodCall = methodCall;
	}

	/**
	 * Process this <code>&lt;call ... &gt;</code>
	 * 
	 * @param modelRoot
	 *            the root node of the data model
	 * @param out
	 *            a <code>Writer</code> to send the output to
	 * @param eventHandler
	 *            a <code>TemplateEventAdapter</code> for handling any events
	 *            that occur during processing
	 */
	public short process(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler) throws IOException {

		// Look for the function first in the template, then in the
		// data model, where it might have been put by an IncludeInstruction.
		String methodName;
		try {
			methodName = methodCall.getName(modelRoot);
		} catch (TemplateException e) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't get function name", e), out, "freemarker.template.instruction.CallInstruction.process",
					TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		TemplateFunctionModel functionModel;

		{
			TemplateModel model;
			try {
				model = methodCall.getTarget().getAsTemplateModel(modelRoot);
			} catch (TemplateException e) {
				eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't get function " + methodName, e), out,
						"freemarker.template.instruction.CallInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
				return TemplateProcessor.OK;
			}

			if ((model instanceof TemplateMethodModel) || (model instanceof TemplateMethodModel2)) {
				try {
					TemplateModel outModel = methodCall.getAsTemplateModel(modelRoot);
					out.write(ExpressionUtils.getAsStringOrEmpty(outModel));
				} catch (TemplateException e) {
					eventHandler.fireExceptionThrown(this, new TemplateException("Method " + methodName + " cannot be written as a String"), out,
							"freemarker.template.instruction.CallInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
				}
				return TemplateProcessor.OK;
			} else if (model instanceof TemplateFunctionModel) {
				functionModel = (TemplateFunctionModel) model;
			} else {
				eventHandler.fireExceptionThrown(this, new TemplateException("Function " + methodName + " has not been defined"), out,
						"freemarker.template.instruction.CallInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
				return TemplateProcessor.OK;
			}
		}

		// Retrieve the values of the expressions to be passed.
		ListLiteral arguments = methodCall.getArguments();

		try {
			List argValues = arguments.getModelList(modelRoot);
			functionModel.callFunction(modelRoot, out, eventHandler, argValues);
		} catch (TemplateException e) {
			eventHandler.fireExceptionThrown(this, e, out, "freemarker.template.instruction.CallInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
		}
		return TemplateProcessor.OK;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representing this instruction subtree
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("call ");
		buffer.append(methodCall);
		return buffer.toString();
	}

	/**
	 * For serialization, read this object normally, then check whether the
	 * variable expression is null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (methodCall == null) {
			throw new InvalidObjectException("Cannot create a CallInstruction with a null method");
		}
	}
}
