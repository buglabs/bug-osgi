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

import org.osgi.framework.BundleContext;


/**
 * A IModletFactory is able to create modlets. The factory descibes which type
 * of modlet it can create.
 * 
 * @author kgilmer
 * 
 */
public interface IModletFactory {
	/**
	 * Module Id that this factory supports. This ID is defined in the Hardware
	 * Module.
	 * 
	 * @return
	 */
	public String getModuleId();

	/**
	 * Get name of factory.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Version of <code>IModlet</code> this factory creates.
	 * 
	 * @return
	 */
	public String getVersion();

	/**
	 * Create an instance of the IModlet.
	 * 
	 * @param context
	 * @param slotId
	 *            0 - 3
	 * @return
	 */
	public IModlet createModlet(BundleContext context, int slotId);

	/**
	 * Create an instance of the IModlet.
	 * 
	 * @param context
	 * @param slotId
	 *            0 - 3
	 * @return
	 */
	public IModlet createModlet(BundleContext context, int slotId, BMIModuleProperties properties);
}
