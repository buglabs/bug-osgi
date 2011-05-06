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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Label;
import java.awt.PopupMenu;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Map;

import org.osgi.framework.Bundle;

import com.buglabs.application.IDesktopApp;


/**
 * This AWT container represents icon and text for an app.
 * @author kgilmer
 *
 */
public class AppWidget extends Container {
	private static final int ICON_SIZE_Y = 32;

	private static final int ICON_SIZE_X = ICON_SIZE_Y;

	private static final long serialVersionUID = -2570755730671986150L;

	private static final int FONT_HEIGHT = 12;

	//TODO determine real screen depth
	private static final int SCREEN_DEPTH = 8;

	/**
	 * Max number of characters an app name can be before truncation.
	 */
	private static final int MAX_NAME_LENGTH = 9;

	private static final String TRUNCATE_STRING = "..";

	private final static Dimension WIDGET_SIZE = new Dimension(64, 44);

	private final PopupMenu menu = new PopupMenu();

	private final IDesktopApp desktopApp;

	private final String appName;

	public AppWidget(final String name, final Map launchListeners, Bundle bundle, Font font) {
		appName = name;
		desktopApp = (IDesktopApp) launchListeners.get(name);

		AppIcon	icon = new AppIcon(getIcon(desktopApp, bundle), Activator.APP_INVERTED);
	
		MouseListener listener = new AppIconMouseListener(icon, desktopApp, menu);
		this.addMouseListener(listener);
		icon.setSize(ICON_SIZE_X, ICON_SIZE_Y);
		icon.setBounds((ICON_SIZE_X / 2), 0, ICON_SIZE_X, ICON_SIZE_Y);

		final Label label = new Label(formatName(name));
		label.setFont(font);
		label.setAlignment(Label.CENTER);
		label.setBounds(0, ICON_SIZE_Y + 2, ICON_SIZE_X * 2, FONT_HEIGHT);

		populateMenu(menu, desktopApp, bundle.getState());
		menu.addActionListener(new AppActionListener(desktopApp, bundle, Activator.getLogService()));
		
		add(icon);
		add(menu);
		add(label);
	}

	/**
	 * Load the icon image for the app
	 * @param desktopApp2
	 * @param bundle 
	 * @return
	 */
	private Image getIcon(IDesktopApp desktopApp2, Bundle bundle) {
		//Custom icon provided by app.
		if (desktopApp2 != null) {
			URL imageFile = desktopApp2.getIcon(ICON_SIZE_Y, ICON_SIZE_Y, SCREEN_DEPTH);
			
			if (imageFile != null) {
				return Activator.toolkit.createImage(imageFile);
			}
		}
		
		//default app icon for IDesktopApp clients
		if (desktopApp2 != null) {
			return Activator.APP_ACTIVE;
		}
		
		//A running app bundle that isn't a IDesktopApp
		if (bundle.getState() == Bundle.ACTIVE) {
			return Activator.BUNDLE_STARTED;
		}
		
		//A non-running bundle.
		return Activator.BUNDLE_STOPPED;
	}

	private void populateMenu(PopupMenu m, IDesktopApp app, int state) {
		//Display app title.
		m.add(appName);
		m.getItem(m.getItemCount() - 1).setEnabled(false);
		m.addSeparator();
		
		//If app contributes a menu, add it.
		if (app != null) {
			String[] items = app.getMenuItems();

			if (items != null && items.length > 0) {
				for (int i = 0; i < items.length; ++i) {
					m.add(items[i]);
				}
				m.addSeparator();
			}
		}

		m.add(AppActionListener.ACTION_BUNDLE_START);
		m.getItem(m.getItemCount() - 1).setEnabled(state != Bundle.ACTIVE);
		m.add(AppActionListener.ACTION_BUNDLE_STOP);
		m.getItem(m.getItemCount() - 1).setEnabled(state == Bundle.ACTIVE);
		m.addSeparator();
		m.add(AppActionListener.ACTION_BUNDLE_UNINSTALL);
	}

	/**
	 * Truncate bundle name if necessary
	 * 
	 * @param name
	 * @return
	 */
	private String formatName(String name) {
		if (name.length() < MAX_NAME_LENGTH) {
			return name;
		}

		return name.substring(0, MAX_NAME_LENGTH - (TRUNCATE_STRING.length())) + TRUNCATE_STRING;
	}

	public Dimension getPreferredSize() {
		return WIDGET_SIZE;
	}

	public Dimension getMinimumSize() {
		return WIDGET_SIZE;
	}
}
