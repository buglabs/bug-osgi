/*
 * Sewing: a Simple framework for Embedded-OSGi Web Development
 * Copyright (C) 2009 Bug Labs
 * Email: bballantine@buglabs.net
 * Site: http://www.buglabs.net
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

package com.buglabs.osgi.sewing.pub.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Wrapper for a map to enforce the types that go into it this gets filled with
 * the request parameters sent in a get or post and past to the controller's
 * methods
 * 
 * @author brian
 * 
 */
public class RequestParameters {

	public static final String REQUEST_BODY_PARAM_KEY = "__request_body";

	private Map inner_map;
	private FormFile file;

	public RequestParameters() {
		inner_map = new HashMap();
		file = null;
	}

	public Object put(String name, String value) {
		return inner_map.put(name, value);
	}

	public String get(String name) {
		return (String) inner_map.get(name);
	}

	public Map getInnerMap() {
		return inner_map;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	/**
	 * Puts all the request parameters into the current RequestParameters
	 * object.
	 * 
	 * If the a param has the same key, it comma separates the vals
	 * 
	 * If the param is a file, it will overwrite the existing file
	 * 
	 * @param params
	 */
	public void add(RequestParameters params) {
		if (params == null)
			return;
		Iterator itr = params.getInnerMap().keySet().iterator();
		String key;
		while (itr.hasNext()) {
			key = (String) itr.next();
			if (inner_map.containsKey(key))
				inner_map.put(key, inner_map.get(key) + "," + params.getInnerMap().get(key));
			else
				inner_map.put(key, params.getInnerMap().get(key));
		}

		if (params.getFile() != null)
			this.setFile(params.getFile());
	}
}