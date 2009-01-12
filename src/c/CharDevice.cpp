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
#include "jni/com_buglabs_bug_jni_common_CharDevice.h"
#include <fcntl.h>
#include <unistd.h>
#include <fstream>
#include <sys/ioctl.h>
#include <errno.h>
#include "CharDevice.h"

#include <iostream>

using namespace std;

jfieldID getFileDescriptorFieldID(JNIEnv * env, jobject obj)
{
	jclass cls = env->GetObjectClass(obj);

	return env->GetFieldID(cls, "fd", "I");
}

void setFileDescriptorField(JNIEnv * env, jobject obj, int fd)
{
	jfieldID fid = getFileDescriptorFieldID(env, obj);
	env->SetIntField(obj, fid, fd);
}

int getFileDescriptorField(JNIEnv * env, jobject obj)
{
	jfieldID fid = getFileDescriptorFieldID(env, obj);
	int fd = env->GetIntField(obj, fid);

	return fd;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_common_CharDevice_open(JNIEnv * env, jobject obj, jstring file, jint mode)
{
	int fd;

	const char * str = NULL;

	str = env->GetStringUTFChars(file, NULL);

	fd = open(str, mode);

	env->ReleaseStringUTFChars(file, str);

	setFileDescriptorField(env, obj, fd);

	return fd;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_common_CharDevice_read(JNIEnv * env, jobject obj)
{
	int buf[1] = {0};

	int retval = 0;
	retval = read(getFileDescriptorField(env, obj), buf, 1);

	if(retval > 0) {
		return buf[0];
	}

	return -1;
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_common_CharDevice_readBytes(JNIEnv * env, jobject obj, jbyteArray buff)
{
	jbyte * primBuff = (jbyte *) env->GetByteArrayElements(buff, 0);
	jsize length = env->GetArrayLength(buff);
	
	int result = read(getFileDescriptorField(env, obj), primBuff, length);
	env->ReleaseByteArrayElements(buff, primBuff, 0);
		
	return result;
}

JNIEXPORT jstring JNICALL Java_com_buglabs_bug_jni_common_CharDevice_getline(JNIEnv * env, jobject obj)
{
	size_t read = 0;
	char * lineread = 0;
	int ret = 0;

	FILE * fdstream = 0;

	fdstream = fdopen(getFileDescriptorField(env, obj), "r");

	ret = getline(&lineread, &read, fdstream);

	//TODO: Throw exception from here
	if(ret < 0) 
	{
		return env->NewStringUTF("");	
	} else {
		return env->NewStringUTF(lineread);
	}

	free(lineread);
	fclose(fdstream);
}

JNIEXPORT jlong JNICALL Java_com_buglabs_bug_jni_common_CharDevice_lseek(JNIEnv * env, jobject obj, jlong offset, jint whence)
{ 
	return lseek(getFileDescriptorField(env, obj), offset, whence);
}

JNIEXPORT jlong JNICALL Java_com_buglabs_bug_jni_common_CharDevice_write(JNIEnv * env, jobject obj, jbyteArray jbuf, jlong count)
{
	void * buff = 0;
	jboolean * boolPtr = 0;
	jlong results = 0;

	buff = env->GetPrimitiveArrayCritical(jbuf, boolPtr); 
	results = write(getFileDescriptorField(env, obj), buff, count);
	env->ReleasePrimitiveArrayCritical(jbuf,  buff, JNI_ABORT);

	return results;

}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_common_CharDevice_ioctl(JNIEnv * env, jobject obj,  jint request)
{
	return ioctl(getFileDescriptorField(env, obj), request);
}


JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_common_CharDevice_close(JNIEnv * env, jobject obj)
{
	return close(getFileDescriptorField(env, obj));
}
