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

#include <stdlib.h>
#include <stdio.h>
#include <linux/videodev2.h>
extern "C" {
#include "jpeglib.h"
#include "bug_v4l.h"
}
#include "jni/com_buglabs_bug_jni_camera_Camera.h"

class JStringWrapper {
public:
	JStringWrapper(JNIEnv *jenv, jstring js);
	~JStringWrapper();

	operator const char *() const;

private:
	JNIEnv *m_jenv;
	const jstring m_jstring;
	const char *m_cstring;
	jboolean m_isCopy;
};
JStringWrapper::JStringWrapper(JNIEnv *jenv, jstring js)
: m_jenv(jenv),
  m_jstring(js),
  m_cstring(jenv->GetStringUTFChars(js, &m_isCopy))
{
}

JStringWrapper::~JStringWrapper()
{
	m_jenv->ReleaseStringUTFChars(m_jstring, m_cstring);
}

JStringWrapper::operator const char *() const
{
	return m_cstring;
}

#define CAMLOG(x)
//#define CAMLOG(x) {x; printf("\n"); fflush(stdout);}


// TODO: these should be in the class
static const int BYTES_PER_PIXEL = 3;
static int current_dev_node = -1;
static unsigned char *jpeg_buffer = NULL;
static size_t jpeg_buffer_size = 0;
static unsigned char *rgb_buffer = NULL;
static v4l2_pix_format raw_fmt;
static v4l2_pix_format resizer_fmt;
static bool half_size = false;

size_t compressYUYV(const struct bug_img &yuv_img)
{
	CAMLOG(printf("compressYUYV: yuv_img: start=%p, length=%lu, width=%d, height=%d, code=%d",
			yuv_img.start, yuv_img.length, yuv_img.width, yuv_img.height, yuv_img.code));
	int bytesPerPixel = 2;
	const unsigned char * data = (unsigned char *) yuv_img.start;

	if (jpeg_buffer_size < (yuv_img.length * 3 / 2)) {
		jpeg_buffer_size = yuv_img.length * 3 / 2;
		jpeg_buffer = (unsigned char *) realloc(jpeg_buffer, jpeg_buffer_size);
		if (jpeg_buffer == NULL) {
			printf("Failed to allocate memory for jpeg_buffer");
			return 0;
		} else {
			CAMLOG(printf("jpeg_buffer now %lu", jpeg_buffer_size));
		}
	}

	FILE * out = fmemopen(jpeg_buffer, jpeg_buffer_size, "w");

	  struct jpeg_compress_struct comp;
	  struct jpeg_error_mgr error;

	  comp.err = jpeg_std_error(&error);
	  jpeg_create_compress(&comp);
	  jpeg_stdio_dest(&comp, out);
	  comp.image_width = yuv_img.width;
	  comp.image_height = yuv_img.height;
	  comp.input_components = 3;
	  comp.in_color_space = JCS_YCbCr;
	  jpeg_set_defaults(&comp);
	  jpeg_set_quality(&comp, 90, TRUE);
	  jpeg_start_compress(&comp, TRUE);

	  unsigned char * yuvBuf = (unsigned char *) malloc(yuv_img.width * 3);
	  if(yuvBuf < 0) {
	    return 0;
	  }

	  for (int i = 0; i < yuv_img.height; ++i) {
		  /*
		   From http://v4l2spec.bytesex.org/spec-single/v4l2.html#V4L2-PIX-FMT-YUYV

		  Example 2-1. V4L2_PIX_FMT_YUYV 4 × 4 pixel image

		  Byte Order. Each cell is one byte.

		  start + 0:	Y'00	Cb00	Y'01	Cr00	Y'02	Cb01	Y'03	Cr01
		  start + 8:	Y'10	Cb10	Y'11	Cr10	Y'12	Cb11	Y'13	Cr11
		  start + 16:	Y'20	Cb20	Y'21	Cr20	Y'22	Cb21	Y'23	Cr21
		  start + 24:	Y'30	Cb30	Y'31	Cr30	Y'32	Cb31	Y'33	Cr31

		  Example 2-1. V4L2_PIX_FMT_UYVY 4 × 4 pixel image

		  Byte Order. Each cell is one byte.

		  start + 0:	Cb00	Y'00	Cr00	Y'01	Cb01	Y'02	Cr01	Y'03
		  start + 8:	Cb10	Y'10	Cr10	Y'11	Cb11	Y'12	Cr11	Y'13
		  start + 16:	Cb20	Y'20	Cr20	Y'21	Cb21	Y'22	Cr21	Y'23
		  start + 24:	Cb30	Y'30	Cr30	Y'31	Cb31	Y'32	Cr31	Y'33
		  */
	    //convert a scanline from YUYV to YCbCr
	    unsigned char * yuvPtr = yuvBuf;
	    const unsigned char * yuyvPtr = data;

	    while(yuvPtr < (unsigned char *) (yuvBuf + (yuv_img.width * 3))) {
	      yuvPtr[0] = yuyvPtr[0];
	      yuvPtr[1] = yuyvPtr[1];
	      yuvPtr[2] = yuyvPtr[3];
	      yuvPtr[3] = yuyvPtr[2];
	      yuvPtr[4] = yuyvPtr[1];
	      yuvPtr[5] = yuyvPtr[3];

	      yuyvPtr += 4;
	      yuvPtr += 6;
	    }

	    jpeg_write_scanlines(&comp, &yuvBuf, 1);
	    data += (yuv_img.width * 2);
	  }

	  free(yuvBuf);
	  jpeg_finish_compress(&comp);
	  fflush(out);

	  size_t dataWritten = ftell(out);

	  fclose(out);
	  jpeg_destroy_compress(&comp);

	  return dataWritten;
}

