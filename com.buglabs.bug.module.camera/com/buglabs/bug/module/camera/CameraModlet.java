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
package com.buglabs.bug.module.camera;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

import com.buglabs.bug.bmi.pub.BMIModuleProperties;
import com.buglabs.bug.bmi.pub.IModlet;
import com.buglabs.bug.dragonfly.module.IModuleControl;
import com.buglabs.bug.dragonfly.module.IModuleLEDController;
import com.buglabs.bug.dragonfly.module.IModuleProperty;
import com.buglabs.bug.dragonfly.module.ModuleProperty;
import com.buglabs.bug.jni.camera.Camera;
import com.buglabs.bug.jni.camera.CameraControl;
import com.buglabs.bug.module.camera.pub.ICamera2Device;
import com.buglabs.bug.module.camera.pub.ICamera2ModuleControl;
import com.buglabs.bug.module.camera.pub.ICameraModuleControl;
import com.buglabs.services.ws.IWSResponse;
import com.buglabs.services.ws.PublicWSDefinition;
import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.services.ws.PublicWSProvider2;
import com.buglabs.services.ws.WSResponse;
import com.buglabs.util.osgi.LogServiceUtil;

/**
 * 
 * @author kgilmer
 * 
 */
public class CameraModlet implements IModlet, ICamera2Device, PublicWSProvider2, IModuleControl {
	private static final String JPEG_MIME_TYPE = "image/jpg";
	private static boolean suspended = false;

	private List modProps;

	private final BundleContext context;

	private final int slotId;

	private final String moduleName;

	private ServiceRegistration moduleControlReg;

	private ServiceRegistration camera2DeviceReg;

	private LogService logService;

	private Camera camera;
	
	protected static final String PROPERTY_MODULE_NAME = "moduleName";

	private String moduleId;

	private CameraModuleControl cameraControl;

	private ServiceRegistration cameraModuleControlReg;
	private ServiceRegistration camera2ModuleControlReg;

	private CameraControl cc;

	private ServiceRegistration moduleLedControllerReg;
	private String pictureServiceName = "Picture";
	private BMIModuleProperties properties;
	private ServiceRegistration pictureWSReg;
	private ServiceRegistration cameraControlWSReg;
	
	private boolean isCameraOpen = false;
	private boolean isCameraStarted = false;
	
	private int previewsGrabbed = 0;
	private int fullsGrabbed = 0;
	
	int getPreviewsGrabbed() {
		return previewsGrabbed;
	}
	
	int getFullsGrabbed() {
		return fullsGrabbed;
	}

	public CameraModlet(BundleContext context, int slotId, String moduleId) {
		this.context = context;
		this.slotId = slotId;
		this.moduleId = moduleId;
		this.moduleName = "Camera";
		this.properties = null;
	}

	public CameraModlet(BundleContext context, int slotId, String moduleId, BMIModuleProperties properties) {
		this(context, slotId, moduleId);
		this.properties = properties;
	}

	public String getModuleId() {
		return moduleId;
	}

	public int getSlotId() {
		return slotId;
	}

	public void setup() throws Exception {
		logService = LogServiceUtil.getLogService(context);
	}

	public void start() throws Exception {
		modProps = new ArrayList();

		camera = new Camera();
		cc = new CameraControl();
		cameraControl = new CameraModuleControl(cc, this);
		cameraModuleControlReg = context.registerService(ICameraModuleControl.class.getName(), cameraControl, createBasicServiceProperties());
		camera2ModuleControlReg = context.registerService(ICamera2ModuleControl.class.getName(), cameraControl, createBasicServiceProperties());
		Dictionary modProperties = createBasicServiceProperties();
		modProperties.put("Power State", suspended ? "Suspended": "Active");
		moduleControlReg = context.registerService(IModuleControl.class.getName(), this, modProperties);
		camera2DeviceReg = context.registerService(ICamera2Device.class.getName(), this, createBasicServiceProperties());
		moduleLedControllerReg = context.registerService(IModuleLEDController.class.getName(), cameraControl, createBasicServiceProperties());
		pictureWSReg = context.registerService(PublicWSProvider.class.getName(), this, null);
		cameraControlWSReg = context.registerService(PublicWSProvider.class.getName(), cameraControl, null);
	}

	private Dictionary createBasicServiceProperties() {
		Dictionary p = new Hashtable();
		p.put("Provider", this.getClass().getName());
		p.put("Slot", Integer.toString(slotId));

		if (properties != null) {
			if (properties.getDescription() != null)
				p.put("ModuleDescription", properties.getDescription());
			
			if (properties.getSerial_num() != null)
				p.put("ModuleSN", properties.getSerial_num());
			
			// these are ints so don't need a null check
			p.put("ModuleVendorID", "" + properties.getVendor());			
			p.put("ModuleRevision", "" + properties.getRevision());
		}
		
		return p;
	}

	private void updateIModuleControlProperties(){
		if (moduleControlReg!=null){
			Dictionary modProperties = createBasicServiceProperties();
			modProperties.put("Power State", suspended ? "Suspended": "Active");
			moduleControlReg.setProperties(modProperties);
		}
	}

	public void stop() throws Exception {
		cameraStop();
		cameraClose();

		cameraModuleControlReg.unregister();
		camera2ModuleControlReg.unregister();
		camera2DeviceReg.unregister();
		moduleControlReg.unregister();
		moduleLedControllerReg.unregister();
		pictureWSReg.unregister();
		cameraControlWSReg.unregister();
		camera.close();
	}

