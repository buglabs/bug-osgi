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

package freemarker.template.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import freemarker.template.Compileable;
import freemarker.template.InputSource;
import freemarker.template.TemplateException;
import freemarker.template.TextEncoding;

/**
 * Retrieves cacheable objects through the file system. This is the default
 * retriever for <code>FileTemplateCache</code>.
 * 
 * @version $Id: FileRetriever.java 1130 2005-10-04 11:42:11Z run2000 $
 */
public class FileRetriever implements CacheRetriever, TextEncoding, RegistryAccepter {

	/** The root directory where the retriever will get files. */
	protected File directoryRoot;
	/** The filename suffix required for a file to be retrieved. */
	protected String filenameSuffix;
	/** The text encoding of the template files. */
	protected String encoding;
	/** The template registry to use to instantiate objects. */
	protected TemplateRegistry registry;

	/** Creates new FileRetriever. */
	public FileRetriever() {
	}

	/**
	 * Constructs a FileRetriever with a directory in which it will look for
	 * template files.
	 * 
	 * @param path
	 *            the absolute path of the directory containing templates for
	 *            this retriever
	 * @throws IllegalArgumentException
	 *             the root directory is null
	 */
	public FileRetriever(String path) {
		setConnection(path);
	}

	/**
	 * Creates a new FileRetriever, with a directory root.
	 * 
	 * @param rootDir
	 *            the root directory for the file system
	 * @throws IllegalArgumentException
	 *             the root directory is null
	 */
	public FileRetriever(File rootDir) {
		setPath(rootDir);
	}

	/**
	 * Corresponds to checkCacheDir for file-system implementations.
	 * 
	 * @return <code>true</code> if the connection is ok, otherwise
	 *         <code>false</code>
	 * @throws TemplateException
	 *             the directory no longer exists, or is not a directory
	 */
	public boolean connectionOk() throws TemplateException {
		if (directoryRoot == null) {
			throw new TemplateException("Root directory is not defined");
		}
		if (!directoryRoot.isDirectory()) {
			throw new TemplateException('"' + directoryRoot.getAbsolutePath() + "\" is not a directory or does not exist");
		}
		return true;
	}

	/**
	 * Sets the root directory for this retriever.
	 * 
	 * @param path
	 *            the absolute path of the directory containing files for this
	 *            retriever.
	 * @throws IllegalArgumentException
	 *             the root directory is null
	 */
	public void setConnection(String path) {
		if (path == null) {
			throw new IllegalArgumentException("Root cache path cannot be null");
		}
		setPath(new File(path));
	}

	/**
	 * Gets the connection for this retriever. Corresponds to getPath for
	 * file-system implementations.
	 */
	public String getConnection() {
		if (directoryRoot == null) {
			return null;
		}
		return directoryRoot.toString();
	}

	/**
	 * Sets the root directory for this retriever.
	 * 
	 * @param dir
	 *            the root directory containing files for this retriever
	 * @throws IllegalArgumentException
	 *             the root directory is null
	 */
	public void setPath(File dir) {
		if (dir == null) {
			throw new IllegalArgumentException("Root cache directory cannot be null");
		}
		this.directoryRoot = dir;
	}

	/**
	 * Returns the root directory for this retriever.
	 * 
	 * @return the root directory containing files for this retriever
	 */
	public File getPath() {
		return directoryRoot;
	}

	/**
	 * Sets the file suffix. If set, files that do not have this suffix will be
	 * ignored when read into the cache.
	 * 
	 * @param filenameSuffix
	 *            the optional filename suffix of files to be read for this
	 *            retriever.
	 */
	public void setFilenameSuffix(String filenameSuffix) {
		this.filenameSuffix = filenameSuffix;
	}

	/**
	 * Returns the file suffix. If set, files that do not have this suffix will
	 * be ignored when read into the cache.
	 * 
	 * @return the optional filename suffix of files to be read for this
	 *         retriever.
	 */
	public String getFilenameSuffix() {
		return filenameSuffix;
	}

	/**
	 * Tests whether the object still exists in the template repository. This
	 * may be redundant. Instead, lastModified could throw an appropriate
	 * exception.
	 * 
	 * @param location
	 *            the location of the object to be tested
	 * @return <code>true</code> if the object still exists in the repository,
	 *         otherwise <code>false</code>
	 * @see #lastModified
	 */
	public boolean exists(String location) {

		try {
			File file = nameToFile(location);
			return (file.exists());
		} catch (TemplateException e) {
			return false;
		}
	}

