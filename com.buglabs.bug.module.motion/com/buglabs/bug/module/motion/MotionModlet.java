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
package com.buglabs.bug.module.motion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.bug.accelerometer.pub.AccelerometerConfiguration;
import com.buglabs.bug.accelerometer.pub.IAccelerometerControl;
import com.buglabs.bug.accelerometer.pub.IAccelerometerRawFeed;
import com.buglabs.bug.accelerometer.pub.IAccelerometerSampleFeed;
import com.buglabs.bug.accelerometer.pub.IAccelerometerSampleProvider;
import com.buglabs.bug.bmi.api.AbstractBUGModlet;
import com.buglabs.bug.bmi.sysfs.BMIDevice;
import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.bug.dragonfly.module.IModuleLEDController;
import com.buglabs.bug.jni.accelerometer.Accelerometer;
import com.buglabs.bug.jni.common.CharDeviceInputStream;
import com.buglabs.bug.jni.common.CharDeviceUtils;
import com.buglabs.bug.jni.motion.MDACCControl;
import com.buglabs.bug.jni.motion.Motion;
import com.buglabs.bug.module.motion.pub.AccelerationWS;
import com.buglabs.bug.module.motion.pub.IMDACCModuleControl;
import com.buglabs.bug.module.motion.pub.IMotionRawFeed;
import com.buglabs.bug.module.motion.pub.IMotionSubject;
import com.buglabs.bug.module.motion.pub.MotionWS;
import com.buglabs.services.ws.PublicWSProvider;

/**
 * Modlet for the MOTION module.
 * @author kgilmer
 *
 */
public class MotionModlet extends AbstractBUGModlet implements IMDACCModuleControl, IModuleLEDController {
	// default is 0; make ours +1 since better than BUGview at accelerometer functionality.
	private final static int OUR_ACCELEROMETER_SERVICES_RANKING = 1;

	private ServiceRegistration moduleRef;

	private MotionRawFeed motiond;

	private AccelerometerSimpleRawFeed acceld;

	private Motion motionDevice;

	private InputStream motionIs;

	private InputStream accIs;

	private MotionSubject motionSubject;

	private Accelerometer accDevice;

	private MDACCControl mdaccControlDevice;

	private AccelerometerControl accControl;

	private AccelerometerSampleProvider asp;
	private boolean suspended;

	public MotionModlet(BundleContext context, int slotId, String moduleId, String moduleName, BMIDevice device) {
		super(context, moduleId, device, "MOTION");	
	}

	public void start() throws Exception {		
		Dictionary modProperties = getCommonProperties();
		modProperties.put("Power State", suspended ? "Suspended": "Active");
		moduleRef = context.registerService(IModuleControl.class.getName(), this, modProperties);

		motionSubject.start();

		registerService(IMotionSubject.class.getName(), motionSubject, getCommonProperties());
		registerService(IMotionRawFeed.class.getName(), motiond, getCommonProperties());
		registerService(IModuleLEDController.class.getName(), this, getCommonProperties());

		MotionWS motionWS = new MotionWS();
		motionSubject.register(motionWS);
		registerService(PublicWSProvider.class.getName(), motionWS, null);		

		configureAccelerometer();

		registerService(IAccelerometerRawFeed.class.getName(), acceld, createServicePropertiesWithRanking(OUR_ACCELEROMETER_SERVICES_RANKING));
		asp = new AccelerometerSampleProvider(acceld);
		registerService(IAccelerometerSampleProvider.class.getName(), asp, createServicePropertiesWithRanking(OUR_ACCELEROMETER_SERVICES_RANKING));
		registerService(IAccelerometerSampleFeed.class.getName(), acceld, createServicePropertiesWithRanking(OUR_ACCELEROMETER_SERVICES_RANKING));
		registerService(IAccelerometerControl.class.getName(), accControl, createServicePropertiesWithRanking(OUR_ACCELEROMETER_SERVICES_RANKING));
		AccelerationWS accWs = new AccelerationWS(asp, getLog());
		registerService(PublicWSProvider.class.getName(), accWs, null);
		registerService(IMDACCModuleControl.class.getName(), this, getCommonProperties());
	}

