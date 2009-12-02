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

import java.io.File;
import java.util.Iterator;

import freemarker.template.cache.CacheEventAdapter;
import freemarker.template.cache.CacheRetriever;
import freemarker.template.cache.Cacheable;
import freemarker.template.cache.CachingStrategy;
import freemarker.template.cache.FileRetriever;
import freemarker.template.cache.LoDWithRefreshCachingStrategy;
import freemarker.template.cache.LoadAdHocCachingStrategy;
import freemarker.template.cache.LoadOnDemandCachingStrategy;
import freemarker.template.cache.NullCachingStrategy;
import freemarker.template.cache.PreloadCachingStrategy;
import freemarker.template.cache.RegistryAccepter;
import freemarker.template.cache.TemplateRegistry;
import freemarker.template.cache.Updateable;

/**
 * <p>
 * A <code>TemplateCache</code> that loads templates from a filesystem. Given a
 * directory path, the cache assumes by default that all files in the directory
 * are templates. It can optionally be given a filename suffix for templates.
 * </p>
 * 
 * <p>
 * The default loading policy is {@link #LOAD_ON_DEMAND}: templates are loaded
 * into the cache only when requested, each template's modification date is
 * checked each time it is requested, and the periodic updates are used only to
 * remove deleted templates from the cache. If the loading policy is set to
 * {@link #PRELOAD}, all templates are loaded when the loading policy is set,
 * and all files are checked during each periodic update. If template files will
 * not be changed frequently, use {@link #PRELOAD} with a long delay value for
 * maximum performance.
 * </p>
 * 
 * <p>
 * A combination of the two above is {@link #LOAD_ON_DEMAND_WITH_REFRESH_CACHE}.
 * This loads a template on demand the first time it is requested. Subsequently,
 * it will do periodic refreshes on any templates that have been requested from
 * its cache. This saves memory if some templates are unlikely to be needed,
 * since they won't occupy memory until they are requested, while improving
 * performance over {@link #LOAD_ON_DEMAND} since it doesn't have to check the
 * file system every time a template is requested.
 * </p>
 * 
 * <p>
 * For maximum flexibility {@link #LOAD_AD_HOC} mode exists so that all
 * templates are loaded when the loading policy is set but files are not
 * refreshed periodically. Instead, one can write a client that will ask the
 * <code>FileTemplateCache</code> to update a single template via the
 * {@link freemarker.template.cache.Updateable#update(String)} method.
 * Applications with a large number of templates many of which are not
 * frequently updated will work well with {@link #LOAD_AD_HOC} mode.
 * </p>
 * 
 * <p>
 * The first argument to the <code>getItem()</code> method is interpreted as the
 * template's path relative to the cache's root directory, using a forward slash
 * (<code>/</code>) as a separator (this is to facilitate using URL path info to
 * request templates). For example, if a <code>TemplateCache</code> object was
 * made for the directory <code>templates</code>, which contains a subdirectory
 * <code>foo</code>, in which there is a template file called
 * <code>index.html</code>, you would call
 * <code>getItem("foo/index.html")</code> to retrieve that template.
 * </p>
 * 
 * <p>
 * If a second argument is specified in a call to <code>getItem()</code>, this
 * will determine the type of object to be retrieved. The types that can be
 * returned depend on the <code>TemplateRegistry</code>. Three types of object
 * are registered with the <code>TemplateRegistry</code> by default:
 * </p>
 * <ul>
 * <li><code>template</code> -- returns a {@link Template} object. This is the
 * default type to be retrieved by the cache.</li>
 * <li><code>unparsed</code> -- returns an {@link UnparsedTemplate} object. This
 * behaves like other <code>Template</code> objects, but its contents wont be
 * parsed by FM-Classic.</li>
 * <li><code>binary</code> -- returns a {@link BinaryData} object. This is
 * useful for caching other file types, such as images.</li>
 * </ul>
 * 
 * <p>
 * The owner of the cache should implement the <code>CacheListener</code>
 * interface and register itself using <code>addCacheListener()</code>.
 * </p>
 * 
 * <p>
 * If the template cannot read its cache directory, the periodic updates will be
 * cancelled until the next time a loading policy is set.
 * </p>
 * 
 * @see TemplateCache
 * @see CacheEvent
 * @see CacheListener
 * @see Updateable#update(String)
 * @version $Id: FileTemplateCache.java 987 2004-10-05 10:13:24Z run2000 $
 */
public class FileTemplateCache implements TemplateCache, TextEncoding, RegistryAccepter {

