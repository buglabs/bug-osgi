package com.buglabs.bug.bmi.sysfs;

import java.io.File;

public interface BMIDeviceNodeFactory {
	static final String MODULE_ID_SERVICE_PROPERTY = "PRODUCT.ID";
	
	BMIDevice createBMIDeviceNode(File baseDirectory, int slotIndex);
}
