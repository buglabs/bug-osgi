JAVAH = javah

ifdef LTIB_ROOTFS
PHONEME_HOME=$(LTIB_ROOTFS)/tmp/phoneme-advanced/cdc/build/linux-arm-mx31
endif

empty :=
space := $(empty) $(empty)
tmpdirs := $(subst /,$(space),$(PHONEME_HOME))
target := $(word $(words $(tmpdirs)),$(tmpdirs))
tokens := $(subst -,$(space),$(target))
OS := $(word 1,$(tokens))
ARCH := $(word 2,$(tokens))

LIB_DIR = ../../lib/

ifndef BUG_LINUX_SRC
	BUG_LINUX_SRC=$(CHECKOUT_DIR)/com.freescale.ltib2/rpm/BUILD/linux-2.6.19.2
endif

INCLUDE = -Iinclude/
INCLUDE += -I$(STAGING_BUG_INCDIR)
INCLUDE += -I$(PHONEME_HOME)/../../src/share/
INCLUDE += -I$(PHONEME_HOME)/../../src/$(OS)
INCLUDE += -I$(PHONEME_HOME)/../../src/$(OS)-$(ARCH)
INCLUDE += -I$(PHONEME_HOME)/../../src/$(ARCH)
INCLUDE += -I$(PHONEME_HOME)/../../src/
INCLUDE += -I$(PHONEME_HOME)/../../src/share/javavm/export
INCLUDE += -I$(BUG_LINUX_SRC)/include

LD_FLAGS = -L$(CHECKOUT_DIR)/com.buglabs.bug.jni.common/lib
LD_FLAGS = -L${STAGING_LIBDIR}/bug
LD_FLAGS += -lCommon -ljpeg

ALL_CXXFLAGS += $(INCLUDE) $(CXXFLAGS)

JNI_CLASSES = com.buglabs.bug.jni.camera.Camera
JNI_CLASSES += com.buglabs.bug.jni.camera.CameraControl

ifdef DEBUG
LIBRARY = libCamera_g.so
CXXFLAGS += -g
else
LIBRARY = libCamera.so
endif

SRCS = $(foreach jniclass,$(JNI_CLASSES),$(subst .,,$(suffix $(jniclass))).cpp)

OBJS = $(SRCS:.cpp=.o)

JNI_HEADERS = $(foreach jniheader,$(subst .,_,$(JNI_CLASSES)), include/jni/$(jniheader).h) 

printvars:
	@echo "JNI_CLASSES: " $(JNI_CLASSES)
	@echo "CXXFLAGS: " $(CXXFLAGS)
	@echo "INCLUDE: " $(INCLUDE)
	@echo "SRCS: " $(SRCS)
	@echo "JNI_HEADER: " $(JNI_HEADER)
	@echo "OBJS: " $(OBJS)
	@echo "tmpdirs: " 
	@echo "OS: " $(OS)
	@echo "ARCH: " $(ARCH)

all: clean jni libs

jni:
		$(JAVAH)  -d include/jni -classpath $(JAVAH_CLASSPATH):$(COMMON_CLASSPATH):. $(JNI_CLASSES)

%.so: $(OBJS)
	$(CXX) $(CXXFLAGS) $(OBJS) $(LD_FLAGS) -o $(LIBRARY) -shared

%.o: %.cpp
	$(CXX) $(ALL_CXXFLAGS) -c -v $^ -o $@

libs: $(LIBRARY)
	cp $(LIBRARY) $(LIB_DIR)

clean:
	$(RM) $(OBJS) $(LIBRARY) $(LIB_DIR)/$(LIBRARY) include/jni/*.h
