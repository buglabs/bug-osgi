/* Copyright (c) 2007, 2008 Bug Labs, Inc.
 * All rights reserved.
 *   
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License version  
 * 2 only, as published by the Free Software Foundation.   
 *   
 * This program is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * General Public License version 2 for more details (a copy is  
 * included at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html).   
 *   
 * You should have received a copy of the GNU General Public License  
 * version 2 along with this work; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA   
 *
 */
package com.buglabs.bug.jni.input;

public class InputEvent {
	public TimeVal time;
	public int type;
	public int code;
	public long value;

	public final static byte EV_SYN = 0x00;
	public final static byte EV_KEY	= 0x01;
	public final static byte EV_REL	= 0x02;
	public final static byte EV_ABS	= 0x03;
	public final static byte EV_MSC	= 0x04;
	public final static byte EV_SW	= 0x05;
	public final static byte EV_LED	= 0x11;
	public final static byte EV_SND	= 0x12;
	public final static byte EV_REP	= 0x14;
	public final static byte EV_FF	= 0x15;
	public final static byte EV_PWR	= 0x16;
	public final static byte EV_FF_STATUS = 0x17;
	public final static byte EV_MAX = 0x1f;
	
}
