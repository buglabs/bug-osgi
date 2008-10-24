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
#include "jni/com_buglabs_bug_jni_gps_GPS.h"
#include <termios.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <linux/bmi/bmi_gps.h>
#include <sys/ioctl.h>
#include "CharDevice.h"

#define DEBUG
#undef DEBUG
/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_RLEDOFF
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1RLEDOFF(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_GPS_RLEDOFF, 0);
}

/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_RLEDON
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1RLEDON(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_GPS_RLEDON, 0);
}

/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_GLEDOFF
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1GLEDOFF(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_GPS_GLEDOFF, 0);
}

/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_GLEDON
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1GLEDON(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_GPS_GLEDON, 0);
}

/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_GETSTAT
 * Signature: ()I
 */
JNIEXPORT int JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1GETSTAT(JNIEnv * env, jobject obj)
{
	unsigned int status = 0;
	
	int result = ioctl(getFileDescriptorField(env, obj), BMI_GPS_GETSTAT, &status);
	
	if(result < 0) {
		//TODO: throw exception
		return result;
	}
#ifdef DEBUG
	printf("GPS IOX: %x\n", status);
#endif
	return status;
}

/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_SETRST
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1SETRST(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_GPS_SETRST, 0);	
}

/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_CLRRST
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1CLRRST(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_GPS_CLRRST, 0);
}

/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_ACTIVE_ANT
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1ACTIVE_1ANT(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_GPS_ACTIVE_ANT, 0);
}

/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_PASSIVE_ANT
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1PASSIVE_1ANT(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_GPS_PASSIVE_ANT, 0);
}
