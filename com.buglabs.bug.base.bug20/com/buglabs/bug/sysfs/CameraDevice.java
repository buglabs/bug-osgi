package com.buglabs.bug.sysfs;

import java.io.File;

/**
 * A work-in-progress for sysfs class for java clients for the Camera BMI module.
 * @author kgilmer
 *
 */
public final class CameraDevice extends BMIDevice {

	/**
	 * @param directory base BMI directory
	 * @param slot slot index
	 */
	public CameraDevice(File directory, int slot) {
		super(directory, slot);
	}
}
