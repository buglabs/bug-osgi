#include <linux/input.h>
#include <linux/types.h>
#include <fcntl.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>


#define EVENT_DEV "/dev/input/mouse0"

int main(int argc, char ** argv)
{
	int fd = 0, result = 0, i = 0;
	const char * dev = NULL;
	
	struct input_event ev[64];
	
	if(argc > 1) {
		dev = argv[1];
	} else {
		dev = EVENT_DEV;
	}
	
	printf("DEV: %s\n", dev);

	fd = open(dev, O_RDWR);
	
	if(fd < 0) { 
		perror("Unable to open event device\n");
		exit(1);
	} else {
		printf("Opened %s\n", dev);
	}
	
	while(1) {
		result = read(fd, ev, sizeof(struct input_event) * 64);
		
		if(result < sizeof(struct input_event)) {
				printf("Error reading input event\n");
		} 
		else {
			for( i = 0; i < result / sizeof(struct input_event); ++i) {
					printf("One event\n");
			}	
		}
	}

	close(fd);
}
