/* Copyright (c) 2007, 2008 Bug Labs, Inc.
 * All rights reserved.
 *   
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License version  
 * 2 only, as published by the Free Software Foundation.   
 *   
 * This program is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * General Public License version 2 for more details (a copy is  
 * included at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html).   
 *   
 * You should have received a copy of the GNU General Public License  
 * version 2 along with this work; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA   
 *
 */
package com.buglabs.services.ws;

/**
 * This interface is a base interface for Bug services.
 * 
 * @author ken
 * 
 */
public interface PublicWSProvider {
	public static final int GET = 1;

	public static final int PUT = 2;

	public static final int POST = 3;

	public static final int DELETE = 4;
	
	public static final String PACKAGE_ID = "com.buglabs.service.ws";

	/**
	 * @param operation
	 *            HTTP operation. See IPublicServiceProvider.GET, etc.
	 * @return The description of what the service requires and provides.
	 */
	public PublicWSDefinition discover(int operation);

	/**
	 * Execute a service. This is a proxy to a native OSGi style service.
	 * 
	 * @param operation
	 *            PublicWSProvider.GET, .PUT, .POST, .DELETE
	 * @param input
	 * @return
	 */
	public IWSResponse execute(int operation, String input);

	/**
	 * @return Name that this service uses.
	 */
	public String getPublicName();

	/**
	 * @return A brief description of the service.
	 */
	public String getDescription();
}
