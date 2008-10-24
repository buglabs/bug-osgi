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
package com.buglabs.util.trackers;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.buglabs.menu.IMenuNode;
import com.buglabs.menu.IMenuProvider;

/**
 * Tracks when a menu provider is available.
 * 
 * @author ken
 * 
 */
public class MenuServiceTracker implements ServiceTrackerCustomizer {

	private final IMenuNode menu;

	private final BundleContext context;

	private final String menuPath;

	private IMenuProvider menuProvider;

	public MenuServiceTracker(BundleContext context, String menuPath, IMenuNode menu) {
		this.context = context;
		this.menuPath = menuPath;
		this.menu = menu;
	}

	public Object addingService(ServiceReference reference) {

		menuProvider = (IMenuProvider) context.getService(reference);

		menuProvider.registerMenu(menuPath, menu);

		return reference;
	}

	public void modifiedService(ServiceReference reference, Object service) {

	}

	public void removedService(ServiceReference reference, Object service) {
		if (menuProvider != null) {
			menuProvider.unregisterMenu(menuPath);
		}
	}

}
