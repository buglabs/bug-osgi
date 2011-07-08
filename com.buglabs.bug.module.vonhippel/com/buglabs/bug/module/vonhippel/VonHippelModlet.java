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
package com.buglabs.bug.module.vonhippel;

import java.io.IOException;
import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.bug.bmi.pub.AbstractBUGModlet;
import com.buglabs.bug.bmi.sysfs.BMIDevice;
import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.bug.dragonfly.module.IModuleLEDController;
import com.buglabs.bug.jni.common.CharDeviceUtils;
import com.buglabs.bug.jni.vonhippel.VonHippel;
import com.buglabs.bug.module.vonhippel.pub.IVonHippelModuleControl;
import com.buglabs.bug.module.vonhippel.pub.VonHippelWS;
import com.buglabs.services.ws.PublicWSProvider;

/**
 * Modlet for von Hippel Module
 */
public class VonHippelModlet extends AbstractBUGModlet {
	private ServiceRegistration moduleRef;

	private VonHippel vhDevice;

	private VonHippelModuleControl vhc;

	private boolean suspended;

	public VonHippelModlet(BundleContext context, int slotId, String moduleId, String moduleName, BMIDevice properties) {
		super(context, moduleId, properties, moduleName);		
	}

	public void start() throws Exception {
		Dictionary modProperties = getCommonProperties();
		modProperties.put("Power State", suspended ? "Suspended": "Active");
		moduleRef = context.registerService(IModuleControl.class.getName(), this, modProperties);
		registerService(IVonHippelModuleControl.class.getName(), vhc, getCommonProperties());
		registerService(IModuleLEDController.class.getName(), vhc, getCommonProperties());
		registerService(PublicWSProvider.class.getName(), new VonHippelWS(vhc), null);
	}

	public void stop() throws Exception {
		//close any open resources

		if (moduleRef != null) {
			moduleRef.unregister();
		}
	
		//close /dev/bmi_vh_ctl_m#
		if (vhDevice != null ){
			vhDevice.close();
		}
		super.stop();
	}

	/**
	 * 
	 */
	private void updateIModuleControlProperties(){
		if (moduleRef!=null){
			Dictionary modProperties = getCommonProperties();
			modProperties.put("Power State", suspended ? "Suspended": "Active");
			moduleRef.setProperties(modProperties);
		}
	}


	/* (non-Javadoc)
	 * @see com.buglabs.bug.dragonfly.module.IModuleControl#resume()
	 */
	public int resume() throws IOException {
		int result = -1;

		result = vhDevice.ioctl_BMI_VH_RESUME();

		if (result < 0) {
			throw new IOException("ioctl BMI_VH_RESUME failed");
		}
		suspended = false;
		updateIModuleControlProperties();
		return result;
	}
	

	/* (non-Javadoc)
	 * @see com.buglabs.bug.dragonfly.module.IModuleControl#suspend()
	 */
	public int suspend() throws IOException {
		int result = -1;

		result = vhDevice.ioctl_BMI_VH_SUSPEND();

		if (result < 0) {
			throw new IOException("ioctl BMI_VH_SUSPEND failed");
		}
		suspended = true;
		updateIModuleControlProperties();
		return result;
	}

	/* (non-Javadoc)
	 * @see com.buglabs.bug.bmi.pub.AbstractBUGModlet#setup()
	 */
	public void setup() throws Exception {
		int slot = getSlotId();
		String devnode_vh = "/dev/bmi_vh_control_m" + slot;
		vhDevice = new VonHippel();
		CharDeviceUtils.openDeviceWithRetry(vhDevice, devnode_vh, 2);
		vhc = new VonHippelModuleControl(vhDevice, getSlotId());	
	}

	@Override
	public boolean isSuspended() {
		return suspended;
	}

}