static int grab_frame(JNIEnv *env, int dev_node, struct bug_img &yuv_img)
{
	CAMLOG(printf("grab_frame(dev_node: %d)", dev_node));

	int ret = 0;
	if (current_dev_node != dev_node) {
		CAMLOG(printf("switching to %d (sort of)", dev_node));
		/*
		 * We used to switch between the RAW and RESIZER nodes.
		 * It's more stable if we just switch the resolution
		 * of the RESIZER between the asked-for-at-open-resizing-size
		 * and the asked-for-at-open-raw-size
		 *
		ret = bug_camera_switch_to_dev(dev_node);
		if (ret != 0) {
			printf("Failed to switch mode: ret=%d", ret);
			return ret;
		}
		*/
		v4l2_pix_format *pFormat = &resizer_fmt;
		if (dev_node == V4L2_DEVNODE_RAW) {
			pFormat = &raw_fmt;
		}
		ret = bug_camera_change_resizer_to(pFormat->width, pFormat->height);
		if (ret != 0) {
			printf("Failed to change resizer to %dx%d: ret=%d",
				pFormat->width,
				pFormat->height,
				ret);
			return ret;
		}
		current_dev_node = dev_node;
	}

	CAMLOG(printf("bug_camera_grab()"));
	ret = bug_camera_grab(&yuv_img);
	if (ret != 0) {
		printf("failed to grab image: ret=%d", ret);
		return ret;
	}
	CAMLOG(printf("bug_camera_grab returned yuv_img: start=%p, length=%lu, width=%d, height=%d, code=%d",
			yuv_img.start, yuv_img.length, yuv_img.width, yuv_img.height, yuv_img.code));

	return 0;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_Camera_bug_1camera_1open
  (JNIEnv *env,
		  jobject jobj,
		  jstring jmedia_node,
		  jint slot_num,
		  jint raw_width,
		  jint raw_height,
		  jint resize_width,
		  jint resize_height)
{
	CAMLOG(printf("jni bug camera open: slot_num=%d, raw_width=%d, raw_height=%d, resize_width=%d, resize_height=%d",
			slot_num, raw_width, raw_height, resize_width, resize_height));
	JStringWrapper media_node(env, jmedia_node);

	raw_fmt.width = raw_width;
	raw_fmt.height = raw_height;
	// format needs to be YUYV for the yuv2rgb functions to work properly
	raw_fmt.pixelformat = V4L2_PIX_FMT_YUYV;

	// TODO: I think this only really works if they ask for 320x240
	if (resize_width < 640) {
		// resizer can't go that low so half it afterwards
		half_size = true;
		resize_width = resize_width * 2;
		resize_height = resize_height * 2;
	} else {
		half_size = false;
	}
	resizer_fmt.width = resize_width;
	resizer_fmt.height = resize_height;
	resizer_fmt.pixelformat = raw_fmt.pixelformat;// this is ignored but we set it to the same just in case

	// we'll start in 'preview' mode
	// (note that we don't use V4L2_DEVNODE_PREVIEW -
	// it is the output of the color processing hardware built into the OMAP
	// and we're using a sensor with color processing built-in)
	// actually we'll just use the RESIZER all the time and switch its size
	// to the same as RAW's when we want a full-frame (see comment in grab_frame)
	current_dev_node = V4L2_DEVNODE_RESIZER;
	CAMLOG(printf("bug_camera_open(media: %s, dev_node: %d, slot_num: %d, "
			"raw.width=%d, raw.height=%d, raw.format=%d, resize.width=%d, resize.height=%d, resize.format=%d)",
			(const char*) media_node,
			current_dev_node,
			slot_num,
			raw_fmt.width,
			raw_fmt.height,
			raw_fmt.pixelformat,
			resizer_fmt.width,
			resizer_fmt.height,
			resizer_fmt.pixelformat));
	return bug_camera_open(media_node, current_dev_node, slot_num, &raw_fmt, &resizer_fmt);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_Camera_bug_1camera_1close
  (JNIEnv *, jobject)
{
	// seems like a good point to say we're done with the buffers
	free(jpeg_buffer);
	jpeg_buffer = NULL;
	jpeg_buffer_size = 0;

	free(rgb_buffer);
	rgb_buffer = NULL;

	CAMLOG(printf("calling bug_camera_close()"));
	const int ret = bug_camera_close();
	CAMLOG(printf("bug_camera_close() returned %d", ret));
	return ret;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_Camera_bug_1camera_1start
  (JNIEnv *, jobject)
{
	CAMLOG(printf("calling bug_camera_start()"));
	const int ret = bug_camera_start();
	CAMLOG(printf("bug_camera_start() returned %d", ret));
	return ret;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_Camera_bug_1camera_1stop
  (JNIEnv *, jobject)
{
	CAMLOG(printf("calling bug_camera_stop()"));
	const int ret = bug_camera_stop();
	CAMLOG(printf("bug_camera_stop() returned %d", ret));
	return ret;
}

JNIEXPORT jboolean JNICALL Java_com_buglabs_bug_jni_camera_Camera_bug_1camera_1grab_1preview
  (JNIEnv *env, jobject, jintArray jbuf)
{
	if (jbuf == NULL) {
		CAMLOG(printf("jni grab preview: caller didn't give us a buffer"));
	}
	jint *buf = env->GetIntArrayElements(jbuf, NULL);
	if (buf == NULL) {
		printf("jni grab preview: failed to get elements of caller's buffer");
		return false;
	}

	struct bug_img yuv_img;
	grab_frame(env, V4L2_DEVNODE_RESIZER, yuv_img);

	CAMLOG(printf("jni grab preview: yuv2rgba into caller's buffer: buf=%p, half_size=%d, 0", buf, half_size));
    yuv2rgba(&yuv_img, (int*) buf, half_size, 0);
	CAMLOG(printf("jni grab preview: conversion done"));
	CAMLOG(printf("jni grab preview: yuv_img: start=%p, length=%lu, width=%d, height=%d, code=%d",
			yuv_img.start, yuv_img.length, yuv_img.width, yuv_img.height, yuv_img.code));
    env->ReleaseIntArrayElements(jbuf, buf, 0);
    return true;
}

JNIEXPORT jbyteArray JNICALL Java_com_buglabs_bug_jni_camera_Camera_bug_1camera_1grab_1raw
  (JNIEnv *env, jobject)
{
	CAMLOG(printf("jni grab raw"));
	struct bug_img yuv_img;
	grab_frame(env, V4L2_DEVNODE_RAW, yuv_img);
	const size_t jpeg_size = compressYUYV(yuv_img);

	CAMLOG(printf("asking for java byte array of size %lu", jpeg_size));
    jbyteArray java_buffer = env->NewByteArray(jpeg_size);
	if (java_buffer == NULL) {printf("failed to alloc java_buffer of size %lu", jpeg_size); return NULL;}

	CAMLOG(printf("got java_buffer at %p", java_buffer));
	CAMLOG(printf("Copying %lu bytes from jpeg_buffer %p to java_buffer %p",
			jpeg_size, jpeg_buffer, java_buffer));
    env->SetByteArrayRegion(java_buffer, 0, jpeg_size, (jbyte*) jpeg_buffer);

	CAMLOG(printf("return java_buffer %p", java_buffer));
    return java_buffer;
}
