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

/**
 * Determines a character encoding to be used when retrieving a text file.
 * 
 * @version $Id: TextEncoding.java 987 2004-10-05 10:13:24Z run2000 $
 */
public interface TextEncoding {

	/**
	 * Sets the character encoding to be used when reading template files.
	 * 
	 * @param encoding
	 *            the name of the encoding to be used; this will be passed to
	 *            the constructor of <code>InputStreamReader</code>.
	 */
	public void setEncoding(String encoding);

	/**
	 * Returns the character encoding to be used when reading template files.
	 * 
	 * @return the name of the encoding to be used; this will be passed to the
	 *         constructor of <code>InputStreamReader</code>.
	 */
	public String getEncoding();

}
