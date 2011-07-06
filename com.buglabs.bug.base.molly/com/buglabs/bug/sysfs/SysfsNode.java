package com.buglabs.bug.sysfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * An abstract class for files within sysfs.  This class has helper methods in dealing with the files.
 * @author kgilmer
 *
 */
public abstract class SysfsNode {

	protected final File root;
	private static final String CRLF = System.getProperty("line.separator");
	//private static final LogService log = LogServiceUtil.getLogService(Activator.getDefault().getBundleContext());

	public SysfsNode(File root) {
		if (!root.exists() || !root.isDirectory()) {
			throw new IllegalArgumentException("Invalid sysfs directory: " + root.getAbsolutePath());
		}
		
		this.root = root;
	}
	
	/**
	 * null-safe.
	 * @param sn
	 * @return
	 */
	protected static String parseMultiInt(String sn) {
		if (sn == null) {
			return "";
		}
		
		String[] elems = sn.split("/");
		String ssn = "";
		if (elems != null) {
			for (int i = 0; i < elems.length; ++i) {
				ssn += parseInt(elems[i]);
			}
		}

		return ssn;
	}

	/**
	 * Null-safe.  Given a number like "0x2f" convert to integer.
	 * 
	 * @param sn
	 * @return the integer or 0 if passed value is null.  Invalid numbers will generate exceptions.
	 */
	protected static int parseInt(String sn) {
		if (sn == null) {
			return 0;
		}
		
		return Integer.parseInt(sn.substring(2), 16);
	}

	/**
	 * @param sn
	 * @return the integer as hex or 0 if passed value is null.  Invalid numbers will generate exceptions.
	 */
	protected static String parseHexInt(String sn) {
		if (sn == null) {
			return "0";
		}
		
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
	protected static String pad(String s, int len, char j) {

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
	 * @return first line or null if file read fails (file does not exist, etc.).
	 */
	protected static String getFirstLineofFile(File file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			return br.readLine();
		} catch (IOException e) {
			//Just ignore errors and return null;
			return null;
		}
	}
	

	/**
	 * Write a line to a file.
	 * @param file
	 * @param line
	 * @throws IOException
	 */
	protected void println(File file, String line) throws IOException {
		//TODO consider caching here.
		FileWriter fos = new FileWriter(file);
		fos.write(line);
		fos.close();
	}
}
