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
 * @author aturley
 *
 * This class represents the type of security used by an an access point. The
 * types are None, WPA, WEP, and None.
 *
 */
public interface IAccessPointSecurity {
	/**
	 * Return true if the access point does not use any form of security,
	 * false otherwise.
	 * 
	 * @return true if the access point does not use any form of security, 
	 * false otherwise
	 */
	
	public boolean isNone();
	/**
	 * Return true if the access point uses a form of security other than WPA
	 * or WEP, false otherwise.
	 * 
	 * @return true if the access point uses a form of security other than WPA
	 * or WEP, false otherwise
	 */
	public boolean isOther();
	
	/**
	 * Return true if the access point uses WEP security, false otherwise.
	 * @return true if the access point uses WEP security, false otherwise
	 */
	public boolean isWEP();
	
	/**
	 * Return true if the access point uses WPA security, false otherwise.
	 * @return true if the access point uses WPA security, false otherwise
	 */
	public boolean isWPA();

	/**
	 * Set the security object to indicate that the access point does not use
	 * any form of security.
	 */
	public void setNone();
	
	/**
	 * Set the security object to indicate that the access point uses a
	 * security method other than WEP or WPA.
	 */
	public void setOther();
	
	/**
	 * Set the security object to indicate that the access point uses WEP
	 * security.
	 */
	public void setWEP();
	
	/**
	 * Set the security object to indicate that the access point uses WPA
	 * security.
	 */
	public void setWPA();
}
