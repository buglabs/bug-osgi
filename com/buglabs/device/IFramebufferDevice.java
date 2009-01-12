/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
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
	 * 
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
	 * @param light
	 *            true for on, false for off.
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
