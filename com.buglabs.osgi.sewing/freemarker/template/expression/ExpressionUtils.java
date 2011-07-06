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

package freemarker.template.expression;

import freemarker.template.TemplateMethodModel2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

/**
 * Utility methods for evaluating expressions. These are used in various parts
 * of the {@link freemarker.template.expression} package. They are also
 * generally useful when dealing with <code>TemplateModel</code>s directly, such
 * as when retrieving parameters from a {@link TemplateMethodModel2} parameter
 * list.
 * 
 * @author Nicholas Cull
 * @version $Id: ExpressionUtils.java 1081 2005-08-28 10:51:16Z run2000 $
 * @since 1.7.5
 */
public final class ExpressionUtils {

	/** The expression can return type String. */
	public static final int EXPRESSION_TYPE_STRING = 2;
	/** The expression can return type Number. */
	public static final int EXPRESSION_TYPE_NUMBER = 4;
	/** The expression can return type List. */
	public static final int EXPRESSION_TYPE_LIST = 8;
	/** The expression can return type Hash. */
	public static final int EXPRESSION_TYPE_HASH = 16;
	/** The expression can return type Transform. */
	public static final int EXPRESSION_TYPE_TRANSFORM = 32;
	/** The expression can return type Method. */
	public static final int EXPRESSION_TYPE_METHOD = 64;
	/** The expression is a variable, so can return anything. */
	public static final int EXPRESSION_TYPE_VARIABLE = 126;

	/**
	 * Private constructor, indicating this class is not meant to be
	 * instantiated.
	 */
	private ExpressionUtils() {
	}

	/**
	 * <p>
	 * Determines the "truth" of a given template model. This is determined as
	 * follows:
	 * </p>
	 * <ul>
	 * <li>If the template model is <code>null</code>, or is empty according to
	 * the {@link freemarker.template.TemplateModel#isEmpty} method, then it is
	 * false</li>
	 * <li>Otherwise, its true.</li>
	 * </ul>
	 * 
	 * @param model
	 *            the <code>TemplateModel</code> to be tested, possibly
	 *            <code>null</code>
	 * @return <code>true</code> if the model evaluates to true, otherwise
	 *         <code>false</code>
	 * @throws TemplateModelException
	 *             the truth of the template model could not be determined
	 */
	public static boolean isTrue(TemplateModel model) throws TemplateModelException {
		if ((model == null) || (model.isEmpty())) {
			return false;
		}
		return true;
	}

	/**
	 * <p>
	 * Determines the given TemplateModel's <code>String</code> value. This is a
	 * simple case of getting the
	 * {@link freemarker.template.TemplateScalarModel#getAsString()} value from
	 * the model, assuming it implements TemplateScalarModel. If it's a
	 * TemplateNumberModel, we return a base-10 encoding of the number value. If
	 * it's empty or <code>null</code>, return <code>null</code>.
	 * </p>
	 * 
	 * @param model
	 *            the <code>TemplateModel</code> to get the <code>String</code>
	 *            value from, possibly <code>null</code>
	 * @return the String value, or <code>null</code> if the model is empty
	 * @throws TemplateModelException
	 *             the <code>String</code> value of the template model could not
	 *             be determined
	 */
	public static String getAsString(TemplateModel model) throws TemplateModelException {

		if (model == null) {
			return null;
		}
		if (model instanceof TemplateScalarModel) {
			if (model.isEmpty()) {
				return null;
			}
			return ((TemplateScalarModel) model).getAsString();
		} else if (model instanceof TemplateNumberModel) {
			if (model.isEmpty()) {
				return null;
			}
			return Long.toString(((TemplateNumberModel) model).getAsNumber());
		}
		throw new TemplateModelException("Model is not a TemplateScalarModel or a TemplateNumberModel, it's a " + model.getClass().toString());
	}

