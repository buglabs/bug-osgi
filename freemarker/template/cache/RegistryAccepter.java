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

/**
 * Interface to allow a {@link TemplateRegistry} to be set. This is normally
 * used for {@link CacheRetriever}s that want to be able to parameterize the
 * type of {@link freemarker.template.Template} objects to create.
 * 
 * @author Nicholas Cull
 * @version $Id: RegistryAccepter.java 987 2004-10-05 10:13:24Z run2000 $
 */
public interface RegistryAccepter {

	/**
	 * Sets a template registry implementation to use when creating new
	 * templates.
	 * 
	 * @param registry
	 *            the registry to be used for creating new objects
	 */
	public void setTemplateRegistry(TemplateRegistry registry);

	/**
	 * Retrieves the current TemplateRegistry in use.
	 * 
	 * @return the registry currently in use when creating new objects
	 */
	public TemplateRegistry getTemplateRegistry();
}
