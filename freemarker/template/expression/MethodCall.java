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
 *
 * 22 October 1999: This class added by Holger Arendt.
 */

package freemarker.template.expression;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import freemarker.template.FastScalar;
import freemarker.template.TemplateEventAdapter;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModel2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateRuntimeHandler;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.instruction.FunctionModel;

/**
 * A unary operator that calls a TemplateMethodModel. It associates with other
 * {@link Variable} expressions to its left.
 * 
 * @version $Id: MethodCall.java 1149 2005-10-09 07:41:19Z run2000 $
 */
public final class MethodCall implements Unary, Variable, Serializable {
	/**
	 * @serial a list of expressions to be passed to the method call as
	 *         arguments, either as template models or as strings.
	 */
	private final ListLiteral arguments;
	/**
	 * @serial The variable for which the method will be called.
	 */
	private Variable target;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 1316315814259810692L;

	/**
	 * Constructor that takes a <code>List</code> of {@link Expression}s to be
	 * evaluated at method call time.
	 * 
	 * @param arguments
	 *            a <code>List</code> of {@link Expression}s
	 * @throws NullPointerException
	 *             the argument list is null
	 */
	public MethodCall(List arguments) {
		this.arguments = new ListLiteral(arguments);
	}

	/**
	 * Retrieve the {@link Expression}s to be evaluated at call time.
	 * 
	 * @return a {@link ListLiteral} of arguments to be evaluated
	 */
	public ListLiteral getArguments() {
		return arguments;
	}

	/**
	 * Retrieve the name of this portion of the variable.
	 * 
	 * @param modelRoot
	 *            the model to be used in cases where the variable is the result
	 *            of an {@link freemarker.template.expression.Expression}.
	 * @throws TemplateException
	 *             the name could not be determined
	 */
	public String getName(TemplateWriteableHashModel modelRoot) throws TemplateException {
		return target.getName(modelRoot);
	}

	/**
	 * The {@link freemarker.template.TemplateModel} value of this method call.
	 * 
	 * @param modelRoot
	 *            the template model that will be evaluated by the expression
	 * @return the <code>TemplateModel</code> returned by the method call
	 * @throws TemplateException
	 *             the expression could not be evaluated for some reason
	 */
	public TemplateModel getAsTemplateModel(TemplateWriteableHashModel modelRoot) throws TemplateException {
		TemplateModel targetModel = target.getAsTemplateModel(modelRoot);

		if ((targetModel == null) || (targetModel.isEmpty())) {
			return null;
		}

		if (targetModel instanceof TemplateMethodModel2) {
			TemplateMethodModel2 targetMethod = (TemplateMethodModel2) targetModel;
			List argumentModels = arguments.getModelList(modelRoot);

			return targetMethod.exec(argumentModels);
		} else if (targetModel instanceof TemplateMethodModel) {
			TemplateMethodModel targetMethod = (TemplateMethodModel) targetModel;
			List argumentStrings = arguments.getValueList(modelRoot);

			return targetMethod.exec(argumentStrings);
		} else if (targetModel instanceof FunctionModel) {
			FunctionModel functionModel = (FunctionModel) targetModel;
			StringWriter sw = new StringWriter();
			TemplateRuntimeHandler eventHandler = TemplateEventAdapter.NullEventAdapter;
			List argValues = arguments.getModelList(modelRoot);

			try {
				functionModel.callFunction(modelRoot, sw, eventHandler, argValues);
				sw.close();
			} catch (IOException e) {
				throw new TemplateException(target.getName(modelRoot) + " method call failed", e);
			}

			return new FastScalar(sw.toString());
		}

		throw new TemplateException(target.getName(modelRoot) + " is not a TemplateMethodModel or a TemplateMethodModel2, it's a " + targetModel.getClass().getName());
	}

