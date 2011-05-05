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
import java.util.Iterator;
import java.util.List;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateProcessor;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.compiler.ParseException;
import freemarker.template.compiler.TemplateBuilder;
import freemarker.template.expression.Expression;
import freemarker.template.expression.ExpressionUtils;

/**
 * An instruction representing a switch-case structure.
 * 
 * @version $Id: SwitchInstruction.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class SwitchInstruction implements Instruction, TemplateProcessor, Serializable {

	private Expression testExpression;
	private List caseInstructions;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -1924465112006963670L;

	/**
	 * Serialized form is a switch expression, an array of zero or more case
	 * instructions, and an optional default instruction.
	 * 
	 * @serialField
	 *                  switchExpression Expression the switch expression to
	 *                  test case instructions against
	 * @serialField
	 *                  caseInstructions CaseInstruction[] an array of case
	 *                  instructions to test against the switch expression
	 * @serialField
	 *                  defaultInstruction DefaultCaseInstruction an optional
	 *                  default instruction
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("switchExpression", Expression.class),
			new ObjectStreamField("caseInstructions", CaseInstruction[].class), new ObjectStreamField("defaultInstruction", DefaultCaseInstruction.class) };

	/**
	 * Constructor that takes an expression to be evaluated when testing each of
	 * the <code>case</code> statements.
	 * 
	 * @param testExpression
	 *            the expression to be tested.
	 * @throws NullPointerException
	 *             the test expression was null
	 * @throws IllegalArgumentException
	 *             the testExpression was not a scalar or number
	 */
	public SwitchInstruction(Expression testExpression) {
		if ((testExpression.getType() & (ExpressionUtils.EXPRESSION_TYPE_STRING | ExpressionUtils.EXPRESSION_TYPE_NUMBER)) == 0) {
			throw new IllegalArgumentException("Expression for switch instruction must be numeric or scalar");
		}
		this.testExpression = testExpression;
		this.caseInstructions = new ArrayList();
	}

	/**
	 * Adds a case instruction to the switch.
	 * 
	 * @param caseInstruction
	 *            a <code>CaseInstruction</code> to be evaluated.
	 */
	public void addCase(DefaultCaseInstruction caseInstruction) {
		caseInstructions.add(caseInstruction);
	}

	/**
	 * Is this an end instruction?
	 * 
	 * @return <code>false</code>, indicating that this is not an end
	 *         instruction
	 */
	public boolean isEndInstruction() {
		return false;
	}

	/**
	 * Determine what type of end instruction this is, if any.
	 * 
	 * @return <code>NONE</code>, indicating that this is not an end instruction
	 */
	public int getEndType() {
		return Instruction.NONE;
	}

	/**
	 * A {@link freemarker.template.compiler.TemplateBuilder} can call this
	 * method to have an <code>Instruction</code> call it back to be built. This
	 * implementation calls the
	 * {@link freemarker.template.compiler.TemplateBuilder#buildStatement(SwitchInstruction)}
	 * method, passing back a reference to itself. This approach is intended to
	 * make type-checking of {@link Instruction} objects unnecessary.
	 * 
	 * @param builder
	 *            the builder to be called back by this method
	 */
	public TemplateProcessor callBuilder(TemplateBuilder builder) throws ParseException {
		return builder.buildStatement(this);
	}

	/**
	 * Process this <code>&lt;switch ... &gt;</code> instruction.
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
		TemplateModel switchModel;

		try {
			switchModel = testExpression.getAsTemplateModel(modelRoot);
		} catch (TemplateException e) {
			eventHandler.fireExceptionThrown(this, e, out, "freemarker.template.instruction.SwitchInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
			return TemplateProcessor.OK;
		}

		boolean processedCase = false;
		Iterator iterator = caseInstructions.iterator();
		while (iterator.hasNext()) {
			DefaultCaseInstruction caseInstruction = (DefaultCaseInstruction) iterator.next();
			boolean processCase = false;

			// Fall through if a previous case tested true.
			if ((processedCase) || (caseInstruction.isDefault())) {
				processCase = true;
			} else {
				// Otherwise, if this case isn't the default, test it.
				Expression caseExpression = caseInstruction.getExpression();
				try {
					processCase = ExpressionUtils.isEqual(switchModel, caseExpression.getAsTemplateModel(modelRoot));
				} catch (TemplateException e) {
					eventHandler.fireExceptionThrown(this, e, out, "freemarker.template.instruction.SwitchInstruction.process", TemplateRuntimeHandler.SEVERITY_WARNING);
				}
			}

			if (processCase) {
				short result;
				result = caseInstruction.process(modelRoot, out, eventHandler);
				processedCase = true;
				if (result == TemplateProcessor.BREAK) {
					// If a break is encountered, return immediately
					return TemplateProcessor.OK;
				} else if (result != TemplateProcessor.OK) {
					return result;
				}
			}
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
		buffer.append("switch ");
		buffer.append(testExpression);

		if (caseInstructions != null) {
			buffer.append(' ');
			buffer.append(caseInstructions);
		}
		return buffer.toString();
	}

	/**
	 * For serialization, write this object as an Expression, an array of case
	 * objects, and an optional default object.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();
		List caseList = new ArrayList(caseInstructions);
		DefaultCaseInstruction defaultInstruction = null;

		if (!caseList.isEmpty()) {
			int lastIndex = caseList.size() - 1;
			defaultInstruction = (DefaultCaseInstruction) caseList.get(lastIndex);
			if (defaultInstruction.isDefault()) {
				caseList.remove(lastIndex);
			} else {
				defaultInstruction = null;
			}
		}

		CaseInstruction[] cases = new CaseInstruction[caseList.size()];
		caseList.toArray(cases);

		fields.put("switchExpression", testExpression);
		fields.put("caseInstructions", cases);
		fields.put("defaultInstruction", defaultInstruction);
		stream.writeFields();
	}

	/**
	 * For serialization, read this object as an Expression, an array of case
	 * objects, and an optional default object.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();
		CaseInstruction[] cases;
		DefaultCaseInstruction defaultInstruction;

		testExpression = (Expression) fields.get("switchExpression", null);
		cases = (CaseInstruction[]) fields.get("caseInstructions", null);
		defaultInstruction = (DefaultCaseInstruction) fields.get("defaultInstruction", null);

		if (testExpression == null) {
			throw new InvalidObjectException("Cannot create a SwitchInstruction with a null test");
		}
		if (cases == null) {
			caseInstructions = new ArrayList(1);
		} else {
			caseInstructions = new ArrayList(Arrays.asList(cases));
		}
		if (defaultInstruction != null) {
			caseInstructions.add(defaultInstruction);
		}
	}
}
