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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TableViewer {
	public static final int NONE = 0;

	public static final int NO_HEADER = 1;

	public static final int COLUMN_SEPARATOR = 2;

	public static final int HEADER_SEPARATOR = 4;

	private static final String CRLF = "\r\n";

	private List columns;

	private IContentProvider contentProvider;

	private ILabelProvider labelProvider;

	private final int style;

	private String columnBorder = " ";

	private IRowFilter filter;

	public TableViewer() {
		style = NONE;
	}

	public TableViewer(int style) {
		this.style = style;
	}

	public void addColumn(TableColumn column) {
		if (columns == null) {
			columns = new ArrayList();
		}

		columns.add(column);
	}

	public void setContentProvider(IContentProvider contentProvider) {
		this.contentProvider = contentProvider;

	}

	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	public void setFilter(IRowFilter filter) {
		this.filter = filter;
	}

	public void render(Writer out) throws RenderException, IOException {
		if (contentProvider == null) {
			throw new RenderException("Must create a content provider.");
		}

		if (labelProvider == null) {
			throw new RenderException("Must create a label provider.");
		}

		if ((style & COLUMN_SEPARATOR) != 0) {
			columnBorder = " | ";
		}

		// First render the table to determine column widths.
		writeTable(null);

		if ((style & NO_HEADER) == 0) {
			for (Iterator i = columns.iterator(); i.hasNext();) {
				TableColumn c = (TableColumn) i.next();
				TableColumn.pad('_', c.getName(), TableColumn.NONE, c.getMaxWidth(), makeString('_', columnBorder.length()), out);
			}
			out.write(CRLF);
		}

		if ((style & HEADER_SEPARATOR) != 0) {
			int tableWidth = 0;

			for (Iterator i = columns.iterator(); i.hasNext();) {
				tableWidth += ((TableColumn) i.next()).getMaxWidth();
				tableWidth += columnBorder.length();
			}

			TableColumn.pad('_', "", TableColumn.NONE, tableWidth - 1, "", out);
			out.write(CRLF);
		}

		// Now render the table to the writer.
		writeTable(out);
		out.flush();
	}

	private String makeString(char c, int len) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < len; ++i) {
			sb.append(c);
		}

		return sb.toString();
	}

	private void writeTable(Writer write) throws IOException {
		Object row;
		int ci;
		Writer out = write;
		StringWriter tempWriter;
		contentProvider.reset();
		while ((row = contentProvider.getNextRow()) != null) {
			ci = 0;
			if (filter != null) {
				tempWriter = new StringWriter();
				out = tempWriter;
			}

			for (Iterator i = columns.iterator(); i.hasNext();) {
				TableColumn tc = (TableColumn) i.next();
				tc.render(labelProvider.getText(row, ci), columnBorder, out);
				ci++;
			}

			if (filter != null) {
				if (filter.isMatch(out.toString()) && write != null) {
					write.write(out.toString());
					write.write(CRLF);
				}
			}

			if (out != null) {
				out.write(CRLF);
			}
		}

		if (out != null) {
			out.flush();
		}
	}

	public String getColumnBorder() {
		return columnBorder;
	}

	public void setColumnBorder(String columnBorder) {
		this.columnBorder = columnBorder;
	}
}
