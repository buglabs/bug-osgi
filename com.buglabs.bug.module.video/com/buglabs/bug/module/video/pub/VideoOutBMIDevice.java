package com.buglabs.bug.module.video.pub;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.Arrays;

import org.osgi.service.log.LogService;

import com.buglabs.bug.bmi.sysfs.BMIDevice;
import com.buglabs.bug.module.video.Activator;

/**
 * Access sysfs items for video module.
 * WIP
 * 
 * @author kgilmer
 *
 */
public final class VideoOutBMIDevice extends BMIDevice {

	
    /** @param filePath the name of the file to open. Not sure if it
	 * can accept URLs or just filenames. Path handling could be better, and buffer sizes are hardcoded
    */
    private static String readFileAsString(File file)  throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(file) );
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
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

	/* sysfs filenames for monitor EDID data */
	private static final String	MONITOR_INFO_FILENAME_DVI = "dvi_edid";
	private static final String	MONITOR_INFO_FILENAME_VGA = "vga_edid";


	/**
	 * @return String representing resolution in "<width>x<height>" format. 
	 */
	public String getResolution() {
		// TODO: really read it when exposed by driver
		return "1280x1024";
	}
	
	public String[] getMonitorResolutions() {

		String tmp = null;
		//try to read edid_info stream
		if(this.isOFF())
			return (String[]) null;
		else if (this.isVGA()) {
			try {
				tmp = readFileAsString(new File(root, MONITOR_INFO_FILENAME_VGA) );
			} catch (IOException e) {
				Activator.getLog().log(LogService.LOG_ERROR, "Unable to read VGA modes.", e);
				return (String[]) null;
			}
		}
		else if (this.isDVI) {
			try {
				tmp = readFileAsString(new File(root, MONITOR_INFO_FILENAME_VGA) );
			} catch (IOException e) {
				Activator.getLog().log(LogService.LOG_ERROR, "Unable to read DVI modes.", e);
				return (String[]) null;
			}
		}

		// tmp is empty, or a null flag
		if(tmp == null || tmp.length() == 0 ||  tmp.equals("(none)") )
			return (String[]) null;

		int startModes = 0, endModes = 0;

		// on read, parse out =\nMODE INFO\n====\n <DATA> ==\nDisplay Char
	    String CRLF = System.getProperty("line.separator");

		String modeStart ="MODE INFO"+CRLF+"===="+CRLF;
		startModes = tmp.indexOf(modeStart);
		if( startModes > 0) {
			startModes += modeStart.length();
			endModes = tmp.indexOf( "== Display Char",startModes);
			String modes = 	tmp.substring(startModes, endModes);
			//split <data> into lines, and return them
			return modes.split(System.getProperty("line.separator"));
		}
		Activator.getLog().log(LogService.LOG_ERROR, "could not find modes section of edid.", e);
		return (String[]) null;
	}


	/* sysfs filenames and constants for monitor modes */
	private static final String VMODE_FILENAME = "vmode";
	private static final String DVI_MODE = "DVI";
	private static final String VGA_MODE = "VGA";
	private static final String OFF_MODE= "OFF";
	private static final String AUTO_MODE = "AUTO";

	

	/**
	 * @return true if in VGA mode.
	 */
	public boolean isVGA() {
		String modeSentenceVGA = "DVI [VGA] OFF AUTO";
		return getFirstLineofFile(new File(root, VMODE_FILENAME)).equals(modeSentenceVGA);
	}

	/**
	 * @return true if in DVI mode.
	 */
	public boolean isDVI() {
		String modeSentenceDVI = "[DVI] VGA OFF AUTO";
		return getFirstLineofFile(new File(root, VMODE_FILENAME)).equals(modeSentenceDVI);
	}
	
	/**
	 * @return true if in DVI mode.
	 */
	public boolean isOff() {
		String modeSentenceOff= "DVI VGA [OFF] AUTO";
		return getFirstLineofFile(new File(root, VMODE_FILENAME)).equals(modeSentenceOff);
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


	/**
	 * @return true if video disabled.
	 */
	public boolean setOff() {
		try {
			println(new File(root, VMODE_FILENAME), OFF_MODE);
		} catch (IOException e) {
			Activator.getLog().log(LogService.LOG_ERROR, "Unable to set Off mode.", e);
			return false;
		}
		return true;
	}


	/**
	 * @return true if video autodetect worked OK
	 */
	public boolean setAuto() {
		try {
			println(new File(root, VMODE_FILENAME), AUTO_MODE);
		} catch (IOException e) {
			Activator.getLog().log(LogService.LOG_ERROR, "Unable to set autodetect mode.", e);
			return false;
		}
		return true;
	}
}
