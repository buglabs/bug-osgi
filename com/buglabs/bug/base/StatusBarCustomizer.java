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
package com.buglabs.bug.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.buglabs.bug.base.pub.ITimeProvider;
import com.buglabs.status.IStatusBarProvider;

/**
 * This class demonstrates a status bar entry by showing the system time.
 * 
 * @author kgilmer
 *
 */
class StatusBarCustomizer implements ServiceTrackerCustomizer {

	private class UpdateClock extends TimerTask {

		private final IStatusBarProvider statusBar;

		private final String key;

		private SimpleDateFormat formatter;

		public UpdateClock(String key, IStatusBarProvider statusBar) {
			this.key = key;
			this.statusBar = statusBar;
			formatter = new SimpleDateFormat("HH:mm");
		}

		public void run() {
			Date time = timeInfo.getTime();
			//don't write the time unless it's legit
			if (System.currentTimeMillis() > 9000000){
				statusBar.write(key, formatter.format(time));
			}
			else{
				statusBar.write(key, new boolean[][]{{false}});
			}
			//write a blank pixel to overwrite fbprogress
		}

	}

	private final BundleContext context;

	private String timeKey;

	private final ITimeProvider timeInfo;

	private Timer timer;

	private IStatusBarProvider provider;

	public StatusBarCustomizer(BundleContext context, ITimeProvider timeInfo) {
		this.context = context;
		this.timeInfo = timeInfo;

	}

	public Object addingService(ServiceReference reference) {
		provider = (IStatusBarProvider) context.getService(reference);

		// display time on screen.
		timer = new Timer();
		timeKey = provider.acquireRegion(5);
		if (timeKey != null) {
			timer.scheduleAtFixedRate(new UpdateClock(timeKey, provider), 0, 1000);
		}

		return provider;
	}

	public void modifiedService(ServiceReference reference, Object service) {

	}

	public void removedService(ServiceReference reference, Object service) {
		timer.cancel();
		provider.releaseRegion(timeKey);
	}
}