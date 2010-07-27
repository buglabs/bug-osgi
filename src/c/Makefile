JAVAH = javah
#note, this uses GCC, not G++ like most of the other JNI.  aplay is written in c. ;D
# env vars from eclipse: 
# CXXFLAGS:  -B ${ROOTFS_BUG}/lib/ -B ${ROOTFS_BUG}/usr/lib/ -iwithprefix include -O2 -fsigned-char -mabi=aapcs-linux -mfpu=vfp -mfloat-abi=softfp
# GCC = arm-none-linux-gnueabi-gcc

ifdef LTIB_ROOTFS
PHONEME_HOME=$(LTIB_ROOTFS)/tmp/phoneme-advanced/cdc/build/linux-arm-mx31
endif
#empty :=
#space := $(empty) $(empty)
#tmpdirs := $(subst /,$(space),$(PHONEME_HOME))
#target := $(word $(words $(tmpdirs)),$(tmpdirs))
#tokens := $(subst -,$(space),$(target))
#OS := $(word 1,$(tokens))
#ARCH := $(word 2,$(tokens))

LD_FLAGS += -lasound
LD_FLAGS += -lCommon

LIB_DIR = ../../lib/

INCLUDE = -I./include/
INCLUDE += -I./include/alsa
INCLUDE += -I./include/jni
ifdef USE_PHONEME
INCLUDE += -I$(PHONEME_HOME)/../../src/share/
INCLUDE += -I$(PHONEME_HOME)/../../src/$(OS)
INCLUDE += -I$(PHONEME_HOME)/../../src/$(OS)-$(ARCH)
INCLUDE += -I$(PHONEME_HOME)/../../src/$(ARCH)
INCLUDE += -I$(PHONEME_HOME)/../../src/
INCLUDE += -I$(PHONEME_HOME)/../../src/share/javavm/export
endif
INCLUDE += -I$(CLASSPATH_INCDIR)
INCLUDE += -I$(BUG_LINUX_SRC)/include/





ALL_CXXFLAGS += $(INCLUDE) $(CXXFLAGS)

UNIX_JNI_CLASSES+=cx.ath.matthew.unix.UnixServerSocket
UNIX_JNI_CLASSES+=cx.ath.matthew.unix.UnixSocket
UNIX_JNI_CLASSES+=cx.ath.matthew.unix.USInputStream
UNIX_JNI_CLASSES+=cx.ath.matthew.unix.USOutputStream
CGI_JNI_CLASSES+=cx.ath.matthew.cgi.CGI

ifdef DEBUG
LIBRARY = libAudio_g.so
CXXFLAGS += -g
else
CGI_LIBRARY = libcgi-java.so
UNIX_LIBRARY = libunix-java.so
endif

SRCS = cgi-java.c
SRCS+= unix-java.c

OBJS = cgi-java.o
OBJS+= unix-java.o

JNI_HEADERS = $(foreach jniheader,$(subst .,_,$(JNI_CLASSES)), include/jni/$(jniheader).h) 

printvars:
	@echo "JNI_CLASSES: " $(JNI_CLASSES)
	@echo "CXXFLAGS: " $(CXXFLAGS)
	@echo "ALL_CXXFLAGS: " $(ALL_CXXFLAGS)
	@echo "INCLUDE: " $(INCLUDE)
	@echo "SRCS: " $(SRCS)
	@echo "JNI_HEADERS: " $(JNI_HEADERS)
	@echo "OBJS: " $(OBJS)
	@echo "tmpdirs: " 
	@echo "OS: " $(OS)
	@echo "ARCH: " $(ARCH)
	@echo "TOP: " $(TOP)
	@echo "LTIB_ROOTFS: " $(LTIB_ROOTFS)
	@echo "CGI_LIBRARY: " $(CGI_LIBRARY)
	@echo "UNIX_LIBRARY: " $(UNIX_LIBRARY)

all: clean jni libs

jni:
	$(JAVAH) -o include/jni/cgi-java.h -classpath $(JAVAH_CLASSPATH):.:$(CLASSPATH) $(CGI_JNI_CLASSES)
	$(JAVAH) -o include/jni/unix-java.h  -classpath $(JAVAH_CLASSPATH):.:$(CLASSPATH) $(UNIX_JNI_CLASSES)
	
		
libcgi-java.so: $(OBJS)
	$(CC) $(CXXFLAGS) $(OBJS) $(LD_FLAGS) -o $(CGI_LIBRARY) -shared $(LDFLAGS)
	$(CC) $(CXXFLAGS) $(OBJS) $(LD_FLAGS) -o $(UNIX_LIBRARY) -shared $(LDFLAGS)
	
cgi-java.o: 
	$(CC) $(ALL_CXXFLAGS) -c -v cgi-java.c -o $@

unix-java.o:
	$(CC) $(ALL_CXXFLAGS) -c -v unix-java.c -o $@
	
libs: 
	make $(CGI_LIBRARY) 
	make $(UNIX_LIBRARY)
	cp $(CGI_LIBRARY) $(LIB_DIR)
	cp $(UNIX_LIBRARY) $(LIB_DIR)
	rm *.o

clean:
	$(RM) $(OBJS) $(LIBRARY) $(LIB_DIR)/*.so include/jni/*.h
	

