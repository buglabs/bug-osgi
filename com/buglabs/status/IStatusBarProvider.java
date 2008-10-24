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
package com.buglabs.status;


/**
 * A Status Bar is a bitmap display that can show the user simple status
 * messages and icons. Clients may request resources from the Status Bar
 * Provider in either bitmap form or text (String) form. The position and scale
 * of the region are not controlled by the client. The client should not assume
 * that it's region is always visible at all times.
 * 
 * @author ken
 * 
 */
public interface IStatusBarProvider {
	/**
	 * Get bitmap resource from Status Bar provider.
	 * 
	 * @param clientId
	 *            ID of client
	 * @param height
	 *            required height for status bitmap
	 * @param width
	 *            required width for status bitmap
	 * @return A key associated with requested region or <code>null</code> if
	 *         no region can be allocated.
	 */
	public String acquireRegion(int height, int width);

	/**
	 * Get text resource from Status Bar provider.
	 * 
	 * @param clientId
	 * @param length
	 * @return A key associated with requested region or <code>null</code> if
	 *         no region can be allocated.
	 */
	public String acquireRegion(int length);

	/**
	 * Write a bitmap to the status screen. Must successfully aquire region
	 * before calling this method.
	 * 
	 * @param clientId
	 * @param bitmap
	 */
	public void write(String key, boolean[][] bitmap);

	/**
	 * Write a text message to the status screen. Must successfully aquire
	 * region before calling this method.
	 * 
	 * @param clientId
	 * @param message
	 */
	public void write(String key, String message);

	public void releaseRegion(String key);
}
