/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package com.buglabs.bug.module.vonhippel.pub;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author jconnolly
 * 
 * Interface to control Von Hippel module. For complete module docmentation see:
 * http://bugcommunity.com/wiki/images/f/f2/BUG2_vonHippel_Module_Design_Spec_wiki_2008_08_17.pdf
 * 
 */
public interface IVonHippelModuleControl {
	/**
	 * Set module's Red LED off. Note that the state of the LED will also be
	 * reflected in GPIO pin 3
	 * 
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public int LEDRedOff() throws IOException;

	/**
	 * Set module's Red LED on. Note that the state of the LED will also be
	 * reflected in GPIO pin 3
	 * 
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public int LEDRedOn() throws IOException;

	/**
	 * Set module's Green LED off. Note that the state of the LED will also be
	 * reflected in GPIO pin 2
	 * 
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public int LEDGreenOff() throws IOException;

	/**
	 * Set module's Green LED on. Note that the state of the LED will also be
	 * reflected in GPIO pin 2
	 * 
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public int LEDGreenOn() throws IOException;

	/**
	 * Returns status of module. As passed up from ioctl. for information about
	 * the format please see line 500 at
	 * bug-linux-2.6.27.2/drivers/bmi/pims/vonhippel/bmi_vh.c
	 * 
	 * @return integer containing status information of GPIO and IOX bits
	 * @throws IOException
	 */
	public int getStatus() throws IOException;

	/**
	 * Sets specified pin as an output pin. Note that the driver sets pins 2 and
	 * 3 as out by default
	 * 
	 * @param pin
	 *            to set as Out
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public void makeGPIOOut(int pin) throws IOException;

	/**
	 * Sets specified pin as an in pin. Note that the driver sets pins 0 and 1
	 * as in by default
	 * 
	 * @param pin
	 *            to set as Out
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public void makeGPIOIn(int pin) throws IOException;

	/**
	 * Sets specified pin as high
	 * 
	 * @param pin
	 *            to set as high
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public void setGPIO(int pin) throws IOException;

	/**
	 * Sets specified pin as low
	 * 
	 * @param pin
	 *            to set as low
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public void clearGPIO(int pin) throws IOException;

	/**
	 * Sets specified IOX pin as an out pin
	 * 
	 * @param IOX
	 *            pin to set as Out
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public void makeIOXOut(int pin) throws IOException;

	/**
	 * Sets specified IOX pin as an in pin
	 * 
	 * @param IOX
	 *            pin to set as in
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public void makeIOXIn(int pin) throws IOException;

	/**
	 * Sets specified IOX pin as high. Note that pin 0 will set Red LED on (not
	 * GPIO red), 1 will set yellow LED on, 2 will set green LED on (not GPIO
	 * LED), 3 will set blue LED on.
	 * 
	 * @precondition pin should be set as out
	 * @param IOX
	 *            pin to set as high
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public void setIOX(int pin) throws IOException;

	/**
	 * Sets specified IOX pin as high. Note that pin 0 will set Red LED off (not
	 * GPIO red), 1 will set yellow LED off, 2 will set green LED off (not GPIO
	 * LED), 3 will set blue LED off.
	 * 
	 * @precondition pin should be set as out
	 * @param IOX
	 *            pin to set as low
	 * @return value returned by ioctl.
	 * @throws IOException
	 */
	public void clearIOX(int pin) throws IOException;

	/**
	 * Set RDAC to adjust LDO voltage
	 * 
	 * @param resistance
	 *            value
	 * @throws IOException
	 */
	public void setRDACResistance(int resistance) throws IOException;

	/**
	 * Get RDAC resistance value from ioctl
	 * 
	 * @param resistance
	 *            value
	 * @throws IOException
	 * @returns rdac value
	 */
	public int getRDACResistance() throws IOException;

	/**
	 * Writes control word to ADC for channel 0 single-ended conversion
	 * 
	 * @throws IOException
	 */
	public void doADC() throws IOException;

	/**
	 * Reads ADC channel 0 (last conversion)
	 * 
	 * @return value returned by ioctl
	 * @throws IOException
	 */
	public int readADC() throws IOException;

	/**
	 * Write DAC (both channels and input registers) Note that this method
	 * should be refactored to accept params
	 * 
	 * @throws IOException
	 */
	public void doDAC() throws IOException;

	/**
	 * Read DAC channel A Note that this method should be refactored to return
	 * value
	 * 
	 * @throws IOException
	 */
	public void readDAC() throws IOException;

	/**
	 * Gets the input stream associated with the RS232 port on Von Hippel
	 * module. This implementation is based on the javax.microedition.commports
	 * API. The port is set up with the following parameters in
	 * VonHippelModuleControl: baudrate=9600 bitsperchar=8 stopbits=1
	 * parity=none autocts=off autorts=off blocking=off
	 * 
	 * @deprecated
	 * @return stream associated with RS232 input (reading)
	 */
	public InputStream getRS232InputStream();

	/**
	 * Gets the output stream associated with the RS232 port on Von Hippel
	 * module. This implementation is based on the javax.microedition.commports
	 * API. The port is set up with the following parameters in
	 * VonHippelModuleControl: baudrate=9600 bitsperchar=8 stopbits=1
	 * parity=none autocts=off autorts=off blocking=off
	 * @deprecated
	 * @return stream associated with RS232 output (writing)
	 */
	public OutputStream getRS232OutputStream();

}
