package com.buglabs.bug.module.lcd.pub;

import java.io.IOException;

import com.buglabs.module.IModuleControl;

public interface ILCDModuleControl extends IModuleControl {
	public int LEDRedOff() throws IOException;		// Turn off red LED
	public int LEDRedOn() throws IOException;
	public int LEDGreenOff() throws IOException;		// Turn off red LED
	public int LEDGreenOn() throws IOException;

	/**
	 * @param val Set's the intensity of the backlight 0-7.
	 * 
	 */
	public int setBlackLight(int val) throws IOException;		// Set IOX backlight bits [2:0]
	
	/**
	 * 
	 * @return The stat of IOX. The 3 LSBs reprersent the state of the backlight. 
	 */
	public int getStatus() throws IOException;		// Get IOX state
	
	public int disable() throws IOException;	// Power down module
	public int enable() throws IOException;	// Power up module	
}