	/**
	 * A setter that calls a method model. The value to be set is passed to the
	 * method as the last parameter. The return value is ignored.
	 * 
	 * @param modelRoot
	 *            the root model, for determining context
	 * @param value
	 *            the value to be assigned
	 * @throws TemplateException
	 *             there was a problem performing the assignment
	 */
	public void setTemplateModel(TemplateWriteableHashModel modelRoot, TemplateModel value) throws TemplateException {

		TemplateModel targetModel = target.getAsTemplateModel(modelRoot);

		if (targetModel == null) {
			return;
		}

		if (targetModel instanceof TemplateMethodModel2) {
			TemplateMethodModel2 targetMethod = (TemplateMethodModel2) targetModel;
			List argumentModels = arguments.getModelList(modelRoot);

			argumentModels.add(value);
			targetMethod.exec(argumentModels);
			return;
		} else if (targetModel instanceof TemplateMethodModel) {
			TemplateMethodModel targetMethod = (TemplateMethodModel) targetModel;
			List argumentStrings = arguments.getValueList(modelRoot);

			argumentStrings.add(ExpressionUtils.getAsStringOrEmpty(value));
			targetMethod.exec(argumentStrings);
			return;
		}

		throw new TemplateException(target.getName(modelRoot) + " is not a TemplateMethodModel or a TemplateMethodModel2, it's a " + targetModel.getClass().getName());
	}

	/**
	 * Are both the target and the arguments specified?
	 * 
	 * @return <code>true</code> if both target and arguments are specified,
	 *         otherwise <code>false</code>
	 */
	public boolean isComplete() {
		return (target != null);
	}

	/**
	 * Determine the type of result that can be calculated by this expression.
	 * This is in the form of an integer constant ored together from values in
	 * the {@link ExpressionUtils} class.
	 */
	public int getType() {
		return ExpressionUtils.EXPRESSION_TYPE_VARIABLE;
	}

	/**
	 * Determine whether result calculated by this expression is a constant
	 * value.
	 */
	public boolean isConstant() {
		return false;
	}

	/**
	 * Retrieve the operator association for this operator.
	 * 
	 * @return <code>POSTFIX</code>.
	 */
	public int getAssociationType() {
		return POSTFIX;
	}

	/**
	 * Sets the target of this <code>MethodCall</code> operator.
	 * 
	 * @throws IllegalArgumentException
	 *             the target is of the wrong type for this operator.
	 */
	public void setTarget(Expression target) {
		if (!(target instanceof Variable) || !(target.isComplete())) {
			throw new IllegalArgumentException("Variable expression required for method call");
		}
		this.target = (Variable) target;
	}

	/**
	 * Retrieves the target of this <code>MethodCall</code> operator.
	 * 
	 * @return an {@link Expression} representing the
	 *         <code>TemplateMethodModel</code> to be called
	 */
	public Expression getTarget() {
		return target;
	}

	/**
	 * Return the precedence for this operator to the caller. Used for
	 * associating operators according to precedence.
	 * 
	 * @return an integer indicating the precedence of this operator
	 */
	public int getPrecedence() {
		return ExpressionBuilder.PRECEDENCE_VARIABLE;
	}

	/**
	 * For serialization purposes, resolve a deserialized instance to an
	 * instance in the expression cache.
	 */
	private Object readResolve() throws ObjectStreamException {
		if (isComplete()) {
			return ExpressionCache.cacheExpression(this);
		}
		return this;
	}

	/**
	 * For serialization, read this object normally, then check whether the
	 * argument list is null.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		if (arguments == null) {
			throw new InvalidObjectException("Cannot create a MethodCall with a null argument list");
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of this expression
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (target == null) {
			buffer.append("???");
		} else {
			buffer.append(target);
		}
		buffer.append('(');
		buffer.append(arguments);
		buffer.append(')');
		return buffer.toString();
	}

	/**
	 * Determines whether this object is equal to the given object.
	 * 
	 * @param o
	 *            the object to be compared with
	 * @return <code>true</code> if the objects are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof MethodCall)) {
			return false;
		}

		final MethodCall methodCall = (MethodCall) o;

		if (!arguments.equals(methodCall.arguments)) {
			return false;
		}
		if (target == null) {
			return methodCall.target == null;
		}
		return target.equals(methodCall.target);
	}

	/**
	 * Returns the hash code for this operator.
	 * 
	 * @return the hash code of this object
	 */
	public int hashCode() {
		int result;
		result = arguments.hashCode();
		result = 29 * result + (target != null ? target.hashCode() : 0);
		return result;
	}

	/**
	 * Resolves the current expression, possibly into a different expression
	 * object. This is loosely equivalent to the serialization protocol's
	 * <code>readResolve</code> method. Situations where this may be used are:
	 * <ul>
	 * <li>Caching frequently-used expression objects</li>
	 * <li>Evaluating constant expressions, and returning a constant reference</li>
	 * </ul>
	 */
	public Expression resolveExpression() {
		return ExpressionCache.cacheExpression(this);
	}
}
