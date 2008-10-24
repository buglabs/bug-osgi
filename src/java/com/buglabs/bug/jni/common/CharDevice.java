package com.buglabs.bug.jni.common;

public class CharDevice {

	protected int fd;

	static {
		System.loadLibrary("Common");
	}

	public int getFileDescriptor() {
		return fd;
	}

	public native int open(String file, int mode);
	public native String getline();
	public native int read();

	public int read(byte[] b) {
		return readBytes(b);
	}
	
	/**
	 * 	There's a bug somewhere (gcc, libc, dlopen, or jvm) where 
	 *	overloaded functions don't map well through jni.
	 *	Therefore, the function overloading is at the java layer
	 *	and renamed the native method to readBytes
	 */
	private native int readBytes(byte[] b);

	public native long lseek(long offset, int whence);
	public native long write(byte[] buf, long count);
	public native int ioctl(int request);
	public native int close();
}
