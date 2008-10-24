#include "jni/com_buglabs_bug_jni_motion_Motion.h"
#include <termios.h>
#include <string.h>
#include <stdio.h>
#include <linux/input.h>
#include <linux/bmi/bmi_mdacc.h>
#include <unistd.h>
#include "CharDevice.h"

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_motion_Motion_ioctl_1BMI_1MDACC_1MOTION_1DETECTOR_1GET_1STATUS(JNIEnv * env, jobject jobj)
{
	int status = 0;
	
	int retval = ioctl(getFileDescriptorField(env, jobj), BMI_MDACC_MOTION_DETECTOR_GET_STATUS, &status);
	
	if(retval < 0) {
		return -1;
	}
	
	return status;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_motion_Motion_ioctl_1BMI_1MDACC_1MOTION_1DETECTOR_1RUN(JNIEnv * env, jobject jobj)
{
	return ioctl(getFileDescriptorField(env, jobj), BMI_MDACC_MOTION_DETECTOR_RUN, 0);
}

/*
 * Class:     com_buglabs_bug_jni_motion_Motion
 * Method:    ioctl_BMI_MDACC_MOTION_DETECTOR_STOP
 * Signature: ()
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_motion_Motion_ioctl_1BMI_1MDACC_1MOTION_1DETECTOR_1STOP(JNIEnv * env, jobject jobj) {
	return ioctl(getFileDescriptorField(env, jobj), BMI_MDACC_MOTION_DETECTOR_STOP, 0);
}

