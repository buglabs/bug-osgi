package com.buglabs.bug.sysfs;

import java.io.File;
import java.io.IOException;

import com.buglabs.bug.sysfs.SysfsNode;

/**
 * A class to control a single-color or multi-color LED.  
 * 
 * @author kgilmer
 * 
 */
public class LEDDevice extends SysfsNode {

	/**
	 * LED is single color.
	 */
	public static final int TYPE_MONO_COLOR = 1;
	/**
	 * LED is tri color
	 */
	public static final int TYPE_TRI_COLOR = 2;

	/**
	 * Constant for single color LED
	 */
	public static final int COLOR_MONO = 0;
	
	/**
	 * Color constants for tri color LEDs.
	 */
	public static final int COLOR_RED = 0;
	public static final int COLOR_GREEN = 1;
	public static final int COLOR_BLUE = 2;

	/**
	 * Used in the construction of file paths for LED sysfs API in Linux .
	 */
	private static final String FILE_BRIGHTNESS = "brightness";
	private static final String FILE_TRIGGER = "trigger";
	private static final String FILE_MAX_BRIGHTNESS = "max_brightness";
	private static final String PLATFORM_NAME = "omap3bug";

	/**
	 * Name of LED in sysfs
	 */
	private final String name;
	
	/**
	 * Number of colors LED supports
	 */
	private final int type;
	
	
	/**
	 * Arrays to access files to control and read LED state.
	 */
	private final File[] ledBase;
	private final File[] ledBrightness;
	private final File[] ledMaxBrightness;
	private final File[] ledTrigger;

	/**
	 * Constructor for mono LED.
	 * 
	 * @param root
	 * @param name
	 * @param color
	 */
	public LEDDevice(File root, String name, String color) {
		super(root);
		this.name = name;
		this.type = TYPE_MONO_COLOR;

		ledBase = new File[1];
		ledBase[0] = new File(root, createLEDFilename(name, color));

		ledBrightness = new File[1];
		ledBrightness[0] = new File(ledBase[0], FILE_BRIGHTNESS);

		ledMaxBrightness = new File[1];
		ledMaxBrightness[0] = new File(ledBase[0], FILE_MAX_BRIGHTNESS);

		ledTrigger = new File[1];
		ledTrigger[0] = new File(ledBase[0], FILE_TRIGGER);
	}

	/**
	 * Constructor for tri-color LED
	 * 
	 * @param root
	 * @param name
	 */
	public LEDDevice(File root, String name) {
		super(root);
		this.name = name;
		this.type = TYPE_TRI_COLOR;

		ledBase = new File[3];
		ledBase[COLOR_RED] = new File(root, createLEDFilename(name, "red"));
		ledBase[COLOR_GREEN] = new File(root, createLEDFilename(name, "green"));
		ledBase[COLOR_BLUE] = new File(root, createLEDFilename(name, "blue"));

		ledBrightness = new File[3];
		ledBrightness[COLOR_RED] = new File(ledBase[COLOR_RED], FILE_BRIGHTNESS);
		ledBrightness[COLOR_GREEN] = new File(ledBase[COLOR_GREEN], FILE_BRIGHTNESS);
		ledBrightness[COLOR_BLUE] = new File(ledBase[COLOR_BLUE], FILE_BRIGHTNESS);
		
		ledMaxBrightness = new File[3];
		ledMaxBrightness[COLOR_RED] = new File(ledBase[COLOR_RED], FILE_MAX_BRIGHTNESS);
		ledMaxBrightness[COLOR_GREEN] = new File(ledBase[COLOR_GREEN], FILE_MAX_BRIGHTNESS);
		ledMaxBrightness[COLOR_BLUE] = new File(ledBase[COLOR_BLUE], FILE_MAX_BRIGHTNESS);

		ledTrigger = new File[3];
		ledTrigger[COLOR_RED] = new File(ledBase[COLOR_RED], FILE_TRIGGER);
		ledTrigger[COLOR_GREEN] = new File(ledBase[COLOR_GREEN], FILE_TRIGGER);
		ledTrigger[COLOR_BLUE] = new File(ledBase[COLOR_BLUE], FILE_TRIGGER);
	}

	/**
	 * @return name of LED.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return type of LED
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param n
	 * @param color
	 * @return String representing directory name for LED control.
	 */
	private String createLEDFilename(String n, String color) {
		return PLATFORM_NAME + ":" + color + ":" + n;
	}

	/**
	 * Set the brightness for the specified LED.
	 * @param color
	 * @param brightness
	 * @throws IOException
	 */
	public void setBrightness(int color, int brightness) throws IOException {
		println(ledBrightness[color], "" + brightness);
	}

	/**
	 * @param color
	 * @return the current brightness of the specified LED.
	 */
	public int getBrightness(int color) {
		return Integer.parseInt(getFirstLineofFile(ledBrightness[color]));
	}

	/**
	 * Set a trigger on a LED.
	 * @param color
	 * @param trigger
	 * @throws IOException
	 */
	public void setTrigger(int color, String trigger) throws IOException {
		println(ledTrigger[color], trigger);
	}

	/**
	 * @param color
	 * @return The LED trigger that is currently set.  "none" means no trigger is active.
	 * @throws IOException
	 */
	public String getLEDTrigger(int color) throws IOException {
		String line = getFirstLineofFile(ledTrigger[color]);

		String[] elems = line.split("[");
		elems = elems[1].split("]");

		return elems[0];
	}

	/**
	 * Get the Trigger types this LED supports.  These trigger types can be set using setLEDTrigger.
	 * @param color
	 * @return
	 * @throws IOException
	 */
	public String[] getLEDTriggers(int color) throws IOException {
		String line = getFirstLineofFile(ledTrigger[color]);

		String[] elems = line.split(" ");

		for (int i = 0; i < elems.length; ++i) {
			if (elems[i].startsWith("[")) {
				elems[i] = elems[i].substring(1, elems[i].length() - 1);
				// Only one trigger is set, so we only have to strip this once.
				break;
			}
		}

		return elems;
	}
	
	/**
	 * @return The number of colors this LED supports.  Zero based.
	 */
	public int getColorCount() {
		return ledBase.length;
	}
}
