/*
 * FreeMarker: a tool that allows Java programs to generate HTML
 * output using templates.
 * Copyright (C) 1998-2004 Benjamin Geer
 * Email: beroul@users.sourceforge.net
 *
 * 28 June 1999: Modified by Steve Chiu to ignore extra parentheses.
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

package freemarker.template.expression;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import freemarker.template.FastHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateWriteableHashModel;
import freemarker.template.compiler.ParseException;

/**
 * A TemplateParser can use this class's static {@link #buildExpression} method
 * to build a complete {@link Expression} or sub-expression from a <tt>List</tt>
 * of {@link Expression}s.
 * 
 * @version $Id: ExpressionBuilder.java 1123 2005-10-04 10:48:25Z run2000 $
 */
public final class ExpressionBuilder {

	// Precedences for each type of operator
	static final int PRECEDENCE_VARIABLE = 1;
	static final int PRECEDENCE_NEGATION = 2;
	static final int PRECEDENCE_MULTIPLICATION = 3;
	static final int PRECEDENCE_ADDITION = 4;
	static final int PRECEDENCE_COMPARISON = 5;
	static final int PRECEDENCE_EQUALITY = 6;
	static final int PRECEDENCE_AND = 7;
	static final int PRECEDENCE_OR = 8;

	// Indicates the range of precedence operators
	static final int MIN_PRECEDENCE = 1;
	static final int MAX_PRECEDENCE = 8;

	/** An empty model used when evaluating constant expressions. */
	static final TemplateWriteableHashModel emptyModel = new FastHash();

	// Error messages.
	private static final String MISSING_LEFT = "Missing expression to left of operator";
	private static final String MISSING_RIGHT = "Missing expression to right of operator";
	private static final String SYNTAX_ERROR = "Syntax error in expression";
	private static final String PARSER_ERROR = "Parser error in expression";

	/**
	 * Private constructor, indicating this class is not meant to be
	 * instantiated.
	 */
	private ExpressionBuilder() {
	}

	/**
	 * Builds an {@link Expression} or sub-{@link Expression}.
	 * 
	 * @param elements
	 *            a <code>List</code> of <code>ExpressionElements</code>.
	 * @return the complete <code>Expression</code> or sub-
	 *         <code>Expression</code>.
	 * @throws ParseException
	 *             the builder could not create a valid <code>Expression</code>
	 */
	public static Expression buildExpression(List elements) throws ParseException {

		// In descending order of operator precendence, associate each kind
		// of operator with its operands.
		int level = MIN_PRECEDENCE;
		while (elements.size() > 1 && level <= MAX_PRECEDENCE) {
			associateOperators(elements, level);
			level++;
		}

		// The result should be a one-element list containing a
		// complete Expression.
		if (elements.size() == 1) {
			Object element = elements.get(0);

			if (element instanceof Expression) {
				Expression expression = (Expression) element;
				if (expression.isComplete()) {
					return expression;
				} else {
					// If the Expression isn't complete, it's
					// because the parser gave us an operator we
					// don't know about.
					throw new ParseException(PARSER_ERROR);
				}
			} else {
				throw new ParseException(SYNTAX_ERROR);
			}
		} else {
			throw new ParseException(SYNTAX_ERROR);
		}
	}

	/**
	 * Associates operators with their operands.
	 * 
	 * @param elements
	 *            a <code>List</code> of <code>ExpressionElements</code>.
	 * @param precedence
	 *            the current precedence level.
	 * @throws ParseException
	 *             the builder could not create a valid <code>Expression</code>
	 */
	private static void associateOperators(List elements, int precedence) throws ParseException {

		ListIterator iterator = elements.listIterator();

		// Loop through the elements.
		while (iterator.hasNext()) {
			Expression element = (Expression) iterator.next();

			if (element instanceof Operator) {

				// If we find one of the operators we're looking for, and
				// it's incomplete, associate it with its operand(s).
				int elementPrecedence = ((Operator) element).getPrecedence();
				if (elementPrecedence == precedence) {
					if (element instanceof Binary) {
						Binary binary = (Binary) element;
						if (!binary.isComplete()) {
							// Skip back over the operator.
							iterator.previous();

							Expression left = getPreviousExpression(iterator);

							// Skip forward over the operator.
							iterator.next();

							Expression right = getNextExpression(iterator);

							setBinaryExpression(binary, left, right, iterator);
						}
					} else if (element instanceof Unary) {
						Unary unary = (Unary) element;
						if (!unary.isComplete()) {
							// Associate the operator with the operand
							// either to the left or to the right,
							// depending on the operator's association
							// type.
							Expression target;
							switch (unary.getAssociationType()) {
							case Unary.PREFIX:
								target = getNextExpression(iterator);
								break;
							case Unary.POSTFIX:
								// Skip back over the operator.
								iterator.previous();

								target = getPreviousExpression(iterator);

								// Skip forward over the operator.
								iterator.next();
								break;
							default:
								throw new ParseException(PARSER_ERROR);
							}
							setUnaryExpression(unary, target, iterator);
						}
					} else {
						throw new ParseException(PARSER_ERROR);
					}
				}
			}
		}
	}

