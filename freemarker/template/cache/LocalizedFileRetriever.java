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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Compileable;
import freemarker.template.InputSource;
import freemarker.template.TemplateException;
import freemarker.template.TextEncoding;

/**
 * <p>
 * Retrieves cacheable objects through the file system. This version performs
 * locale-based searching for files: first it looks for the most-localized file,
 * then works back to the default (base) filename. Filenames are of the format:
 * </p>
 * 
 * <pre>
 * (filename)_(language)_(country).(file extension)
 * </pre>
 * 
 * <p>
 * For instance, an HTML file encoded in Australian English would have the
 * filename: helloworld_en_AU.html
 * </p>
 * 
 * <p>
 * Based on code donated to the FreeMarker project by Jonathan Revusky as part
 * of the <a href="http://niggle.sourceforge.net/" target="_top">Niggle</a> web
 * application framework.
 * </p>
 * 
 * @version $Id: LocalizedFileRetriever.java 1130 2005-10-04 11:42:11Z run2000 $
 */
public class LocalizedFileRetriever implements CacheRetriever, TextEncoding, RegistryAccepter {

	/** The root directory where the retriever will get files. */
	protected File directoryRoot;
	/** The filename suffix required for a file to be retrieved. */
	protected String filenameSuffix;
	/** The text encoding of the template files. */
	protected String encoding;
	/** The template registry to use to instantiate objects. */
	protected TemplateRegistry registry;

	/** The localization. */
	protected Locale locale = Locale.getDefault();
	protected List localeExtensions = new ArrayList(0);

	private static final String defaultEncoding = System.getProperty("file.encoding");
	private static final Map encodingMap = getEncodings();

	private static Map getEncodings() {
		Map encoding = new HashMap();
		encoding.put("ar", "ISO-8859-6");
		encoding.put("be", "ISO-8859-5");
		encoding.put("bg", "ISO-8859-5");
		encoding.put("ca", "ISO-8859-1");
		encoding.put("cs", "ISO-8859-2");
		encoding.put("da", "ISO-8859-1");
		encoding.put("de", "ISO-8859-1");
		encoding.put("el", "ISO-8859-7");
		encoding.put("en", "ISO-8859-1");
		encoding.put("es", "ISO-8859-1");
		encoding.put("et", "ISO-8859-1");
		encoding.put("fi", "ISO-8859-1");
		encoding.put("fr", "ISO-8859-1");
		encoding.put("hr", "ISO-8859-2");
		encoding.put("hu", "ISO-8859-2");
		encoding.put("is", "ISO-8859-1");
		encoding.put("it", "ISO-8859-1");
		encoding.put("iw", "ISO-8859-8");
		encoding.put("ja", "Shift_JIS");
		encoding.put("ko", "EUC-KR"); // Requires JDK 1.1.6
		encoding.put("lt", "ISO-8859-2");
		encoding.put("lv", "ISO-8859-2");
		encoding.put("mk", "ISO-8859-5");
		encoding.put("nl", "ISO-8859-1");
		encoding.put("no", "ISO-8859-1");
		encoding.put("pl", "ISO-8859-2");
		encoding.put("pt", "ISO-8859-1");
		encoding.put("ro", "ISO-8859-2");
		encoding.put("ru", "ISO-8859-5");
		encoding.put("sh", "ISO-8859-5");
		encoding.put("sk", "ISO-8859-2");
		encoding.put("sl", "ISO-8859-2");
		encoding.put("sq", "ISO-8859-2");
		encoding.put("sr", "ISO-8859-5");
		encoding.put("sv", "ISO-8859-1");
		encoding.put("tr", "ISO-8859-9");
		encoding.put("uk", "ISO-8859-5");
		encoding.put("zh", "GB2312");
		encoding.put("zh_TW", "Big5");
		return encoding;
	}

	/** Creates new FileRetriever. */
	public LocalizedFileRetriever() {
	}

