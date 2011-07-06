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
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import freemarker.template.RootModelWrapper;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.compiler.ParseException;
import freemarker.template.compiler.TemplateBuilder;
import freemarker.template.expression.Identifier;

/**
 * <p>
 * An instruction representing a function definition. Function calls are
 * represented by the {@link CallInstruction} class.
 * </p>
 * 
 * <p>
 * Unlike other instructions, <code>FunctionInstruction</code> is not added
 * directly to the {@link freemarker.template.TemplateProcessor} chain. Instead,
 * instances are added directly to the template using the
 * {@link freemarker.template.FunctionTemplateProcessor#addFunction} method. At
 * run time, these functions can be added to the data model for speedy lookup.
 * </p>
 * 
 * <p>
 * At compile time, instances of <code>FunctionInstruction</code> are replaced
 * by {@link NOOPInstruction}, so that the
 * {@link freemarker.template.TemplateProcessor} tree doesn't have to deal with
 * null values.
 * </p>
 * 
 * @version $Id: FunctionInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class FunctionInstruction extends GenericStartInstruction implements Serializable {

	private Identifier name;
	private List argumentNames;
	private boolean localScope;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 4974797072025033790L;

	/**
	 * Serialized form is an Identifier containing the function name, an array
	 * of zero or more Identifiers containing the argument list, and a boolean
	 * indicating the scope of assigned variables. This is primarily for type
	 * correctness and avoids having to serialize a List object.
	 * 
	 * @serialField
	 *                  name Identifier the name of the function
	 * @serialField
	 *                  arguments Identifier[] a list of arguments to be
	 *                  supplied to the function
	 * @serialField
	 *                  scope a boolean indicating whether function variables
	 *                  should be in local scope (<code>true</code>) or global
	 *                  scope ( <code>false</code>)
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("name", Identifier.class), new ObjectStreamField("arguments", Identifier[].class),
			new ObjectStreamField("scope", Boolean.TYPE) };

	/**
	 * Constructor for a function definition.
	 * 
	 * @param name
	 *            the name of the function
	 * @param arguments
	 *            a List of Identifiers containing the names of each of the
	 *            arguments
	 * @throws NullPointerException
	 *             the name or argument list is null
	 */
	public FunctionInstruction(Identifier name, List arguments) {
		if (name == null) {
			throw new NullPointerException("Function name cannot be null");
		}
		if (arguments == null) {
			throw new NullPointerException("Argument list of function cannot be null");
		}
		this.name = name;
		this.argumentNames = arguments;
		this.localScope = false;
	}

	/**
	 * Constructor for a function definition.
	 * 
	 * @param name
	 *            the name of the function
	 * @param arguments
	 *            a List of Identifiers containing the names of each of the
	 *            arguments
	 * @throws NullPointerException
	 *             the name or argument list is null
	 */
	public FunctionInstruction(Identifier name, List arguments, boolean localScope) {
		if (name == null) {
			throw new NullPointerException("Function name cannot be null");
		}
		if (arguments == null) {
			throw new NullPointerException("Argument list of function cannot be null");
		}
		this.name = name;
		this.argumentNames = arguments;
		this.localScope = localScope;
	}

	/**
	 * Retrieve the function name.
	 * 
	 * @return the name of the function as a String
	 */
	public String getName() {
		return name.getName();
	}

	/**
	 * Retrieve a list of argument names.
	 * 
	 * @return a List of Identifiers representing the argument names
	 */
	public List getArgumentNames() {
		return argumentNames;
	}

	/**
	 * Is this the right kind of instruction for the given
	 * {@link EndInstruction}?
	 * 
	 * @param endInstruction
	 *            the end instruction we're testing
	 * @return <code>true</code> if the <code>EndInstruction</code> is a
	 *         function end instruction, otherwise <code>false</code>
	 */
	public boolean testEndInstruction(Instruction endInstruction) {
		return (endInstruction.getEndType() == FUNCTION_END);
	}

	/**
	 * Call the {@link freemarker.template.compiler.TemplateBuilder} with this
	 * function instruction.
	 * 
	 * @param builder
	 *            the <code>TemplateBuilder</code> to be called back
	 */
	public TemplateProcessor callBuilder(TemplateBuilder builder) throws ParseException {
		return builder.buildStatement(this);
	}

	/**
	 * Processes the contents of this <code>&lt;function ... &gt;</code> and
	 * outputs the resulting text to a <code>Writer</code>.
	 * <p>
	 * Todo: RootModelWrapper should probably be handled by FunctionModel.
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

		if (localScope) {
			RootModelWrapper localModelRoot = new RootModelWrapper(modelRoot);
			try {
				return body.process(localModelRoot, out, eventHandler);
			} finally {
				// Reset the local context to avoid circular dependencies
				localModelRoot.reset();
			}
		} else {
			return body.process(modelRoot, out, eventHandler);
		}
	}

	/**
	 * Is this function declared with global or local scope. This determines how
	 * the modelRoot context is set up.
	 * 
	 * @return <code>true</code> if this function is declared in local scope,
	 *         otherwise <code>false</code>
	 */
	public boolean isLocalScope() {
		return localScope;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representing this instruction subtree
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("function ");
		buffer.append(name);
		buffer.append(' ');
		buffer.append(argumentNames);
		buffer.append(' ');
		buffer.append(body);
		return buffer.toString();
	}

	/**
	 * For serialization, write this object as an Identifier containing the
	 * function name, an array of Identifiers containing the argument list, and
	 * a boolean indicating the scope of any variables.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();
		Identifier[] identifierArray = new Identifier[argumentNames.size()];

		argumentNames.toArray(identifierArray);

		fields.put("name", name);
		fields.put("arguments", identifierArray);
		fields.put("scope", localScope);
		stream.writeFields();
	}

	/**
	 * For serialization, read this object as an Identifier containing the
	 * function name, an array of Identifiers containing the argument list, and
	 * a boolean indicating the scope of any variables. Check whether either the
	 * name or the argument list is null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();

		name = (Identifier) fields.get("name", null);
		if (name == null) {
			throw new InvalidObjectException("Cannot create a FunctionInstruction with a null name");
		}

		Identifier[] argumentArray = (Identifier[]) fields.get("arguments", null);
		if (argumentArray == null) {
			throw new InvalidObjectException("Cannot create a ListLiteral with a null argument list");
		}
		argumentNames = new ArrayList(Arrays.asList(argumentArray));

		// Compatibility with the previous serialized form means that any
		// functions without this variable get global scope by default.
		localScope = fields.get("scope", false);
	}
}
