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
package com.buglabs.menu;

/**
 * An interface for menu controllers.
 * 
 * @author ken
 * 
 */
public interface IMenuController {

	/**
	 * Tell the controller that a button event has occurred.
	 * 
	 * @param buttonId
	 */
	public abstract void buttonPress(int buttonId);

	/**
	 * Redraw existing content on the menu display.
	 */
	public abstract void refreshDisplay();

	/**
	 * Reset menu to root and refresh the display.
	 */
	public abstract void resetDisplay();

	/**
	 * Clean up the display.
	 */
	public abstract void clearDisplay();

	/**
	 * @return <code>true</code> if the menu is on the root node (at rest).
	 */
	public abstract boolean isRoot();
	
	/**
	 * 
	 * @param path The path to check the current node against
	 * @return <code>true</code> if the current node corresponds to this path
	 */
	public abstract boolean isCurrentNode(String path);
	
	/**
	 * Moves up the tree towards the root.
	 * @param numLevels The number of times to move up.  If this is greater than the
	 * number of steps to the root, the current node will be set to the root.
	 */
	public abstract void selectParent(int numLevels);

}