	/**
	 * Constructs a FileRetriever with a directory in which it will look for
	 * template files.
	 * 
	 * @param path
	 *            the absolute path of the directory containing templates for
	 *            this retriever
	 */
	public LocalizedFileRetriever(String path) {
		setConnection(path);
	}

	/**
	 * Creates a new FileRetriever, with a directory root.
	 * 
	 * @param rootDir
	 *            the root directory for the file system
	 */
	public LocalizedFileRetriever(File rootDir) {
		setPath(rootDir);
	}

	/**
	 * Corresponds to checkCacheDir for file-system implementations.
	 * 
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
	 */
	public void setConnection(String path) {
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
	 */
	public void setPath(File dir) {
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
			FileLocale file = getLocalizedFile(location);
			return (file != null);
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
	 * subdirectories. For localization purposes, determine when we find any
	 * localization suffixes, and remove them.
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

			// If the item is a file, see if we need to to read it.
			if (file.isFile()) {
				// If we have no filename suffix, or if we have one and this
				// file ends with it, check the file.
				if (filenameSuffix == null || filename.endsWith(filenameSuffix)) {
					visitedFiles.add(relativeDirPath + getRootFile(filename));
				}
			} else if (file.isDirectory()) {
				// If the item is a directory, recursively read it.
				readDirectory(file, relativeDirPath + filename + '/', visitedFiles);
			}
		}
	}

