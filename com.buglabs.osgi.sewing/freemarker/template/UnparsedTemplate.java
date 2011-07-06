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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;

/**
 * <p>
 * An <code>UnparsedTemplate</code> consists only of text. No logic beyond
 * simple outputting is performed.
 * 
 * <p>
 * You can pass the filename of the template to the constructor, in which case
 * it is read immediately. Once read, the unparsed template is stored in an an
 * character array for later use.
 * 
 * <p>
 * To process the unparsed template, call the {@link #process} method, which
 * takes an optional tree of {@link TemplateModel} objects as its data model.
 * The root node of the tree must be a {@link TemplateWriteableHashModel}.
 * 
 * <p>
 * Any error messages will be included as HTML comments in the output.
 * 
 * <p>
 * To facilitate multithreading, <code>UnparsedTemplate</code> objects are
 * immutable; if you need to recompile a template, you must make a new
 * <code>UnparsedTemplate</code> object. In most cases, it will be sufficient to
 * let a {@link TemplateCache} do this for you.
 * 
 * @see TemplateCache
 * @version $Id: UnparsedTemplate.java 1177 2005-10-10 13:24:39Z run2000 $
 */

public class UnparsedTemplate extends AbstractTemplate implements Serializable {
	/**
	 * The unparsed template text.
	 * 
	 * @serial The unparsed template text
	 */
	protected char[] templateText;
	private transient int hashCode;

	/** Serialization UUID for this class. */
	private static final long serialVersionUID = 6100824836126362261L;

