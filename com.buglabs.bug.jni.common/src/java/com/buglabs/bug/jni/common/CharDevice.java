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

/**
 * Access files from Java and allow ioctls on device nodes.  This class is meant to be used with
 * Linux device nodes.
 * 
 * @author aroman
 *
 */
public class CharDevice {

	protected int fd;

	static {
		System.loadLibrary("Common");
	}

	public int getFileDescriptor() {
		return fd;
	}

	/**
	 * Open a file.
	 * @param file
	 * @param mode Refer to FCNTL_H class for constants to be passed in.
	 * @return negative value on failure.
	 */
	public native int open(String file, int mode);

	/**
	 * @return a full line from the file.
	 */
	public native String getline();

	/**
	 * @return a byte as int from the file.
	 */
	public native int read();

	/**
	 * @param b
	 * @return number of bytes read.
	 */
	public int read(byte[] b) {
		return readBytes(b);
	}

	/**
	 * There's a bug somewhere (gcc, libc, dlopen, or jvm) where overloaded
	 * functions don't map well through jni. Therefore, the function overloading
	 * is at the java layer and renamed the native method to readBytes
	 */
	private native int readBytes(byte[] b);

	/**
	 * @param offset
	 * @param whence
	 * @return
	 */
	public native long lseek(long offset, int whence);

	public native long write(byte[] buf, long count);

	/**
	 * @param request
	 * @return
	 */
	public native int ioctl(int request);

	/**
	 * @return
	 */
	public native int close();
}
