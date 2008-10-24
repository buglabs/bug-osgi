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
package com.buglabs.device;


/**
 * This defines how to display information on the BUG base LCD.  This interface is used to lock the display.  This ensures only one client at a time and write to the base display.
 * 
 * @author ken
 * 
 */
public interface IBaseDisplay {
	/**
	 * A client must first lock the display before writing.
	 * 
	 * @return
	 */
	public IFramebufferDevice lock(String clientId);

	/**
	 * A client unlocks the display after use for other clients.
	 * 
	 * @throws InvalidClientException
	 */
	public void unlock(String clientId) throws InvalidClientException;
}
