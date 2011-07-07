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
package com.buglabs.bug.bmi.pub;

/**
 * modlet is a software component that handles all runtime service aspects of a
 * given hardware module.
 * 
 * @author kgilmer
 * 
 */
public interface IModlet {
	/**
	 * Return the MODULE ID. This comes from the hardware.
	 * 
	 * @return
	 */
	public String getModuleId();

	/**
	 * @return Slot that the Module is currently connected to.
	 */
	public int getSlotId();

	/**
	 * Connect to any devices or do any initialization. This is a good place to
	 * throw an exception if the expected hardware environment is not valid.
	 * 
	 * @throws Exception
	 */
	public void setup() throws Exception;

	/**
	 * Begin modlet. Any services that the modlet supports should be registered
	 * here.
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception;

	/**
	 * Unregister services and release any resources.
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception;
}
