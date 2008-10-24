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
