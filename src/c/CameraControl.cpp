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
#include "jni/com_buglabs_bug_jni_camera_CameraControl.h"
#include <linux/videodev2.h>
extern "C" {
#include "bug_v4l.h"
}

/*
  ioctl: VIDIOC_QUERYCTRL  id=0x9a0911 (V4L2_CID_FLASH_STROBE)
    name    : Flash Strobe
    id      : 0x9a0911
    type    : 0x3
    minimum : 0
    maximum : 2
    default : 0
    flags   : 0x0
    ioctl   : VIDIOC_QUERYMENU (Flash Strobe)
      0 : OFF
      1 : TORCH (LOW BEAM)
      2 : PULSE (HIGH BEAM)
    ioctl   : VIDIOC_G_CTRL (Flash Strobe)
*/

static const int STROBE_OFF = 0;
static const int STROBE_TORCH_AKA_LOW_BEAM = 1;
static const int STROBE_PULSE_AKA_HIGH_BEAM = 2;

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1FLASH_1HIGH_1BEAM(JNIEnv * env, jobject jobj)
{
	return set_ctrl(V4L2_CID_FLASH_STROBE, STROBE_PULSE_AKA_HIGH_BEAM);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1FLASH_1LOW_1BEAM(JNIEnv * env, jobject jobj)
{
	return set_ctrl(V4L2_CID_FLASH_STROBE, STROBE_TORCH_AKA_LOW_BEAM);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1FLASH_1LED_1OFF(JNIEnv * env, jobject jobj)
{
	set_ctrl(V4L2_CID_FLASH_STROBE, STROBE_OFF);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1FLASH_1LED_1ON(JNIEnv * env, jobject jobj)
{
	// TODO: What was the difference  between High or Low Beam and On on the old camera?
	return JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1FLASH_1LOW_1BEAM(env, jobj);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1RED_1LED_1OFF(JNIEnv * env, jobject jobj)
{
	return set_red_led(0);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1RED_1LED_1ON(JNIEnv * env, jobject jobj)
{
	return set_red_led(1);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1GREEN_1LED_1OFF(JNIEnv * env, jobject jobj)
{
	return set_green_led(0);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1GREEN_1LED_1ON(JNIEnv * env, jobject jobj)
{
	return set_green_led(1);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1SELECT(JNIEnv * env, jobject jobj, jint slot)
{
	printf("NOT IMPLEMENTED\n");return -1;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1GET_1SELECTED(JNIEnv * env, jobject jobj)
{
	return get_input_slot();
}


JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1SUSPEND
  (JNIEnv * env, jobject jobj){
	printf("NOT IMPLEMENTED\n");return -1;
	//int fd = getFileDescriptorField(env, jobj);
	//return ioctl(fd, BMI_CAM_SUSPEND);
}


JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1RESUME
(JNIEnv * env, jobject jobj){
	printf("NOT IMPLEMENTED\n");return -1;
	//int fd = getFileDescriptorField(env, jobj);
	//return ioctl(fd, BMI_CAM_RESUME);
}
