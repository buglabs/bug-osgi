package com.buglabs.bug.jni.vonhippel.pub;

import java.io.IOException;

import com.buglabs.module.IModuleControl;

public interface IVonHippelModuleControl extends IModuleControl {
	public int LEDRedOff() throws IOException;	
	public int LEDRedOn() throws IOException;
	public int LEDGreenOff() throws IOException;
	public int LEDGreenOn() throws IOException;
	
	public VonHippelStatus getStatus();
	public void makeGPIOOut(int pin);
	public void makeGPIOIn(int pin);
	public void setGPIO(int pin);
	public void clearGPIO(int pin);
	public void makeIOXOut(int pin);
	public void makeIOXIn(int pin);
	public void setIOX(int pin);
	public void clearIOX(int pin);
	public void setRDACResistance(int resistance);
	public int getRDACResistance();
	public void doADC();
	public int readADC();
	public void doDAC();
	public void readDAC();
}