	private CachingStrategy cStrategy;
	private CacheRetriever cRetriever;
	private TemplateRegistry cTemplates;
	private int loadingPolicy;
	private CacheEventAdapter cEventHandler;
	private String aDefaultTemplateType;
	private String aTextEncoding;
	private long delay;
	private int maximumAge;

	/**
	 * Used with {@link #setLoadingPolicy} to indicate that templates should be
	 * loaded as they are requested.
	 */
	public static final int LOAD_ON_DEMAND = 0;

	/**
	 * Used with {@link #setLoadingPolicy} to indicate that templates should be
	 * preloaded.
	 */
	public static final int PRELOAD = 1;

	/**
	 * Used with {@link #setLoadingPolicy} to indicate that templates are
	 * preloaded but there is no automatic updating of them. Instead, only named
	 * templates are updated when the cache is requested to do so.
	 */
	public static final int LOAD_AD_HOC = 2;

	/**
	 * Used with {@link #setLoadingPolicy} to indicate that no files are cached.
	 */
	public static final int NULL_CACHE = 3;

	/**
	 * Used with {@link #setLoadingPolicy} to indicate that templates should be
	 * loaded as they are requested. Once loaded, they are periodically
	 * refreshed as per the {@link #PRELOAD} policy, rather than checked at each
	 * request.
	 */
	public static final int LOAD_ON_DEMAND_WITH_REFRESH_CACHE = 4;

	/**
	 * Constructs an empty <code>FileTemplateCache</code>.
	 */
	public FileTemplateCache() {
		cStrategy = new LoadOnDemandCachingStrategy();
		cRetriever = new FileRetriever();
		cEventHandler = new CacheEventAdapter();
		cTemplates = new TemplateRegistry();
		aDefaultTemplateType = "template";

		cStrategy.setCacheRetriever(cRetriever);
		cStrategy.setEventHandler(cEventHandler);
		cStrategy.setDefaultTemplate(aDefaultTemplateType);
		((RegistryAccepter) cRetriever).setTemplateRegistry(cTemplates);

		loadingPolicy = LOAD_ON_DEMAND;
	}

	/**
	 * Constructs a <code>FileTemplateCache</code> with a directory in which it
	 * will look for template files.
	 * 
	 * @param path
	 *            the absolute path of the directory containing templates for
	 *            this cache.
	 * @throws IllegalArgumentException
	 *             the root directory is null
	 */
	public FileTemplateCache(String path) {
		cStrategy = new LoadOnDemandCachingStrategy();
		cRetriever = new FileRetriever(path);
		cEventHandler = new CacheEventAdapter();
		cTemplates = new TemplateRegistry();
		aDefaultTemplateType = "template";
		delay = 5;
		maximumAge = 0;

		cStrategy.setCacheRetriever(cRetriever);
		cStrategy.setEventHandler(cEventHandler);
		cStrategy.setDefaultTemplate(aDefaultTemplateType);
		((RegistryAccepter) cRetriever).setTemplateRegistry(cTemplates);

		loadingPolicy = LOAD_ON_DEMAND;
	}

	/**
	 * Constructs a <code>FileTemplateCache</code> with a directory in which it
	 * will look for template files.
	 * 
	 * @param dir
	 *            the directory containing templates for this cache.
	 * @throws IllegalArgumentException
	 *             the root directory is null
	 */
	public FileTemplateCache(File dir) {
		cStrategy = new LoadOnDemandCachingStrategy();
		cRetriever = new FileRetriever(dir);
		cEventHandler = new CacheEventAdapter();
		cTemplates = new TemplateRegistry();
		aDefaultTemplateType = "template";
		delay = 5;
		maximumAge = 0;

		cStrategy.setCacheRetriever(cRetriever);
		cStrategy.setEventHandler(cEventHandler);
		cStrategy.setDefaultTemplate(aDefaultTemplateType);
		((RegistryAccepter) cRetriever).setTemplateRegistry(cTemplates);

		loadingPolicy = LOAD_ON_DEMAND;
	}

	/**
	 * Constructs a <code>FileTemplateCache</code> with a directory in which it
	 * will look for template files, and a delay representing the number of
	 * seconds between cache updates.
	 * 
	 * @param path
	 *            the absolute path of the directory containing templates for
	 *            this cache.
	 * @param delay
	 *            the number of seconds between cache updates.
	 */
	public FileTemplateCache(String path, long delay) {
		this(path);
		setDelay(delay);
	}

	/**
	 * Constructs a <code>FileTemplateCache</code> with a directory in which it
	 * will look for template files, and a delay representing the number of
	 * seconds between cache updates.
	 * 
	 * @param dir
	 *            the directory containing templates for this cache.
	 * @param delay
	 *            the number of seconds between cache updates.
	 */
	public FileTemplateCache(File dir, long delay) {
		this(dir);
		setDelay(delay);
	}