	/**
	 * Returns a list of objects (<code>String</code>s) to pre-load the cache
	 * with.
	 * 
	 * @return a <code>List</code> of <code>String</code>s to preload the cache
	 *         with
	 */
	public List getPreloadData() throws TemplateException {
		List visitedFiles = new LinkedList();

		try {
			readDirectory(directoryRoot, "", visitedFiles);
		} catch (IOException e) {
			throw new TemplateException("Could not get preload data", e);
		}
		return visitedFiles;
	}

	/**
	 * Recursively updates the cache from the files in a (sub)directory and its
	 * subdirectories.
	 * 
	 * @param dir
	 *            the directory to be read.
	 * @param relativeDirPath
	 *            a string representing the directory's path relative to the
	 *            root cache directory.
	 * @param visitedFiles
	 *            a List of files that have been visited so far.
	 */
	protected void readDirectory(File dir, String relativeDirPath, List visitedFiles) throws IOException {
		String[] filenames = dir.list();

		if (filenames == null) {
			throw new IOException("Could not get file list from directory \"" + dir.getAbsolutePath() + '"');
		}

		// Iterate through the items in the directory.
		for (int fileNum = 0; fileNum < filenames.length; fileNum++) {
			String filename = filenames[fileNum];
			File file = new File(dir, filename);
			String elementName = relativeDirPath + filename;

			// If the item is a file, see if we need to to read it.
			if (file.isFile()) {
				// If we have no filename suffix, or if we have one and this
				// file ends with it, check the file.
				if (filenameSuffix == null || filename.endsWith(filenameSuffix)) {
					visitedFiles.add(elementName);
				}
			} else if (file.isDirectory()) {
				// If the item is a directory, recursively read it.
				readDirectory(file, elementName + '/', visitedFiles);
			}
		}
	}

	/**
	 * <p>
	 * Determines when the object in the template repository was last modified.
	 * </p>
	 * 
	 * @throws TemplateException
	 *             is thrown whenever the item:
	 *             <ul>
	 *             <li>does not exist</li>
	 *             <li>is the wrong type (eg. directory, not file)</li>
	 *             <li>has an invalid file suffix</li>
	 *             </ul>
	 */
	public long lastModified(String location) throws TemplateException {
		File file;

		if (!isSuffixValid(location)) {
			throw new TemplateException("Invalid suffix in filename \"" + location + '"');
		}

		file = nameToFile(location);

		if (!file.isFile()) {
			throw new TemplateException('"' + file.getPath() + "\" is not a file or does not exist");
		}

		return file.lastModified();
	}

	/**
	 * Determine whether the filename ends with the appropriate filename suffix.
	 * 
	 * @param name
	 *            the filename to be checked
	 * @return is the filename suffix ok?
	 * @throws TemplateException
	 *             the suffix is invalid
	 */
	protected boolean isSuffixValid(String name) throws TemplateException {
		if (!(filenameSuffix == null || name.endsWith(filenameSuffix))) {
			throw new TemplateException("The requested name, \"" + name + "\", does not have the filename suffix \"" + filenameSuffix + '"');
		}
		return true;
	}

	/**
	 * Converts a cache element name to a <tt>File</tt>.
	 * 
	 * @param name
	 *            the filename relative to the directory root of the retriever
	 * @return the fully qualified filename
	 */
	protected File nameToFile(final String name) throws TemplateException {
		String pathBuf = new String(name);

		// As a sanity check, make sure Windows users can't escape path
		// checking by using Windows file separators.
		if (File.separatorChar != '/') {
			pathBuf.replace(File.separatorChar, '/');
		}

		// Make sure the path is absolutely-positioned
		if (pathBuf.charAt(0) != '/')
			pathBuf = '/' + pathBuf;

		// Resolve occurrences of "//" in the normalized path
		while (true) {
			int index = pathBuf.indexOf("//");
			if (index < 0)
				break;
			pathBuf = pathBuf.substring(0, index) + pathBuf.substring(index + 1);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true) {
			int index = pathBuf.indexOf("/./");
			if (index < 0)
				break;
			pathBuf = pathBuf.substring(0, index) + pathBuf.substring(index + 2);
		}

		// Resolve occurrences of "/../" in the normalized path
		while (true) {
			int index = pathBuf.indexOf("/../");
			if (index < 0)
				break;
			if (index == 0) {
				// Trying to go outside our context
				throw new TemplateException("Invalid relative path found in filename \"" + name + '"');
			}
			int index2 = pathBuf.lastIndexOf('/', index - 1);
			pathBuf = pathBuf.substring(0, index2) + pathBuf.substring(index + 3);
		}

		// Remove leading '/' character prior to appending to root directory
		pathBuf = pathBuf.substring(1);

		// Replace forward slashes with the operating system's
		// file separator, if it's not a forward slash.
		if (File.separatorChar != '/') {
			pathBuf.replace('/', File.separatorChar);
		}

		return new File(directoryRoot, pathBuf);
	}