	private void configureAccelerometer() {
		AccelerometerConfiguration config = accDevice.ioctl_BMI_MDACC_ACCELEROMETER_GET_CONFIG();
		config.setDelay((short) 250);
		config.setDelayResolution((byte) 5);
		config.setDelayMode((byte) 1);
		config.setRun((byte) 1);
		accDevice.ioctl_BMI_MDACC_ACCELEROMETER_SET_CONFIG(config);
	}

	public void stop() throws Exception {
		moduleRef.unregister();
		motionSubject.interrupt();
		motionDevice.ioctl_BMI_MDACC_MOTION_DETECTOR_STOP();
		motionIs.close();
		motionDevice.close();
		mdaccControlDevice.close();	
		asp.close();
		accDevice.ioctl_BMI_MDACC_ACCELEROMETER_STOP();
		accIs.close();
		super.stop();
	}
	
	private Dictionary createServicePropertiesWithRanking(final int serviceRanking) {
		final Dictionary p = getCommonProperties();
		p.put(Constants.SERVICE_RANKING, new Integer(serviceRanking));
		return p;
	}
	private void updateIModuleControlProperties(){
		if (moduleRef!=null){
			Dictionary modProperties = getCommonProperties();
			modProperties.put("Power State", suspended ? "Suspended": "Active");
			moduleRef.setProperties(modProperties);
		}
	}

	public int resume() throws IOException {
		int result = -1;

		result = mdaccControlDevice.ioctl_BMI_MDACC_CTL_RESUME();
		if (result < 0) {
			throw new IOException("ioctl BMI_MDACC_CTL_RESUME failed");
		}
		suspended = false;
		updateIModuleControlProperties();
		return result;
	}
	

	public int suspend() throws IOException {
		int result = -1;

		result = mdaccControlDevice.ioctl_BMI_MDACC_CTL_SUSPEND();

		if (result < 0) {
			throw new IOException("ioctl BMI_MDACC_CTL_SUSPEND failed");
		}
		suspended = true;
		updateIModuleControlProperties();
		return result;
	}


	public void setup() throws Exception {
		int slot = getSlotId();
		String devnode_motion = "/dev/bmi_mdacc_mot_m" + slot;
		String devnode_acc = "/dev/bmi_mdacc_acc_m" + slot;
		String devnode_mdacc_control = "/dev/bmi_mdacc_ctl_m" + slot;
		motionDevice = new Motion();
		CharDeviceUtils.openDeviceWithRetry(motionDevice, devnode_motion, 2);

		int retval = motionDevice.ioctl_BMI_MDACC_MOTION_DETECTOR_RUN();
		if (retval < 0) {
			throw new IOException("IOCTL Failed on: " + devnode_motion);
		}

		accDevice = new Accelerometer();
		CharDeviceUtils.openDeviceWithRetry(accDevice, devnode_acc, 2);

		accIs = new CharDeviceInputStream(accDevice);
		
		accControl = new AccelerometerControl(accDevice);
		acceld = new AccelerometerSimpleRawFeed(accIs, accControl);

		mdaccControlDevice = new MDACCControl();
		CharDeviceUtils.openDeviceWithRetry(mdaccControlDevice, devnode_mdacc_control, 2);

		motionIs = new CharDeviceInputStream(motionDevice);
		motiond = new MotionRawFeed(motionIs);
		motionSubject = new MotionSubject(motiond.getInputStream(), this, getLog());
	}

	public int setLEDGreen(boolean state) throws IOException {
		if (mdaccControlDevice == null) {
			return -1;
		} else if (state) {
			return mdaccControlDevice.ioctl_BMI_MDACC_CTL_GREEN_LED_ON();
		} else {
			return mdaccControlDevice.ioctl_BMI_MDACC_CTL_GREEN_LED_OFF();
		}
	}

	public int setLEDRed(boolean state) throws IOException {
		if (mdaccControlDevice == null) {
			return -1;
		} else if (state) {
			return mdaccControlDevice.ioctl_BMI_MDACC_CTL_RED_LED_ON();
		} else {
			return mdaccControlDevice.ioctl_BMI_MDACC_CTL_RED_LED_OFF();
		}
	}

	public boolean isSuspended() {	
		return suspended;
	}
}
