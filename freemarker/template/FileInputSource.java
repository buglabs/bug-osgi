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
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Provides a file to be compiled into a FM-Classic template.
 * 
 * @version $Id: FileInputSource.java 1155 2005-10-09 09:22:08Z run2000 $
 * @since 1.9
 */
public class FileInputSource extends InputSource {

	/**
	 * Create an empty FileInputSource.
	 */
	public FileInputSource() {
	}

	/**
	 * Create a FileInputSource with the supplied File.
	 * 
	 * @param file
	 *            the file to be provided by this input source
	 * @throws FileNotFoundException
	 *             the file could not be found
	 */
	public FileInputSource(File file) throws FileNotFoundException {
		super(new FileInputStream(file));
	}

	/**
	 * Create a FileInputSource with the supplied File and character encoding.
	 * 
	 * @param file
	 *            the file to be provided by this input source
	 * @param encoding
	 *            the character encoding for the given file
	 * @throws FileNotFoundException
	 *             the file could not be found
	 */
	public FileInputSource(File file, String encoding) throws FileNotFoundException {
		super(new FileInputStream(file), encoding);
	}

	/**
	 * Create a FileInputSource with the supplied filename.
	 * 
	 * @param filename
	 *            the file to be provided by this input source
	 * @throws FileNotFoundException
	 *             the file could not be found
	 */
	public FileInputSource(String filename) throws FileNotFoundException {
		super(new FileInputStream(filename));
	}

	/**
	 * Create a FileInputSource with the supplied filename and character
	 * encoding.
	 * 
	 * @param filename
	 *            the file to be provided by this input source
	 * @param encoding
	 *            the character encoding for the given file
	 * @throws FileNotFoundException
	 *             the file could not be found
	 */
	public FileInputSource(String filename, String encoding) throws FileNotFoundException {
		super(new FileInputStream(filename), encoding);
	}

	/**
	 * Set the File to be provided by this FileInputSource.
	 * 
	 * @param file
	 *            the file for this FileInputSource
	 * @throws FileNotFoundException
	 *             the file could not be found
	 */
	public void setFile(File file) throws FileNotFoundException {
		stream = new FileInputStream(file);
	}

	/**
	 * Set the File to be provided by this FileInputSource.
	 * 
	 * @param file
	 *            the file for this FileInputSource
	 * @param encoding
	 *            the character encoding for this file
	 * @throws FileNotFoundException
	 *             the file could not be found
	 */
	public void setFile(File file, String encoding) throws FileNotFoundException {
		stream = new FileInputStream(file);
		this.encoding = encoding;
	}

	/**
	 * Set the filename of the file to be provided by this FileInputSource.
	 * 
	 * @param filename
	 *            the file for this FileInputSource
	 * @throws FileNotFoundException
	 *             the file could not be found
	 */
	public void setFilename(String filename) throws FileNotFoundException {
		stream = new FileInputStream(filename);
	}

	/**
	 * Set the filename of the file to be provided by this FileInputSource.
	 * 
	 * @param filename
	 *            the file for this FileInputSource
	 * @param encoding
	 *            the character encoding for this file
	 * @throws FileNotFoundException
	 *             the file could not be found
	 */
	public void setFilename(String filename, String encoding) throws FileNotFoundException {
		stream = new FileInputStream(filename);
		this.encoding = encoding;
	}
}
