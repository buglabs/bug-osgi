package com.buglabs.bug.sysfs;

import java.io.File;
import java.io.IOException;

/**
 * Access sysfs items for video module.
 * WIP
 * 
 * @author kgilmer
 *
 */
public final class VideoOutDevice extends BMIDevice {

	
	public VideoOutDevice(File directory, int slot) {
		super(directory, slot);
	}

	/*
	 * resolution
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
	

	public boolean isVGA() {
		return getFirstLineofFile(new File(root, VMODE_FILENAME)).equals(VGA_MODE);
	}

	public boolean isDVI() {
		return getFirstLineofFile(new File(root, VMODE_FILENAME)).equals(DVI_MODE);
	}
	
	public boolean setVGA() {
		try {
			println(new File(root, VMODE_FILENAME), VGA_MODE);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean setDVI() {
		try {
			println(new File(root, VMODE_FILENAME), DVI_MODE);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
