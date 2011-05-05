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

import java.io.IOException;
import java.io.InputStream;

import freemarker.template.compiler.ParseException;

/**
 * Defines an interface for three ways of compiling a template: from an
 * <tt>InputStream</tt>, and optionally, a character encoding.
 * 
 * @version $Id: Compileable.java 1130 2005-10-04 11:42:11Z run2000 $
 */
public interface Compileable {

	/**
	 * Compiles the template from an <code>InputStream</code>, using the
	 * platform's default character encoding. If the template has already been
	 * compiled, this method does nothing.
	 * 
	 * @param stream
	 *            an <code>InputStream</code> from which the template can be
	 *            read.
	 * @deprecated use the {@link #compile} method to supply source streams to
	 *             the template compiler
	 */
	public void compileFromStream(InputStream stream) throws IOException, ParseException;

	/**
	 * Compiles the template from an <code>InputStream</code>, using the
	 * specified character encoding. If the template has already been compiled,
	 * this method does nothing.
	 * 
	 * @param stream
	 *            an <code>InputStream</code> from which the template can be
	 *            read.
	 * @param encoding
	 *            the text encoding of the <code>InputStream</code>
	 * @deprecated use the {@link #compile} method to supply source streams to
	 *             the template compiler
	 */
	public void compileFromStream(InputStream stream, String encoding) throws IOException, ParseException;

	/**
	 * Compiles the template from an <code>InputSource</code>. If the template
	 * has already been compiled, this method does nothing.
	 * 
	 * @param source
	 *            an <code>InputSource</code> from which the template can be
	 *            read.
	 */
	public void compile(InputSource source) throws IOException, IllegalArgumentException;
}
