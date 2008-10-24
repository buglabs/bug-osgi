/* Copyright (c) 2007, 2008 Bug Labs, Inc.
 * All rights reserved.
 *   
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License version  
 * 2 only, as published by the Free Software Foundation.   
 *   
 * This program is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * General Public License version 2 for more details (a copy is  
 * included at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html).   
 *   
 * You should have received a copy of the GNU General Public License  
 * version 2 along with this work; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA   
 *
 */
package com.buglabs.util;


/**
 * Back-port of OSGi R4 Version class.
 * 
 * @author kgilmer
 * 
 */
public class Version {
	static Version emptyVersion = new Version(0, 0, 0);

	private final int major;

	private final int minor;

	private final int micro;

	private final String qualifier;

	public Version(int major, int minor, int micro, String qualifier) {
		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.qualifier = qualifier;
	}

	public Version(int major, int minor, int micro) {
		this(major, minor, micro, "");
	}

	public Version(String version) {
		String[] elements = StringUtil.split(version, ".");
		this.major = Integer.parseInt(elements[0]);
		this.minor = Integer.parseInt(elements[1]);
		this.micro = Integer.parseInt(elements[2]);

		if (elements.length == 4) {
			this.qualifier = elements[3];
		} else {
			qualifier = "";
		}
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getMicro() {
		return micro;
	}

	public String getQualifier() {
		return qualifier;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Version)) {
			return super.equals(obj);
		}

		Version extV = (Version) obj;

		if (this.getQualifier() != null) {
			if (this.major == extV.getMajor() && this.micro == extV.getMicro()
					&& this.minor == extV.getMinor()
					&& this.qualifier.equals(extV.getQualifier())) {
				return true;
			}
		} else {
			if (this.major == extV.getMajor() && this.micro == extV.getMicro()
					&& this.minor == extV.getMinor()) {
				return true;
			}
		}

		return false;
	}
	
	public static int compare(Version v1, Version v2) {
		if (v1.equals(v2)) {
			return 0;
		}
		
		if (v1.getMajor() > v2.getMajor()) {
			return -1;
		}
		
		if (v1.getMajor() < v2.getMajor()) {
			return 1;
		}
		
		if (v1.getMinor() > v2.getMinor()) {
			return -1;
		}
		
		if (v1.getMinor() < v1.getMinor()) {
			return 1;
		}
		
		if (v1.getMicro() > v2.getMicro()) {
			return -1;
		}
		
		if (v1.getMicro() < v2.getMicro()) {
			return 1;
		}
		
		return v1.getQualifier().compareTo(v2.getQualifier());
	}

}
