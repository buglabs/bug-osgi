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