	/**
	 * Returns the loading policy currently in effect
	 * 
	 * @return a loading policy value
	 */
	public synchronized int getLoadingPolicy() {
		return loadingPolicy;
	}

	/**
	 * <p>
	 * Sets the loading policy for this <code>FileTemplateCache</code>. If
	 * {@link #LOAD_ON_DEMAND}, templates will be loaded as they are requested,
	 * and each template's file modification date will be checked each time it
	 * is requested. If {@link #PRELOAD}, all templates in the cache directory
	 * and its subdirectories will be loaded when the cache is started, and new
	 * templates will be added to the cache each time it is updated. If
	 * {@link #LOAD_AD_HOC}, all templates in the cache directory and its
	 * subdirectories will be loaded when the cache is created and a particular
	 * template file's modification date will be checked each time the client
	 * requests the update of that and only that template.
	 * </p>
	 * <p>
	 * Defaults to {@link #LOAD_ON_DEMAND}.
	 * </p>
	 * 
	 * @param loadingPolicy
	 *            cache mode
	 * @throws IllegalArgumentException
	 *             the caching policy is invalid
	 */
	public void setLoadingPolicy(int loadingPolicy) {
		CachingStrategy cNewStrategy;

		switch (loadingPolicy) {
		case LOAD_AD_HOC:
			cNewStrategy = new LoadAdHocCachingStrategy();
			break;
		case PRELOAD:
			cNewStrategy = new PreloadCachingStrategy();
			break;
		case LOAD_ON_DEMAND:
			cNewStrategy = new LoadOnDemandCachingStrategy();
			break;
		case NULL_CACHE:
			cNewStrategy = new NullCachingStrategy();
			break;
		case LOAD_ON_DEMAND_WITH_REFRESH_CACHE:
			cNewStrategy = new LoDWithRefreshCachingStrategy();
			break;
		default:
			throw new IllegalArgumentException("Cannot determine caching policy to be used");
		}
		cNewStrategy.setCacheRetriever(cRetriever);
		cNewStrategy.setEventHandler(cEventHandler);
		cNewStrategy.setDefaultTemplate(aDefaultTemplateType);
		cNewStrategy.setDelay(delay);
		cNewStrategy.setMaximumAge(maximumAge);

		cStrategy.stopAutoUpdate();
		cNewStrategy.startAutoUpdate();

		// Synchronized so that getLoadingPolicy won't lie to us
		synchronized (this) {
			this.loadingPolicy = loadingPolicy;
			cStrategy = cNewStrategy;
		}
	}

	/**
	 * Sets the template cache root directory.
	 * 
	 * @param path
	 *            the absolute path of the directory containing templates for
	 *            this cache.
	 * @throws IllegalArgumentException
	 *             the root directory is null
	 */
	public void setPath(String path) {
		if (path == null) {
			throw new IllegalArgumentException("Root cache path cannot be null");
		}
		cStrategy.clearCache();
		cRetriever.setConnection(path);
	}

	/**
	 * Returns the template cache root directory.
	 * 
	 * @return the absolute path of the directory containing templates for this
	 *         cache.
	 */
	public String getPath() {
		return cRetriever.getConnection();
	}

	/**
	 * Sets the template cache root directory.
	 * 
	 * @param dir
	 *            the root directory containing templates for this cache
	 * @throws IllegalArgumentException
	 *             the root directory is null
	 */
	public void setDirectory(File dir) {
		if (dir == null) {
			throw new IllegalArgumentException("Root cache directory cannot be null");
		}
		setPath(dir.toString());
	}

	/**
	 * Returns the template cache root directory.
	 * 
	 * @return the root directory containing templates for this cache
	 */
	public File getDirectory() {
		return new File(cRetriever.getConnection());
	}

	/**
	 * <p>
	 * Sets the interval between two cache updates. This is meaningful only if
	 * the cache policy is set to {@link #LOAD_ON_DEMAND},
	 * {@link #LOAD_ON_DEMAND_WITH_REFRESH_CACHE} or {@link #PRELOAD}.
	 * </p>
	 * 
	 * <p>
	 * Defaults to five seconds.
	 * </p>
	 * 
	 * @param delay
	 *            the number of seconds between cache updates
	 */
	public void setDelay(long delay) {
		this.delay = delay;
		cStrategy.setDelay(delay);
	}

