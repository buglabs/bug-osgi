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
package com.buglabs.tableviewer;

import java.io.IOException;
import java.io.Writer;

public class TableColumn {
	public static final int NONE = 1;

	public static int CENTER = 2;

	public static int RIGHT = 4;

	private final String name;

	private final int style;

	private int maxWidth = 0;

	public TableColumn(String name) {
		this.name = name;
		style = NONE;
		maxWidth = name.length();
	}

	public TableColumn(String name, int style) {
		this.name = name;
		this.style = style;
		maxWidth = name.length();
	}

	public String getName() {
		return name;
	}

	protected int getStyle() {
		return style;
	}

	protected int getMaxWidth() {
		return maxWidth;
	}

	public static void pad(char padChar, String text, int style, int width, String columnBorder, Writer out) throws IOException {
		StringBuffer sb = new StringBuffer();

		if ((style & NONE) != 0) {
			sb.append(text);
			int l = width - text.length();

			for (int i = 0; i < l; ++i) {
				sb.append(padChar);
			}

			sb.append(columnBorder);
			out.write(sb.toString());
		} else if ((style & CENTER) != 0) {
			int l = (width - text.length()) / 2;

			for (int i = 0; i < l; ++i) {
				sb.append(padChar);
			}
			sb.append(text);
			for (int i = 0; i < l; ++i) {
				sb.append(padChar);
			}

			if (l * 2 < (width - text.length())) {
				sb.append(padChar);
			}

			sb.append(columnBorder);
			out.write(sb.toString());
		} else if ((style & RIGHT) != 0) {
			int l = width - text.length();

			for (int i = 0; i < l; ++i) {
				sb.append(padChar);
			}
			sb.append(text);
			sb.append(columnBorder);
			out.write(sb.toString());
		}
	}

	protected void render(String text, String columnBorder, Writer out) throws IOException {
		if (text.length() > maxWidth) {
			maxWidth = text.length();
		}

		if (out != null) {
			pad(' ', text, this.style, maxWidth, columnBorder, out);
		}
	}

}
