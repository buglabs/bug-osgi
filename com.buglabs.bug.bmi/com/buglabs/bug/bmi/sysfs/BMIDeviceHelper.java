package com.buglabs.bug.bmi.sysfs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;

/**
 * Entry-point into the sysfs API for BUG20.  
 * This API is a null-safe way of reading and writing to sysfs entries for BUG drivers that support them.
 * 
 * @author kgilmer
 *
 */
public final class BMIDeviceHelper {

	/**
	 * Utility class, not instantiatable.
	 */
	private BMIDeviceHelper() {		
	}
	
	/**
	 * @param context BundleContext used to load extensions contributed by bundles.
	 * @return The BMIDevices attached at the time of the call.  If a given array element is null, no module is attached to that slot.
	 * @throws IOException IOException on File I/O error
	 */
	public static BMIDevice[] getDevices(BundleContext context) throws IOException {

		BMIDevice[] devs = new BMIDevice[BMIDevice.MAX_BMI_SLOTS];

		for (int i = 0; i < BMIDevice.MAX_BMI_SLOTS; ++i) {
			File prodFile = getBMIDeviceRoot(i);
			if (!validBMIDeviceRoot(prodFile)) {
				devs[i] = null;
				continue;
			}

			devs[i] = BMIDevice.createFromSYSDirectory(context, prodFile, i);
		}
		
		return devs;
	}
	/*
	*//**
	 * @return The BMIDevices attached at the time of the call.  
	 * If a given array element is null, no module is attached to that slot.
	 * This method will not return extensions to the base BMIDevice. 
	 * @throws IOException on File I/O error
	 *//*
	public static BMIDevice[] getDevices() throws IOException {

		BMIDevice[] devs = new BMIDevice[BMIDevice.MAX_BMI_SLOTS];

		for (int i = 0; i < BMIDevice.MAX_BMI_SLOTS; ++i) {
			File prodFile = getBMIDeviceRoot(i);
			if (!validBMIDeviceRoot(prodFile)) {
				devs[i] = null;
				continue;
			}

			devs[i] = BMIDevice.createFromSYSDirectory(null, prodFile, i);
		}
		
		return devs;
	}
	*/
	/**
	 * @return list of BMIDevices that are currently attached, or empty list if no modules are attached.
	 * @throws IOException on File I/O error
	 */
	public static List<BMIDevice> getAttachedDevices(BundleContext context) throws IOException {
		BMIDevice[] devs = getDevices(context);
		
		List<BMIDevice>  l = new ArrayList<BMIDevice>();
		
		for (int i = 0; i < BMIDevice.MAX_BMI_SLOTS; ++i) {
			if (devs[i] != null) {
				l.add(devs[i]);
			}
		}
		
		return l;
	}
	
	/**
	 * @param slot Legal values: 0 - 3
	 * @return The BMIDevice that exists at the passed slot or null if no device attached.
	 * @throws IOException on File I/O error
	 */
	public static BMIDevice getDevice(BundleContext context, int slot) throws IOException {
		if (slot < 0 || slot > (BMIDevice.MAX_BMI_SLOTS - 1)) {
			return null;
		}
		
		return getDevices(context)[slot];
	}


	/**
	 * @param directory directory of sysfs root
	 * @return true if a module is inserted and recognized by BMI, false otherwise.
	 */
	private static boolean validBMIDeviceRoot(File directory) {
		if (!directory.exists() || !directory.isDirectory()) {
			return false;
		}
		
		return directory.listFiles().length > 0;
	}

	/**
	 * @param i slot index
	 * @return valid directory of BMI device root
	 * @throws IOException 
	 */
	private static File getBMIDeviceRoot(int i) throws IOException {
		File root = new File("/sys/class/bmi/bmi-" + i + "/bmi-dev-" + i);
		
		if (!root.exists() || root.isFile())
			throw new IOException("Invalid BMI device root directory: " + root);
		
		return root;
	}


}
