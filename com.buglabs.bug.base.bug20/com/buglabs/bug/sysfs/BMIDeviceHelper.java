package com.buglabs.bug.sysfs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry-point into the sysfs API for BUG20.  This API is a null-safe way of reading and writing to sysfs entries for BUG drivers that support them.
 * 
 * @author kgilmer
 *
 */
public class BMIDeviceHelper {

	/**
	 * @return The BMIDevices attached at the time of the call.  If a given array element is null, no module is attached to that slot.
	 * @throws IOException
	 */
	public static BMIDevice[] getDevices() {

		BMIDevice[] devs = new BMIDevice[4];

		for (int i = 0; i < 4; ++i) {
			File prodFile = getBMIDeviceRoot(i);
			if (!validBMIDeviceRoot(prodFile)) {
				devs[i] = null;
				continue;
			}

			devs[i] = BMIDevice.createFromSYSDirectory(prodFile, i);
		}
		
		return devs;
	}
	
	/**
	 * @return list of BMIDevices that are currently attached, or empty list if no modules are attached.
	 */
	public static List<BMIDevice> getAttachedDevices() {
		BMIDevice[] devs = getDevices();
		
		List<BMIDevice>  l = new ArrayList<BMIDevice>();
		
		for (int i = 0; i < 4; ++i) {
			if (devs[i] != null) {
				l.add(devs[i]);
			}
		}
		
		return l;
	}
	
	/**
	 * @param slot Legal values: 0 - 3
	 * @return The BMIDevice that exists at the passed slot or null if no device attached.
	 */
	public static BMIDevice getDevice(int slot) {
		if (slot < 0 || slot > 3) {
			return null;
		}
		
		return getDevices()[slot];
	}


	/**
	 * @param prodFile
	 * @return true if a module is inserted and recognized by BMI, false otherwise.
	 */
	private static boolean validBMIDeviceRoot(File prodFile) {
		if (!prodFile.exists() || !prodFile.isDirectory()) {
			return false;
		}
		
		return prodFile.listFiles().length > 0;
	}

	/**
	 * @param i
	 * @return
	 */
	private static File getBMIDeviceRoot(int i) {
		return new File("/sys/class/bmi/bmi-" + i + "/bmi-dev-" + i);
	}


}
