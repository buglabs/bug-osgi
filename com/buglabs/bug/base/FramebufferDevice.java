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
package com.buglabs.bug.base;

import com.buglabs.bug.jni.basedisplay.NativeBaseDisplay;
import com.buglabs.bug.jni.common.FCNTL_H;
import com.buglabs.device.IFramebufferDevice;

/**
 * Base implementation of base LCD framebuffer.
 * @author kgilmer
 *
 */
public class FramebufferDevice implements IFramebufferDevice {
	private static final String FRAME_BUFFER_DEVICE_NODE = "/dev/fb0";
	private static final double SCALE_FACTOR = 1.6;
	private static final String BASELCD_TTF = "/usr/share/fonts/ttf/baselcd.ttf";

	/*
	private static final double SCALE_FACTOR = 1.6;
*/
	private static final int SCREEN_DEPTH = 1;

	public final int SCREEN_WIDTH = 160;

	public final int SCREEN_HEIGHT = 20;
	public final String PROP_FONT = "com.buglabs.bug.base.font";
public final String PROP_BASEDISPLAY_FB = "com.buglabs.bug.base.display";
	private NativeBaseDisplay nbd;

	private String fontpath;

	public FramebufferDevice() {
		fontpath = System.getProperty(PROP_FONT, BASELCD_TTF);
		String bd = System.getProperty(PROP_BASEDISPLAY_FB, FRAME_BUFFER_DEVICE_NODE);
		nbd = new NativeBaseDisplay();

		nbd.open(bd, FCNTL_H.O_RDWR);
		nbd.backlightON();
	}

	public void clear() {
		nbd.clearBuff();
	}

	private boolean[][] createBlankBitmap(int width, int height, boolean val) {
		boolean[][] b = new boolean[width][height];

		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				b[i][j] = val;
			}
		}

		return b;
	}

	public void dispose() {
		nbd.clearBuff();
		nbd.render();
		nbd.backlightOFF();
		nbd.close();
	}

	public int getDepth() {
		return SCREEN_DEPTH;
	}

	public int getHeight() {
		return SCREEN_HEIGHT;
	}

	public int getWidth() {
		return SCREEN_WIDTH;
	}

	public void redraw() {
		nbd.render();
	}

	public void setBacklight(boolean light) {
		if (light) {
			nbd.backlightON();
		} else {
			nbd.backlightOFF();
		}
	}

	public void write(int x, int y, boolean[][] bitmap) {
		nbd.writeBuff(x, y, bitmap);
		nbd.render();
	}

	public void write(int x, int y, String message, int charWidth, int charHeight) {
		nbd.writeBuff(x, y, createBlankBitmap(charHeight, (int)((charWidth  * message.length()) / SCALE_FACTOR), false));
		nbd.writeMsg(x, y, message, fontpath, charWidth, charHeight);
		nbd.render();
	}
	
	public void write(int x, int y, String message, String fontFile, int charWidth, int charHeight) {
		nbd.writeBuff(x, y, createBlankBitmap(charHeight, (int)((charWidth  * message.length()) / SCALE_FACTOR), false));
		nbd.writeMsg(x, y, message, fontFile, charWidth, charHeight);
		nbd.render();
	}

}
