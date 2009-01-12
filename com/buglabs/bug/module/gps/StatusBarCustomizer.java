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
package com.buglabs.bug.module.gps;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.position.Position;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.buglabs.bug.module.gps.pub.IPositionProvider;
import com.buglabs.status.IStatusBarProvider;

public class StatusBarCustomizer implements ServiceTrackerCustomizer {

	private final BundleContext context;

	private IPositionProvider position;

	private Timer timer;

	private IStatusBarProvider statusBar;

	private String positionKey;

	public StatusBarCustomizer(BundleContext context) {
		this.context = context;
	}

	public Object addingService(ServiceReference reference) {
		Object svc = context.getService(reference);

		if (svc instanceof IStatusBarProvider) {
			statusBar = (IStatusBarProvider) svc;
		} else if (svc instanceof IPositionProvider) {
			position = (IPositionProvider) svc;
		}

		if (position != null && statusBar != null) {
			timer = new Timer();
			positionKey = statusBar.acquireRegion(10);

			if (positionKey != null) {
				timer.schedule(new LocationUpdate(positionKey, statusBar), 0, 10000);
			}
		}

		return svc;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		// TODO Auto-generated method stub

	}

	public void removedService(ServiceReference reference, Object svc) {
		if (timer != null) {
			timer.cancel();
		}

		if (positionKey != null) {
			statusBar.releaseRegion(positionKey);
		}

		if (svc instanceof IStatusBarProvider) {
			statusBar = null;
		} else if (svc instanceof IPositionProvider) {
			position = null;
		}
	}

	private class LocationUpdate extends TimerTask {

		private final String positionKey;

		private final IStatusBarProvider sb;

		public LocationUpdate(String positionKey, IStatusBarProvider statusBar) {
			this.positionKey = positionKey;
			sb = statusBar;
		}

		public void run() {
			if (position != null && sb != null) {
				Position posval = position.getPosition();
				if (posval != null) {
					String pos = posval.getLongitude().toString().substring(1, 3) + "," + position.getPosition().getLatitude().toString().substring(1, 3) + ","
							+ position.getPosition().getAltitude().toString().substring(1, 3);
					sb.write(positionKey, pos);
				}
			}
		}
	}
}
