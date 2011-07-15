/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
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
			if (this.major == extV.getMajor() && this.micro == extV.getMicro() && this.minor == extV.getMinor() && this.qualifier.equals(extV.getQualifier())) {
				return true;
			}
		} else {
			if (this.major == extV.getMajor() && this.micro == extV.getMicro() && this.minor == extV.getMinor()) {
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
