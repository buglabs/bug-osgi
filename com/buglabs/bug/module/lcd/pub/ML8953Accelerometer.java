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
package com.buglabs.bug.module.lcd.pub;

import java.io.IOException;

import com.buglabs.bug.accelerometer.pub.AccelerometerSample;

public interface ML8953Accelerometer {
	/**
	 * Read the X-Axis data from the accelerometer device.
	 * @return X-Axis Data
	 */
	public short readX() throws IOException;
	
	/**
	 * Read the Y-Axis data from the accelerometer device.
	 * @return Y-Axis Data
	 */
	public short readY() throws IOException;
	
	/**
	 * Read the Z-Axis data from the accelerometer device.
	 * @return Z-Axis Data
	 */
	public short readZ() throws IOException;
	
	/**
	 * Writes a value to a register on the accelerometer device.
	 * 
	 * @param address address of the register in the accelerometer device
	 * @param value value to set the register to
	 */
	public void writeRegister(byte address, byte value) throws IOException;
	
	/**
	 * Reads the value of a register on the accelerometer device.
	 * 
	 * @param address address of the register in the accelerometer device
	 * @return value read from the register
	 */
	public byte readRegister(byte address) throws IOException;
	
	/**
	 * Reads a single sample of the X,Y,Z axes. 
	 */
	public AccelerometerSample readSample() throws IOException;
		
	/**
	 * (ADXL345-specific)
	 * Wait for the accelerometer to trigger an interrupt on INT1.
	 * You must have configured interrupts by setting the INT_ENABLE
	 * register appropriately.
	 * You must have configured the INT_MAP register appropriately
	 * for interrupts to be triggerede on INT1.
	 * This is a blocking call. You must read the INT_SOURCE
	 * register to determine what triggered the interrupt.
	 */
	public void accelInterruptWaitINT1();
	
	/**
	 * (ADXL345-specific)
	 * As accelInterruptWaitINT1 but for interrupts on the INT2 pin.
	 */
	public void accelInterruptWaitINT2();
	
	/**
	 * (ADXL345-specific)
	 * INT_SOURCE bit values. Refer to the datasheet for more
	 * information.
	 */
	public final byte INT_OVERRUN		= 1 << 0;
	public final byte INT_WATERMARK 	= 1 << 1;
	public final byte INT_FREE_FALL		= 1 << 2;
	public final byte INT_INACTIVITY	= 1 << 3;
	public final byte INT_ACTIVITY 		= 1 << 4;
	public final byte INT_DOUBLE_TAP 	= 1 << 5;
	public final byte INT_SINGLE_TAP 	= 1 << 6;
	public final byte INT_DATA_READY 	= (byte) (1 << 7);
	//
	// (ADXL345-specific)
	// Refer to the datasheet for more information as to their usage.
	// Be sure you understand the implications of changing these.
	//
	public final byte REG_DEVID				= 0x00; // Device ID. READ-ONLY
	public final byte REG_THRESH_TAP		= 0x1D; // Tap threshold
	public final byte REG_OFSX				= 0x1E; // X-axis offset
	public final byte REG_OFSY				= 0x1F; // Y-axis offset
	public final byte REG_OFSZ				= 0x20; // Z-axis offset
	public final byte REG_DUR				= 0x21; // Tap duration
	public final byte REG_LATENT			= 0x22; // Tap latency
	public final byte REG_WINDOW	 		= 0x23; // Tap window
	public final byte REG_THRESH_ACT		= 0x24; // Activity threshold
	public final byte REG_THRESH_INACT		= 0x25; // Inactivity threshold
	public final byte REG_TIME_INACT		= 0x26; // Inactivity time
	public final byte REG_ACT_INACT_CTL		= 0x27; // Axis enable control for [in]activity detection
	public final byte REG_THRESH_FF			= 0x28; // Free-fall threshold
	public final byte REG_TIME_FF			= 0x29; // Free-fall time
	public final byte REG_TAP_AXES			= 0x2A; // Axis control for tap/double tap
	public final byte REG_ACT_TAP_STATUS	= 0x2B; // Source of tap/double tap. READ-ONLY
	public final byte REG_BW_RATE			= 0x2C; // Data rate and power mode control
	public final byte REG_POWER_CTL			= 0x2D; // Power-saving features control
	public final byte REG_INT_ENABLE		= 0x2E; // Interrupt enable control
	public final byte REG_INT_MAP			= 0x2F; // Interrupt mapping control
	public final byte REG_INT_SOURCE		= 0x30; // Source of interrupts. READ-ONLY
	public final byte REG_DATA_FORMAT		= 0x31; // Data format control
	public final byte REG_DATAX0			= 0x32; // X-Axis Data 0. READ-ONLY
	public final byte REG_DATAX1			= 0x33; // X-Axis Data 1. READ-ONLY
	public final byte REG_DATAY0			= 0x34; // Y-Axis Data 0. READ-ONLY
	public final byte REG_DATAY1			= 0x35; // Y-Axis Data 1. READ-ONLY
	public final byte REG_DATAZ0			= 0x36; // Z-Axis Data 0. READ-ONLY
	public final byte REG_DATAZ1			= 0x37; // Z-Axis Data 1. READ-ONLY
	public final byte REG_FIFOCTL			= 0x38; // FIFO control
	public final byte REG_FIFO_STATUS		= 0x39; // FIFO status. READ-ONLY
}
