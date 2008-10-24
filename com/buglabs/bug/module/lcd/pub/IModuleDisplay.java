package com.buglabs.bug.module.lcd.pub;

import java.awt.Frame;

/**
 * Display devices capable of supporting GUI widgets provide this service.
 * @author kgilmer
 *
 */
public interface IModuleDisplay {
	public static final String MODULE_ID = "LCD";
	/**
	 * @return Returns the base frame used to create controls on the display.
	 */
	public Frame getFrame();
}
