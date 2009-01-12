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
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_buglabs_bug_jni_common_CharDevice */

#ifndef _Included_com_buglabs_bug_jni_common_CharDevice
#define _Included_com_buglabs_bug_jni_common_CharDevice
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_buglabs_bug_jni_common_CharDevice
 * Method:    open
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_common_CharDevice_open
  (JNIEnv *, jobject, jstring, jint);

/*
 * Class:     com_buglabs_bug_jni_common_CharDevice
 * Method:    getline
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_buglabs_bug_jni_common_CharDevice_getline
  (JNIEnv *, jobject);

/*
 * Class:     com_buglabs_bug_jni_common_CharDevice
 * Method:    read
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_common_CharDevice_read
  (JNIEnv *, jobject);

/*
 * Class:     com_buglabs_bug_jni_common_CharDevice
 * Method:    readBytes
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_common_CharDevice_readBytes
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     com_buglabs_bug_jni_common_CharDevice
 * Method:    lseek
 * Signature: (JI)J
 */
JNIEXPORT jlong JNICALL Java_com_buglabs_bug_jni_common_CharDevice_lseek
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     com_buglabs_bug_jni_common_CharDevice
 * Method:    write
 * Signature: ([BJ)J
 */
JNIEXPORT jlong JNICALL Java_com_buglabs_bug_jni_common_CharDevice_write
  (JNIEnv *, jobject, jbyteArray, jlong);

/*
 * Class:     com_buglabs_bug_jni_common_CharDevice
 * Method:    ioctl
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_common_CharDevice_ioctl
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_buglabs_bug_jni_common_CharDevice
 * Method:    close
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_common_CharDevice_close
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
