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
