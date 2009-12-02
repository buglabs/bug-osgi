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
import java.io.Serializable;
import java.io.Writer;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.expression.Expression;
import freemarker.template.expression.ExpressionUtils;
import freemarker.template.expression.Variable;

/**
 * An instruction that outputs the value of a
 * {@link freemarker.template.expression.Variable}.
 * 
 * @version $Id: VariableInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class VariableInstruction extends EmptyInstruction implements Serializable {

	/**
	 * @serial the expression to be evaluated and written to the output stream
	 */
	private final Expression expression;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 7720592044181771388L;

	/**
	 * Constructor that takes a
	 * {@link freemarker.template.expression.Expression} to be output.
	 * 
	 * @param expression
	 *            the expression to be output
	 * @throws NullPointerException
	 *             the expression was null
	 * @throws IllegalArgumentException
	 *             the expression was not a scalar nor a number
	 */
	public VariableInstruction(Expression expression) {
		if ((expression.getType() & (ExpressionUtils.EXPRESSION_TYPE_STRING | ExpressionUtils.EXPRESSION_TYPE_NUMBER)) == 0) {
			throw new IllegalArgumentException("Variable instruction must be scalar or numeric");
		}
		this.expression = expression;
	}

	/**
	 * Process this <code>${ ... }</code> instruction.
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

		String variableValue;
		TemplateModel variableModel;

		try {
			variableModel = expression.getAsTemplateModel(modelRoot);
			variableValue = ExpressionUtils.getAsStringOrEmpty(variableModel);
		} catch (TemplateException e) {
			String variableName;
			try {
				if (expression instanceof Variable) {
					variableName = ((Variable) expression).getName(modelRoot);
				} else {
					variableName = "expression";
				}
			} catch (TemplateException te) {
				eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't get value of expression, " + "or resolve its name", te), out,
						"freemarker.template.instruction.VariableInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
				return TemplateProcessor.OK;
			}
			eventHandler.fireExceptionThrown(this, new TemplateException("Couldn't get value of expression " + variableName, e), out,
					"freemarker.template.instruction.VariableInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		out.write(variableValue);
		return TemplateProcessor.OK;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representing this instruction subtree
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("output ");
		if (expression != null) {
			buffer.append(expression);
		}
		return buffer.toString();
	}
}