	/**
	 * Returns the interval between two cache updates. This is meaningful only
	 * if the cache policy is set to {@link #LOAD_ON_DEMAND},
	 * {@link #LOAD_ON_DEMAND_WITH_REFRESH_CACHE} or {@link #PRELOAD}.
	 * 
	 * @return the number of seconds between cache updates
	 */
	public long getDelay() {
		return cStrategy.getDelay();
	}

	/**
	 * <p>
	 * Sets the maximum age a cache item can be before it is evicted from the
	 * cache. The age is determined as the number of cache updates since the
	 * item was last accessed. This is meaningful only if the cache policy is
	 * set to {@link #LOAD_ON_DEMAND} or
	 * {@link #LOAD_ON_DEMAND_WITH_REFRESH_CACHE}.
	 * 
	 * <p>
	 * Defaults to never expiring.
	 * </p>
	 * 
	 * @param age
	 *            the maximum age before an item is evicted from the cache, or 0
	 *            to indicate that items should never be evicted
	 */
	public void setMaximumAge(int age) {
		this.maximumAge = age;
		cStrategy.setMaximumAge(age);
	}

	/**
	 * Retrieves the maximum age a cache item can be before it is evicted from
	 * the cache. The age is determined as the number of cache updates since the
	 * item was last accessed. This is meaningful only if the cache policy is
	 * set to {@link #LOAD_ON_DEMAND} or
	 * {@link #LOAD_ON_DEMAND_WITH_REFRESH_CACHE}.
	 * 
	 * @return the maximum age before an item is evicted from the cache, or 0 to
	 *         indicate that items are never evicted
	 */
	public int getMaximumAge() {
		return cStrategy.getMaximumAge();
	}

	/**
	 * Sets the character encoding to be used when reading template files. If
	 * <code>null</code> is specified, the default encoding will be used.
	 * 
	 * @param encoding
	 *            the name of the encoding to be used; this will be passed to
	 *            the constructor of <code>InputStreamReader</code>.
	 */
	public void setEncoding(String encoding) {
		aTextEncoding = encoding;
		((TextEncoding) cRetriever).setEncoding(encoding);
	}

	/**
	 * Returns the character encoding to be used when reading template files. If
	 * <code>null</code> is returned, the default encoding is used.
	 * 
	 * @return the name of the encoding to be used; this will be passed to the
	 *         constructor of <code>InputStreamReader</code>.
	 */
	public String getEncoding() {
		return ((TextEncoding) cRetriever).getEncoding();
	}

	/**
	 * Sets the template suffix. If set, files that do not have this suffix will
	 * be ignored when read into the cache.
	 * 
	 * @param filenameSuffix
	 *            the optional filename suffix of template files to be read for
	 *            this cache.
	 */
	public void setFilenameSuffix(String filenameSuffix) {
		((FileRetriever) cRetriever).setFilenameSuffix(filenameSuffix);
	}

	/**
	 * Returns the template suffix. If set, files that do not have this suffix
	 * will be ignored when read into the cache.
	 * 
	 * @return the optional filename suffix of template files to be read for
	 *         this cache.
	 */
	public String getFilenameSuffix() {
		return ((FileRetriever) cRetriever).getFilenameSuffix();
	}

	/**
	 * Registers a {@link CacheListener} for this <code>Cache</code>.
	 * 
	 * @param listener
	 *            the <code>CacheListener</code> to be registered.
	 * @see CacheListener
	 */
	public void addCacheListener(CacheListener listener) {
		cEventHandler.addCacheListener(listener);
	}

	/**
	 * Unregisters a {@link CacheListener} for a <code>Cache</code>.
	 * 
	 * @param listener
	 *            the <code>CacheListener</code> to be unregistered.
	 * @see CacheListener
	 */
	public void removeCacheListener(CacheListener listener) {
		cEventHandler.removeCacheListener(listener);
	}

	/**
	 * Retrieves all the {@link CacheListener}s associated with this cache.
	 * 
	 * @return an array of <code>CacheListener</code>s
	 */
	public CacheListener[] getCacheListeners() {
		return cEventHandler.getCacheListeners();
	}

	/**
	 * Stops the updating of the cache. Normally do this immediately prior to
	 * cache destruction.
	 */
	public void stopAutoUpdate() {
		cStrategy.stopAutoUpdate();
	}

	/**
	 * Returns a list of cached files.
	 * 
	 * @return a list of cached files
	 */
	public Iterator listCachedFiles() {
		return cStrategy.listCachedFiles();
	}

