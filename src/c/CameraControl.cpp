#include "jni/com_buglabs_bug_jni_camera_CameraControl.h"
#include "linux/bmi/bmi_camera.h"
#include "CharDevice.h"

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1FLASH_1HIGH_1BEAM(JNIEnv * env, jobject jobj)
{
	int fd = getFileDescriptorField(env, jobj);
	return ioctl(fd, BMI_CAM_FLASH_HIGH_BEAM);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1FLASH_1LOW_1BEAM(JNIEnv * env, jobject jobj)
{
	int fd = getFileDescriptorField(env, jobj);
	return ioctl(fd, BMI_CAM_FLASH_LOW_BEAM);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1FLASH_1LED_1OFF(JNIEnv * env, jobject jobj)
{
	int fd = getFileDescriptorField(env, jobj);
	return ioctl(fd, BMI_CAM_FLASH_LED_OFF);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1FLASH_1LED_1ON(JNIEnv * env, jobject jobj)
{
	int fd = getFileDescriptorField(env, jobj);
	return ioctl(fd, BMI_CAM_FLASH_LED_ON);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1RED_1LED_1OFF(JNIEnv * env, jobject jobj)
{
	int fd = getFileDescriptorField(env, jobj);
	return ioctl(fd, BMI_CAM_RED_LED_OFF);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1RED_1LED_1ON(JNIEnv * env, jobject jobj)
{
	int fd = getFileDescriptorField(env, jobj);
	return ioctl(fd, BMI_CAM_RED_LED_ON);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1GREEN_1LED_1OFF(JNIEnv * env, jobject jobj)
{
	int fd = getFileDescriptorField(env, jobj);
	return ioctl(fd, BMI_CAM_GREEN_LED_OFF);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1GREEN_1LED_1ON(JNIEnv * env, jobject jobj)
{
	int fd = getFileDescriptorField(env, jobj);
	return ioctl(fd, BMI_CAM_GREEN_LED_ON);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1SELECT(JNIEnv * env, jobject jobj, jint slot)
{
	int fd = getFileDescriptorField(env, jobj);
	return ioctl(fd, BMI_CAM_SELECT, slot);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_camera_CameraControl_ioctl_1BMI_1CAM_1GET_1SELECTED(JNIEnv * env, jobject jobj)
{
	int fd = getFileDescriptorField(env, jobj);
	return ioctl(fd, BMI_CAM_GET_SELECTED);
}



