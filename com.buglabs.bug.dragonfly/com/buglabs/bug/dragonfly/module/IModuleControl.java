/*******************************************************************************
 * Copyright (c) 2008, 2009, 2011 Bug Labs, Inc.
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
package com.buglabs.bug.dragonfly.module;

import java.io.IOException;
import java.util.List;

/**
 * An implementor exposes a BUG module to the runtime. This module can be
 * queried and manipulated by clients such as the IDE.
 * 
 * @author ken
 * 
 */
public interface IModuleControl {

	/**
	 * Return a list of <code>IModuleProperty</code> elements.
	 * 
	 * @return list of module properties
	 */
	List<IModuleProperty> getModuleProperties();

	/**
	 * Get the human-readable name of this module.
	 * 
	 * @return name of module
	 */
	String getModuleName();

	/**
	 * @return slot (0 - 3) that device is attached to.
	 */
	int getSlotId();

	/**
	 * Set a property. This is from a client request.
	 * 
	 * @param property property to set
	 * @return true if successful, false otherwise
	 */
	boolean setModuleProperty(IModuleProperty property);

	/**
	 * Suspend the module.
	 * 
	 * @return 1 if successful
	 * @throws IOException
	 *             on failure
	 */
	int suspend() throws IOException;

	/**
	 * Resume the module.
	 * 
	 * @return 1 if successful
	 * @throws IOException
	 *             on failure
	 */
	int resume() throws IOException;
}
