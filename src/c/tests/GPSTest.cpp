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
