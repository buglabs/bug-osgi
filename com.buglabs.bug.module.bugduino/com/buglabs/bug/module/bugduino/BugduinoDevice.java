package com.buglabs.bug.module.lcd;

import java.io.File;
import java.io.FilenameFilter;

import com.buglabs.bug.sysfs.SysfsNode;

/**
 * Exposes sysfs entry for ML3953 devices to Java clients.
 * 
 * @author jconnolly
 * 
 */
public class BugduinoDevice extends SysfsNode {

	/*
	 * Device entries: driver uevent input subsystem name modalias power
	 * position disable
	 */
	//private static final String BUGDUINO_ROOT = "/sys/devices/platform/i2c_omap.3/i2c-3/3-0070/i2c-5/5-0017/";
	// /sys/class/bmi/bmi_bugduino_slot2 
	private static BugduinoDevice instance;
	private final int slot;

	/**
	 * @param root
	 *            file root in sysfs for device entry.
	 */
	protected BugduinoDevice(File root) {
		super(root);
		slot = Integer.parseInt(root.getName().substring(0, 1)) - 4;
	}

	/**
	 * @return the name of the device
	 */
	public String getName() {
		return getFirstLineofFile(new File(root, "name"));
	}

	/**
	 * @return the slot # the device is attached to.
	 */
	public int getSlot() {
		return slot;
	}
/*
	public static BugduinoDevice getInstance() {
		if (instance == null) {
			instance = new BugduinoDevice(new File(BUGDUINO_ROOT));
		}
		return instance;
	}
*/

}

