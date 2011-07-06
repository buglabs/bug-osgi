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
#include <iostream>
#include <fstream>
#include <termios.h>
#include <fcntl.h>
#include <string.h>
#include <stdio.h>

using namespace std;

#define GPS_DEV "/dev/ttyUSB0"

void printSettings(struct termios settings);

int main(int argc, char ** argv)
{
	int fd = 0, result = 0;
	FILE * fdstream;
	char * lineptr = NULL;
	size_t len = 0;
	string dev;
	string errormsg;
	
	if(argc > 1) {
		dev	= argv[1];
	} else {
		dev = GPS_DEV;
	}
	
	cout << "dev = " << dev << endl;
	
	struct termios current, newsettings;
	
	//O_NOCTTY <-- Don't listen to CTRl-C
	fd = open(dev.c_str(), O_RDWR | O_NOCTTY);
	if(fd < 0) { 
		errormsg = "Unable to open " + dev;
		
		perror(errormsg.c_str());
		exit(1);
	} else {
		cout << "Opened " << GPS_DEV << endl;
	}
	
	printSettings(current);
	
	memset(&newsettings, 0, sizeof(struct termios));
	
	newsettings.c_cflag = B9600 | CS8 | CLOCAL | CREAD;
	newsettings.c_iflag = IGNPAR | IGNCR;
	newsettings.c_oflag = 0;
	newsettings.c_lflag = ICANON;
	
	result = tcsetattr(fd, TCSANOW |TCSAFLUSH, &newsettings);
	if(result < 0) 
	{
		perror("Unable to configure serial port");
	}
	
	fdstream = fdopen(fd, "r");
	
	while(true)
	{
		int read = getline(&lineptr, &len, fdstream);
		cout << lineptr;
	}
	
	//TODO: Handle break
	//free lineptr
	
	fclose(fdstream);

	close(fd);
}

void printSettings(struct termios settings)
{
	
}
