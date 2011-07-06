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
import java.io.Writer;

import freemarker.template.TemplateException;
import freemarker.template.TemplateListModel;
import freemarker.template.TemplateListModel2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.compiler.ParseException;
import freemarker.template.compiler.TemplateBuilder;
import freemarker.template.expression.Expression;
import freemarker.template.expression.ExpressionUtils;
import freemarker.template.expression.Identifier;
import freemarker.template.expression.Variable;

/**
 * An instruction that processes a {@link freemarker.template.TemplateListModel}
 * or {@link freemarker.template.TemplateListModel2}. This can be either in the
 * form of a <code>&lt;list ... as ...&gt;</code> instruction or a
 * <code>&lt;foreach ... in ...&gt;</code> instruction.
 * 
 * @version $Id: ListInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class ListInstruction extends GenericStartInstruction {
	/**
	 * @serial the expression that evaluates to a template list model
	 */
	private final Expression listExpression;
	/**
	 * @serial the variable used to index over the list
	 */
	private final Identifier indexVariable;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -5366066980827389242L;

	/**
	 * Constructs a list/foreach instruction containing the list variable and
	 * the index variable.
	 * 
	 * @param listExpression
	 *            a variable or list expression referring to a
	 *            <code>TemplateListModel</code>.
	 * @param indexVariable
	 *            an arbitrary index variable.
	 * @throws NullPointerException
	 *             list expression or index variable are null
	 * @throws IllegalArgumentException
	 *             the list expression wasn't a list
	 */
	public ListInstruction(Expression listExpression, Identifier indexVariable) {
		if (indexVariable == null) {
			throw new NullPointerException("Index variable in list instruction cannot be null");
		}
		if ((listExpression.getType() & ExpressionUtils.EXPRESSION_TYPE_LIST) == 0) {
			throw new IllegalArgumentException("List instruction must contain a list");
		}
		this.listExpression = listExpression;
		this.indexVariable = indexVariable;
	}

	/**
	 * Is this the right kind of instruction for the given
	 * {@link EndInstruction}?
	 * 
	 * @param endInstruction
	 *            the end instruction we're testing
	 * @return <code>true</code> if the <code>EndInstruction</code> is a list
	 *         end instruction, otherwise <code>false</code>
	 */
	public boolean testEndInstruction(Instruction endInstruction) {
		return (endInstruction.getEndType() == LIST_END);
	}

	/**
	 * Call the {@link freemarker.template.compiler.TemplateBuilder} with this
	 * list instruction.
	 * 
	 * @param builder
	 *            the <code>TemplateBuilder</code> to be called back
	 */
	public TemplateProcessor callBuilder(TemplateBuilder builder) throws ParseException {
		return builder.buildStatement(this);
	}

	/**
	 * Processes the <code>&lt;list ... &gt;</code> or
	 * <code>&lt;foreach ... &gt;</code> instruction.
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

		if (body == null) {
			return TemplateProcessor.OK;
		}

		String listVariableName;
		try {
			if (listExpression instanceof Variable) {
				listVariableName = ((Variable) listExpression).getName(modelRoot);
			} else {
				listVariableName = "list literal";
			}
		} catch (TemplateException te) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't resolve name of list variable", te), out,
					"freemarker.template.instruction.ListInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		TemplateModel baseModel;
		try {
			baseModel = listExpression.getAsTemplateModel(modelRoot);
		} catch (TemplateException te) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't get referent of " + listVariableName, te), out,
					"freemarker.template.instruction.ListInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		try {
			if ((baseModel == null) || (baseModel.isEmpty())) {
				return TemplateProcessor.OK;
			}
		} catch (TemplateModelException e) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't perform list instruction on " + listVariableName, e), out,
					"freemarker.template.instruction.ListInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		ListArgumentWrapper wrapper = null;
		String indexName = indexVariable.getName();

		try {
			if (baseModel instanceof TemplateListModel2) {
				wrapper = new List2ArgumentWrapper(modelRoot, (TemplateListModel2) baseModel, indexName);
			} else if (baseModel instanceof TemplateListModel) {
				wrapper = new List1ArgumentWrapper(modelRoot, (TemplateListModel) baseModel, indexName);
			} else if (baseModel instanceof TemplateScalarModel) {
				wrapper = new ScalarArgumentWrapper(modelRoot, baseModel, indexName);
			}
		} catch (TemplateModelException e) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't initialize list " + listVariableName, e), out,
					"freemarker.template.instruction.ListInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
		}

		try {
			// Loop, setting the index to refer to each element in turn.
			short result;
			while (wrapper.next()) {
				result = body.process(wrapper, out, eventHandler);
				if (result == TemplateProcessor.BREAK) {
					break;
				} else if (result != TemplateProcessor.OK) {
					return result;
				}
			}
		} catch (TemplateModelException e) {
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't read from list " + listVariableName, e), out,
					"freemarker.template.instruction.ListInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
		} finally {
			// Clear any local variables still initialized.
			wrapper.reset();
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
		buffer.append("list ");
		buffer.append(listExpression);
		buffer.append(" as ");
		buffer.append(indexVariable);
		buffer.append(' ');
		buffer.append(body);
		return buffer.toString();
	}

	/**
	 * When deserializing this object, read the object normally, test whether
	 * the list expression or index variable is null, then recreate the
	 * transient iterator field.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		stream.defaultReadObject();
		if ((listExpression == null) || (indexVariable == null)) {
			throw new InvalidObjectException("Cannot create a ListInstruction with a null expression or index variable");
		}
	}
}
