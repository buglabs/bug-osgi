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
package com.buglabs.bug.jni.camera;

import com.buglabs.bug.jni.common.CharDevice;

/**
 * Camera device.
 *
 */
public class Camera extends CharDevice {
	static {
		System.loadLibrary("Camera");
	}
			
	/**
	 * @param media_node
	 * @param slot_num
	 * @param raw_height
	 * @param raw_width
	 * @param resize_height
	 * @param resize_width
	 * @return 0 on success
	 */
	public native int bug_camera_open(final String media_node,
			int slot_num,
			int raw_height,
			int raw_width,
			int resize_height,
			int resize_width);
	
	/**
	 * Close the camera device
	 * @return 0 on success
	 */
	public native int bug_camera_close();

	/**
	 * start acquiring frames (typically for preview mode)
	 *  camera must have been opened first
	 *  
	 * @return 0 on success
	 */
	public native int bug_camera_start();
	

	/**
	 * stop acquiring frames (typically for preview mode)
	 * camera may be closed one it' stopped
	 * @return 0 on success
	 */
	public native int bug_camera_stop();

	/**
	 * grab a preview-sized image as RGB. If pixelBuffer is null the frame
	 * will still be grabbed, but then thrown away - which is useful
	 * on startup to allow the sensor to settle
	 * 
	 * @param pixelBuffer array to load with values
	 * @return 0 on success
	 */
	public native boolean bug_camera_grab_preview(int [] pixelBuffer);
	
	
	/**
	 * Grab a raw-sized image as JPEG
	 * @return byte array of image
	 */
	public native byte[] bug_camera_grab_raw();
	
	
	/**
	 * flush the frame queue
	 * @return 0 on success
	 */
	public native int bug_camera_flush_queue();
}
