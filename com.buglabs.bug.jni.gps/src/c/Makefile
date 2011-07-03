JAVAH = javah

LIB_DIR = ../../lib/

INCLUDE = -I./include/
INCLUDE += -I$(CLASSPATH_INCDIR)
INCLUDE += -I$(BUG_LINUX_SRC)/include/
INCLUDE += -I$(STAGING_BUG_INCDIR)

LD_FLAGS = -L${STAGING_LIBDIR}/bug
LD_FLAGS += -lCommon

ALL_CXXFLAGS += $(INCLUDE) $(CXXFLAGS)
 
JNI_CLASSES = com.buglabs.bug.jni.gps.GPS
JNI_CLASSES += com.buglabs.bug.jni.gps.GPSControl

ifdef DEBUG
LIBRARY = libGPS_g.so
CXXFLAGS += -g
else
LIBRARY = libGPS.so
endif

SRCS = $(foreach jniclass,$(JNI_CLASSES),$(subst .,,$(suffix $(jniclass))).cpp)

OBJS = $(SRCS:.cpp=.o)

JNI_HEADERS = $(foreach jniheader,$(subst .,_,$(JNI_CLASSES)), include/jni/$(jniheader).h) 

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
		

%.so: $(OBJS)
	$(CXX) $(CXXFLAGS) $(OBJS) $(LD_FLAGS) -o $(LIBRARY) -shared $(LDFLAGS)

%.o: %.cpp
	$(CXX) $(ALL_CXXFLAGS) -c $^ -o $@

libs: $(LIBRARY)
	cp $(LIBRARY) $(LIB_DIR)

clean:
	$(RM) $(OBJS) $(LIBRARY) $(LIB_DIR)/$(LIBRARY) include/jni/*.h

