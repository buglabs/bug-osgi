JAVAH = javah

LIB_DIR = ../../lib/

INCLUDE = -I./include/

INCLUDE += -I$(CLASSPATH_INCDIR)
INCLUDE += -I$(BUG_LINUX_SRC)/include/

ALL_CXXFLAGS += $(INCLUDE) $(CXXFLAGS)
JNI_CLASSES = com.buglabs.bug.jni.common.CharDevice

ifdef DEBUG
LIBRARY = libCommon_g.so
CXXFLAGS += -g
else
LIBRARY = libCommon.so
endif

SRCS = $(subst .,,$(suffix $(JNI_CLASSES))).cpp

OBJS = $(SRCS:.cpp=.o)

JNI_HEADER = include/$(subst .,_,$(JNI_CLASSES)).h

printvars:
	@echo "JNI_CLASSES: " $(JNI_CLASSES)
	@echo "CXXFLAGS: " $(CXXFLAGS)
	@echo "ALL_CXXFLAGS: " $(ALL_CXXFLAGS)
	@echo "INCLUDE: " $(INCLUDE)
	@echo "SRCS: " $(SRCS)
	@echo "JNI_HEADER: " $(JNI_HEADER)
	@echo "OBJS: " $(OBJS)
	@echo "tmpdirs: " 
	@echo "OS: " $(OS)
	@echo "ARCH: " $(ARCH)
	@echo "TOP: " $(TOP)

all: clean jni libs

jni:
	$(JAVAH)  -d $(JAVAH_OUT) -classpath $(JAVAH_CLASSPATH):. $(JNI_CLASSES)

%.so: $(OBJS)
	$(CXX) $(CXXFLAGS) $(OBJS) $(LD_FLAGS) -o $(LIBRARY) -shared $(LDFLAGS)

%.o: %.cpp
	$(CXX) $(ALL_CXXFLAGS) -c -v $^ -o $@

libs: $(LIBRARY)
	cp -v $(LIBRARY) $(LIB_DIR)

clean:
	$(RM) $(OBJS) $(LIBRARY) $(LIB_DIR)/$(LIBRARY) include/jni/*.h

