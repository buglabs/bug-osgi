package com.buglabs.nmea;

import java.util.ArrayList;
import java.util.List;

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
		if (s.length() == 0 || seperator.length() == 0) {
			return (new String[] {""});
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
}
