package com.buglabs.services.ws;

import java.util.Map;

/*
 * A simple form parser
 * 
 * @author BBalantine
 * 
 */
public class HttpFormParser {

	
	/**
	 * @input param_string - ex. key1=value1&key2=value2
	 * @input map
	 * @return map
	 */
	static protected Map parseParamString(String param_string, Map param_map) {
		String k = "", v = "";
		char mode = 'k';
		int len = param_string.length();
		for (int n = 0; n != len; ++n) {
			char c = param_string.charAt(n);
			if (c == '&') {
				param_map.put(unescape(k), unescape(v));
				k = v = "";
				mode = 'k';
			} else if (c == '=') {
				mode = 'v';
			} else {
				if (mode == 'k') {
					k += c;
				} else if (mode == 'v') {
					v += c;
				}
			}
			if (n == len - 1 && k.length() > 0 && v.length() > 0)
				param_map.put(unescape(k), unescape(v));

		}
		return param_map;
	}

	static protected String unescape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int l = s.length();
		int ch = -1;
		int b, sumb = 0;
		for (int i = 0, more = -1; i < l; i++) {
			/* Get next byte b from URL segment s */
			switch (ch = s.charAt(i)) {
			case '%':
				ch = s.charAt(++i);
				int hb = (Character.isDigit((char) ch) ? ch - '0'
						: 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
				ch = s.charAt(++i);
				int lb = (Character.isDigit((char) ch) ? ch - '0'
						: 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
				b = (hb << 4) | lb;
				break;
			case '+':
				b = ' ';
				break;
			default:
				b = ch;
			}
			/* Decode byte b as UTF-8, sumb collects incomplete chars */
			if ((b & 0xc0) == 0x80) { // 10xxxxxx (continuation byte)
				sumb = (sumb << 6) | (b & 0x3f); // Add 6 bits to sumb
				if (--more == 0)
					sbuf.append((char) sumb); // Add char to sbuf
			} else if ((b & 0x80) == 0x00) { // 0xxxxxxx (yields 7 bits)
				sbuf.append((char) b); // Store in sbuf
			} else if ((b & 0xe0) == 0xc0) { // 110xxxxx (yields 5 bits)
				sumb = b & 0x1f;
				more = 1; // Expect 1 more byte
			} else if ((b & 0xf0) == 0xe0) { // 1110xxxx (yields 4 bits)
				sumb = b & 0x0f;
				more = 2; // Expect 2 more bytes
			} else if ((b & 0xf8) == 0xf0) { // 11110xxx (yields 3 bits)
				sumb = b & 0x07;
				more = 3; // Expect 3 more bytes
			} else if ((b & 0xfc) == 0xf8) { // 111110xx (yields 2 bits)
				sumb = b & 0x03;
				more = 4; // Expect 4 more bytes
			} else /*if ((b & 0xfe) == 0xfc)*/{ // 1111110x (yields 1 bit)
				sumb = b & 0x01;
				more = 5; // Expect 5 more bytes
			}
			/* No need to test if the UTF-8 encoding is well-formed */
		}
		return sbuf.toString();

	}

}
