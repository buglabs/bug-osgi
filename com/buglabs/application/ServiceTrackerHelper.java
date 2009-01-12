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
 * 
 * @author kgilmer
 * 
 */
public class ServiceTrackerHelper {
	/**
	 * @param context
	 *            BundleContext
	 * @param services
	 *            Services to be tracked
	 * @param runnable
	 *            Object handling service changes
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
	 * @param context
	 *            BundleContext
	 * @param services
	 *            Services to be tracked
	 * @param runnable
	 *            Object handling service changes
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
	 * @param context
	 *            BundleContext
	 * @param service
	 *            Service to be tracked
	 * @param runnable
	 *            Object handling service changes
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
	 * @param context
	 *            BundleContext
	 * @param services
	 *            Services to be tracked
	 * @param runnable
	 *            Object handling service changes
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
	 * @param context
	 *            BundleContext
	 * @param services
	 *            Services to be tracked
	 * @param runnable
	 *            Object handling service changes
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
	 * @param context
	 *            BundleContext
	 * @param services
	 *            Services to be tracked
	 * @param runnable
	 *            Object handling service changes
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
