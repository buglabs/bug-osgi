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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import freemarker.template.cache.Cache;
import freemarker.template.cache.Cacheable;

/**
 * <p>
 * A base class from which {@link Template} implementations are subclassed. This
 * class implements all the interfaces required, and provides some of the basic
 * machinery required to compile and cache a <code>Template</code>.
 * </p>
 * 
 * @see Template
 * @see UnparsedTemplate
 * @version $Id: AbstractTemplate.java 1144 2005-10-09 06:31:56Z run2000 $
 */
public abstract class AbstractTemplate implements TemplateProcessor, Cacheable, Compileable, Cloneable {

	/** The <code>Cache</code> to which this template belongs (if any). */
	protected Cache cache;

	/**
	 * Constructs an empty template.
	 */
	public AbstractTemplate() {
	}

	/**
	 * Constructs a template by compiling it from an <code>InputSource</code>.
	 * Calls {@link #compile(InputSource)}.
	 * 
	 * @param source
	 *            an <code>InputSource</code> from which the template can be
	 *            read.
	 */
	public AbstractTemplate(InputSource source) throws IOException, IllegalArgumentException {
		compile(source);
	}

	/**
	 * Constructs a template by compiling it from a file. Calls
	 * {@link #compile(InputSource)}.
	 * 
	 * @param filePath
	 *            the absolute path of the template file to be compiled.
	 * @deprecated use the {@link InputSource} contructor to supply source
	 *             streams to the template compiler
	 */
	public AbstractTemplate(String filePath) throws IOException {
		compile(new FileInputSource(filePath));
	}

	/**
	 * Constructs a template by compiling it from a file. Calls
	 * {@link #compile(InputSource)}.
	 * 
	 * @param file
	 *            a <code>File</code> representing the template file to be
	 *            compiled.
	 * @deprecated use the {@link InputSource} contructor to supply source
	 *             streams to the template compiler
	 */
	public AbstractTemplate(File file) throws IOException {
		compile(new FileInputSource(file));
	}

	/**
	 * Constructs a template by compiling it from an <code>InputStream</code>.
	 * Calls {@link #compile(InputSource)}.
	 * 
	 * @param stream
	 *            an <code>InputStream</code> from which the template can be
	 *            read.
	 * @deprecated use the {@link InputSource} contructor to supply source
	 *             streams to the template compiler
	 */
	public AbstractTemplate(InputStream stream) throws IOException {
		compile(new InputSource(stream));
	}

	/**
	 * Constructs a template by compiling it from a <code>Reader</code>. Calls
	 * {@link #compile(InputSource)}.
	 * 
	 * @param stream
	 *            a <code>Reader</code> from which the template can be read.
	 * @deprecated use the {@link InputSource} contructor to supply source
	 *             streams to the template compiler
	 */
	public AbstractTemplate(Reader stream) throws IOException {
		compile(new InputSource(stream));
	}

	/**
	 * Reads a template from a file, by getting the file's
	 * <code>FileInputStream</code> and using it to call
	 * {@link #compile(InputSource)}, using the platform's default character
	 * encoding.
	 * 
	 * @param filePath
	 *            the absolute path of the template file to be compiled.
	 * @deprecated use the {@link #compile} method to supply source streams to
	 *             the template compiler
	 */
	public void compileFromFile(String filePath) throws IOException {
		compile(new FileInputSource(filePath));
	}

	/**
	 * Reads a template from a file, by getting the file's
	 * <code>FileInputStream</code> and using it to call
	 * {@link #compile(InputSource)}, using the platform's default character
	 * encoding.
	 * 
	 * @param file
	 *            a <code>File</code> representing the template file to be
	 *            compiled.
	 * @deprecated use the {@link #compile} method to supply source streams to
	 *             the template compiler
	 */
	public void compileFromFile(File file) throws IOException {
		compile(new FileInputSource(file));
	}

	/**
	 * Reads the template from an <code>InputStream</code>, using the platform's
	 * default character encoding. If the template has already been compiled,
	 * this method does nothing. Calls {@link #compile(InputSource)} to perform
	 * parsing.
	 * 
	 * @param stream
	 *            an <code>InputStream</code> from which the template can be
	 *            read.
	 * @deprecated use the {@link #compile} method to supply source streams to
	 *             the template compiler
	 */
	public void compileFromStream(InputStream stream) throws IOException {
		compile(new InputSource(stream));
	}

	/**
	 * Compiles the template from an <code>InputStream</code>, using the
	 * specified character encoding. If the template has already been compiled,
	 * this method does nothing. Calls {@link #compile(InputSource)} to perform
	 * parsing.
	 * 
	 * @param stream
	 *            an <code>InputStream</code> from which the template can be
	 *            read.
	 * @param encoding
	 *            the character encoding of the input stream
	 * @deprecated use the {@link #compile} method to supply source streams to
	 *             the template compiler
	 */
	public void compileFromStream(InputStream stream, String encoding) throws IOException {
		compile(new InputSource(stream, encoding));
	}

	/**
	 * Compiles the template from a <code>Reader</code>. If the template has
	 * already been compiled, this method does nothing. Calls
	 * {@link #compile(InputSource)} to perform parsing.
	 * 
	 * @param stream
	 *            an <code>Reader</code> from which the template can be read.
	 * @deprecated use the {@link #compile} method to supply source streams to
	 *             the template compiler
	 */
	public void compileFromStream(Reader stream) throws IOException {
		compile(new InputSource(stream));
	}

	/**
	 * Compiles the template from an <code>InputSource</code>. If the template
	 * has already been compiled, this method does nothing.
	 * 
	 * @param source
	 *            an <code>InputSource</code> from which the template can be
	 *            read.
	 */
	public abstract void compile(InputSource source) throws IOException, IllegalArgumentException;

	/**
	 * Sets the {@link freemarker.template.cache.Cache} that this object is
	 * stored in.
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
	 * <p>
	 * Clones the current template.
	 * </p>
	 * 
	 * <p>
	 * Cloning is used in {@link freemarker.template.cache.Cache}s, whenever we
	 * need to create a new template: rather than simply creating a new
	 * Template, we ask a {@link freemarker.template.cache.TemplateRegistry} to
	 * create one for us. <code>TemplateRegistry</code> uses the clone function
	 * to take an existing template, copy it, and return the copy to the cache,
	 * where it is then populated.
	 * </p>
	 * 
	 * @return a copy of the current template.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Processes the template, using data from the template model, writing any
	 * events to the <code>TemplateEventAdapter</code>, and outputs the
	 * resulting text to a <code>Writer</code>.
	 * 
	 * @param modelRoot
	 *            the root node of the data model.
	 * @param out
	 *            a <code>Writer</code> to send the output to.
	 * @param eventHandler
	 *            a <code>TemplateEventAdapter</code> for handling any events
	 *            that occur during processing.
	 */
	public abstract short process(TemplateWriteableHashModel modelRoot, Writer out, TemplateRuntimeHandler eventHandler) throws IOException;

	/**
	 * Processes the template, using data from a template model, and outputs the
	 * resulting text to a <code>Writer</code>.
	 * 
	 * @param modelRoot
	 *            the root node of the data model. If <code>null</code>, an
	 *            empty data model is used.
	 * @param out
	 *            a <code>Writer</code> to output the text to.
	 */
	public abstract void process(TemplateWriteableHashModel modelRoot, Writer out) throws IOException;

	/**
	 * Processes the template, using an empty data model, and outputs the
	 * resulting text to a <code>Writer</code>.
	 * 
	 * @param out
	 *            a <code>Writer</code> to output the text to.
	 */
	public abstract void process(Writer out) throws IOException;

}
