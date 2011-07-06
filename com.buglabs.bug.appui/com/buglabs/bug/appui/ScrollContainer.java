/*******************************************************************************
 * Copyright (c) 2011 Bug Labs, Inc.
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
package com.buglabs.bug.appui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

/**
 * A container for scrolling icons.
 * 
 * @author kgilmer
 * 
 */
class ScrollContainer extends Container {
	private static final long serialVersionUID = 7194753406047720880L;
	private static final int MAX_HIEGHT = 3000;
	private static final int PREFERRED_HEIGHT = 200;
	private static Dimension preferredSize;
	private static Dimension maxSize;

	/**
	 * @param width w of container
	 */
	public ScrollContainer(int width) {
		preferredSize = new Dimension(width, PREFERRED_HEIGHT);
		maxSize = new Dimension(width, MAX_HIEGHT);
		this.setBackground(Color.WHITE);
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#getMaximumSize()
	 */
	public Dimension getMaximumSize() {
		return maxSize;
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return preferredSize;
	}
}
