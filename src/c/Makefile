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

INCLUDE = -Iinclude/
INCLUDE += -I$(STAGING_BUG_INCDIR)
INCLUDE += -I$(PHONEME_HOME)/../../src/share/
INCLUDE += -I$(PHONEME_HOME)/../../src/$(OS)
INCLUDE += -I$(PHONEME_HOME)/../../src/$(OS)-$(ARCH)
INCLUDE += -I$(PHONEME_HOME)/../../src/$(ARCH)
INCLUDE += -I$(PHONEME_HOME)/../../src/
INCLUDE += -I$(PHONEME_HOME)/../../src/share/javavm/export

LD_FLAGS = -L${STAGING_LIBDIR}/bug
LD_FLAGS += -lCommon

ALL_CXXFLAGS += $(INCLUDE) $(CXXFLAGS)

JNI_CLASSES = com.buglabs.bug.jni.input.InputDevice

ifdef DEBUG
LIBRARY = libInputDevice_g.so
CXXFLAGS += -g
else
LIBRARY = libInputDevice.so
endif

SRCS = $(subst .,,$(suffix $(JNI_CLASSES))).cpp

OBJS = $(SRCS:.cpp=.o)

JNI_HEADER = include/$(subst .,_,$(JNI_CLASSES)).h

printvars:
	@echo "JNI_CLASSES: " $(JNI_CLASSES)
	@echo "CXXFLAGS: " $(CXXFLAGS)
	@echo "INCLUDE: " $(INCLUDE)
	@echo "SRCS: " $(SRCS)
	@echo "JNI_HEADER: " $(JNI_HEADER)
	@echo "LIBRARY: " $(LIBRARY)
	@echo "OBJS: " $(OBJS)
	@echo "tmpdirs: " 
	@echo "OS: " $(OS)
	@echo "ARCH: " $(ARCH)

all: clean jni libs

jni:
		$(JAVAH)  -d include/jni -classpath $(JAVAH_CLASSPATH):$(COMMON_CLASSPATH):. $(JNI_CLASSES)

libs: $(LIBRARY)
	cp $(LIBRARY) $(LIB_DIR)

%.so: $(OBJS)
	$(CXX) $(CXXFLAGS) $(OBJS) $(LD_FLAGS) -o $(LIBRARY) -shared

%.o: %.cpp
	$(CXX) $(ALL_CXXFLAGS) -c -v $^ -o $@

clean:
	$(RM) $(OBJS) $(LIBRARY) $(LIB_DIR)/$(LIBRARY) include/jni/*.h
