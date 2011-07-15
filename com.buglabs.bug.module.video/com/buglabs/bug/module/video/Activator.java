/*******************************************************************************
 * Copyright (c) 2010 Bug Labs, Inc.
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
package com.buglabs.bug.module.video;

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.bug.bmi.api.AbstractBUGModuleActivator;
import com.buglabs.bug.bmi.api.BMIModuleProperties;
import com.buglabs.bug.bmi.api.IModlet;
import com.buglabs.bug.bmi.sysfs.BMIDevice;
import com.buglabs.bug.bmi.sysfs.BMIDeviceNodeFactory;
import com.buglabs.bug.module.video.pub.VideoOutBMIDevice;

public class Activator extends AbstractBUGModuleActivator {

	private ServiceRegistration sysfsSr;


	public void start(BundleContext context) throws Exception {
		super.start(context);
	
		Dictionary d = new Hashtable();
		d.put(BMIDeviceNodeFactory.MODULE_ID_SERVICE_PROPERTY, getModuleId());
		sysfsSr = context.registerService(BMIDeviceNodeFactory.class.getName(), new VideoBMIDeviceNodeFactory(), d);
	}

	public void stop(BundleContext context) throws Exception {
		sysfsSr.unregister();
		super.stop(context);
	}

	public IModlet createModlet(BundleContext context, int slotId, BMIDevice properties) {
		return new VideoModlet(context, slotId, getModuleId(), properties);
	}

	private class VideoBMIDeviceNodeFactory implements BMIDeviceNodeFactory {

		@Override
		public BMIDevice createBMIDeviceNode(File baseDirectory, int slotIndex) {
			return new VideoOutBMIDevice(baseDirectory, slotIndex);
		}
	}
}
