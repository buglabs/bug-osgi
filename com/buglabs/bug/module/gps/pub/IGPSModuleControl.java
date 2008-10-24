package com.buglabs.bug.module.gps.pub;

import java.io.IOException;

import com.buglabs.module.IModuleControl;
import com.buglabs.module.IModuleLEDController;

public interface IGPSModuleControl extends IModuleControl, IModuleLEDController {
	
	public  static final int STATUS_PASSIVE_ANTENNA = 0x40;
	public static final int STATUS_ACTIVE_ANTENNA = 0x80;
	
	/**
	 * bit 0: GPS FIX Active Low.
	 * bit 1: Overcurrent condition caused by the active antenna path. Active Low.
	 * bit 2: Output to wake up device from sleep after push_to_fix. Active High.
	 * bit 3: Input to download firmware to flash.
	 * bit 4: Unused
	 * bit 5: Unused
	 * bit 7, 6: 0, 1 Passtive Antenna (External Antenna)
	 * 			 1, 0 Active Antenna (Internal Antenna)
	 * 
	 * @return the value of the IOX register
	 */
	public int getStatus() throws IOException;
	
	/**
	 * Use the active (internal) antenna of the gps device.
	 * 
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public int setActiveAntenna() throws IOException;
	
	/**
	 * Use the passive (external) antenna of the gps device.
	 * 
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public int setPassiveAntenna() throws IOException;
}
