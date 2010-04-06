/*
 * Copyright ThinkTank Maths Limited 2006 - 2008
 *
 * This file is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 */
package com.openlapi;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.bug.module.gps.Activator;
import com.buglabs.bug.module.gps.pub.INMEARawFeed;

import thinktank.j2me.TTUtils;

/**
 * Implementation of {@link LocationProvider} that uses BUG GPS module for input.
 * 
 * @author kgilmer
 */
public class LocationProviderBUG extends LocationProviderSimplified {
	private volatile NMEADaemon daemon = null;

	/**
	 * @param criteria
	 * @throws LocationException
	 */
	LocationProviderBUG(Criteria criteria)
			throws LocationException {
		TTUtils.log("OpenLAPI BUG mode");
		startBackend();
	}

	protected void startBackend() throws LocationException {
		if (daemon != null)
			return;

		//Get GPS service from OSGi service registry.
		INMEARawFeed feed = getRawFeed();
		if (feed == null) {
			throw new LocationException("Unable to access GPS service: " + INMEARawFeed.class.getName());
		}
		
		//Access the input stream
		InputStream input;
		try {
			input = feed.getInputStream();
		} catch (IOException e) {
			throw new LocationException("Failed to get GPS input stream: " + e.getMessage());
		}
		
		daemon = new NMEADaemon(this, input, new NMEADaemon.ValidStreamCallback() {
			public void onSuccess() {
			}

			public void onFailure(String failed) {
				TTUtils.log("INVALID NMEA: " + failed);
				daemon.end();
			}
		});
		new Thread(daemon).start();
	}

	/**
	 * Using the GPS bundle activator's BundleContext, try to get a INMEARawFeed.
	 * @return INMEARawFeed or NULL if no service is available.
	 */
	private INMEARawFeed getRawFeed() {
		BundleContext bc = Activator.getInstance().getBundleContext();
		
		ServiceReference sr = bc.getServiceReference(INMEARawFeed.class.getName());
		
		if (sr == null) {
			return null;
		}
		
		return (INMEARawFeed) bc.getService(sr);
	}

	protected void stopBackend() {
		if (daemon == null)
			return;

		daemon.end();
		daemon = null;
	}

}
