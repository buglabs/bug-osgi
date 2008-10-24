#include "jni/com_buglabs_bug_jni_motion_MDACCControl.h"
#include <termios.h>
#include <string.h>
#include <stdio.h>
#include <linux/input.h>
#include <linux/bmi/bmi_mdacc.h>
#include <unistd.h>
#include "CharDevice.h"

/*
 * Class:     com_buglabs_bug_jni_motion_MDACCControl
 * Method:    ioctl_BMI_MDACC_CTL_RED_LED_OFF
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_motion_MDACCControl_ioctl_1BMI_1MDACC_1CTL_1RED_1LED_1OFF(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_MDACC_CTL_RED_LED_OFF, 0);
}

/*
 * Class:     com_buglabs_bug_jni_motion_MDACCControl
 * Method:    ioctl_BMI_MDACC_CTL_RED_LED_ON
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_motion_MDACCControl_ioctl_1BMI_1MDACC_1CTL_1RED_1LED_1ON(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_MDACC_CTL_RED_LED_ON, 0);	
}

/*
 * Class:     com_buglabs_bug_jni_motion_MDACCControl
 * Method:    ioctl_BMI_MDACC_CTL_GREEN_LED_OFF
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_motion_MDACCControl_ioctl_1BMI_1MDACC_1CTL_1GREEN_1LED_1OFF(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_MDACC_CTL_GREEN_LED_OFF, 0);
}

/*
 * Class:     com_buglabs_bug_jni_motion_MDACCControl
 * Method:    ioctl_BMI_MDACC_CTL_GREEN_LED_ON
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_motion_MDACCControl_ioctl_1BMI_1MDACC_1CTL_1GREEN_1LED_1ON(JNIEnv * env, jobject obj)
{
	return ioctl(getFileDescriptorField(env, obj), BMI_MDACC_CTL_GREEN_LED_ON, 0);
}
