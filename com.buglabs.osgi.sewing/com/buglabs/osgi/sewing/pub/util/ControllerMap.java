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
import java.util.Map;

import com.buglabs.osgi.sewing.pub.ISewingControllerFactory;
import com.buglabs.osgi.sewing.pub.SewingController;

/**
 * Wrapper for a Map to enforce the types that go into it This stores the
 * implementer's mapping of controller names to controller objects
 * getControllerMap() then returns this in the application setup and stores the
 * mapping when a request for a page comes in, this map is used to instantiate
 * and call the controller
 * 
 * @author brian
 * 
 */
public class ControllerMap {

	private Map inner_map;

	public ControllerMap() {
		inner_map = new HashMap();
	}

	public Object put(String controllerName, SewingController controller) {
		return inner_map.put(controllerName, controller);
	}

	public Object put(String controllerName, ISewingControllerFactory controllerFactory) {
		return inner_map.put(controllerName, controllerFactory);
	}

	public SewingController get(String controllerName) {
		Object o = inner_map.get(controllerName);
		if (o instanceof ISewingControllerFactory)
			return ((ISewingControllerFactory) o).getController();
		else
			return (SewingController) inner_map.get(controllerName);
	}

	public Map getInnerMap() {
		return inner_map;
	}

}