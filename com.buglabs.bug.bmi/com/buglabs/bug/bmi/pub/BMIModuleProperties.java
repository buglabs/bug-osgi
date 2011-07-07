package com.buglabs.bug.bmi.pub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.buglabs.bug.bmi.sysfs.BMIDevice;

/**
 * A bean-style class for properties associated with a BMI module attached to
 * BUG.
 * 
 * @author kgilmer
 * 
 */
public class BMIModuleProperties {
	private String description;
	private int gpio_usage;
	private int power_use;
	private int revision;
	private int vendor;
	private int bus_usage;
	private int memory_size;
	private int power_charging;
	private String product_id;
	private String serial_num;
	private static BMIDevice device;

	private BMIModuleProperties(int gpioUsage, int powerUse, String productId) {
		this.gpio_usage = gpioUsage;
		this.power_use = powerUse;	
		this.product_id = productId;
	}

	/**
	 * Create an instance of BMIModuleProperties class using the base BMI /sys
	 * filesystem directory, for example /sys/devices/conn-m1.
	 * 
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	public static BMIModuleProperties createFromSYSDirectory(File directory) throws IOException {
		if (directory == null || !directory.exists() || !directory.isDirectory()) {
			throw new IOException("Directory is invalid: " + directory.getAbsolutePath());
		}

		//Note: this may change in the future if we need it to.  Basically the only thing we NEED 
		//from sysfs is the product ID.  Isaacs may add the description and others back but right now all we 
		//NEED is prodct.

		//String description = loadFile(new File(directory, "description"));
		//int gpioUsage = parseInt(loadFile(new File(directory, "gpio_usage")));
		//int powerUse = parseInt(loadFile(new File(directory, "power_use")));
		int revision = parseInt(loadFile(new File(directory, "revision")));
		int vendor = parseInt(loadFile(new File(directory, "vendor")));
		//int busUsage = parseInt(loadFile(new File(directory, "bus_usage")));
		//int memorySize = parseInt(loadFile(new File(directory, "memory_size")));
		//int powerCharging = parseInt(loadFile(new File(directory, "power_charging")));
		String productId = parseHexInt(loadFile(new File(directory, "product")));
		//String serialNum = parseMultiInt(loadFile(new File(directory, "serial_num")));

		return new BMIModuleProperties(revision, vendor, productId);
	}
	
	public static BMIModuleProperties createFromBMIDevice(BMIDevice device) throws IOException {
		return new BMIModuleProperties(device.getRevision(), device.getVendor(), device.getProductId());
	}

	public String getDescription() {
		return description;
	}

	public int getGpio_usage() {
		return gpio_usage;
	}

	public int getPower_use() {
		return power_use;
	}

	public int getRevision() {
		return revision;
	}

	public int getVendor() {
		return vendor;
	}

	public int getBus_usage() {
		return bus_usage;
	}

	public int getMemory_size() {
		return memory_size;
	}

	public int getPower_charging() {
		return power_charging;
	}

	public String getProduct_id() {
		return product_id;
	}

	public String getSerial_num() {
		return serial_num;
	}

	/**
	 * Given a number like "0x2f" convert to integer.
	 * 
	 * @param sn
	 * @return
	 */
	private static int parseInt(String sn) {
		return Integer.parseInt(sn.substring(2), 16);
	}

	private static String parseHexInt(String sn) {

		return pad(Integer.toString(Integer.parseInt(sn.substring(2), 16), 16).toUpperCase(), 4, '0');
	}

	/**
	 * Pad a string to length len of char j
	 * 
	 * @param s
	 * @param len
	 * @param j
	 * @return
	 */
	private static String pad(String s, int len, char j) {

		if (s.length() >= len) {
			return s;
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < len - s.length(); ++i) {
			sb.append(j);
		}
		sb.append(s);

		return sb.toString();
	}

	/**
	 * Return first line of file as a string.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static String loadFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		return br.readLine();
	}
}
