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
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;

/**
 * <p>
 * An immutable {@link freemarker.template.TemplateModel} wrapper for a
 * {@link FunctionInstruction}s. This approach allows us to add functions to the
 * data model, while still enforcing the restriction that every element of the
 * data model should implement the {@link freemarker.template.TemplateModel}
 * interface.
 * </p>
 * 
 * <p>
 * Notably, this class is a direct subclass of
 * {@link freemarker.template.TemplateModel}, rather than going through an
 * intermediate interface.
 * </p>
 * 
 * @version $Id: FunctionModel.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class FunctionModel implements Serializable, TemplateFunctionModel {
	/**
	 * @serial the function instruction to be evaluated when this model is
	 *         called
	 */
	private final FunctionInstruction function;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -3155379731690082587L;

	/**
	 * Constructor for the function model.
	 * 
	 * @param function
	 *            the function to be stored in the model
	 * @throws NullPointerException
	 *             the function is null
	 */
	public FunctionModel(FunctionInstruction function) {
		if (function == null) {
			throw new NullPointerException("The function instruction cannot be null");
		}
		this.function = function;
	}

	/**
	 * Is the function model populated?
	 * 
	 * @return <code>true</code> if there is no function instruction associated
	 *         with the model, otherwise <code>false</code>
	 */
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Calls the specified {@link FunctionInstruction}.
	 * 
	 * @param modelRoot
	 *            the root of the template model
	 * @param out
	 *            the output stream to send the results
	 * @param eventHandler
	 *            handler for any events fired
	 * @param argValues
	 *            the arguments passed to the function
	 * @throws java.io.IOException
	 *             there was an IO error writing the results
	 */
	public void callFunction(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler, List argValues) throws IOException {

		// Test whether we have a matching number of arguments
		List argumentNames = function.getArgumentNames();
		if (argValues.size() > argumentNames.size()) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Too many arguments to function call"), out, "freemarker.template.instruction.CallInstruction.process",
					TemplateRuntimeHandler.SEVERITY_WARNING);
			return;
		}

		FunctionArgumentWrapper functionWrapper;
		TemplateWriteableHashModel localModel = null;

		if (function.isLocalScope()) {
			functionWrapper = new LocalFunctionArgumentWrapper(argumentNames, argValues);
		} else {
			functionWrapper = new GlobalFunctionArgumentWrapper(argumentNames, argValues);
		}

		try {
			localModel = functionWrapper.getWrapper(modelRoot);

			// Call the function.
			function.process(localModel, out, eventHandler);

		} finally {
			functionWrapper.cleanUp(localModel);
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representing this instruction subtree
	 */
	public String toString() {
		return function.toString();
	}

	/**
	 * For serialization, read this object normally, then check whether the
	 * function instruction is null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (function == null) {
			throw new InvalidObjectException("Cannot create a FunctionModel with a null instruction");
		}
	}
}
