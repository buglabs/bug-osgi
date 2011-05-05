/*
 * Sewing: a Simple framework for Embedded-OSGi Web Development
 * Copyright (C) 2009 Bug Labs
 * Email: bballantine@buglabs.net
 * Site: http://www.buglabs.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */

package com.buglabs.osgi.sewing;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

import com.buglabs.osgi.sewing.servicetracker.SewingServiceTracker;
import com.buglabs.util.ServiceFilterGenerator;

/**
 * BundleActivator for Sewing.
 * 
 */
public class Activator implements BundleActivator {

	private SewingServiceTracker stc;
	private ServiceTracker st;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		LogManager.setContext(context);
		// Create the service tracker and run it.
		stc = new SewingServiceTracker(context);
		Filter f = context.createFilter(ServiceFilterGenerator.generateServiceFilter(stc.getServices()));
		st = new ServiceTracker(context, f, stc);
		st.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		stc.stop();
		st.close();
	}
}
