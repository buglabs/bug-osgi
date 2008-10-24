package com.buglabs.bug.module.camera;

import org.osgi.service.log.LogService;

import com.buglabs.bug.input.pub.InputEventProvider;
import com.buglabs.bug.module.camera.pub.ICameraButtonEventProvider;

public class CameraInputEventProvider extends InputEventProvider implements ICameraButtonEventProvider {

	public CameraInputEventProvider(String inputDevice, LogService log) {
		super(inputDevice, log);
	}

}
