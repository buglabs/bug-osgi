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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.osgi.framework.Bundle;
import org.osgi.service.log.LogService;

import com.buglabs.application.IDesktopApp;


/**
 * This class acts on app icon menu items.
 * @author kgilmer
 * 
 */
public class AppActionListener implements ActionListener {

	protected static final String ACTION_BUNDLE_UNINSTALL = "uninstall";
	protected static final String ACTION_BUNDLE_STOP = "stop";
	protected static final String ACTION_BUNDLE_START = "start";
	private final IDesktopApp interaction;
	private final Bundle bundle;
	private final LogService log;

	/**
	 * @param interaction Application
	 * @param bundle App Bundle
	 * @param log Log Service
	 */
	public AppActionListener(IDesktopApp interaction, Bundle bundle, LogService log) {
		this.interaction = interaction;
		this.bundle = bundle;
		this.log = log;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(final ActionEvent e) {
		final String cmd = e.getActionCommand();
		try {
			if (cmd.equals(ACTION_BUNDLE_START)) {
				bundle.start();
			} else if (cmd.equals(ACTION_BUNDLE_STOP)) {
				bundle.stop();
			} else if (cmd.equals(ACTION_BUNDLE_UNINSTALL)) {
				bundle.uninstall();
			} else if (interaction != null && interaction.getMenuItems() != null) {
				String[] items = interaction.getMenuItems();
				for (int i = 0; i < items.length; ++i) {
					if (items[i].equals(cmd)) {
						interaction.menuSelected(cmd);
						return;
					}
				}
			}
		} catch (Exception ex) {
			log.log(LogService.LOG_ERROR, "AppUI failed to " + cmd + " bundle " + bundle.getLocation(), ex);
		}
	}

}
