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

//#define CAMLOG(x)
#define CAMLOG(x) {x; printf("\n"); fflush(stdout);}


// TODO: these should be in the class
static const int BYTES_PER_PIXEL = 3;
static int current_dev_node = -1;
static unsigned char *jpeg_buffer = NULL;
static size_t jpeg_buffer_size = 0;
static unsigned char *rgb_buffer = NULL;
static v4l2_pix_format raw_fmt;
static v4l2_pix_format resize_fmt;
static bool half_size = false;

size_t compressUYVY2(const struct bug_img &yuv_img)
{
	int bytesPerPixel = 2;
	const unsigned char * data = (unsigned char *) yuv_img.start;

	if (jpeg_buffer_size < (yuv_img.length * 3 / 2)) {
		jpeg_buffer_size = yuv_img.length * 3 / 2;
		jpeg_buffer = (unsigned char *) realloc(jpeg_buffer, jpeg_buffer_size);
		if (jpeg_buffer == NULL) {
			CAMLOG(printf("Failed to allocate memory for jpeg_buffer"));
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
	    //convert a scanline from CbYCrY to YCbCr
	    unsigned char * yuvPtr = yuvBuf;
	    const unsigned char * uyvyPtr = data;

	    while(yuvPtr < (unsigned char *) (yuvBuf + (yuv_img.width * 3))) {
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

static size_t yuv2jpeg(const struct bug_img &yuv_img)
{
	CAMLOG(printf("yuv2jpeg()"));
	if (jpeg_buffer_size < (yuv_img.length * 3 / 2)) {
		jpeg_buffer_size = yuv_img.length * 3 / 2;
		jpeg_buffer = (unsigned char *) realloc(jpeg_buffer, jpeg_buffer_size);
		if (jpeg_buffer == NULL) {
			CAMLOG(printf("Failed to allocate memory for jpeg_buffer"));
			return 0;
		} else {
			CAMLOG(printf("jpeg_buffer now %lu", jpeg_buffer_size));
		}
	}

	CAMLOG(printf("opening jpeg_buffer %p for %lu", jpeg_buffer, jpeg_buffer_size));
    FILE *out = fmemopen(jpeg_buffer, jpeg_buffer_size, "w");
    if (out == NULL) {
    	CAMLOG(printf("Failed to open jpeg_buffer output stream"));
    	return 0;
    }

    struct jpeg_compress_struct comp;
    struct jpeg_error_mgr error;

    comp.err = jpeg_std_error(&error);
    jpeg_create_compress(&comp);
    jpeg_stdio_dest(&comp, out);
    comp.image_width = yuv_img.width;
    comp.image_height = yuv_img.height;
    comp.input_components = BYTES_PER_PIXEL;
    comp.in_color_space = JCS_YCbCr;
    jpeg_set_defaults(&comp);
    jpeg_set_quality(&comp, 90, TRUE);

    jpeg_start_compress(&comp, TRUE);

    const size_t line_length = yuv_img.width * BYTES_PER_PIXEL;

    unsigned char *yuv_ptr = (unsigned char*) yuv_img.start;
    for (int i = 0; i < yuv_img.height; ++i, yuv_ptr += line_length) {
        jpeg_write_scanlines(&comp, &yuv_ptr, 1);
    }

    jpeg_finish_compress(&comp);
    fflush(out);

    size_t dataWritten = ftell(out);
    fclose(out);
    jpeg_destroy_compress(&comp);

    CAMLOG(printf("yuv2jpg returning %lu", dataWritten));
    return dataWritten;
}

static int grab_frame(JNIEnv *env, int dev_node, struct bug_img &yuv_img)
{
	CAMLOG(printf("grab_frame(dev_node: %d)", dev_node));

	int ret = 0;
	if (current_dev_node != dev_node) {
		CAMLOG(printf("switching to %d", dev_node));
		ret = bug_camera_switch_to_dev(dev_node);
		if (ret != 0) {
			CAMLOG(printf("Failed to switch mode: ret=%d", ret));
			return ret;
		}
		current_dev_node = dev_node;
	}

	CAMLOG(printf("bug_camera_grab()"));
	ret = bug_camera_grab(&yuv_img);
	if (ret != 0) {
		CAMLOG(printf("failed to grab image: ret=%d", ret));
		return ret;
	}

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
	JStringWrapper media_node(env, jmedia_node);

	raw_fmt.width = raw_width;
	raw_fmt.height = raw_height;
	raw_fmt.pixelformat = V4L2_PIX_FMT_UYVY;

	// TODO: I think this only really works if they ask for 320x240
	if (resize_width < 640) {
		// resizer can't go that low so half it afterwards
		half_size = true;
		resize_width = resize_width * 2;
		resize_height = resize_height * 2;
	} else {
		half_size = false;
	}
	resize_fmt.width = resize_width;
	resize_fmt.height = resize_height;
	resize_fmt.pixelformat = V4L2_PIX_FMT_YUYV;

	// we'll start in preview mode
	// (note that we don't use V4L2_DEVNODE_PREVIEW -
	// it is the output of the color processing hardware built into the OMAP
	// and we're using a sensor with color processing built-in)
	current_dev_node = V4L2_DEVNODE_RESIZER;
	CAMLOG(printf("bug_camera_open(media: %s, dev_node: %d, slot_num: %d, "
			"raw.width=%d, raw.height=%d, raw.format=%d, resize.width=%d, resize.height=%d, resize.format=%d)",
			(const char*) media_node,
			current_dev_node,
			slot_num,
			raw_fmt.width,
			raw_fmt.height,
			raw_fmt.pixelformat,
			resize_fmt.width,
			resize_fmt.height,
			resize_fmt.pixelformat));
	return bug_camera_open(media_node, current_dev_node, slot_num, &raw_fmt, &resize_fmt);
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

JNIEXPORT jbyteArray JNICALL Java_com_buglabs_bug_jni_camera_Camera_bug_1camera_1grab_1preview
  (JNIEnv *env, jobject)
{
	struct bug_img yuv_img;
	grab_frame(env, V4L2_DEVNODE_RESIZER, yuv_img);

	const size_t rgb_size = yuv_img.length * 3 / 2;
    rgb_buffer = (unsigned char*) realloc(rgb_buffer, rgb_size);
    if (rgb_buffer == NULL) {
    	CAMLOG(printf("Failed to realloc rgb buffer to %lu", rgb_size));
    	return NULL;
    }

    CAMLOG(printf("yuv2rgb, rgb size=%lu, half_size=%d", rgb_size, half_size));
    yuv2rgb(&yuv_img, rgb_buffer, half_size);

	CAMLOG(printf("asking for java byte array of size %lu", rgb_size));
    jbyteArray java_buffer = env->NewByteArray(rgb_size);
	if (java_buffer == NULL) {CAMLOG(printf("failed to alloc java_buffer of size %lu", rgb_size)); return NULL;}

	CAMLOG(printf("got java_buffer at %p", java_buffer));
	CAMLOG(printf("Copying %lu bytes from rgb_buffer %p to java_buffer %p",
			rgb_size, rgb_buffer, java_buffer));
    env->SetByteArrayRegion(java_buffer, 0, rgb_size, (jbyte*) rgb_buffer);

	CAMLOG(printf("return java_buffer %p", java_buffer));
    return java_buffer;
}

JNIEXPORT jbyteArray JNICALL Java_com_buglabs_bug_jni_camera_Camera_bug_1camera_1grab_1raw
  (JNIEnv *env, jobject)
{
	struct bug_img yuv_img;
	grab_frame(env, V4L2_DEVNODE_RAW, yuv_img);
	const size_t jpeg_size = compressUYVY2(yuv_img);

	CAMLOG(printf("asking for java byte array of size %lu", jpeg_size));
    jbyteArray java_buffer = env->NewByteArray(jpeg_size);
	if (java_buffer == NULL) {CAMLOG(printf("failed to alloc java_buffer of size %lu", jpeg_size)); return NULL;}

	CAMLOG(printf("got java_buffer at %p", java_buffer));
	CAMLOG(printf("Copying %lu bytes from jpeg_buffer %p to java_buffer %p",
			jpeg_size, jpeg_buffer, java_buffer));
    env->SetByteArrayRegion(java_buffer, 0, jpeg_size, (jbyte*) jpeg_buffer);

	CAMLOG(printf("return java_buffer %p", java_buffer));
    return java_buffer;
}
