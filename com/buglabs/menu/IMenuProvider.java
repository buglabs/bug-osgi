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
 * Implementors provide a menu system to the end user. Clients can provide
 * sub-menus.
 * 
 * @author ken
 * 
 */
public interface IMenuProvider {
	/**
	 * @param path
	 *            String denotion of path using '.' character as seperator.  To contribute to the root menu pass an empty string or <code>null</code>.
	 *            Example: "child1.subchild1.me".
	 * @param menu
	 *            Reference to the sub-root menu.
	 */
	public void registerMenu(String path, IMenuNode menu);

	/**
	 * Notify the menu provider that a menu node has changed. Should cause the
	 * menu system to refresh if the path is visible on the screen.
	 * 
	 * @param path
	 */
	public void notifyMenuUpdate(String path);

	/**
	 * Remove a submenu.
	 * 
	 * @param path
	 */
	public void unregisterMenu(String path);
	
	/**
	 * Moves up the tree towards the root.
	 * @param numLevels The number of times to move up.  If this is greater than the
	 * number of steps to the root, the current node will be set to the root.
	 */
	public void selectParent(int numLevels);
	
	/**
	 * 
	 * @param path
	 * @return <code>true</code> if path is the current node, <code>false</code> otherwise.
	 */
	public boolean isCurrentNode(String path);

}
