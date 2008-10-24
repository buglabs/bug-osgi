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
#include <iostream>
#include <jni.h>
#include "jni/com_buglabs_bug_jni_camera_Camera.h"
#include <fcntl.h>
#include <linux/videodev.h>
#include <sys/ioctl.h>
#include <string.h>
#include <errno.h>
#include <sys/mman.h>
#include "CharDevice.h"
#include "Camera.h"

extern "C" {
#include "jpeglib.h"
}

using namespace std;

size_t compressUYVY(int fd, unsigned char * buff, int length) {
	int bytesPerPixel = 2;
	int byteSize = WIDTH * HEIGHT * bytesPerPixel;
	unsigned char * data = 0;
	unsigned char * realdata = (unsigned char *) malloc(byteSize);
	memset(realdata, 0, byteSize);
	data = realdata;
	
	int result = read(fd, data, byteSize);
	
	if(result < 0) {
		perror("Unable to read device\n");
	}
	
	if(result < byteSize) {
		//TODO: Throw exception
		perror("Unable to read complete file");
	}
	
	  int i = 0;
	  
	  FILE * out = fmemopen(buff, length, "w");

	  struct jpeg_compress_struct comp;
	  struct jpeg_error_mgr error;
	  
	  comp.err = jpeg_std_error(&error);
	  jpeg_create_compress(&comp);
	  jpeg_stdio_dest(&comp, out);
	  comp.image_width = WIDTH;
	  comp.image_height = HEIGHT;
	  comp.input_components = 3;
	  comp.in_color_space = JCS_YCbCr;
	  jpeg_set_defaults(&comp);
	  jpeg_set_quality(&comp, 90, TRUE);
	  jpeg_start_compress(&comp, TRUE);


	  unsigned char * yuvBuf = (unsigned char *) calloc(WIDTH, 3);
	  
	  if(yuvBuf < 0) {
	    return FALSE;
	  }

	  for (i = 0; i < HEIGHT; ++i) {
	    //convert a scanline from CbYCrY to YCbCr
	    unsigned char * yuvPtr = yuvBuf;
	    unsigned char * uyvyPtr = data;

	    while(yuvPtr < (unsigned char *) (yuvBuf + (WIDTH * 3))) {
	      yuvPtr[0] = uyvyPtr[1];
	      yuvPtr[1] = uyvyPtr[0];
	      yuvPtr[2] = uyvyPtr[2];
	      yuvPtr[3] = uyvyPtr[3];
	      yuvPtr[4] = uyvyPtr[0];
	      yuvPtr[5] = uyvyPtr[2];
	      
	      uyvyPtr = uyvyPtr + 4;
	      yuvPtr = yuvPtr + 6;
	    }

	    jpeg_write_scanlines(&comp, &yuvBuf, 1);
	    data += (WIDTH * 2);
	  }
	 
	  free(yuvBuf);
	  free(realdata);
	  jpeg_finish_compress(&comp);
	  fflush(out);
	  
	  size_t dataWritten = ftell(out);
	  
	  fclose(out);
	  jpeg_destroy_compress(&comp);
	 
	  lseek(fd, 0, SEEK_SET);
	  
	  return dataWritten; 
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_Camera_init(JNIEnv * env, jobject jobj)
{
	struct v4l2_format format;
	struct v4l2_streamparm parm;
	struct v4l2_crop crop;
	
	memset(&format, 0, sizeof(format));
	memset(&parm, 0, sizeof(parm));
	memset(&crop, 0, sizeof(crop));
	
	format.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	
	if(ioctl(getFileDescriptorField(env, jobj), VIDIOC_G_FMT, &format) == -1) {
		perror(strcat("unable to set format", strerror(errno)));
		return -1;
	}
	
	format.fmt.pix.width = WIDTH;
	format.fmt.pix.height = HEIGHT;
	format.fmt.pix.pixelformat = V4L2_PIX_FMT_UYVY;
	format.fmt.pix.sizeimage = WIDTH * HEIGHT * 16 / 8;
	format.fmt.pix.bytesperline = WIDTH * 16 / 8;
	
	if(ioctl(getFileDescriptorField(env, jobj), VIDIOC_S_FMT, &format) == -1) {
		perror(strcat("Unable to set video format", strerror(errno)));
		return errno;
	}

	//set crop parameters 
	crop.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	crop.c.left = 0;
	crop.c.top = 0;
	crop.c.width = WIDTH;
	crop.c.height = HEIGHT;
	
	if(ioctl(getFileDescriptorField(env, jobj), VIDIOC_S_CROP, &crop) < 0)
	{
		perror(strcat("Unable to set crop\n", strerror(errno)));
	    return errno;
	}
	
	//set capture parameters
		parm.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		parm.parm.capture.timeperframe.numerator = 1;
		parm.parm.capture.timeperframe.denominator = 5;
		parm.parm.capture.capturemode = V4L2_MODE_HIGHQUALITY;
		
		if(ioctl(getFileDescriptorField(env, jobj), VIDIOC_S_PARM, &parm) < 0) {
			perror(strcat("Unable to set capture parameters", strerror(errno)));
			return errno;
		}
	
	return 0;
}

JNIEXPORT jbyteArray JNICALL Java_com_buglabs_bug_jni_camera_Camera_grabFrame(JNIEnv * env, jobject jobj)
{
	int fd = getFileDescriptorField(env, jobj);
	
	unsigned char * buff = 0;
	struct v4l2_streamparm parm;
	
	//workaround for driver
	Java_com_buglabs_bug_jni_camera_Camera_init(env, jobj);
	
	struct v4l2_format fmt;
	fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	if(ioctl(fd, VIDIOC_G_FMT, &fmt) < 0) {
		perror("IOCTL Failed: VIDIOC_G_FMT \n");
	}

	buff = (unsigned char *) malloc(fmt.fmt.pix.sizeimage);
	
	memset(buff, 0, fmt.fmt.pix.sizeimage);
	
	
	//int bytesRead = read(getFileDescriptorField(env, jobj), buff, fmt.fmt.pix.sizeimage);
	//printf("Read: %i bytes\n", bytesRead);
	
	
	size_t jpegsize = compressUYVY(fd, buff, fmt.fmt.pix.sizeimage);
	
	//workaround for driver
		parm.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		parm.parm.capture.timeperframe.numerator = 1;
		parm.parm.capture.timeperframe.denominator = 30;
		parm.parm.capture.capturemode = 0;
			
		if(ioctl(getFileDescriptorField(env, jobj), VIDIOC_S_PARM, &parm) < 0) {
					perror(strcat("Unable to set reset parameters", strerror(errno)));
		}
			
		
		
	jbyteArray dirbuff = env->NewByteArray(jpegsize);
	
	if(dirbuff != NULL) {
		env->SetByteArrayRegion(dirbuff, 0, jpegsize, (jbyte *) buff);
	}
	
	free(buff);
	
	return dirbuff;
}

