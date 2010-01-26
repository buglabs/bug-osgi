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
package com.buglabs.bug.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.osgi.service.log.LogService;

import com.buglabs.menu.AbstractMenuNode;

/**
 * This menu node cleans up pre-existing LCD calibration files and restarts the BUG.
 * @author kgilmer
 *
 */
class RemovePointerCalMenuNode extends AbstractMenuNode {

	private final LogService log;

	public RemovePointerCalMenuNode(LogService log) {
		super("Calibrate LCD");
		this.log = log;
	}
	
	public void execute() throws Exception {
		execute("rm /etc/pointercal");
		execute("/sbin/reboot");
	}
	
	/**
	 * @param cmd
	 * @return null on success, or String of error message on failure.
	 * @throws IOException
	 */
	private String execute(String cmd) throws IOException {
		String s = null;
		StringBuffer sb = new StringBuffer();
		boolean hasError = false;
		log.log(LogService.LOG_INFO, "Executing: " + cmd);
		Process p = Runtime.getRuntime().exec(cmd);
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		while ((s = stdError.readLine()) != null) {
			sb.append(s);
			hasError = true;
		}

		if (hasError) {
			new IOException("Failed to execute command: " + sb.toString());
		}

		BufferedReader stdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
		sb = new StringBuffer();
		while ((s = stdOut.readLine()) != null) {
			sb.append(s);
			sb.append('\n');
		}

		return sb.toString();
	}
	
}