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
#include <linux/fb.h>
#include <sys/ioctl.h>
#include <string.h>
#include <errno.h>
#include <sys/mman.h>
#include "CharDevice.h"
#include "Camera.h"

extern "C" {
#include "jpeglib.h"
}

#define TFAIL -1
#define TPASS 0

using namespace std;

size_t compressUYVY(int fd, unsigned char * buff, int length, int sizeX, int sizeY) {
	int bytesPerPixel = 2;
	int byteSize = sizeX * sizeY * bytesPerPixel;
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
	  comp.image_width = sizeX;
	  comp.image_height = sizeY;
	  comp.input_components = 3;
	  comp.in_color_space = JCS_YCbCr;
	  jpeg_set_defaults(&comp);
	  jpeg_set_quality(&comp, 90, TRUE);
	  jpeg_start_compress(&comp, TRUE);


	  unsigned char * yuvBuf = (unsigned char *) calloc(sizeX, 3);
	  
	  if(yuvBuf < 0) {
	    return FALSE;
	  }

	  for (i = 0; i < sizeY; ++i) {
	    //convert a scanline from CbYCrY to YCbCr
	    unsigned char * yuvPtr = yuvBuf;
	    unsigned char * uyvyPtr = data;

	    while(yuvPtr < (unsigned char *) (yuvBuf + (sizeX * 3))) {
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
	    data += (sizeX * 2);
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

int camera_overlay_setup(int fd_v4l, struct v4l2_format *fmt, int disp_lcd, int sens_width, int sens_height, int cam_rotate, int cam_framerate)
{
        struct v4l2_streamparm parm;
        v4l2_std_id id;
        struct v4l2_control ctl;
        struct v4l2_crop crop;

        if (ioctl(fd_v4l, VIDIOC_S_OUTPUT, &disp_lcd) < 0)
        {
                printf("VIDIOC_S_OUTPUT failed\n");
                return TFAIL;
        } 

        ctl.id = V4L2_CID_PRIVATE_BASE;
		ctl.value = cam_rotate;
        if (ioctl(fd_v4l, VIDIOC_S_CTRL, &ctl) < 0)
        {
                printf("set control failed\n");
                return TFAIL;
        } 

        crop.type = V4L2_BUF_TYPE_VIDEO_OVERLAY;
        crop.c.left = 0;
        crop.c.top = 0;
        crop.c.width = sens_width;
        crop.c.height = sens_height;
        if (ioctl(fd_v4l, VIDIOC_S_CROP, &crop) < 0)
        {
                printf("set cropping failed\n");
                return TFAIL;
        } 

        if (ioctl(fd_v4l, VIDIOC_S_FMT, fmt) < 0)
        {
                printf("set format failed\n");
                return TFAIL;
        } 

        if (ioctl(fd_v4l, VIDIOC_G_FMT, fmt) < 0)
        {
                printf("get format failed\n");
                return TFAIL;
        } 

        if (ioctl(fd_v4l, VIDIOC_G_STD, &id) < 0)
        {
                printf("VIDIOC_G_STD failed\n");
                return TFAIL;
        } 

        parm.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        parm.parm.capture.timeperframe.numerator = 1;
        parm.parm.capture.timeperframe.denominator = cam_framerate;
        parm.parm.capture.capturemode = 0;
         
        if (ioctl(fd_v4l, VIDIOC_S_PARM, &parm) < 0)
        {
                printf("VIDIOC_S_PARM failed\n");
                return TFAIL;
        } 

        parm.parm.capture.timeperframe.numerator = 0;
        parm.parm.capture.timeperframe.denominator = 0;

        if (ioctl(fd_v4l, VIDIOC_G_PARM, &parm) < 0)
        {
                printf("get frame rate failed\n");
                return TFAIL;
        } 

        printf("frame_rate is %d\n", parm.parm.capture.timeperframe.denominator);
        return TPASS;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_Camera_initExt(JNIEnv * env, jobject jobj,
								      int sizeX, int sizeY,
								      int pixelFormat, boolean highQuality)
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
	
	format.fmt.pix.width = sizeX;
	format.fmt.pix.height = sizeY;
	format.fmt.pix.pixelformat = pixelFormat;
	format.fmt.pix.sizeimage = sizeX * sizeY * 16 / 8;
	format.fmt.pix.bytesperline = sizeY * 16 / 8;
	
	if(ioctl(getFileDescriptorField(env, jobj), VIDIOC_S_FMT, &format) == -1) {
		perror(strcat("Unable to set video format", strerror(errno)));
		return errno;
	}

	//set crop parameters 
	crop.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	crop.c.left = 0;
	crop.c.top = 0;
	crop.c.width = sizeX;
	crop.c.height = sizeY;
	
	if(ioctl(getFileDescriptorField(env, jobj), VIDIOC_S_CROP, &crop) < 0)
	{
		perror(strcat("Unable to set crop\n", strerror(errno)));
	    return errno;
	}

	//set capture parameters
		parm.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		parm.parm.capture.timeperframe.numerator = 1;
		parm.parm.capture.timeperframe.denominator = 5;
		parm.parm.capture.capturemode = highQuality ? V4L2_MODE_HIGHQUALITY : 0;
		
		if(ioctl(getFileDescriptorField(env, jobj), VIDIOC_S_PARM, &parm) < 0) {
			perror(strcat("Unable to set capture parameters", strerror(errno)));
			return errno;
		}
	
	return 0;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_Camera_init(JNIEnv * env, jobject jobj)
{
  return Java_com_buglabs_bug_jni_camera_Camera_initExt(env, jobj,
							DEFAULT_WIDTH, DEFAULT_HEIGHT,
							V4L2_PIX_FMT_UYVY, true);
}

JNIEXPORT jbyteArray JNICALL Java_com_buglabs_bug_jni_camera_Camera_grabFrameExt(JNIEnv * env, jobject jobj,
										 int sizeX, int sizeY,
										 int format, boolean highQuality)
{
	int fd = getFileDescriptorField(env, jobj);
	
	unsigned char * buff = 0;
	struct v4l2_streamparm parm;
	
	//workaround for driver
	Java_com_buglabs_bug_jni_camera_Camera_initExt(env, jobj, sizeX, sizeY, format, highQuality);
	
	struct v4l2_format fmt;
	fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	if(ioctl(fd, VIDIOC_G_FMT, &fmt) < 0) {
		perror("IOCTL Failed: VIDIOC_G_FMT \n");
	}

	buff = (unsigned char *) malloc(fmt.fmt.pix.sizeimage);
	
	memset(buff, 0, fmt.fmt.pix.sizeimage);
	
	
	//int bytesRead = read(getFileDescriptorField(env, jobj), buff, fmt.fmt.pix.sizeimage);
	//printf("Read: %i bytes\n", bytesRead);
	
	
	size_t jpegsize = compressUYVY(fd, buff, fmt.fmt.pix.sizeimage, sizeX, sizeY);
	
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


JNIEXPORT jbyteArray JNICALL Java_com_buglabs_bug_jni_camera_Camera_grabFrame(JNIEnv * env, jobject jobj)
{
  return Java_com_buglabs_bug_jni_camera_Camera_grabFrameExt(env, jobj,
							     DEFAULT_WIDTH, DEFAULT_HEIGHT,
							     V4L2_PIX_FMT_UYVY, true);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_Camera_overlayinit(JNIEnv * env, jobject jobj, jint left, jint top, jint width, jint height)
{
	struct v4l2_format fmt;
    struct v4l2_framebuffer fb_v4l2;
 
    int fd_v4l;
    int fd_fb = 0;
    struct fb_fix_screeninfo fix;
    struct fb_var_screeninfo var;
    
    fd_v4l = getFileDescriptorField(env, jobj);
    fmt.type = V4L2_BUF_TYPE_VIDEO_OVERLAY;
    fmt.fmt.win.w.top =  top;	//g_display_top ;
    fmt.fmt.win.w.left = left;	//g_display_left;
    fmt.fmt.win.w.width = width;	//g_display_width;
    fmt.fmt.win.w.height = height;	//g_display_height;
    if (camera_overlay_setup(fd_v4l, &fmt, 1, 1600, 1200, 7, 0) < 0) {
    	printf("Setup overlay failed.\n");
        return TFAIL;
	}

    memset(&fb_v4l2, 0, sizeof(fb_v4l2)); 
  
    if (ioctl(fd_v4l, VIDIOC_G_FBUF, &fb_v4l2) < 0) {
    	printf("Get framebuffer failed\n");
        return TFAIL;
    }
    fb_v4l2.flags = V4L2_FBUF_FLAG_OVERLAY;
    if (ioctl(fd_v4l, VIDIOC_S_FBUF, &fb_v4l2) < 0) {
    	printf("Set framebuffer failed\n");
        return TFAIL;
    } 

    if (ioctl(fd_v4l, VIDIOC_G_FBUF, &fb_v4l2) < 0) {
        printf("Get framebuffer failed\n");
        return TFAIL;
    }
    return TPASS;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_Camera_overlaystart(JNIEnv * env, jobject jobj)
{
	int i;
	int overlay = 1;
	struct v4l2_control ctl;
	int fd_v4l = getFileDescriptorField(env, jobj);
	
	
	//camera_overlay_setup(...);
	
	if (ioctl(fd_v4l, VIDIOC_OVERLAY, &overlay) < 0)
	{
		printf("VIDIOC_OVERLAY start failed\n");
		return TFAIL;
	} 
	
	ctl.id = V4L2_CID_PRIVATE_BASE + 1;
    if (ioctl(fd_v4l, VIDIOC_S_CTRL, &ctl) < 0)
    {
    	printf("set ctl failed\n");
        return TFAIL;
    }
    return TPASS;
/*       if (g_camera_color == 1) {
                ctl.id = V4L2_CID_BRIGHTNESS;
                for (i = 0; i < 0xff; i+=0x20) {
		            ctl.value = i;
                	printf("change the brightness %d\n", i);
                    ioctl(fd_v4l, VIDIOC_S_CTRL, &ctl);
		            sleep(1);
                } 
		}
		else if (g_camera_color == 2) {
                ctl.id = V4L2_CID_SATURATION;
                for (i = 25; i < 150; i+= 25) {
		            ctl.value = i;
	                printf("change the color saturation %d\n", i);
                    ioctl(fd_v4l, VIDIOC_S_CTRL, &ctl);
		            sleep(5);
                } 
		}
		else if (g_camera_color == 3) {
                ctl.id = V4L2_CID_RED_BALANCE;
                for (i = 0; i < 0xff; i+=0x20) {
		            ctl.value = i;
	                printf("change the red balance %d\n", i);
                    ioctl(fd_v4l, VIDIOC_S_CTRL, &ctl);
		            sleep(1);
                } 
		}
		else if (g_camera_color == 4) {
                ctl.id = V4L2_CID_BLUE_BALANCE;
                for (i = 0; i < 0xff; i+=0x20) {
		            ctl.value = i;
                	printf("change the blue balance %d\n", i);
                    ioctl(fd_v4l, VIDIOC_S_CTRL, &ctl);
		            sleep(1);
                } 
		}
		else if (g_camera_color == 5) {
                ctl.id = V4L2_CID_BLACK_LEVEL;
                for (i = 0; i < 4; i++) {
		            ctl.value = i;
                	printf("change the black balance %d\n", i);
                    ioctl(fd_v4l, VIDIOC_S_CTRL, &ctl);
		            sleep(5);
                } 
        } 
        else {
		        sleep(timeout);
        }
*/    
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_Camera_overlaystop(JNIEnv * env, jobject jobj)
{		
        int overlay = 0;
        int fd_v4l = getFileDescriptorField(env, jobj);
        
        if (ioctl(fd_v4l, VIDIOC_OVERLAY, &overlay) < 0)
        {
                printf("VIDIOC_OVERLAY stop failed\n");
                return TFAIL;
        }
        return TPASS;
}
