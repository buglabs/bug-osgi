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
#include "jni/com_buglabs_bug_jni_vonhippel_VonHippel.h"

extern "C" {
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
#include <linux/bmi/bmi_vh.h>
}
#include "CharDevice.h"

#define DEBUG
#undef DEBUG

static void perror_msg_and_die(char *s) {
	printf("bmi-vh.c: %s\n", s);
	exit(-1);
}
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1RLEDOFF
(JNIEnv * env, jobject jobj) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_RLEDOFF, 0);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1RLEDON
(JNIEnv * env, jobject jobj) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_RLEDON, 0);

}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1GLEDOFF
(JNIEnv * env, jobject jobj) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_GLEDOFF, 0);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1GLEDON
(JNIEnv * env, jobject jobj) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_GLEDON, 0);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1GETSTAT
(JNIEnv * env, jobject jobj) {
	int stat;
	ioctl(getFileDescriptorField(env, jobj), BMI_VH_GETSTAT, &stat);
	return stat;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1MKGPIO_1OUT
(JNIEnv * env, jobject jobj, jint pin ) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_MKGPIO_OUT, (pin & 0x000F));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1MKGPIO_1IN
(JNIEnv * env, jobject jobj, jint pin) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_MKGPIO_IN, (pin & 0x000F));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1SETGPIO
(JNIEnv * env, jobject jobj, jint pin) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_SETGPIO, (pin & 0x000F));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1CLRGPIO
(JNIEnv * env, jobject jobj, jint pin) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_CLRGPIO, pin & (0x000F));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1MKIOX_1OUT
(JNIEnv * env, jobject jobj, jint pin) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_MKIOX_OUT, pin & (0x000F));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1MKIOX_1IN
(JNIEnv * env, jobject jobj, jint pin) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_MKIOX_IN, pin & (0x000F));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1SETIOX
(JNIEnv * env, jobject jobj, jint pin) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_SETIOX, pin & (0x000F));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1CLRIOX
(JNIEnv * env, jobject jobj, jint pin) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_CLRIOX, pin & (0x000F));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1SETRDAC
(JNIEnv * env, jobject jobj, jint resistance) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_VH_SETRDAC, resistance & (0xFF));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1RDRDAC
(JNIEnv * env, jobject jobj) {
	int stat;
	ioctl(getFileDescriptorField(env, jobj), BMI_VH_RDRDAC, &stat);
	return stat;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1ADCWR
(JNIEnv * env, jobject jobj, jint control) {
	struct vh_adc_wr adc_wr;
	adc_wr.w1 = (control & 0xF);
	control >>= 8;
	adc_wr.w2 = (control & 0xF);
	return (ioctl(getFileDescriptorField(env, jobj), BMI_VH_ADCWR, &adc_wr));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1ADCRD
(JNIEnv * env, jobject jobj) {
	int reading;
	ioctl(getFileDescriptorField(env, jobj), BMI_VH_ADCRD, &reading);
	return reading;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1DACWR
(JNIEnv * env, jobject jobj, jint control) {
	struct vh_dac_wr dac_wr;
	dac_wr.w1 = (control & 0xFF);
	control >>= 8;
	dac_wr.w2 = (control & 0xFF);
	return (ioctl(getFileDescriptorField(env, jobj), BMI_VH_DACWR, &dac_wr));
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1DACRD
(JNIEnv * env, jobject jobj) {
	int reading;
	ioctl(getFileDescriptorField(env, jobj), BMI_VH_DACRD, &reading);
	return reading;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1READ_1SPI
(JNIEnv * env, jobject jobj) {
	struct spi_xfer spi_xfer;
	int returnval;
	ioctl(getFileDescriptorField(env, jobj), BMI_VH_DACRD, &spi_xfer);
	int addr = (spi_xfer.addr << 16 & 0x0F00); 
	int data0 = (spi_xfer.data[0] << 8 & 0x00F0);
	int data1 = (spi_xfer.data[1] & 0x000F);
	returnval = ( addr | data0 | data1);
	return returnval;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_vonhippel_VonHippel_ioctl_1BMI_1VH_1WRITE_1SPI
(JNIEnv * env, jobject jobj, jint control) {
	struct spi_xfer spi_xfer;
	spi_xfer.addr = ((control >> 16) & 0x0F); 
	spi_xfer.data[0] = ((control >> 8) & 0x0F);
	spi_xfer.data[1] = (control & 0x0F);
	ioctl(getFileDescriptorField(env, jobj), BMI_VH_WRITE_SPI, &spi_xfer);
}