	/**
	 * Update a named template if in the {@link #LOAD_AD_HOC} mode . Do nothing
	 * if in other modes.
	 * 
	 * @param name
	 *            of template to update
	 */
	public void update(String name) {
		try {
			cStrategy.update(name);
		} catch (InterruptedException e) {
			// Propagate the interrupted status up to the caller
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Update a named template if in the {@link #LOAD_AD_HOC} mode. Do nothing
	 * if in other modes.
	 * 
	 * @param name
	 *            the name of template to update
	 * @param type
	 *            the type of template to update
	 */
	public void update(String name, String type) {
		try {
			cStrategy.update(name, type);
		} catch (InterruptedException e) {
			// Propagate the interrupted status up to the caller
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Updates the cache. In {@link #LOAD_AD_HOC} mode, this does nothing.
	 */
	public void update() {
		try {
			cStrategy.update();
		} catch (InterruptedException e) {
			// Propagate the interrupted status up to the caller
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Gets the specified template from the cache, using the default template
	 * type.
	 * 
	 * @param name
	 *            a string uniquely identifying the template.
	 * @return the template corresponding to the name, or <code>null</code> if
	 *         not found.
	 * @see #setDefaultTemplate
	 */
	public Cacheable getItem(String name) {
		return cStrategy.getItem(name);
	}

	/**
	 * Gets the specified template type from the cache.
	 * 
	 * @param name
	 *            a string uniquely identifying the template.
	 * @param type
	 *            the type of template to be retrieved
	 * @return the template corresponding to the name, or <code>null</code> if
	 *         not found.
	 * @see #setDefaultTemplate
	 */
	public Cacheable getItem(String name, String type) {
		return cStrategy.getItem(name, type);
	}

	/**
	 * Gets the {@link freemarker.template.cache.CacheRetriever} currently in
	 * use.
	 * 
	 * @return the <code>CacheRetriever</code> used for retrieving templates
	 */
	public synchronized CacheRetriever getRetriever() {
		return cRetriever;
	}

	/**
	 * Sets the {@link freemarker.template.cache.CacheRetriever} to be used for
	 * the cache.
	 * 
	 * @param cRetriever
	 *            the <code>CacheRetriever</code> to be used for retrieving
	 *            templates
	 */
	public void setRetriever(CacheRetriever cRetriever) {
		if (this.cRetriever != cRetriever) {
			if (cRetriever instanceof RegistryAccepter) {
				((RegistryAccepter) cRetriever).setTemplateRegistry(cTemplates);
			}
			((TextEncoding) cRetriever).setEncoding(aTextEncoding);

			synchronized (this) {
				this.cRetriever = cRetriever;
				cStrategy.setCacheRetriever(cRetriever);
			}
		}
	}

	/**
	 * Retrieves the current {@link freemarker.template.cache.TemplateRegistry}
	 * in use.
	 * 
	 * @return the <code>TemplateRegistry</code> to used when retrieving items
	 *         to be cached
	 */
	public synchronized TemplateRegistry getTemplateRegistry() {
		return cTemplates;
	}

	/**
	 * Sets a {@link freemarker.template.cache.TemplateRegistry} implementation
	 * to use when creating new templates.
	 * 
	 * @param cRegistry
	 *            the <code>TemplateRegistry</code> to be used when retrieving
	 *            items to be cached
	 */
	public synchronized void setTemplateRegistry(TemplateRegistry cRegistry) {
		cTemplates = cRegistry;
		if (cRetriever instanceof RegistryAccepter) {
			((RegistryAccepter) cRetriever).setTemplateRegistry(cTemplates);
		}
	}

	/**
	 * Retrieves the default template type to be created when retrieving items
	 * from the cache.
	 * 
	 * @return the type of template cached by default
	 */
	public String getDefaultTemplate() {
		return aDefaultTemplateType;
	}

	/**
	 * Sets the default template type to be created when retrieving items from
	 * the cache. The types that can be set depend on the
	 * {@link freemarker.template.cache.TemplateRegistry}. Three types of object
	 * are registered with the <code>TemplateRegistry</code> by default:
	 * <ul>
	 * <li><code>template</code> -- returns a {@link Template} object.</li>
	 * <li><code>unparsed</code> -- returns an {@link UnparsedTemplate} object.
	 * This behaves like other <code>Template</code> objects, but its contents
	 * wont be parsed by FM-Classic.</li>
	 * <li><code>binary</code> -- returns a {@link BinaryData} object. This is
	 * useful for caching other file types, such as images.</li>
	 * </ul>
	 * 
	 * @param aTemplateType
	 *            the type of template to be cached by default
	 */
	public void setDefaultTemplate(String aTemplateType) {
		aDefaultTemplateType = aTemplateType;
		cStrategy.setDefaultTemplate(aTemplateType);
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("FileTemplateCache, ");
		buffer.append(cStrategy);
		return buffer.toString();
	}
}
