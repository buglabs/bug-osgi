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
 * Represents a response to a web service call. Web server needs to know how to
 * stucture the data for the client.
 * 
 * @author ken
 * 
 */
public interface IWSResponse {
	/**
	 * Return an appropriate mime-type for content being passed.
	 * 
	 * @return
	 */
	public String getMimeType();

	/**
	 * Get content of response.
	 * 
	 * @return
	 */
	public Object getContent();

	/**
	 * Return true if the repsonse is an error. If the response is an error,
	 * mimeType and Content are ignored.
	 * 
	 * @return
	 */
	public boolean isError();

	/**
	 * Return error code if the response is an error.
	 * 
	 * @return
	 */
	public int getErrorCode();

	/**
	 * Return error message if the response is an error.
	 * 
	 * @return
	 */
	public String getErrorMessage();
}