	/**
	 * Retrieves the appropriate data to be stored in the cache.
	 * 
	 * @param location
	 *            the filename, relative to the root directory, of the template
	 *            data to load
	 * @param type
	 *            the type of item to be loaded
	 * @return the template data
	 */
	public Cacheable loadData(String location, String type) throws TemplateException {
		File file = nameToFile(location);

		try {
			FileInputStream inputStream = new FileInputStream(file);
			Compileable template = (Compileable) registry.getTemplate(type);

			if (encoding == null) {
				template.compile(new InputSource(inputStream));
			} else {
				template.compile(new InputSource(inputStream, encoding));
			}
			inputStream.close();
			return (Cacheable) template;
		} catch (java.io.IOException e) {
			throw new TemplateException("Could not load data", e);
		} catch (NullPointerException e) {
			throw new TemplateException("Could not load data", e);
		}
	}

	/**
	 * Sets the character encoding to be used when reading template files.
	 * 
	 * @param encoding
	 *            the name of the encoding to be used; this will be passed to
	 *            the constructor of <tt>InputStreamReader</tt>.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Returns the character encoding to be used when reading template files.
	 * 
	 * @return the name of the encoding to be used; this will be passed to the
	 *         constructor of <tt>InputStreamReader</tt>.
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Sets a template registry implementation to use when creating new
	 * templates.
	 */
	public void setTemplateRegistry(TemplateRegistry cRegistry) {
		registry = cRegistry;
	}

	/**
	 * Gets the current template registry implementation in use.
	 */
	public TemplateRegistry getTemplateRegistry() {
		return registry;
	}

	/**
	 * Is this file retriever equal to another object?
	 * 
	 * @param o
	 *            the object to compare this object with
	 * @return <code>true</code> if the objects are equal, otherwise
	 *         <code>false</code>
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof FileRetriever))
			return false;

		final FileRetriever fileRetriever = (FileRetriever) o;

		if (directoryRoot == null ? fileRetriever.directoryRoot != null : !directoryRoot.equals(fileRetriever.directoryRoot))
			return false;
		if (encoding == null ? fileRetriever.encoding != null : !encoding.equals(fileRetriever.encoding))
			return false;
		if (filenameSuffix == null ? fileRetriever.filenameSuffix != null : !filenameSuffix.equals(fileRetriever.filenameSuffix))
			return false;
		if (registry == null ? fileRetriever.registry != null : !registry.equals(fileRetriever.registry))
			return false;

		return true;
	}

	/**
	 * Retrieve the hash code for this object
	 * 
	 * @return the hash code
	 */
	public int hashCode() {
		int result = 13;
		result = 29 * result + (directoryRoot != null ? directoryRoot.hashCode() : 0);
		result = 29 * result + (filenameSuffix != null ? filenameSuffix.hashCode() : 0);
		result = 29 * result + (encoding != null ? encoding.hashCode() : 0);
		result = 29 * result + (registry != null ? registry.hashCode() : 0);
		return result;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a <code>String</code> representation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		if (directoryRoot != null) {
			buffer.append("Root path: ");
			buffer.append(directoryRoot);
		}
		if (filenameSuffix != null) {
			buffer.append(", filename suffix: ");
			buffer.append(filenameSuffix);
		}
		if (encoding != null) {
			buffer.append(", encoding: ");
			buffer.append(encoding);
		}
		if (registry != null) {
			buffer.append(", registry: ");
			buffer.append(registry);
		}
		return buffer.toString();
	}
}
