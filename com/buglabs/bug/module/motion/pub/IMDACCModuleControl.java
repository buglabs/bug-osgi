package com.buglabs.bug.module.motion.pub;

import java.io.IOException;

import com.buglabs.module.IModuleControl;

public interface IMDACCModuleControl extends IModuleControl {
	public int LEDRedOff() throws IOException;		// Turn off red LED
	public int LEDRedOn() throws IOException;
	public int LEDGreenOff() throws IOException;		// Turn off red LED
	public int LEDGreenOn() throws IOException;
}
