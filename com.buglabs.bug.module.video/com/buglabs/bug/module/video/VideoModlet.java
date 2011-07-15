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

import java.awt.Frame;
import java.io.IOException;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import com.buglabs.bug.bmi.api.AbstractBUGModlet;
import com.buglabs.bug.bmi.sysfs.BMIDevice;
import com.buglabs.bug.bmi.sysfs.BMIDeviceHelper;
import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.bug.dragonfly.module.IModuleProperty;
import com.buglabs.bug.module.video.pub.IVideoModuleControl;
import com.buglabs.bug.module.video.pub.VideoOutBMIDevice;
import com.buglabs.services.ws.IWSResponse;
import com.buglabs.services.ws.PublicWSDefinition;
import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.services.ws.PublicWSProvider2;
import com.buglabs.services.ws.PublicWSProviderWithParams;
import com.buglabs.services.ws.WSResponse;
import com.buglabs.util.xml.XmlNode;

/**
 * Video Modlet class.
 * 
 * @author dfindlay
 * 
 */
public class VideoModlet extends AbstractBUGModlet implements IVideoModuleControl, com.buglabs.bug.module.lcd.pub.IModuleDisplay, PublicWSProviderWithParams {
	private String serviceName = "Video";
	
	private boolean suspended;

	private VideoOutBMIDevice videoOutDevice;

	public VideoModlet(BundleContext context, int slotId, String moduleId, BMIDevice properties2) {
		super(context, moduleId, properties2, "VIDEO");
	}

	public void setup() throws Exception {
		this.videoOutDevice = (VideoOutBMIDevice) BMIDeviceHelper.getDevice(context, getSlotId());
	}

	public void start() throws Exception {
		Dictionary modProperties = getCommonProperties();
		modProperties.put("Power State", suspended ? "Suspended" : "Active");
		
		registerService(IModuleControl.class.getName(), this, modProperties);
		registerService(IVideoModuleControl.class.getName(), this, modProperties);
		registerService(com.buglabs.bug.module.lcd.pub.IModuleDisplay.class.getName(), this, modProperties);
		registerService(PublicWSProvider.class.getName(), this, null);
	}

	public void stop() throws Exception {
		super.stop();
	}
	
	public boolean setModuleProperty(IModuleProperty property) {
		if (!property.isMutable()) {
			return false;
		}
		if (property.getName().equals("State")) {
			return true;
		}
		if (property.getName().equals("Power State")) {
			if (((String) property.getValue()).equals("Suspend")) {

				if (! (suspend() == 1)) {
					getLog().log(LogService.LOG_ERROR, "An error occured while changing suspend state.");
				}
			} else if (((String) property.getValue()).equals("Resume")) {
				if (! (resume() == 1)) {
					getLog().log(LogService.LOG_ERROR, "An error occured while changing suspend state.");
				} 
			}
		}

		return false;
	}

	public int resume() {
		try {
			videoOutDevice.resume();
			return 1;
		} catch (IOException e) {
			return 0;
		}
	}

	public int suspend() {
		try {
			videoOutDevice.suspend();
			return 1;
		} catch (IOException e) {
			return 0;
		}		
	}
	
	public Frame getFrame() {
		int[] ds = getDisplaySize();
		Frame frame = new Frame();
		frame.setSize(ds[0], ds[1]);
		frame.setResizable(false);
		frame.setVisible(true);
		return frame;
	}

	public boolean isVGA() {
		return videoOutDevice.isVGA();
	}

	public boolean isDVI() {
		return videoOutDevice.isDVI();
	}

	public boolean setVGA() {
		return videoOutDevice.setVGA();
	}

	public boolean setDVI() {
		return videoOutDevice.setDVI();
	}

	@Override
	public PublicWSDefinition discover(int operation) {
		if (operation == PublicWSProvider2.GET) {
			return new PublicWSDefinition() {

				public List<String> getParameters() {
					return null;
				}

				public String getReturnType() {
					return "text/xml";
				}
			};
		}

		return null;
	}

	@Override
	public IWSResponse execute(int operation, String input) {
		// not called because we implement the extended one below
		return null;
	}

	@Override
	public IWSResponse execute(int operation, String input, Map<String, String> get, Map<String, String> post) {
		try {
			if (get.containsKey("suspend")) {
				videoOutDevice.suspend();
			}
			if (get.containsKey("resume")) {
				videoOutDevice.resume();
			}
			if (get.containsKey("dvi")) {
				videoOutDevice.setDVI();
			}
			if (get.containsKey("vga")) {
				videoOutDevice.setVGA();
			}
			
			for (Object key : get.keySet()) {
				System.out.println(key + "=" + get.get(key));
			}
			System.out.println("post map");
			for (Object key : post.keySet()) {
				System.out.println(key + "=" + post.get(key));
			}
			
			if (operation == PublicWSProvider2.GET) {
				return new WSResponse(getVideoInfoXml(), "text/xml");
			}
			return null;
		} catch (IOException e) {
			getLog().log(LogService.LOG_ERROR, "Failed to execute web service.", e);
			return new WSResponse(0, "Failed to execute web service: " + e.getMessage());
		}
	}

	@Override
	public String getPublicName() {
		return serviceName;
	}

	@Override
	public String getDescription() {
		return "This service can return video display information.";

	}

	@Override
	public void setPublicName(String name) {
		serviceName = name;
	}
	
	private String getVideoInfoXml() {
		XmlNode root = new XmlNode("VideoInfo");

		root.addChild(new XmlNode("Mode", isVGA() ? "VGA" : "DVI"));
		root.addChild(new XmlNode("Resolution", getResolution()));
		
		return root.toString();
	}

	@Override
	public String getResolution() {
		return videoOutDevice.getResolution();
	}

	@Override
	public boolean isSuspended() {
		return suspended;
	}

	@Override
	public int[] getDisplaySize() {	
		int[] s = new int[2];
		String [] elems = videoOutDevice.getResolution().split("x");
		s[0] = Integer.parseInt(elems[0]);
		s[1] = Integer.parseInt(elems[1]);
		
		return s;
	}
}
