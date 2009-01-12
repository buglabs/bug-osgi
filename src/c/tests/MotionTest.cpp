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
#include <fcntl.h>
#include <sys/ioctl.h>
#include <linux/bmi/bmi_mdacc.h>

#include <iostream>
#include <string>
#include <unistd.h>

using namespace std;


int main(int argc, char ** argv) {

	if(argc < 3) {
		cout << "Usage: " << argv[0] << " <devicenode> <numofsamples>" << endl;
	}

	string devnode = argv[1];
	int samples = atoi(argv[2]);

	int fd_motion = open(devnode.c_str(), O_RDWR);
	if(fd_motion < 0) {
		cerr << "Unable to open" << devnode << endl;
		exit(1);
	}

	int res = ioctl(fd_motion, BMI_MDACC_MOTION_DETECTOR_RUN, 0);
	if(res < 0) {
		cerr << "Unable to ioctl RUN" << endl;
	}
	
	for(int i = 0; i < samples; ++i) {
		int sample = 0;
		
		if(read(fd_motion, &sample, 1) < 0) {
			cout << "ERROR" << endl;
		} else {
			cout << "Sample[i]: 0x" << cout.hex <<  sample << endl;
		}
	}
	
	res = ioctl(fd_motion, BMI_MDACC_MOTION_DETECTOR_RUN, 0);
	if(res < 0) {
		cerr << "Unable to ioctl STOP" << endl;
	}
	
	close(fd_motion);

	return 0;
}