	/**
	 * <p>
	 * Determines the given TemplateModel's <code>String</code> value. This is a
	 * simple case of getting the
	 * {@link freemarker.template.TemplateScalarModel#getAsString()} value from
	 * the model, assuming it implements TemplateScalarModel. If it's a
	 * TemplateNumberModel, we return a base-10 encoding of the number value. If
	 * it's empty or <code>null</code>, return the empty string.
	 * </p>
	 * 
	 * @param model
	 *            the <code>TemplateModel</code> to get the <code>String</code>
	 *            value from, possibly <code>null</code>
	 * @return the String value, or the empty String if the model is empty
	 * @throws TemplateModelException
	 *             the <code>String</code> value of the template model could not
	 *             be determined
	 * @since 1.9
	 */
	public static String getAsStringOrEmpty(TemplateModel model) throws TemplateModelException {
		String result = getAsString(model);
		return (result == null) ? "" : result;
	}

	/**
	 * <p>
	 * Determines the given TemplateModel's <code>long</code> value. This is a
	 * simple case of getting the
	 * {@link freemarker.template.TemplateNumberModel#getAsNumber()} value from
	 * the model, assuming it implements TemplateNumberModel. If it's empty or
	 * <code>null</code>, return 0.
	 * </p>
	 * 
	 * @param model
	 *            the <code>TemplateModel</code> to get the <code>long</code>
	 *            value from, possibly <code>null</code>
	 * @throws TemplateModelException
	 *             the <code>String</code> value of the template model could not
	 *             be determined
	 */
	public static long getAsNumber(TemplateModel model) throws TemplateModelException {
		if (model == null) {
			return 0;
		}
		try {
			if (model.isEmpty()) {
				return 0;
			}
			return ((TemplateNumberModel) model).getAsNumber();
		} catch (ClassCastException e) {
			throw new TemplateModelException("Model is not a TemplateNumberModel, it's a " + model.getClass().toString(), e);
		}
	}

	/**
	 * Determines whether both sides of an expression are equal. Can deal with
	 * <code>null</code> values on either side of the test.
	 * 
	 * @param leftModel
	 *            the left-hand <code>TemplateModel</code> to be compared,
	 *            possibly <code>null</code>
	 * @param rightModel
	 *            the right-hand <code>TemplateModel</code> to be compared,
	 *            possibly <code>null</code>
	 * @return <code>true</code> if the models are equal in value, otherwise
	 *         <code>false</code>
	 * @throws TemplateModelException
	 *             the template models could not be compared
	 */
	public static boolean isEqual(TemplateModel leftModel, TemplateModel rightModel) throws TemplateModelException {

		if ((leftModel instanceof TemplateNumberModel) && (rightModel instanceof TemplateNumberModel)) {
			long leftValue = getAsNumber(leftModel);
			long rightValue = getAsNumber(rightModel);

			return leftValue == rightValue;
		} else {
			String leftValue = getAsString(leftModel);
			String rightValue = getAsString(rightModel);

			if (leftValue == null) {
				return ((rightValue == null) || (rightValue.length() == 0));
			} else if (rightValue == null) {
				return leftValue.length() == 0;
			} else {
				return leftValue.equals(rightValue);
			}
		}
	}

	/**
	 * Compares two numeric expressions. Can deal with <code>null</code> values
	 * on either side of the comparison.
	 * 
	 * @param leftModel
	 *            the left-hand <code>TemplateModel</code> to be compared,
	 *            possibly <code>null</code>
	 * @param rightModel
	 *            the right-hand <code>TemplateModel</code> to be compared,
	 *            possibly <code>null</code>
	 * @return &lt;0 if the left model is less than the right model, &gt;0 if
	 *         the left model is greater than the right model, or == 0 if the
	 *         models are the same
	 * @throws TemplateModelException
	 *             the template models could not be compared
	 */
	public static long compareNumbers(TemplateModel leftModel, TemplateModel rightModel) throws TemplateModelException {

		long leftValue = getAsNumber(leftModel);
		long rightValue = getAsNumber(rightModel);

		return leftValue - rightValue;
	}
}
