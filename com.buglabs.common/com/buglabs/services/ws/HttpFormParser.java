/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package com.buglabs.services.ws;

import java.util.Map;

/**
 * A simple form parser.
 * @author bballantine
 *
 */
public final class HttpFormParser {

	/**
	 * Utility class.
	 */
	private HttpFormParser() {
	}
	
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
				int hb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
				ch = s.charAt(++i);
				int lb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
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
			} else /* if ((b & 0xfe) == 0xfc) */{ // 1111110x (yields 1 bit)
				sumb = b & 0x01;
				more = 5; // Expect 5 more bytes
			}
			/* No need to test if the UTF-8 encoding is well-formed */
		}
		return sbuf.toString();

	}

}
