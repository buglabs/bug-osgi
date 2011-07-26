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

#include "jni/com_buglabs_bug_jni_bugduino_Bugduino.h"

#include <sys/types.h>
#include <termios.h>
#include <linux/input.h>
#include <linux/bmi/bmi_vh.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <sys/unistd.h#>
#include <sys/ioctl.h>
#include <stdint.h>
#include <sys/wait.h>

#define BMI_BUGDUINO_IOCTL  ('d')                                               
#define INSTRUCTION_WRITE "WRIT"

#include <linux/bmi/bmi_bugduino.h>
#include "CharDevice.h"

#define DEBUG
#undef DEBUG

static int bugduino_write( int slot, char* filename);
static void bugduino_write_dl( int slot, uint8_t *data, size_t data_size );


static void perror_msg_and_die(char *s) {
	printf("bmi-bugduino.c: %s\n", s);
	exit(-1);
}

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_RESET(JNIEnv *env, jobject jobj, jint value) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_RESET, value); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_IOX_CTL
(JNIEnv *env, jobject jobj, jint arg ) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_IOX_CTRL, arg); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_IOX_READ
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_IOX_READ, 0); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_IOX_WRITE
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_IOX_WRITE, 0); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_I2C_WRITE
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_I2C_WRITE, 0); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_I2C_READ
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_I2C_READ, 0); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_ioctl_BMI_BUGDUINO_SPI_XFER
(JNIEnv *env, jobject jobj) {
		return ioctl(getFileDescriptorField(env, jobj), BMI_BUGDUINO_SPI_XFER, 0); }

JNIEXPORT jint JNICALL Java_com_buglabs_bug_jni_bugduino_Bugduino_write_bugduino_program
	(JNIEnv *env, jobject jobj,
    jstring instruction_arg, jint slot_arg,
    jbyteArray arguments_arg, jint arguments_size_arg )
 {


  int slot;
  jbyte *arguments;
  size_t arguments_size;
  const char *instruction;
  jboolean ignore;
  //fprintf( stderr, "NOTE: in function %s\n", __func__ );

  instruction = env->GetStringUTFChars(instruction_arg, &ignore);
  slot = (int)slot_arg;
   //fprintf( stderr, "NOTE: in function 2 %s\n", __func__ );
  arguments = env->GetByteArrayElements(arguments_arg, &ignore);
   //fprintf( stderr, "NOTE: in function 3 %s\n", __func__ );
  arguments_size = (int)arguments_size_arg;

//printf( "Instruction: %s; Slot: %i; Size: %i\n", instruction,
//  slot_arg, arguments_size );
//  for( i = 0; i < arguments_size; ++i ){
//    printf( "!0x%X!\n", *arguments );
//    0++arguments;
//  }
//  puts( "" );
  // it's not that I don't trust Java - I just don't trust JNI.
  if( strncmp( instruction, INSTRUCTION_WRITE,
    strlen( INSTRUCTION_WRITE ) ) == 0 ){
       //fprintf( stderr, "NOTE: in function 6 %s\n", __func__ );
        bugduino_write_dl( slot,(uint8_t*)arguments, arguments_size );
       //fprintf( stderr, "NOTE: in function 7 %s\n", __func__ );
  } else {
    char* buffer;
   //fprintf( stderr, "NOTE: in function 5 %s\n", __func__ );

    buffer = (char*)malloc( 32 );
    strncpy( buffer, instruction, 32 );
    fprintf( stderr, "ERROR: Invalid instruction: %s\n", buffer );
    free( buffer );

    exit( EXIT_FAILURE );
  }
   //fprintf( stderr, "NOTE: in function 4 %s\n", __func__ );

    //if(ret == 0);
    return (jint)0;
      //exit(EXIT_FAILURE);
}

static void bugduino_write_dl( int slot, uint8_t *data, size_t data_size ){
  int fd;
  pid_t pid;

  /* create a temp'data' file to write data to */
  //fprintf( stderr, "NOTE: in function d7 %s\n", __func__ );
  fd = open( "./.avr_data.hex", O_WRONLY | O_CREAT | O_TRUNC,
                S_IWUSR | S_IRUSR | S_IRGRP | S_IROTH );
  write( fd, data, data_size );
  close( fd );

  /* spawn compiler thread, compile arduino code via avrdude */
  pid = vfork();
  if( pid == 0 ){
    // spawed thread
    int iRet = 0;
    iRet = bugduino_write(  slot, (char*)"./.avr_data.hex");
    if(iRet != 0)
        printf("error doing bugduino_write %d",iRet);
  } else {
    //main thread
    printf("waiting on PID %d\n", pid);
    waitpid( pid, NULL, 0 );
  }

  return;
}


/**
 * write a chunk of hex data to an arduino for programming.
 * @slot - target slot with the bugduino on it
 * @data - data to program to the bugduino
 * @dataSz - size of data to program in bytes
*/
static int bugduino_write( int slot, char* filename)
{
    char uart[254];
    char fileCtl[256];
    int fd = 0;
    if(slot < 0 || slot > 3) {
        printf("Slot %d out of range\n.",slot);
        return -5;
    }
    // generate our ioctl file, and our uart file by slot #
    snprintf(uart, 256,"/dev/ttyBMI%d",slot);
    snprintf(fileCtl,256,"/dev/bmi_bugduino_slot%d",slot);
    fd = open(fileCtl,'w');
    if(fd > 0 ) {
        ioctl(fd, BMI_BUGDUINO_RESET,1);
        sleep(1);
        ioctl(fd, BMI_BUGDUINO_RESET,0);
        close(fd);
    }
    else {
        printf("unable to open bmi slot %d ctrl file %s",slot, fileCtl);
        return -6;
    }

    //control block for actual writing of data
    //via the onboard uart.
    {
        int iRet =0;
        char  uart_prefix[] = "-P";
        char  uart_option[256];
        char  file_option[256];
        snprintf( uart_option, 256, "%s%s", uart_prefix, uart );
        printf("uart option %s%s\n", uart_prefix, uart );
        printf("trying to write via uart %s\n",uart_option);

        snprintf(file_option, 256, "-Uflash:w:%s", filename);
        iRet = execl( "/usr/bin/avrdude", "/usr/bin/avrdude",
            "-pm328p",
            "-cstk500v1",
            uart_option,
            "-b57600",
            "-D",
            file_option,
            NULL );
        return iRet;
    }
    return 0;
}
