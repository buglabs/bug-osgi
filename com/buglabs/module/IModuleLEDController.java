package com.buglabs.module;

import java.io.IOException;

public interface IModuleLEDController {
	
	/**
	 * 
	 * @param state on = true, off = false;
	 * @return the return of the underlying ioctl.
	 */
	public int setLEDRed(boolean state) throws IOException;
	public int setLEDGreen(boolean state) throws IOException;
	
	/**
	 * 
	 * @deprecated use setLEDRed.
	 * @return negative value if error occurred.
	 * @throws IOException
	 */
	public int LEDRedOff() throws IOException;		// Turn off red LED
	
	/**
	 * 
	 * @deprecated use setLEDRed.
	 * @return negative value if error occurred.
	 * @throws IOException
	 */
	public int LEDRedOn() throws IOException;
	
	/**
	 * 
	 * @deprecated use setLEDGreen.
	 * @return negative value if error occurred.
	 * @throws IOException
	 */
	public int LEDGreenOff() throws IOException;		// Turn off red LED
	
	/**
	 * 
	 * @deprecated use setLEDGreen.
	 * @return negative value if error occurred.
	 * @throws IOException
	 */
	public int LEDGreenOn() throws IOException;
	
}
