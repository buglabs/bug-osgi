JAVAH = javah
LIB_DIR = ../../lib/
INCLUDE = -I./include/
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
INCLUDE += -I$(ROOTFS_BUG)/usr/include/classpath
INCLUDE += -I$(STAGING_INCDIR)

#LD_FLAGS = -L$(CHECKOUT_DIR)/com.buglabs.bug.jni.common/lib
LD_FLAGS = -L$(ROOTFS_BUG)/usr/lib
LD_FLAGS += -L../../../com.buglabs.bug.jni.common/lib
#LD_FLAGS = -L${STAGING_LIBDIR}/bug
LD_FLAGS += -v -lCommon -ljpeg

ALL_CXXFLAGS += $(INCLUDE) $(CXXFLAGS)
ALL_CCFLAGS += $(INCLUDE) $(CCFLAGS)

JNI_CLASSES = com.buglabs.bug.jni.camera.Camera
JNI_CLASSES += com.buglabs.bug.jni.camera.CameraControl

ifdef DEBUG
LIBRARY = libCamera_g.so
CXXFLAGS += -g
else
LIBRARY = libCamera.so
endif

SRCS = $(foreach jniclass,$(JNI_CLASSES),$(subst .,,$(suffix $(jniclass))).cpp)

OBJS = $(SRCS:.cpp=.o) bug_v4l.o media.o subdev.o

JNI_HEADERS = $(foreach jniheader,$(subst .,_,$(JNI_CLASSES)), include/jni/$(jniheader).h) 
#JNI_HEADER = include/$(subst .,_,$(JNI_CLASSES)).h


all: clean jni libs

jni:
		$(JAVAH)  -d include/jni -classpath $(JAVAH_CLASSPATH):$(COMMON_CLASSPATH):. $(JNI_CLASSES)

%.so: $(OBJS)
	$(CXX) $(CXXFLAGS) $(OBJS) $(LD_FLAGS) -o $(LIBRARY) -shared $(LDFLAGS)

%.o: %.cpp
	$(CXX) $(ALL_CXXFLAGS) -c $^ -o $@

%.o: %.c
	$(CC) $(ALL_CCFLAGS) -c $^ -o $@

libs: $(LIBRARY)
	cp $(LIBRARY) $(LIB_DIR)

clean:
	$(RM) $(OBJS) $(LIBRARY) $(LIB_DIR)/$(LIBRARY) include/jni/*.h
