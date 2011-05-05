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

package freemarker.template;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import freemarker.template.cache.Cache;
import freemarker.template.cache.Cacheable;

/**
 * <p>
 * An application or servlet can instantiate a <code>BinaryData</code> to
 * retrieve a binary file.
 * 
 * <p>
 * You can pass the filename of the binary file to the constructor, in which
 * case it is read in immediately.
 * 
 * <p>
 * To retrive the binary data, call the {@link #process} method.
 * 
 * <p>
 * To facilitate multithreading, <code>BinaryData</code> objects are immutable;
 * if you need to reload a binary file, you must make a new
 * <code>BinaryData</code> object. In most cases, it will be sufficient to let a
 * {@link freemarker.template.cache.Cache} do this for you.
 * 
 * @see FileTemplateCache
 * @version $Id: BinaryData.java 1144 2005-10-09 06:31:56Z run2000 $
 */

public class BinaryData implements Cacheable, Cloneable, Compileable, Serializable {

	/**
	 * The binary data held by this object.
	 * 
	 * @serial The binary data stored as a byte array
	 */
	protected byte[] dataArray;
	/** The cache to which this binary data object belongs (if any). */
	protected transient Cache cache;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = -17848906542414739L;

	/**
	 * Constructs an empty binary object.
	 */
	public BinaryData() {
	}

	/**
	 * Constructs a binary data object by compiling it from a file. Calls
	 * <code>compileFromFile()</code>.
	 * 
	 * @param filePath
	 *            the absolute path of the binary file to be compiled.
	 * @deprecated Use the {@link InputSource} constructor instead
	 */
	public BinaryData(String filePath) throws IOException {
		compile(new FileInputSource(filePath));
	}

	/**
	 * Constructs a BinaryData object by compiling it from a file. Calls
	 * <code>compileFromFile()</code>.
	 * 
	 * @param file
	 *            a <code>File</code> representing the binary file to be
	 *            compiled.
	 * @deprecated Use the {@link InputSource} constructor instead
	 */
	public BinaryData(File file) throws IOException {
		compile(new FileInputSource(file));
	}

	/**
	 * Constructs a template by compiling it from an <code>InputStream</code>.
	 * Calls <code>compileFromStream()</code>.
	 * 
	 * @param stream
	 *            an <code>InputStream</code> from which the template can be
	 *            read.
	 * @deprecated Use the {@link InputSource} constructor instead
	 */
	public BinaryData(InputStream stream) throws IOException {
		compile(new InputSource(stream));
	}

	/**
	 * Clones an existing <code>BinaryData</code> instance.
	 * 
	 * @param source
	 *            an <code>InputSource</code> from which the template can be
	 *            read.
	 */
	public BinaryData(InputSource source) throws IOException {
		compile(source);
	}

	/**
	 * Clones an existing <code>BinaryData</code> instance.
	 * 
	 * @param data
	 *            the <code>BinaryData</code> instance to be cloned
	 */
	public BinaryData(BinaryData data) {
		dataArray = data.dataArray;
		cache = data.cache;
	}

	/**
	 * Reads and compiles a template from a file, by getting the file's
	 * <tt>FileInputStream</tt> and using it to call
	 * <tt>compileFromStream()</tt>, using the platform's default character
	 * encoding.
	 * 
	 * @param filePath
	 *            the absolute path of the template file to be compiled.
	 * @deprecated Use the {@link #compile} method instead
	 */
	public void compileFromFile(String filePath) throws IOException {
		compile(new FileInputSource(filePath));
	}

	/**
	 * Reads and compiles a template from a file, by getting the file's
	 * <tt>FileInputStream</tt> and using it to call
	 * <tt>compileFromStream()</tt>, using the platform's default character
	 * encoding.
	 * 
	 * @param file
	 *            a <tt>File</tt> representing the template file to be compiled.
	 * @deprecated Use the {@link #compile} method instead
	 */
	public void compileFromFile(File file) throws IOException {

		if (!file.exists()) {
			throw new FileNotFoundException("Template file " + file.getName() + " not found");
		}
		if (!file.canRead()) {
			throw new IOException("Can't read from template file " + file.getName());
		}

		compile(new FileInputSource(file));
	}

	/**
	 * Compiles the template from an <tt>InputStream</tt>, using the platform's
	 * default character encoding. If the template has already been compiled,
	 * this method does nothing.
	 * 
	 * @param stream
	 *            an <tt>InputStream</tt> from which the template can be read.
	 * @deprecated Use the {@link #compile} method instead
	 */
	public void compileFromStream(InputStream stream) throws IOException {
		compile(new InputSource(stream));
	}

	/**
	 * Compiles the template from an <tt>InputStream</tt>, using the specified
	 * character encoding. If the template has already been compiled, this
	 * method does nothing.
	 * 
	 * @param stream
	 *            an <tt>InputStream</tt> from which the template can be read.
	 * @param encoding
	 *            the character encoding to use. For binary data, this does
	 *            nothing.
	 * @deprecated Use the {@link #compile} method instead
	 */
	public void compileFromStream(InputStream stream, String encoding) throws IOException {
		compile(new InputSource(stream, encoding));
	}

	/**
	 * Compiles the template from an <code>InputSource</code>. If the template
	 * has already been compiled, this method does nothing.
	 * 
	 * @param source
	 *            an <code>InputSource</code> from which the template can be
	 *            read.
	 */
	public void compile(InputSource source) throws IOException, IllegalArgumentException {

		ByteArrayOutputStream arrayStream = new ByteArrayOutputStream(8192);
		byte[] buffer = new byte[4096];
		int bytes_read;

		InputStream stream = source.getInputStream();

		if (stream == null) {
			throw new IllegalArgumentException("Cannot compile binary data from supplied InputSource");
		}

		while ((bytes_read = stream.read(buffer)) >= 0) {
			arrayStream.write(buffer, 0, bytes_read);
		}
		arrayStream.close();
		stream.close();

		synchronized (this) {
			dataArray = arrayStream.toByteArray();
		}
	}

	/**
	 * Processes the binary data file, and output the resulting binary data to
	 * an <tt>OutputStream</tt>.
	 * 
	 * @param out
	 *            an <tt>OutputStream</tt> to output the HTML to.
	 */
	public void process(OutputStream out) {
		try {
			if (dataArray != null) {
				out.write(dataArray);
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Sets the {@link freemarker.template.cache.Cache} that this object is
	 * stored in. <tt>IncludeInstruction</tt> objects will be able to request
	 * this <code>Cache</code> at run-time.
	 * 
	 * @param cache
	 *            the <code>Cache</code> that this template belongs to.
	 */
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	/**
	 * Retrieve the {@link freemarker.template.cache.Cache} that this object is
	 * stored in.
	 * 
	 * @return the <code>Cache</code> that this template belongs to.
	 */
	public Cache getCache() {
		return this.cache;
	}

	/**
	 * Clones the current <code>BinaryData</code> object.
	 * 
	 * @return a cloned instance of the current <code>BinaryData</code> object
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("BinaryData, ");
		if (dataArray == null) {
			buffer.append("no");
		} else {
			buffer.append(dataArray.length);
		}
		buffer.append(" bytes.");
		return buffer.toString();
	}
}
