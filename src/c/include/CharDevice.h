#ifndef CHARDEVICE_H_
#define CHARDEVICE_H_

jfieldID getFileDescriptorFieldID(JNIEnv * env, jobject obj);

void setFileDescriptorField(JNIEnv * env, jobject obj, int fd);

int getFileDescriptorField(JNIEnv * env, jobject obj);

#endif /*CHARDEVICE_H_*/
