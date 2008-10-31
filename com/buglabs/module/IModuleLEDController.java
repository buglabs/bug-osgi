package com.buglabs.module;

import java.io.IOException;

/**
 * Implementors have two LEDs, one green, one red, that can be controlled.
 * @author kgilmer
 *
 */
public interface IModuleLEDController {
	
	/**
	 * 
	 * @param state on = true, off = false;
	 * @return the return of the underlying ioctl.
	 */
	public int setLEDRed(boolean state) throws IOException;
	public int setLEDGreen(boolean state) throws IOException;
}
