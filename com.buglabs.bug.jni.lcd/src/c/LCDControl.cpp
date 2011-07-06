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
#include "jni/com_buglabs_bug_jni_lcd_LCDControl.h"
#include <termios.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <linux/bmi/bmi_lcd.h>
#include <sys/ioctl.h>
#include "CharDevice.h"

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1RLEDOFF(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_RLEDOFF, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_RLEDON
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1RLEDON(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_RLEDON, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_GLEDOFF
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1GLEDOFF(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_GLEDOFF, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_GLEDON
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1GLEDON(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_GLEDON, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_VSYNC_DIS
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1VSYNC_1DIS(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_VSYNC_DIS, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_VSYNC_EN
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1VSYNC_1EN(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_VSYNC_EN, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_EN
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1EN(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_EN, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_DIS
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1DIS(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_DIS, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_SER_EN
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1SER_1EN(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_SER_EN, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_SER_DIS
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1SER_1DIS(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_SER_DIS, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_SETRST
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1SETRST(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_SETRST, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_CLRRST
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1CLRRST(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_CLRRST, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_SET_BL
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1SET_1BL(JNIEnv * env, jobject obj, jint slot, jint value)
{
	unsigned int ioctl_val = slot & 0xf;
	ioctl_val |= ((value & 0x7) << 4);
	
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_SET_BL, ioctl_val);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_GETSTAT
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1GETSTAT(JNIEnv * env, jobject obj, jint slot)
{
	unsigned int status = slot;
	
	int retval = ioctl(getFileDescriptorField(env, obj), BMI_LCD_GETSTAT, status);
	
	if(retval < slot) {
		return -1;
	}
	
	return status;
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_ACTIVATE
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1ACTIVATE(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_ACTIVATE, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_DEACTIVATE
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1DEACTIVATE(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_DEACTIVATE, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_SUSPEND
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1SUSPEND(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_SUSPEND, slot);
}

/*
 * Class:     com_buglabs_bug_jni_lcd_LCDControl
 * Method:    ioctl_BMI_LCD_RESUME
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_lcd_LCDControl_ioctl_1BMI_1LCD_1RESUME(JNIEnv * env, jobject obj, jint slot)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_LCD_RESUME, slot);
}
