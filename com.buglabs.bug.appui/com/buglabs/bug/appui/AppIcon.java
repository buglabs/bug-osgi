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

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;

/**
 * An icon that has the ability to show selection via flash() method.
 * @author kgilmer
 *
 */
class AppIcon extends Container {
	private static final long serialVersionUID = -7381567422211751695L;
	private final Image normal;
	private final Image inverted;
	private Image image;

	/**
	 * @param normal regular image
	 * @param inverted inverted image
	 */
	public AppIcon(Image normal, Image inverted) {
		this.normal = normal;
		this.inverted = inverted;
		this.image = normal;
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
		super.paint(g);
	}
	
	/**
	 * @param g graphics to invert
	 */
	public synchronized void invert(Graphics g) {
		image = inverted;
		paint(g);
	}

	/**
	 * @param g graphics to revert
	 */
	public synchronized void revert(Graphics g) {
		image = normal;
		paint(g);
	}
	
	/**
	 * @return true if inverted
	 */
	public synchronized boolean isInverted() {
		return image == inverted;
	}

}
