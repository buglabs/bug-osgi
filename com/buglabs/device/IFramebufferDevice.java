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
 * This interface defines the low-level calls to the base display.
 * 
 * @author ken
 * 
 */
public interface IFramebufferDevice {

	/**
	 * Clear the display.
	 */
	public void clear();
	
	/**
	 * Number of colors the display supports.
	 * @return
	 */
	public int getDepth();
	
	/**
	 * 
	 * @return the height of display in number of pixels.
	 */
	public int getHeight();
	
	/**
	 * 
	 * @return the width of display in number of pixels.
	 */
	public int getWidth();
	
	/**
	 * Refresh the display from the internal buffer.
	 */
	public void redraw();
	
	/**
	 * @param light true for on, false for off.
	 */
	public void setBacklight(boolean light);
	

	/**
	 * @param x
	 * @param y
	 * @param bitmap
	 */
	public void write(int x, int y, boolean[][] bitmap);


	/**
	 * @param x
	 * @param y
	 * @param message
	 * @param charWidth
	 * @param charHeight
	 */
	public void write(int x, int y, String message, int charWidth, int charHeight);
	
	/**
	 * High-level method to write text to base LCD allowing a custom font.
	 * 
	 * @param message
	 */
	public void write(int x, int y, String message, String fontFile, int charWidth, int charHeight);
}
