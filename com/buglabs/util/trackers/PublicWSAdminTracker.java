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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.buglabs.services.ws.PublicWSAdmin;
import com.buglabs.services.ws.PublicWSProvider;

/**
 * A utility class to track the PublicWSAdmin service.
 * 
 * @author ken
 * 
 */
public class PublicWSAdminTracker implements ServiceTrackerCustomizer {

	private List services;

	private final BundleContext context;

	public PublicWSAdminTracker(BundleContext context, List services) {
		this.context = context;
		this.services = services;
	}

	public PublicWSAdminTracker(BundleContext context, PublicWSProvider provider) {
		this.context = context;
		this.services = new ArrayList();

		services.add(provider);
	}

	public Object addingService(ServiceReference reference) {
		PublicWSAdmin admin = (PublicWSAdmin) context.getService(reference);
		if (!services.isEmpty()) {
			for (Iterator i = services.iterator(); i.hasNext();) {
				admin.registerService((PublicWSProvider) i.next());
			}
		}

		return reference;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		// TODO handle this case.
	}

	public void removedService(ServiceReference reference, Object service) {
		PublicWSAdmin admin = (PublicWSAdmin) context.getService(reference);
		if (!services.isEmpty()) {
			for (Iterator i = services.iterator(); i.hasNext();) {
				admin.unregisterService((PublicWSProvider) i.next());
			}
		}
	}

	/**
	 * Create and open a tracker for PublicWSAdmin.
	 * 
	 * @param context
	 * @param providers
	 * @return
	 * @throws InvalidSyntaxException
	 */
	public static ServiceTracker createTracker(BundleContext context, List providers) throws InvalidSyntaxException {
		Filter f = context.createFilter("(" + Constants.OBJECTCLASS + "=" + PublicWSAdmin.class.getName() + ")");

		ServiceTracker tracker = new ServiceTracker(context, f, new PublicWSAdminTracker(context, providers));
		tracker.open();

		return tracker;
	}

	/**
	 * Create and open a tracker for PublicWSAdmin.
	 * 
	 * @param context
	 * @param provider
	 * @return
	 * @throws InvalidSyntaxException
	 */
	public static ServiceTracker createTracker(BundleContext context, PublicWSProvider provider) throws InvalidSyntaxException {
		Filter f = context.createFilter("(" + Constants.OBJECTCLASS + "=" + PublicWSAdmin.class.getName() + ")");

		ServiceTracker tracker = new ServiceTracker(context, f, new PublicWSAdminTracker(context, provider));
		tracker.open();

		return tracker;
	}
}
