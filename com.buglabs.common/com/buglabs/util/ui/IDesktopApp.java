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
package com.buglabs.util.ui;

import java.net.URL;

/**
 * A service that can be utilized to create a Desktop UI like experience for
 * OSGi bundles. Loosely based on the ConciergeGUI bundle at
 * http://concierge.sourceforge.net
 * 
 * @author kgilmer
 * 
 */
public interface IDesktopApp {

	/**
	 * Application icon has been clicked. Applications will typically want to
	 * create or set focus to their primary UIs in here.
	 */
	public void click();

	/**
	 * A custom icon can be displayed. If null returned a default image will be
	 * used.
	 * 
	 * @return URL to an icon to be displayed
	 */
	public URL getIcon(int width, int height, int depth);

	/**
	 * Apps can optionally contribute a flat set of menu items that are
	 * displayed in the App Icon view.
	 * 
	 * IDesktopApp.menuSelected() will be called with the name of item when user
	 * selects that entry.
	 * 
	 * @return a list of menu items for this application.
	 */
	public String[] getMenuItems();

	/**
	 * Call back for when an item is selected by user.
	 * 
	 * @param item
	 */
	public void menuSelected(String item);
}