	public PublicWSDefinition discover(int operation) {
		if (operation == PublicWSProvider2.GET) {
			return new PublicWSDefinition() {

				public List getParameters() {
					return null;
				}

				public String getReturnType() {
					return JPEG_MIME_TYPE;
				}
			};
		}

		return null;
	}

	public IWSResponse execute(int operation, String input) {
		if (operation == PublicWSProvider2.GET) {
			
			// open it if we need to
			cameraOpenDefault();
			cameraStart();
			
			// we'll leave the camera running
			return new WSResponse(new ByteArrayInputStream(grabFull()), JPEG_MIME_TYPE);
		}
		return null;
	}

	public String getPublicName() {
		return pictureServiceName;
	}

	public List getModuleProperties() {
		modProps.clear();

		modProps.add(new ModuleProperty(PROPERTY_MODULE_NAME, getModuleName()));
		modProps.add(new ModuleProperty("Slot", "" + slotId));
		modProps.add(new ModuleProperty("Power State", suspended ? "Suspended": "Active", "String", true));
		
		if (properties != null) {
			modProps.add(new ModuleProperty("Module Description", properties.getDescription()));
			modProps.add(new ModuleProperty("Module SN", properties.getSerial_num()));
			modProps.add(new ModuleProperty("Module Vendor ID", "" + properties.getVendor()));
			modProps.add(new ModuleProperty("Module Revision", "" + properties.getRevision()));
		}
		
		return modProps;
	}

	public String getModuleName() {
		return moduleName;
	}

	public boolean setModuleProperty(IModuleProperty property) {
		if (!property.isMutable()) {
			return false;
		}
		if (property.getName().equals("Power State")) {
			if (((String) property.getValue()).equals("Suspend")){
				try{
				suspend();
				}
			 catch (IOException e) {
				 LogServiceUtil.logBundleException(logService, e.getMessage(), e);
			}
			}
			else if (((String) property.getValue()).equals("Resume")){
				
				try {
					resume();
				} catch (IOException e) {
					LogServiceUtil.logBundleException(logService, e.getMessage(), e);
				}
			}
		}
		
		return false;
	}
	
	public int resume() throws IOException {
		int result = -1;

		result = cc.ioctl_BMI_CAM_RESUME();
		suspended = false;
		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_RESUME failed");
		}
		suspended = false;
		updateIModuleControlProperties();
		return result;
	}
	

	public int suspend() throws IOException {
		int result = -1;

		result = cc.ioctl_BMI_CAM_SUSPEND();
		if (result < 0) {
			throw new IOException("ioctl BMI_CAM_SUSPEND failed");
		}
		suspended = true;
		updateIModuleControlProperties();
		return result;
	}

	public String getFormat() {

		return JPEG_MIME_TYPE;
	}

	public String getDescription() {
		return "This service can return image data from a hardware camera.";
	}

	public void setPublicName(String name) {
		pictureServiceName = name;
	}
	
	public boolean isCameraOpen() {
		return isCameraOpen;
	}
	
	public boolean isCameraStarted() {
		return isCameraStarted;
	}
	
	public synchronized int cameraOpenDefault()
	{
		return cameraOpen(ICamera2Device.DEFAULT_MEDIA_NODE, -1, 2048, 1536, 320, 240);
	}
	
	public synchronized int cameraOpen(
			final String media_node,
			int slot_num,
			int full_height,
			int full_width,
			int preview_height,
			int preview_width)
	{
		if (isCameraOpen) {
			return 0;
		}
		final long before = System.currentTimeMillis();
		final int ret = camera.bug_camera_open(media_node, slot_num, full_height, full_width, preview_height, preview_width);
		final long after = System.currentTimeMillis();
		System.out.println("TIMING: bug_camera_open took " + (after-before) + "ms");
		isCameraOpen = (ret == 0);
		return ret;
	}

	public synchronized int cameraClose()
	{
		if (!isCameraOpen) {
			return 0;
		}
		
		final int ret = camera.bug_camera_close();
		isCameraOpen = !(ret == 0);
		return ret;
	}

	public synchronized int cameraStart()
	{
		if (isCameraStarted) {
			return 0;
		}
		
		final long before = System.currentTimeMillis();
		final int ret = camera.bug_camera_start();
		final long after = System.currentTimeMillis();
		System.out.println("TIMING: bug_camera_start took " + (after-before) + "ms");
		isCameraStarted = (ret == 0);
		return ret;
	}
	
	public synchronized int cameraStop()
	{
		if (!isCameraStarted) {
			return 0;
		}
		final int ret = camera.bug_camera_stop();
		isCameraStarted = !(ret == 0);
		return ret;
	}

	public synchronized boolean grabPreview(int [] pixelBuffer)
	{
		previewsGrabbed++;
		return camera.bug_camera_grab_preview(pixelBuffer);
	}

	public synchronized byte[] grabFull()
	{
		final long before = System.currentTimeMillis();
		final int flushed = camera.bug_camera_flush_queue();
		final long after = System.currentTimeMillis();
		System.out.println("TIMING: bug_camera_flush_queue took " + (after-before) + "ms");
		if (flushed != 0) {
			return null;
		}
		
		fullsGrabbed++;
		final long before2 = System.currentTimeMillis();
		final byte [] jpeg = camera.bug_camera_grab_raw();
		final long after2 = System.currentTimeMillis();
		System.out.println("TIMING: bug_camera_grab_full took " + (after2-before2) + "ms");
		return jpeg;
	}
}
