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
