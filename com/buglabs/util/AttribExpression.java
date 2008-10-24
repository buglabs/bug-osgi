/* Copyright (c) 2007, 2008 Bug Labs, Inc.
 * All rights reserved.
 *   
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License version  
 * 2 only, as published by the Free Software Foundation.   
 *   
 * This program is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * General Public License version 2 for more details (a copy is  
 * included at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html).   
 *   
 * You should have received a copy of the GNU General Public License  
 * version 2 along with this work; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA   
 *
 */
package com.buglabs.util;

/**
 * An XPath attribute.
 * @author kgilmer
 *
 */
class AttribExpression {
	public static final int EQUAL_OPERATOR = 1;

	public static final int NOT_EQUAL_OPERATOR = 2;

	private String name;

	private String value;

	private String tagName;

	private int operator;

	boolean exists = false;

	public AttribExpression(String exprToken) {
		String[] b;
		String[] s = StringUtil.split(exprToken, "[@");

		if (s.length == 2) {
			tagName = s[0];
			b = StringUtil.split(s[1], "=");

			if (b.length == 2) {
				name = b[0];
				value = resolveValue(b[1].substring(0, b[1].length() - 1));
				operator = EQUAL_OPERATOR;
				exists = true;
			} else {
				b = StringUtil.split(s[1], "!=");

				if (b.length == 2) {
					name = b[0];
					value = b[1];
					operator = NOT_EQUAL_OPERATOR;
					exists = true;
				}
			}
		}
	}

	private String resolveValue(String string) {
		// this may resolve a variable to a literal.

		return string.substring(1, string.length() - 1);
	}

	public String toString() {
		if (!exists) {
			return super.toString();
		}
		String op = "?";
		if (operator == EQUAL_OPERATOR) {
			op = "=";
		} else if (operator == NOT_EQUAL_OPERATOR) {
			op = "!=";
		}

		return tagName + "[@" + name + op + value + "]";
	}

	public String getTagName() {
		return tagName;
	}

	public boolean isExists() {
		return exists;
	}

	public String getName() {
		return name;
	}

	public int getOperator() {
		return operator;
	}

	public String getValue() {
		return value;
	}
}