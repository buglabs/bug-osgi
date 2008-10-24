#include "jni/com_buglabs_bug_jni_accelerometer_Accelerometer.h"

extern "C"{
#include <termios.h>
#include <string.h>
#include <stdio.h>
#include <linux/input.h>
#include <linux/bmi/bmi_mdacc.h>
#include <unistd.h>	
}

#include "CharDevice.h"

#define DEBUG
#undef DEBUG

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_accelerometer_Accelerometer_ioctl_1BMI_1MDACC_1ACCELEROMETER_1RUN(JNIEnv * env, jobject jobj)
{
	return ioctl(getFileDescriptorField(env, jobj), BMI_MDACC_ACCELEROMETER_RUN, 0);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_accelerometer_Accelerometer_ioctl_1BMI_1MDACC_1ACCELEROMETER_1STOP(JNIEnv * env, jobject jobj)
{
	return ioctl(getFileDescriptorField(env, jobj), BMI_MDACC_ACCELEROMETER_STOP, 0);
}

JNIEXPORT jshortArray JNICALL Java_com_buglabs_bug_jni_accelerometer_Accelerometer_readSamples(JNIEnv * env, jobject obj, jint count)
{
	struct mdacc_accel_sample accelsamples[count];
	bzero(&accelsamples, sizeof(accelsamples));
	int result = read(getFileDescriptorField(env, obj), &accelsamples, sizeof(accelsamples));

#ifdef DEBUG
	printf(__FUNCTION__);
	printf("\nresult: %d\n", result);
#endif

	if(result >= sizeof(mdacc_accel_sample)) {
		jshortArray samples = env->NewShortArray(result / 2);

		for(int i = 0; i < result / sizeof(mdacc_accel_sample); ++i) {
			accelsamples[i].adc_0 >>= 6;
			accelsamples[i].adc_1 >>= 6;
			accelsamples[i].adc_2 >>= 6;

#ifdef DEBUG		
			printf("z: %x, y: %x, x: %x \n", accelsamples[i].adc_0, accelsamples[i].adc_1, accelsamples[i].adc_2);
#endif
		}

		env->SetShortArrayRegion(samples, 0, result / 2, (jshort *) &accelsamples);

		return samples;
	}

	return NULL;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_accelerometer_Accelerometer_ioctl_1BMI_1MDACC_1ACCELEROMETER_1SET_1CONFIG(JNIEnv * env, jobject jobj, jobject accel_config)
{
	struct mdacc_accel_config config;

	jclass clsAccelConfig = env->GetObjectClass(accel_config);

	/*jfieldID read_queue_size_id = env->GetFieldID(clsAccelConfig, "read_queue_size", "I");
	jfieldID read_queue_threshold_id = env->GetFieldID(clsAccelConfig, "read_queue_threshold", "I");
	jfieldID delay_id = env->GetFieldID(clsAccelConfig, "delay", "S");
	jfieldID delay_resolution_id = env->GetFieldID(clsAccelConfig, "delay_resolution", "B");
	jfieldID delay_mode_id = env->GetFieldID(clsAccelConfig, "delay_mode", "B");
	jfieldID run_id = env->GetFieldID(clsAccelConfig, "run", "B");
	jfieldID sensitivity_id = env->GetFieldID(clsAccelConfig, "sensitivity", "B");
*/
	jmethodID getReadQueueSizeID = env->GetMethodID(clsAccelConfig, "getReadQueueSize", "()I");
	jmethodID getReadQueueThresholdID = env->GetMethodID(clsAccelConfig, "getReadQueueThreshold", "()I");
	jmethodID getDelayID = env->GetMethodID(clsAccelConfig, "getDelay", "()S");
	jmethodID getDelayResolutionID = env->GetMethodID(clsAccelConfig, "getDelayResolution", "()B");
	jmethodID getDelayModeID = env->GetMethodID(clsAccelConfig, "getDelayMode", "()B");
	jmethodID getRunID = env->GetMethodID(clsAccelConfig, "getRun", "()B");
	jmethodID getSensitivityID = env->GetMethodID(clsAccelConfig, "getSensitivity", "()B");

	config.read_queue_size = env->CallIntMethod(accel_config, getReadQueueSizeID);
	config.read_queue_threshold = env->CallIntMethod(accel_config, getReadQueueThresholdID);
	config.delay = env->CallShortMethod(accel_config, getDelayID);
	config.delay_resolution = env->CallByteMethod(accel_config, getDelayResolutionID);
	config.delay_mode = env->CallByteMethod(accel_config, getDelayModeID);
	config.run = env->CallByteMethod(accel_config, getRunID);
	config.sensitivity = env->CallByteMethod(accel_config, getSensitivityID);

	return ioctl(getFileDescriptorField(env, jobj), BMI_MDACC_ACCELEROMETER_SET_CONFIG, &config);
}

JNIEXPORT jobject JNICALL Java_com_buglabs_bug_jni_accelerometer_Accelerometer_ioctl_1BMI_1MDACC_1ACCELEROMETER_1GET_1CONFIG(JNIEnv * env, jobject jobj)
{
	struct mdacc_accel_config config;
	memset(&config, 0, sizeof(config));

	const char * accelConfigClassName = "com/buglabs/bug/accelerometer/pub/AccelerometerConfiguration";

	int result = ioctl(getFileDescriptorField(env, jobj), BMI_MDACC_ACCELEROMETER_GET_CONFIG, &config);

	if(result < 0) {
		perror("JNI BMI_MDACC_ACCELEROMETER_GET_CONFIG ioctl failed");
		return NULL;
	}

	jclass clsAccelConfig = env->FindClass(accelConfigClassName);

	if(clsAccelConfig == NULL) {
		perror("Unable to find class\n");
		return NULL;
	}

	jmethodID accelConfigConstructorID = env->GetMethodID(clsAccelConfig, "<init>", "()V");
	/*jmethodID setReadQueueSizeID = env->GetMethodID(clsAccelConfig, "setReadQueueSize", "()I");
	jmethodID setReadQueueThresholdID = env->GetMethodID(clsAccelConfig, "setReadQueueThreshold", "()I");
	jmethodID setDelayID = env->GetMethodID(clsAccelConfig, "setDelay", "()S");
	jmethodID setDelayResolutionID = env->GetMethodID(clsAccelConfig, "setDelayResolution", "()B");
	jmethodID setDelayModeID = env->GetMethodID(clsAccelConfig, "setDelayMode", "()B");
	jmethodID setRunID = env->GetMethodID(clsAccelConfig, "setRun", "()B");
	jmethodID setSensitivityID = env->GetMethodID(clsAccelConfig, "setSensitivity", "()B");
	*/
	
	jfieldID read_queue_size_id = env->GetFieldID(clsAccelConfig, "read_queue_size", "I");
	jfieldID read_queue_threshold_id = env->GetFieldID(clsAccelConfig, "read_queue_threshold", "I");
	jfieldID delay_id = env->GetFieldID(clsAccelConfig, "delay", "S");
	jfieldID delay_resolution_id = env->GetFieldID(clsAccelConfig, "delay_resolution", "B");
	jfieldID delay_mode_id = env->GetFieldID(clsAccelConfig, "delay_mode", "B");
	jfieldID run_id = env->GetFieldID(clsAccelConfig, "run", "B");
	jfieldID sensitivity_id = env->GetFieldID(clsAccelConfig, "sensitivity", "B");
	
	jobject acconfig = env->NewObject(clsAccelConfig, accelConfigConstructorID);

	env->SetIntField(acconfig, read_queue_size_id, config.read_queue_size);
	env->SetIntField(acconfig, read_queue_threshold_id, config.read_queue_threshold);
	env->SetShortField(acconfig, delay_id, config.delay);
	env->SetByteField(acconfig, delay_resolution_id, config.delay_resolution);
	env->SetByteField(acconfig, delay_mode_id, config.delay_mode);
	env->SetByteField(acconfig, run_id, config.run);
	env->SetByteField(acconfig, sensitivity_id, config.sensitivity);
	
	/*env->CallVoidMethod(acconfig, setReadQueueSizeID, config.read_queue_size);
	env->CallVoidMethod(acconfig, setReadQueueThresholdID, config.read_queue_threshold);
	env->CallVoidMethod(acconfig, setDelayID, config.delay);
	env->CallVoidMethod(acconfig, setDelayResolutionID, config.delay_resolution);
	env->CallVoidMethod(acconfig, setDelayModeID, config.delay_mode);
	env->CallVoidMethod(acconfig, setRunID, config.run);
	env->CallVoidMethod(acconfig, setSensitivityID, config.sensitivity);
	*/
	return acconfig;
}

