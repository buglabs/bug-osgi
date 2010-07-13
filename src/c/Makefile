JAVAH = javah

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





LIB_DIR = ../../lib/

INCLUDE += -I./include/jni/
INCLUDE += -I./include/
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
INCLUDE += -I$(STAGING_BUG_INCDIR)

LD_FLAGS = -L${STAGING_LIBDIR}/bug
LD_FLAGS += -lCommon

ALL_CXXFLAGS += $(INCLUDE) $(CXXFLAGS)
 

JNI_CLASSES += gnu.io.I2C
JNI_CLASSES += gnu.io.ParallelPort
JNI_CLASSES += gnu.io.RS485
#JNI_CLASSES += gnu.io.Zystem
JNI_CLASSES += gnu.io.Raw
#JNI_CLASSES += gnu.io.RXTXCommDriver
#JNI_CLASSES += gnu.io.RXTXVersion
#JNI_CLASSES += gnu.io.RXTXPort
#JNI_CLASSES += gnu.io.CommPortIdentifier
#JNI_CLASSES += gnu.io.LPRPort



ifdef DEBUG
LIBRARY = libGSM_g.so
CXXFLAGS += -g
else
LIBRARY_SERIAL = librxtxSerial.so
LIBRARY_I2C = librxtxI2C.so
LIBRARY_PARALLEL = librxtxParallel.so
LIBRARY_RAW = librxtxRaw.so
LIBRARY_RS485 = librxtxRS485.so
endif

SRCS = $(foreach jniclass,$(JNI_CLASSES),$(subst .,,$(suffix $(jniclass))).c)

OBJS = $(SRCS:.c=.o)
OBJS += Serial.o

JNI_HEADERS = $(foreach jniheader,$(subst .,_,$(JNI_CLASSES)), include/$(jniheader).h) 

printvars:
	@echo "JNI_CLASSES: " $(JNI_CLASSES)
	@echo "CXXFLAGS: " $(CXXFLAGS)
	@echo "INCLUDE: " $(INCLUDE)
	@echo "SRCS: " $(SRCS)
	@echo "JNI_HEADERS: " $(JNI_HEADERS)
	@echo "OBJS: " $(OBJS)
	@echo "tmpdirs: " 
	@echo "OS: " $(OS)
	@echo "ARCH: " $(ARCH)

all: jni libs

jni:
	$(JAVAH)  -d include/jni -classpath $(JAVAH_CLASSPATH):$(COMMON_CLASSPATH):. $(JNI_CLASSES)
		

librxtxSerial.so: Serial.o
	$(CC) $(CXXFLAGS) SerialImp.o $(LD_FLAGS) -o $(LIBRARY_SERIAL) -shared $(LDFLAGS)
librxtxI2C.so: I2C.o
	$(CC) $(CXXFLAGS) I2CImp.o $(LD_FLAGS) -o $(LIBRARY_I2C) -shared $(LDFLAGS)
librxtxRaw.so: Raw.o
	$(CC) $(CXXFLAGS) RawImp.o $(LD_FLAGS) -o $(LIBRARY_RAW) -shared $(LDFLAGS)
librxtxParallel.so: ParallelPort.o
	$(CC) $(CXXFLAGS) ParallelImp.o $(LD_FLAGS) -o $(LIBRARY_PARALLEL) -shared $(LDFLAGS)
librxtxRS485.so: RS485.o
	$(CC) $(CXXFLAGS) RS485Imp.o $(LD_FLAGS) -o $(LIBRARY_RS485) -shared $(LDFLAGS)
	
I2C.o:
	$(CC) $(ALL_CXXFLAGS) -c -v I2CImp.c -o I2CImp.o
ParallelPort.o:
	$(CC) $(ALL_CXXFLAGS) -c -v ParallelImp.c -o ParallelImp.o
RS485.o:
	$(CC) $(ALL_CXXFLAGS) -c -v RS485Imp.c -o RS485Imp.o
Raw.o:
	$(CC) $(ALL_CXXFLAGS) -c -v RawImp.c -o RawImp.o
Serial.o:
	$(CC) $(ALL_CXXFLAGS) -c -v SerialImp.c -o SerialImp.o


libs: 
	make $(LIBRARY_SERIAL)
	make $(LIBRARY_I2C)
	make $(LIBRARY_RAW)
	make $(LIBRARY_PARALLEL)
	make $(LIBRARY_RS485)
	cp $(LIBRARY_SERIAL) $(LIB_DIR)
	cp $(LIBRARY_I2C)  $(LIB_DIR)
	cp $(LIBRARY_RAW)  $(LIB_DIR) 
	cp $(LIBRARY_PARALLEL)  $(LIB_DIR)
	cp $(LIBRARY_RS485)  $(LIB_DIR)
	

clean:
	$(RM) $(OBJS) $(LIBRARY) $(LIB_DIR)/* include/jni/*.h