	/**
	 * Sets the given unary expression to the given value. The iterator lies
	 * just beyond the unary instruction. If the value cannot be set, an
	 * exception is raised. In addition, if the expression turns out to be a
	 * constant, the expression is replaced with a constant value.
	 * 
	 * @param unary
	 *            the unary operator to be set
	 * @param target
	 *            the expression to set to the unary operator
	 * @param iterator
	 *            the current position of the iterator within the given
	 *            expression elements
	 * @throws ParseException
	 *             the target could not be set to the given unary operator, or a
	 *             constant expression could not be evaluated
	 */
	private static void setUnaryExpression(Unary unary, Expression target, ListIterator iterator) throws ParseException {

		try {
			unary.setTarget(target);
		} catch (NullPointerException e) {
			throw new ParseException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ParseException(e.getMessage(), e);
		}

		// Set the expression to the new value
		try {
			iterator.previous();
			iterator.set(unary.resolveExpression());
		} catch (TemplateException e) {
			throw new ParseException(e.getMessage(), e);
		} finally {
			iterator.next();
		}
	}

	/**
	 * Sets the given unary expression to the given left- and right-hand values.
	 * The iterator lies just beyond the unary instruction. If the value cannot
	 * be set, an exception is raised. In addition, if the expression turns out
	 * to be a constant, the expression is replaced with a constant value.
	 * 
	 * @param binary
	 *            the binary operator to be set
	 * @param left
	 *            the left hand expression of the binary operator
	 * @param right
	 *            the right hand expression of the binary operator
	 * @param iterator
	 *            the current position of the iterator within the given
	 *            expression elements
	 * @throws ParseException
	 *             the target could not be set to the given unary operator, or a
	 *             constant expression could not be evaluated
	 */
	private static void setBinaryExpression(Binary binary, Expression left, Expression right, ListIterator iterator) throws ParseException {

		try {
			binary.setLeft(left);
			binary.setRight(right);
		} catch (NullPointerException e) {
			throw new ParseException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ParseException(e.getMessage(), e);
		}

		// Set the expression to the new value
		try {
			iterator.previous();
			iterator.set(binary.resolveExpression());
		} catch (TemplateException e) {
			throw new ParseException(e.getMessage(), e);
		} finally {
			iterator.next();
		}
	}

	/**
	 * Gets the previous {@link Expression} in the list, and removes it.
	 * 
	 * @param iterator
	 *            an <code>Iterator</code> for the <code>List</code> of
	 *            <code>ExpressionElements</code>.
	 * @return the previous <code>Expression</code>.
	 * @throws ParseException
	 *             the builder could not determine a valid
	 *             <code>Expression</code>
	 */
	private static Expression getPreviousExpression(ListIterator iterator) throws ParseException {

		if (!iterator.hasPrevious()) {
			throw new ParseException(MISSING_LEFT);
		}
		Object element = iterator.previous();
		iterator.remove();
		if (element instanceof Expression) {
			return (Expression) element;
		} else {
			throw new ParseException(MISSING_LEFT);
		}
	}

	/**
	 * Gets the next {@link Expression} in the list, builds it if necessary, and
	 * removes it.
	 * 
	 * @param iterator
	 *            an <code>Iterator</code> for the <code>List</code> of
	 *            <code>ExpressionElements</code>.
	 * @return the next <code>Expression</code>.
	 * @throws ParseException
	 *             the builder could not create a valid <code>Expression</code>
	 */
	private static Expression getNextExpression(ListIterator iterator) throws ParseException {

		if (!iterator.hasNext()) {
			throw new ParseException(MISSING_RIGHT);
		}

		Object element = iterator.next();
		iterator.remove();

		if (element instanceof Expression) {
			return (Expression) element;
		} else {
			throw new ParseException(MISSING_RIGHT);
		}
	}

	/**
	 * Builds a complete {@link Variable}.
	 * 
	 * @param elements
	 *            a <code>List</code> of <code>ExpressionElements</code>.
	 * @return the complete <code>Variable</code>.
	 * @throws ParseException
	 *             the builder could not create a valid <code>Variable</code>
	 */
	public static Variable buildVariable(List elements) throws ParseException {

		Variable expression;
		int size = elements.size();

		// Associate each kind of operator with its operands.
		if (size > 1) {
			expression = associateVariableOperators(elements);
		} else if (size == 1) {
			expression = (Variable) elements.get(0);
		} else {
			throw new ParseException(SYNTAX_ERROR);
		}

		if (expression.isComplete()) {
			return expression;
		} else {
			// If the Expression isn't complete, it's
			// because the parser gave us an operator we
			// don't know about.
			throw new ParseException(PARSER_ERROR);
		}
	}

	/**
	 * Associates {@link Variable} operators with their operands. This takes
	 * advantage of the fact that all {@link Variable} operators are postfix
	 * unary operators.
	 * 
	 * @param elements
	 *            a <code>List</code> of <code>ExpressionElements</code>.
	 * @return The correctly associated variable
	 * @throws ParseException
	 *             something went wrong during the association
	 */
	private static Variable associateVariableOperators(List elements) throws ParseException {

		Iterator iterator = elements.iterator();
		Variable out;
		Object element = iterator.next();

		if (element instanceof Variable) {
			out = (Variable) element;
		} else {
			throw new ParseException(PARSER_ERROR);
		}

		// Loop through the elements.
		while (iterator.hasNext()) {
			element = iterator.next();

			// If we find one of the operators we're looking for
			// associate it with its operand.
			if (element instanceof Unary) {
				Unary unary = (Unary) element;
				try {
					unary.setTarget(out);
				} catch (NullPointerException e) {
					throw new ParseException(e.getMessage(), e);
				} catch (IllegalArgumentException e) {
					throw new ParseException(e.getMessage(), e);
				}
				try {
					out = (Variable) unary.resolveExpression();
				} catch (TemplateException e) {
					throw new ParseException(e.getMessage(), e);
				}
			} else {
				throw new ParseException(PARSER_ERROR);
			}
		}
		return out;
	}
}
