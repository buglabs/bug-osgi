
.phony=all fixup make1 make2 make3

LTIB_BUILD=$(CHECKOUT_DIR)/com.freescale.ltib2

MAKE_COMMAND=make BUILD_LEVEL=$(BUILD_LEVEL) BUILD_NUMBER=$(BUILD_NUMBER) CHECKOUT_DIR=$(CHECKOUT_DIR) LTIB_BUILD=$(LTIB_BUILD) LTIB_ROOT=$(LTIB_ROOT) LTIB_ROOTFS=$(LTIB_ROOTFS) JAVA_HOME=$(JAVA_HOME)

all: fixup phase1 KernelPatch phase2

fixup:
	LTIB_BUILD=$(LTIB_BUILD) LTIB_ROOTFS=$(LTIB_ROOTFS) $(BUILD_SOURCE)/fixup-ltibrc

phase1:
	$(MAKE_COMMAND) -C $(LTIB_BUILD) -f $(BUILD_SOURCE)/makefile.ltib.first

KernelPatch:
	$(MAKE_COMMAND) -C $(CHECKOUT_DIR) -f $(BUILD_SOURCE)/makefile.patch

phase2:
	$(MAKE_COMMAND) -C $(LTIB_BUILD) -f $(BUILD_SOURCE)/makefile.ltib.second

EchoMakeCommand:
	echo MAKE_COMMAND=$(MAKE_COMMAND)

