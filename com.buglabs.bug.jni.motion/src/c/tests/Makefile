SRCS = $(wildcard *.cpp)

OBJS = $(SRCS:.cpp=.o)

TARGET = MotionTest

INCLUDES += -I$(BUG_LINUX_SRC)/include

ALL_CXXFLAGS = $(CXXFLAGS) $(INCLUDES)

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) $? -o $@

%.o: %.cpp
	$(CXX) $(ALL_CXXFLAGS) -c $^

clean:
	$(RM) $(TARGET) $(OBJS)

printvars:
	@echo "TARGET: " $(TARGET)
	@echo "SRCS: " $(SRCS)
	@echo "OBJS: " $(OBJS)
