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
