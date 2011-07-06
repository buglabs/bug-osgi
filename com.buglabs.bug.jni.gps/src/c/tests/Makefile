SRCS = $(wildcard *.cpp)

OBJS = $(SRCS:.cpp=.o)

TARGET = GPSTest

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) $? -o $@

run: $(TARGET)
	./$^

run-ttys0: $(TARGET)
	./$^ /dev/ttys0

clean:
	$(RM) $(TARGET) $(OBJS)

printvars:
	@echo "TARGET: " $(TARGET)
	@echo "SRCS: " $(SRCS)
	@echo "OBJS: " $(OBJS)
