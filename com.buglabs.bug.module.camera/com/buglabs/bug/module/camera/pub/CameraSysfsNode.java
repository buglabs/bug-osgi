package com.buglabs.bug.module.camera.pub;

import java.io.File;

import com.buglabs.bug.bmi.sysfs.BMIDevice;

/**
 * A work-in-progress for sysfs class for java clients for the Camera BMI module.
 * @author kgilmer
 *
 */
public final class CameraSysfsNode extends BMIDevice {

	/**
	 * @param directory base BMI directory
	 * @param slot slot index
	 */
	public CameraSysfsNode(File directory, int slot) {
		super(directory, slot);
	}
}
