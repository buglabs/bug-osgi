SRCS = $(wildcard *.c)

OBJS = $(SRCS:.c=.o)

TARGET = EventTest

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $? -o $@

run: $(TARGET)
	sudo ./$^ /dev/input/event9

clean:
	$(RM) $(TARGET) $(OBJS)

printvars:
	@echo "TARGET: " $(TARGET)
	@echo "SRCS: " $(SRCS)
	@echo "OBJS: " $(OBJS)
