/*
 * FreeMarker: a tool that allows Java programs to generate HTML
 * output using templates.
 * Copyright (C) 1998-2005 Benjamin Geer
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
package freemarker.template;

import java.io.InputStream;
import java.io.Reader;

/**
 * Provides an input stream or character stream to be compiled into a FM-Classic
 * template. Similar in concept to the SAX library's <code>InputSource</code>
 * class.
 * 
 * @version $Id: InputSource.java 1152 2005-10-09 08:48:30Z run2000 $
 * @since 1.9
 */
public class InputSource {
	/** An input stream from which a template can be compiled. */
	protected InputStream stream;
	/** A specific character encoding for the input stream. */
	protected String encoding;
	/** A reader from which a template can be compiled. */
	protected Reader reader;

	/**
	 * Create an empty InputSource.
	 */
	public InputSource() {
	}

	/**
	 * Create an InputSource with the supplied input stream.
	 * 
	 * @param stream
	 *            the input stream to be provided by this InputSource
	 */
	public InputSource(InputStream stream) {
		this.stream = stream;
	}

	/**
	 * Create an InputSource with the supplied input stream and character
	 * encoding.
	 * 
	 * @param stream
	 *            the input stream to be provided by this InputSource
	 * @param encoding
	 *            the character encoding for this input stream
	 */
	public InputSource(InputStream stream, String encoding) {
		this.stream = stream;
		this.encoding = encoding;
	}

	/**
	 * Create an InputSource with the supplied reader.
	 * 
	 * @param reader
	 *            the reader to be provided by this InputSource
	 */
	public InputSource(Reader reader) {
		this.reader = reader;
	}

	/**
	 * Get the input stream provided by this InputSource.
	 * 
	 * @return the input stream for this InputSource
	 */
	public InputStream getInputStream() {
		return stream;
	}

	/**
	 * Set the input stream to be provided by this InputSource.
	 * 
	 * @param stream
	 *            the input stream for this InputSource
	 */
	public void setInputStream(InputStream stream) {
		this.stream = stream;
	}

	/**
	 * Get the character encoding for the input stream provided by this
	 * InputSource.
	 * 
	 * @return the character encoding for the input stream
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Set the character encoding for the input stream provided by this
	 * InputSource.
	 * 
	 * @param encoding
	 *            the character encoding for the input source
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Get the reader provided by this InputSource.
	 * 
	 * @return the reader for this InputSource
	 */
	public Reader getReader() {
		return reader;
	}

	/**
	 * Set the reader to be provided by this InputSource.
	 * 
	 * @param reader
	 *            the reader for this InputSource
	 */
	public void setReader(Reader reader) {
		this.reader = reader;
	}
}
