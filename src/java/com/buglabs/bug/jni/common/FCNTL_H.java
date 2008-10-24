package com.buglabs.bug.jni.common;

public class FCNTL_H {
	/**
	 * From fcntl.h
	 */
	public static final int O_ACCMODE = 0003;
	public static final int O_RDONLY = 00;
	public static final int  O_WRONLY = 01;
	public static final int  O_RDWR	= 02;
	public static final int  O_CREAT = 0100;	/* not fcntl */
	public static final int  O_EXCL	= 0200;	/* not fcntl */
	public static final int  O_NOCTTY =	0400;	/* not fcntl */
	public static final int  O_TRUNC = 01000;	/* not fcntl */
	public static final int  O_APPEND = 02000;
	public static final int  O_NONBLOCK = 04000;
	public static final int  O_NDELAY =	O_NONBLOCK;
	public static final int  O_SYNC	= 010000;
	public static final int  O_FSYNC = O_SYNC;
	public static final int  O_ASYNC = 020000;
}
