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
#include "CharDevice.h"

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_gps_GPS_init(JNIEnv * env, jobject jobj)
{
	struct termios settings; 
	int fd = getFileDescriptorField(env, jobj);
	memset(&settings, 0, sizeof(struct termios));

	settings.c_cflag = B9600 | CS8 | CLOCAL | CREAD;
	settings.c_iflag = IGNPAR | IGNCR;
	settings.c_oflag = 0;
	settings.c_lflag = ICANON;
	tcflush(fd, TCIFLUSH);
	int result = tcsetattr(fd, TCSANOW |TCSAFLUSH, &settings);
	if(result < 0)
	{
		perror("Unable to configure serial port");
	}
	
	//turn off dev messages
	result = write(fd, "$PSRF105,0*3F", 13);

	if(result < 0) 
	{
		perror("Unable to write to serial port");
	}

	return result;
}
