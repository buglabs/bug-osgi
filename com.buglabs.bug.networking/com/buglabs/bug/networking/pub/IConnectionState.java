/*******************************************************************************
 * Copyright (c) 2011 Bug Labs, Inc.
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

package com.buglabs.bug.networking.pub;

/**
 * 
 * Used to receive notification when the state of a network connection 
 * changes.
 * 
 * @author aturley
 *
 */
public interface IConnectionState {
	/**
	 * Returns true if the connection is idle, false otherwise.
	 * 
	 * @return true if the connection is idle, false otherwise
	 */
	public boolean isIdle();
	
	/**
	 * Returns true if the connection is associating, false otherwise.
	 * 
	 * @return true if the connection is associating, false otherwise
	 */
	public boolean isAssociation();
	
	/**
	 * Returns true if the connection is configuring, false otherwise.
	 * 
	 * @return true if the connection is configuring, false otherwise
	 */
	public boolean isConfiguration();
	
	/**
	 * Returns true if the connection is ready, false otherwise.
	 * 
	 * @return true if the connection is ready, false otherwise
	 */
	public boolean isReady();
	
	/**
	 * Returns true if the connection is online, false otherwise.
	 * 
	 * @return true if the connection is online, false otherwise
	 */
	public boolean isOnline();
	
	/**
	 * Returns true if the connection is disconnected, false otherwise.
	 * 
	 * @return true if the connection is disconnected, false otherwise
	 */
	public boolean isDisconnect();
	
	/**
	 * Returns true if the connection failed, false otherwise.
	 * 
	 * @return true if the connection failed, false otherwise
	 */
	public boolean isFailure();
}
