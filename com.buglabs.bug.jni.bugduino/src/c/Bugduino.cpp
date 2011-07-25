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

#include "jni/com_buglabs_bug_jni_bugduino_Bugduino.h"

#include <termios.h>
#include <string.h>
#include <stdio.h>
#include <linux/input.h>
#include <linux/bmi/bmi_vh.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <sys/unistd.h>
#include <sys/ioctl.h>
#include <linux/bmi/bmi_bugduino.h>
#include "CharDevice.h"

#define DEBUG
#undef DEBUG

static void perror_msg_and_die(char *s) {
	printf("bmi-bugduino.c: %s\n", s);
	exit(-1);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_RESET(JNIEnv *env, jobject jobj, jint value) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_RESET, value); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_IOX_CTL
(JNIEnv *env, jobject jobj, jint arg ) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_IOX_CTRL, arg); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_IOX_READ
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_IOX_READ, 0); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_IOX_WRITE
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_IOX_WRITE, 0); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_I2C_WRITE
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_I2C_WRITE, 0); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_I2C_READ
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_I2C_READ, 0); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_SPI_XFER
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_SPI_XFER, 0); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_write_bugduino_program
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_SPI_XFER, 0); }