	/**
	 * Serialized unparsed template as a String object. This means that for the
	 * 1.2 series of JVMs, the serialized unparsed template cannot be larger
	 * than 64kB.
	 * 
	 * @serialField
	 *                  textValue String the <code>String</code> value of this
	 *                  template
	 */
	private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("textValue", String.class) };

	/**
	 * Constructs an empty unparsed template.
	 */
	public UnparsedTemplate() {
	}

	/**
	 * Constructs an unparsed template by compiling it from an InputSource.
	 * Calls <code>compile()</code>.
	 * 
	 * @param source
	 *            the source of the template file to be compiled.
	 */
	public UnparsedTemplate(InputSource source) throws IOException {
		super(source);
	}

	/**
	 * Constructs an unparsed template by compiling it from a file. Calls
	 * <code>compile()</code>.
	 * 
	 * @param filePath
	 *            the absolute path of the template file to be compiled.
	 * @deprecated use the {@link InputSource} contructor to supply source
	 *             streams to the template compiler
	 */
	public UnparsedTemplate(String filePath) throws IOException {
		super(filePath);
	}

	/**
	 * Constructs an unparsed template by compiling it from a file. Calls
	 * <code>compile()</code>.
	 * 
	 * @param file
	 *            a <code>File</code> representing the template file to be
	 *            compiled.
	 * @deprecated use the {@link InputSource} contructor to supply source
	 *             streams to the template compiler
	 */
	public UnparsedTemplate(File file) throws IOException {
		super(file);
	}

	/**
	 * Constructs an unparsed template by compiling it from an
	 * <code>InputStream</code>. Calls <code>compileFromStream()</code>.
	 * 
	 * @param stream
	 *            an <code>InputStream</code> from which the template can be
	 *            read.
	 * @deprecated use the {@link InputSource} contructor to supply source
	 *             streams to the template compiler
	 */
	public UnparsedTemplate(InputStream stream) throws IOException {
		super(stream);
	}

	/**
	 * Constructs an unparsed template by compiling it from a
	 * <code>Reader</code>. Calls <code>compileFromStream()</code>.
	 * 
	 * @param stream
	 *            a <code>Reader</code> from which the template can be read.
	 * @deprecated use the {@link InputSource} contructor to supply source
	 *             streams to the template compiler
	 */
	public UnparsedTemplate(Reader stream) throws IOException {
		super(stream);
	}

	/**
	 * Compiles the template from an <code>InputSource</code>. If the template
	 * has already been compiled, this method does nothing. Calls
	 * {@link #compileFromStream(java.io.Reader)} to perform parsing.
	 * 
	 * @param source
	 *            an <code>InputSource</code> from which the template can be
	 *            read.
	 */
	public void compile(InputSource source) throws IOException, IllegalArgumentException {

		synchronized (this) {
			if (templateText != null) {
				return;
			}
		}

		Reader reader = source.getReader();

		if (reader == null) {
			InputStream stream = source.getInputStream();
			if (stream == null) {
				throw new IllegalArgumentException("InputSource contains neither character nor byte stream");
			}
			String encoding = source.getEncoding();

			if (encoding == null) {
				reader = new InputStreamReader(stream);
			} else {
				reader = new InputStreamReader(stream, encoding);
			}
		}

		StringBuffer textBuf = new StringBuffer();
		BufferedReader br = new BufferedReader(reader);
		char cbuf[] = new char[1024];
		int nSize;

		try {
			nSize = br.read(cbuf);
			while (nSize > 0) {
				textBuf.append(cbuf, 0, nSize);
				nSize = br.read(cbuf);
			}
		} finally {
			try {
				br.close();
			} catch (IOException e1) {
			}
		}

		templateText = textBuf.toString().toCharArray();
		hashCode = 0;
	}

	/**
	 * Processes the contents of this <code>UnparsedTemplate</code> and outputs
	 * the resulting text to a <code>Writer</code>.
	 * 
	 * @param modelRoot
	 *            the root node of the data model.
	 * @param out
	 *            a <code>Writer</code> to send the output to.
	 * @param eventHandler
	 *            a <code>TemplateEventAdapter</code> for handling any events
	 *            that occur during processing.
	 * @throws IOException
	 *             an IO error occurred with the <code>Writer</code> during
	 *             processing
	 */
	public short process(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler) throws IOException {
		if (templateText != null) {
			out.write(templateText);
		}
		return TemplateProcessor.OK;
	}

	/**
	 * Processes the template, using data from a template model, and outputs the
	 * resulting text to a <code>Writer</code>.
	 * 
	 * @param modelRoot
	 *            the root node of the data model.
	 * @param out
	 *            a <code>Writer</code> to output the text to.
	 */
	public void process(TemplateWriteableHashModel modelRoot, Writer out) throws IOException {
		if (templateText != null) {
			out.write(templateText);
		}
	}

	/**
	 * Processes the template, using an empty data model, and outputs the
	 * resulting text to a <code>Writer</code>.
	 * 
	 * @param out
	 *            a <code>Writer</code> to output the text to.
	 */
	public void process(Writer out) throws IOException {
		if (templateText != null) {
			out.write(templateText);
		}
	}

	/**
	 * Return the String value of this object
	 * 
	 * @return the template text, if any, of this object
	 */
	public String toString() {
		if (templateText == null) {
			return "null";
		}
		return new String(templateText);
	}

	/**
	 * Tests this object for equality with the given object.
	 * 
	 * @param o
	 *            the object to be compared against
	 * @return <code>true</code> if the two objects are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UnparsedTemplate)) {
			return false;
		}

		final UnparsedTemplate unparsedTemplate = (UnparsedTemplate) o;
		return Arrays.equals(templateText, unparsedTemplate.templateText);
	}

	/**
	 * Retrieve the hash code for this object
	 * 
	 * @return a hash value corresponding to the value of this object
	 */
	public int hashCode() {
		if (hashCode == 0) {
			if (templateText == null) {
				hashCode = 3;
			}
			hashCode = new String(templateText).hashCode() + 3;
		}
		return hashCode;
	}

	/**
	 * For serialization, write this object as a String.
	 * 
	 * @param stream
	 *            the output stream to write this object to
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		ObjectOutputStream.PutField fields = stream.putFields();

		if (templateText == null) {
			fields.put("textValue", null);
		} else {
			fields.put("textValue", new String(templateText));
		}
		stream.writeFields();
	}

	/**
	 * For serialization, read this object as a String.
	 * 
	 * @param stream
	 *            the input stream to read serialized objects from
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

		ObjectInputStream.GetField fields = stream.readFields();
		String value = (String) fields.get("textValue", null);

		// Recreate the original value
		if (value != null) {
			templateText = value.toCharArray();
		}
	}
}
