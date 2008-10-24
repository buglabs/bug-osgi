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

import java.util.ArrayList;
import java.util.List;

/**
 * An assortment of string utilities to close the gap to J2SE 1.4
 * @author kgilmer
 *
 */
public class StringUtil {

	/**
	 * custom string splitting function as CDC/Foundation does not include
	 * String.split();
	 * 
	 * @param s
	 *            Input String
	 * @param seperator
	 * @return
	 */
	public static String[] split(String s, String seperator) {
		if (s == null || seperator == null || s.length() == 0 || seperator.length() == 0) {
			return (new String[0]);
		}

		List tokens = new ArrayList();
		String token;
		int index_a = 0;
		int index_b = 0;

		while (true) {
			index_b = s.indexOf(seperator, index_a);
			if (index_b == -1) {
				token = s.substring(index_a);

				if (token.length() > 0) {
					tokens.add(token);
				}

				break;
			}
			token = s.substring(index_a, index_b);
			token.trim();
			if (token.length() >= 0) {
				tokens.add(token);
			}
			index_a = index_b + seperator.length();
		}
		String[] str_array = new String[tokens.size()];
		for (int i = 0; i < str_array.length; i++) {
			str_array[i] = (String) (tokens.get(i));
		}
		return str_array;
	}

	/**
	 * Replaces a {@link String} within a {@link String}
	 * @param target {@link String} where replacement needs to be done
	 * @param from {@link String} to replace from
	 * @param to {@link String} to replace to
	 * @return
	 */
	public static String replace (String target, String from, String to) {   
		int start = target.indexOf(from);
		if (start == -1) return target;
		int lf = from.length();
		char [] targetChars = target.toCharArray();
		StringBuffer buffer = new StringBuffer();
		int copyFrom = 0;
		while (start != -1) {
			buffer.append (targetChars, copyFrom, start-copyFrom);
			buffer.append (to);
			copyFrom = start + lf;
			start = target.indexOf (from, copyFrom);
		}
		buffer.append (targetChars, copyFrom, targetChars.length - copyFrom);
		return buffer.toString();
	}	
}
