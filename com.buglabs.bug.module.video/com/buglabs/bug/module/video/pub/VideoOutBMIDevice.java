package com.buglabs.bug.module.video.pub;

import java.io.File;
import java.io.IOException;

import org.osgi.service.log.LogService;

import com.buglabs.bug.module.video.Activator;
import com.buglabs.bug.sysfs.BMIDevice;

/**
 * Access sysfs items for video module.
 * WIP
 * 
 * @author kgilmer
 *
 */
public final class VideoOutBMIDevice extends BMIDevice {

	
	/**
	 * @param directory of BMI device
	 * @param slot slot index
	 */
	public VideoOutBMIDevice(File directory, int slot) {
		super(directory, slot);
	}

	/*
	 * resolution
	 */

	/**
	 * @return String representing resolution in "<width>x<height>" format. 
	 */
	public String getResolution() {
		// TODO: really read it when exposed by driver
		return "1280x1024";
	}
	
	/*
	 * mode
	 */
	private static final String VMODE_FILENAME = "vmode";
	private static final String DVI_MODE = "dvi";
	private static final String VGA_MODE = "vga";
	

	/**
	 * @return true if in VGA mode.
	 */
	public boolean isVGA() {
		return getFirstLineofFile(new File(root, VMODE_FILENAME)).equals(VGA_MODE);
	}

	/**
	 * @return true if in DVI mode.
	 */
	public boolean isDVI() {
		return getFirstLineofFile(new File(root, VMODE_FILENAME)).equals(DVI_MODE);
	}
	
	/**
	 * @return true if enabled VGA mode.
	 */
	public boolean setVGA() {
		try {
			println(new File(root, VMODE_FILENAME), VGA_MODE);
		} catch (IOException e) {
			Activator.getLog().log(LogService.LOG_ERROR, "Unable to set VGA mode.", e);
			return false;
		}
		return true;
	}
	
	/**
	 * @return true if enabled DVI mode.
	 */
	public boolean setDVI() {
		try {
			println(new File(root, VMODE_FILENAME), DVI_MODE);
		} catch (IOException e) {
			Activator.getLog().log(LogService.LOG_ERROR, "Unable to set VGA mode.", e);
			return false;
		}
		return true;
	}
}
