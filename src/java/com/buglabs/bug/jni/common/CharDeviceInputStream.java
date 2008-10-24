package com.buglabs.bug.jni.common;

import java.io.IOException;
import java.io.InputStream;

public class CharDeviceInputStream extends InputStream {
	private CharDevice cdev;

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
