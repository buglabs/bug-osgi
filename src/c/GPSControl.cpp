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

/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_SUSPEND
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1SUSPEND
(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_GPS_SUSPEND, 0);
}

/*
 * Class:     com_buglabs_bug_jni_gps_GPSControl
 * Method:    ioctl_BMI_GPS_RESUME
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPSControl_ioctl_1BMI_1GPS_1RESUME
(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_GPS_RESUME, 0);
}
