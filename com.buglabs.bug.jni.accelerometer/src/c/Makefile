JAVAH = javah

LIB_DIR = ../../lib/

INCLUDE = -I./include/
INCLUDE += -I$(STAGING_BUG_INCDIR)
INCLUDE += -I$(CLASSPATH_INCDIR)
INCLUDE += -I$(BUG_LINUX_SRC)/include/

LD_FLAGS = -L${STAGING_LIBDIR}/bug
LD_FLAGS += -lCommon

ALL_CXXFLAGS += $(INCLUDE) $(CXXFLAGS)

JNI_CLASSES = com.buglabs.bug.jni.accelerometer.Accelerometer

ifdef DEBUG
LIBRARY = libAccelerometer_g.so
CXXFLAGS += -g
else
LIBRARY = libAccelerometer.so
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
	@echo "BUG_LINUX_SRC: " $(BUG_LINUX_SRC)

all: clean jni libs

jni:
	$(JAVAH)  -d include/jni -classpath $(JAVAH_CLASSPATH):$(COMMON_CLASSPATH):. $(JNI_CLASSES)

%.so: $(OBJS)
	$(CXX) $(CXXFLAGS) $(OBJS) $(LD_FLAGS) -o $(LIBRARY) -shared $(LDFLAGS)

%.o: %.cpp
	$(CXX) $(ALL_CXXFLAGS) -c -v $^ -o $@

libs: $(LIBRARY)
	cp $(LIBRARY) $(LIB_DIR)
	
clean:
	$(RM) $(OBJS) $(LIBRARY) $(LIB_DIR)/$(LIBRARY) include/jni/*.h
