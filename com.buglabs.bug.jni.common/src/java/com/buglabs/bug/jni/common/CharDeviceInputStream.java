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
package com.buglabs.bug.jni.common;

import java.io.IOException;
import java.io.InputStream;

/**
 * Treat a CharDeice as an input stream.
 * @author aroman
 *
 */
public class CharDeviceInputStream extends InputStream {
	private CharDevice cdev;

	public boolean equals(Object obj) {
		return cdev.equals(obj);
	}

	public int getFileDescriptor() {
		return cdev.getFileDescriptor();
	}

	public String getline() {
		return cdev.getline();
	}

	public int hashCode() {
		return cdev.hashCode();
	}

	public int ioctl(int request) {
		return cdev.ioctl(request);
	}

	public long lseek(long offset, int whence) {
		return cdev.lseek(offset, whence);
	}

	public int open(String file, int mode) {
		return cdev.open(file, mode);
	}

	public String toString() {
		return cdev.toString();
	}

	public long write(byte[] buf, long count) {
		return cdev.write(buf, count);
	}

	public CharDeviceInputStream(CharDevice cdev) {
		this.cdev = cdev;
	}

	public int read() throws IOException {
		return cdev.read();
	}

	public int read(byte[] buff) throws IOException {
		return cdev.read(buff);
	}

	public void close() throws IOException {
		cdev.close();
	}
}
