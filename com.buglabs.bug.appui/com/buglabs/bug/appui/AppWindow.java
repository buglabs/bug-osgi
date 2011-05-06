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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.ScrollPane;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.Bundle;

/**
 * AppUI Frame.
 * @author kgilmer
 *
 */
public class AppWindow {
	private static final long serialVersionUID = 1142379544558770397L;
	//Map of apps to be shown
	private Map model;
	//Map of apps that implement IDesktopApp
	private Map launchListeners;
	
	private static final Font font = new Font("Arial", Font.PLAIN, 12);

	private static final int HORIZ_MARGIN = 10;

	private static final int VERT_MARGIN = 16;
	private static final int SB_WIDTH = 4;

	private ScrollPane pane;

	private Container paneCont;
	private final Frame f;

	public AppWindow(Frame f) {
		this.f = f;
		
		f.setTitle("BUG Applications");
		f.setBackground(Color.WHITE);

		pane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);		
		pane.setSize(f.getSize());
		
		paneCont = new ScrollContainer(pane.getSize().width - SB_WIDTH);
		paneCont.setLayout(new FlowLayout(FlowLayout.LEFT, HORIZ_MARGIN, VERT_MARGIN));
		pane.add(paneCont);

		f.add(pane);

		f.pack();
	}

	/**
	 * Rebuild window
	 */
	public void refresh() {
		//TODO this could be much more efficient.
		paneCont.removeAll();
		for (Iterator i = model.keySet().iterator(); i.hasNext();) {
			final String name = (String) i.next();
			AppWidget widget = new AppWidget(name, launchListeners, (Bundle) model.get(name), font);
			paneCont.add(widget);
		}
		
		f.pack();
	}
	
	public void setBundles(Map appBundles) {
		this.model = appBundles;
	}

	public void setLaunchClients(Map launchListeners) {
		this.launchListeners = launchListeners;
	}

	public void setVisible(boolean b) {
		f.setVisible(b);
	}

	public void dispose() {
		f.dispose();
	}
}
