package com.buglabs.bug.sysfs;

import java.awt.Point;
import java.io.File;

/**
 * Access sysfs items for video module.
 * WIP
 * 
 * @author kgilmer
 *
 */
public final class VideoOutDevice extends BMIDevice {

	private static final File OUTPUT_FILE = new File("videoout");
	
	public VideoOutDevice(File directory, int slot) {
		super(directory, slot);
	}

	public String getVideoOutput() {
		//hypothetical
		return this.getFirstLineofFile(OUTPUT_FILE);
	}
	
	public Point getResolution() {
		return null;
	}
}
