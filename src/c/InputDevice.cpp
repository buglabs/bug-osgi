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
