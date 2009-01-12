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

using namespace std;

extern "C" {
#include <linux/bmi/bmi_mdacc.h>
#include <stdio.h>
#include <fcntl.h>
#include <sys/ioctl.h>
}

int main(int argc, char ** argv) {
	
	if(argc < 3) {
		cout << "usage: " << argv[0] << " <devnode> <number of samples>" << endl;
		exit(1);
	}
	
	int fd_acc = open(argv[1], O_RDWR);
	int result = 0;
	int samples = atoi(argv[2]);
	
	u_int8_t buff[6] = {0,0,0,0,0,0};
	
	if(fd_acc < 0) {
		perror("Unable to open device\n");
		exit(1);
	}

	result = ioctl(fd_acc, BMI_MDACC_ACCELEROMETER_RUN, 0 );
	if(result < 0) {
		perror("Unable to start acc\n");
		exit(2);
	}
	
	for(int i = 0; i < samples; ++i) {
		result = read(fd_acc, buff, sizeof(buff));
		if(result == 6) {
			printf("sample[%i]: %x %x %x %x %x %x\n", i, buff[0],buff[1],buff[2],buff[3],buff[4],buff[5]);
		} else {
			printf("read failed %i \n", i);
		}
	}
	
	result = ioctl(fd_acc, BMI_MDACC_ACCELEROMETER_STOP, 0 );
	if(result < 0) {
		perror("Unable to stop acc\n");
		exit(3);
	}

	result = close(fd_acc);
	return 0;	
}