	/**
	 * <p>
	 * Determines when the object in the template repository was last modified.
	 * </p>
	 * 
	 * @param location
	 *            the location of the object to be tested
	 * @return milliseconds since 1970 of the time the item was last modified
	 * @throws TemplateException
	 *             is thrown whenever the item:
	 *             <ul>
	 *             <li>does not exist</li>
	 *             <li>is the wrong type (eg. a directory, not a file)</li>
	 *             <li>has an invalid file suffix</li>
	 *             </ul>
	 */
	public long lastModified(String location) throws TemplateException {
		FileLocale file;

		if (!isSuffixValid(location)) {
			throw new TemplateException("Invalid suffix in filename \"" + location + '"');
		}

		file = getLocalizedFile(location);

		if (file == null) {
			throw new TemplateException('"' + location + "\" doesn't exist");
		}

		if (!file.cFile.isFile()) {
			throw new TemplateException('"' + file.cFile.getAbsolutePath() + "\" is a directory");
		}

		return file.cFile.lastModified();
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
	 * Converts a cache element name to a <code>File</code>.
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
		FileLocale file = getLocalizedFile(location);

		try {
			FileInputStream inputStream = new FileInputStream(file.cFile);
			Compileable template = (Compileable) registry.getTemplate(type);

			if (encoding == null) {
				String localeEncoding = getCharset(file.cLocale);
				template.compile(new InputSource(inputStream, localeEncoding));
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
	 *         constructor of <code>InputStreamReader</code>.
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Sets a template registry implementation to use when creating new
	 * templates.
	 * 
	 * @param cRegistry
	 *            the registry to be used for creating new objects
	 */
	public void setTemplateRegistry(TemplateRegistry cRegistry) {
		registry = cRegistry;
	}

	/**
	 * Retrieves the current TemplateRegistry in use.
	 * 
	 * @return the registry currently in use when creating new objects
	 */
	public TemplateRegistry getTemplateRegistry() {
		return registry;
	}

	/**
	 * Sets the locale to use when retrieving files.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
		localeExtensions = getLocaleExtensions(locale);
	}

	/**
	 * Retrieves the locale used when retrieving files.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Creates a list of locales and associated filenames to use when searching
	 * for localized files.
	 */
	protected List getLocaleExtensions(Locale locale) {
		List cLocales = new ArrayList(5);
		String variant = locale.getVariant();
		String country = locale.getCountry();
		String lang = locale.getLanguage();
		LocaleMap cMap;

		// Levels of full specification. We try them in order.
		if (variant.length() > 0) {
			cMap = new LocaleMap();
			cMap.aName = '_' + lang + '_' + country + '_' + variant;
			cMap.cLocale = locale;
			cLocales.add(cMap);
		}
		if (country.length() > 0) {
			cMap = new LocaleMap();
			cMap.aName = '_' + lang + '_' + country;
			cMap.cLocale = new Locale(locale.getLanguage(), locale.getCountry());
			cLocales.add(cMap);
		}
		if (lang.length() > 0) {
			cMap = new LocaleMap();
			cMap.aName = '_' + lang;
			cMap.cLocale = new Locale(locale.getLanguage(), "");
			cLocales.add(cMap);
		}
		cMap = new LocaleMap();
		cMap.aName = "";
		cMap.cLocale = Locale.getDefault();
		cLocales.add(cMap);

		return cLocales;
	}

	/**
	 * Performs a reverse lookup of locale information: given a filename,
	 * determine whether a locale has been used, and if so, strips it back to
	 * the root filename.
	 */
	protected String getRootFile(String aFilename) {
		int dotIndex = aFilename.lastIndexOf('.');
		String basename;
		String extension;
		Iterator iFile = localeExtensions.iterator();
		String aLocale;

		if (dotIndex > 0) {
			basename = aFilename.substring(0, dotIndex);
			extension = aFilename.substring(dotIndex);
		} else {
			basename = aFilename;
			extension = "";
		}

		while (iFile.hasNext()) {
			aLocale = ((LocaleMap) iFile.next()).aName;
			if (basename.endsWith(aLocale)) {
				return basename.substring(0, basename.length() - aLocale.length()) + extension;
			}
		}
		return aFilename;
	}

	/**
	 * Given a base filename, get a localized version, if one is available.
	 * Searches for the most specific localized version first, then works back
	 * to least specific.
	 */
	protected FileLocale getLocalizedFile(String aFilename) throws TemplateException {
		Iterator iFile = localeExtensions.iterator();
		File cFile;
		FileLocale cFileLocale;
		LocaleMap cMap;

		while (iFile.hasNext()) {
			cMap = (LocaleMap) iFile.next();
			cFile = nameToFile(getFilenameFromLocale(aFilename, cMap));
			if (cFile.exists()) {
				cFileLocale = new FileLocale();
				cFileLocale.cFile = cFile;
				cFileLocale.cLocale = cMap.cLocale;
				return cFileLocale;
			}
		}
		return null;
	}

	/**
	 * Given a base filename, and a LocaleMap entry, work out what the filename
	 * should be.
	 */
	protected String getFilenameFromLocale(String aFilename, LocaleMap cLocale) {
		int dotIndex = aFilename.lastIndexOf('.');
		String basename;
		String extension;

		if (dotIndex > 0) {
			basename = aFilename.substring(0, dotIndex);
			extension = aFilename.substring(dotIndex);
		} else {
			basename = aFilename;
			extension = "";
		}
		return basename + cLocale.aName + extension;
	}

	/**
	 * Gets the preferred charset for the given locale, or null if the locale is
	 * not recognized.
	 * 
	 * @param loc
	 *            the locale
	 * @return the preferred charset
	 */
	static public String getCharset(Locale loc) {
		String charset;

		// Try for a full name match (may include country)
		charset = (String) encodingMap.get(loc.toString());
		if (charset != null)
			return charset;

		// If a full name didn't match, try just the language
		charset = (String) encodingMap.get(loc.getLanguage());

		// tweaked so it doesn't return null.
		return charset != null ? charset : defaultEncoding;
	}

	/**
	 * Holds a name to locale mapping
	 */
	static class LocaleMap {
		protected String aName;
		protected Locale cLocale;
	}

	/**
	 * Holds a file and the locale associated with it.
	 */
	static class FileLocale {
		protected File cFile;
		protected Locale cLocale;
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
		if (locale != null) {
			buffer.append(", locale: ");
			buffer.append(locale);
		}
		return buffer.toString();
	}
}
