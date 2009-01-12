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
#include "jni/com_buglabs_bug_jni_input_InputDevice.h"
#include <termios.h>
#include <string.h>
#include <stdio.h>
#include <linux/input.h>
#include <unistd.h>
#include "CharDevice.h"

#define EVENT_BUFF_SIZE 64

JNIEXPORT jobjectArray JNICALL Java_com_buglabs_bug_jni_input_InputDevice_readEvents(JNIEnv * env, jobject jobj)
{
	int rd = 0, i = 0;
	struct input_event events[EVENT_BUFF_SIZE];
	jfieldID typeFID, codeFID, valueFID, timevalFID;
	jfieldID tv_secFID, tv_usecFID;
	
	jclass ieClass = env->FindClass("com/buglabs/bug/jni/input/InputEvent");
	jmethodID cid = env->GetMethodID(ieClass, "<init>", "()V");
	
	typeFID = env->GetFieldID(ieClass, "type", "I");
	codeFID = env->GetFieldID(ieClass, "code", "I");
	valueFID = env->GetFieldID(ieClass, "value", "J");
	timevalFID = env->GetFieldID(ieClass, "time", "Lcom/buglabs/bug/jni/input/TimeVal;");
	
	jclass timevalClass = env->FindClass("com/buglabs/bug/jni/input/TimeVal");
	jmethodID timevalCID = env->GetMethodID(timevalClass, "<init>", "()V");
	
	tv_secFID = env->GetFieldID(timevalClass, "tv_sec", "J");
	tv_usecFID = env->GetFieldID(timevalClass, "tv_usec", "J");
		
	memset(&events, 0, sizeof(events));
	
	rd = read(getFileDescriptorField(env, jobj), &events, sizeof(events));
	
	size_t arraySize = rd / sizeof(struct input_event);
	
	jobjectArray ieArray = env->NewObjectArray(arraySize, ieClass, 0);
	
	for(i = 0; i < arraySize; ++i) {
		jobject obj = env->NewObject(ieClass, cid);
		
		env->SetIntField(obj, typeFID, 0xFFFF & events[i].type);
		env->SetIntField(obj, codeFID, 0xFFFF & events[i].code);
		env->SetLongField(obj, valueFID, 0xFFFFFFFF & events[i].value);
		
		//Create the timeval object and add it to ieClass object
		jobject timevalObj = env->NewObject(timevalClass, timevalCID);
		env->SetLongField(timevalObj, tv_secFID, events[i].time.tv_sec);
		env->SetLongField(timevalObj, tv_usecFID, events[i].time.tv_usec);
		env->SetObjectField(obj, timevalFID, timevalObj);
		
		env->SetObjectArrayElement(ieArray, i, obj);
		
		env->DeleteLocalRef(obj);
		env->DeleteLocalRef(timevalObj);
	}
	
	return ieArray;
}
