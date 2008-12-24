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
package com.buglabs.application;

import java.util.Arrays;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

import com.buglabs.util.ServiceFilterGenerator;

/**
 * Helper class to construct ServiceTrackers.
 * @author kgilmer
 *
 */
public class ServiceTrackerHelper {
	/**
	 * @param context BundleContext
	 * @param services Services to be tracked
	 * @param runnable Object handling service changes
	 * @return
	 * @throws InvalidSyntaxException
	 */
	public static ServiceTracker createAndOpen(BundleContext context, List services, RunnableWithServices runnable) throws InvalidSyntaxException {
		Filter filter = context.createFilter(ServiceFilterGenerator.generateServiceFilter(services));
		ServiceTracker st = new ServiceTracker(context, filter, new ServiceTrackerCustomizerAdapter(context, runnable, services));
		st.open();

		return st;
	}

	/**
	 * @param context BundleContext
	 * @param services Services to be tracked
	 * @param runnable Object handling service changes
	 * @return
	 * @throws InvalidSyntaxException
	 * 
	 */
	public static ServiceTracker createAndOpen(BundleContext context, String[] services, RunnableWithServices runnable) throws InvalidSyntaxException {

		Filter filter = context.createFilter(ServiceFilterGenerator.generateServiceFilter(Arrays.asList(services)));
		ServiceTracker st = new ServiceTracker(context, filter, new ServiceTrackerCustomizerAdapter(context, runnable, Arrays.asList(services)));
		st.open();

		return st;
	}

	/**
	 * @param context BundleContext
	 * @param service Service to be tracked
	 * @param runnable Object handling service changes
	 * @return
	 * @throws InvalidSyntaxException
	 */
	public static ServiceTracker createAndOpen(BundleContext context, String service, RunnableWithServices runnable) throws InvalidSyntaxException {
		Filter filter = context.createFilter(ServiceFilterGenerator.generateServiceFilter(Arrays.asList(new String[] { service })));
		ServiceTracker st = new ServiceTracker(context, filter, new ServiceTrackerCustomizerAdapter(context, runnable, Arrays.asList(new String[] { service })));
		st.open();

		return st;
	}
	
	/**
	 * @param context BundleContext
	 * @param services Services to be tracked
	 * @param runnable Object handling service changes
	 * @return
	 * @throws InvalidSyntaxException
	 */
	public static ServiceTracker createAndOpen(BundleContext context, List services, ServiceChangeListener runnable) throws InvalidSyntaxException {
		Filter filter = context.createFilter(ServiceFilterGenerator.generateServiceFilter(services));
		ServiceTracker st = new ServiceTracker(context, filter, new ServiceTrackerCustomizerAdapter(context, runnable, services));
		st.open();

		return st;
	}

	/**
	 * @param context BundleContext
	 * @param services Services to be tracked
	 * @param runnable Object handling service changes
	 * @return
	 * @throws InvalidSyntaxException
	 */
	public static ServiceTracker createAndOpen(BundleContext context, String[] services, ServiceChangeListener runnable) throws InvalidSyntaxException {

		Filter filter = context.createFilter(ServiceFilterGenerator.generateServiceFilter(Arrays.asList(services)));
		ServiceTracker st = new ServiceTracker(context, filter, new ServiceTrackerCustomizerAdapter(context, runnable, Arrays.asList(services)));
		st.open();

		return st;
	}

	/**
	 * @param context BundleContext
	 * @param services Services to be tracked
	 * @param runnable Object handling service changes
	 * @return
	 * @throws InvalidSyntaxException
	 */
	public static ServiceTracker createAndOpen(BundleContext context, String service, ServiceChangeListener runnable) throws InvalidSyntaxException {
		Filter filter = context.createFilter(ServiceFilterGenerator.generateServiceFilter(Arrays.asList(new String[] { service })));
		ServiceTracker st = new ServiceTracker(context, filter, new ServiceTrackerCustomizerAdapter(context, runnable, Arrays.asList(new String[] { service })));
		st.open();

		return st;
	}
}
