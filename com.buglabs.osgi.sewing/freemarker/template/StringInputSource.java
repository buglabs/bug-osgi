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

import java.io.StringReader;

/**
 * Provides a <code>String</code> to be compiled into a FM-Classic template.
 * 
 * @version $Id: StringInputSource.java 1152 2005-10-09 08:48:30Z run2000 $
 * @since 1.9
 */
public class StringInputSource extends InputSource {

	/**
	 * Create a StringInputSource with the supplied <code>String</code> data.
	 * 
	 * @param source
	 *            the String data for this StringInputSource
	 */
	public StringInputSource(String source) {
		super(new StringReader(source));
	}

	/**
	 * Set the <code>String</code> data to be provided by this
	 * StringInputSource.
	 * 
	 * @param source
	 *            the String source for this StringInputSource
	 */
	public void setString(String source) {
		reader = new StringReader(source);
	}
}
