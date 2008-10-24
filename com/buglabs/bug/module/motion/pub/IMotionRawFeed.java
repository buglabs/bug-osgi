package com.buglabs.bug.module.motion.pub;

import java.io.IOException;
import java.io.InputStream;

public interface IMotionRawFeed {

	/**
	 *  	BMI_MOTION_DETECT_STATUS    
	 * 
	 * This bit is the present status of the the motion detector status pin.
	 * A value of 1 indicates that motion is being detected.  
	 * A value of 0 indicates that motion is not being detected.
	 */
	public final static byte BMI_MOTION_DETECT_STATUS          = (1<<3);
	
	/** 
	 * BMI_MOTION_DETECT_LATCHED_STATUS
	 *
	 * This bit is the latched status of the motion sensor. This bit is set to 1
	 * when the BMI_MOTION_DETECT_STATUS bit changes from 0 to 1. This bit will 
	 * be cleared as the result of an "ioctl(BMI_MDACC_MOTION_GET_STATUS)" or a 
	 * read() system call.  
	 */
	public final static byte BMI_MOTION_DETECT_LATCHED_STATUS  = (1<<2);
	
	/**
	 * BMI_MOTION_DETECT_DELTA
	 *
	 * This bit indicates that the motion detector status has changed from 1 to 0
	 * or has changed from 0 to 1. This bit will be cleared as the result of an 
	 * "ioctl(BMI_MDACC_MOTION_GET_STATUS)" or read() system calls. 
	 */ 
	public final static byte BMI_MOTION_DETECT_DELTA           = (1<<1);
	
	/** 
	 * BMI_MOTION_DETECT_ENABLED
	 *
	 * This bits is the state of the motion detector sampling and status reporting
	 * mechanism. A value of 1 indicates that the motion detector is enabled. A 
	 * value of 0 indicates that the motion detector is disabled.
	 */
	public final static byte BMI_MOTION_DETECT_ENABLED         = (1<<0);
	
	
	/**
	 * Each byte returned by the input stream represents the status byte from the
	 * motion detector.
	 * 
	 * Use BMI_MOTION_DETECT_STATUS, BMI_MOTION_DETECT_LATCHED_STATUS, 
	 * BMI_MOTION_DETECT_DELTA and BMI_MOTION_DETECT_ENABLED to bit mask the 
	 * status byte.
	 *
	 * @throws IOException
	 * @returns InputStream to the status register of the motion detector.
	 */
	public InputStream getInputStream() throws IOException;
}
