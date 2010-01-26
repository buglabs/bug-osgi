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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.log.LogService;

import com.buglabs.bug.menu.pub.LabelValueMenuItem;
import com.buglabs.bug.menu.pub.StackMenuItem;
import com.buglabs.menu.AbstractMenuNode;

public class AboutMenuAdapter extends AbstractMenuNode {

	private static final String MENU_TITLE = "About";
	/**
	 * Utility method to return file as list of strings
	 * @param filename
	 * @param title
	 * @return
	 * @throws IOException
	 */
	public static List getFileAsList(String filename, String title) throws IOException {
		File f = new File(filename);

		List l = new ArrayList();
		l.add(title);
		
		BufferedReader br = new BufferedReader(new FileReader(f));

		String line;
		while ((line = br.readLine()) != null) {
			l.add(line.trim());
		}

		br.close();

		return l;
	}
	/**
	 * Utility method to return file as list of strings
	 * @param filename
	 * @param title
	 * @return
	 * @throws IOException
	 */
	public static String getFileAsString(String filename) throws IOException {
		File f = new File(filename);
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuffer sb = new StringBuffer();
		
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line.trim());
			sb.append("\n");
		}

		br.close();

		return sb.toString();
	}

	/**
	 * Utility method to return first line of a file.
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static String getFirstLine(String filename) throws IOException {
		File f = new File(filename);

		BufferedReader br = new BufferedReader(new FileReader(f));

		String line = br.readLine().trim();

		br.close();
		return line;
	}

	private List children = null;

	private final LogService log;
	
	public AboutMenuAdapter(LogService log) {
		super(MENU_TITLE);
		this.log = log;
	}

	private void createChildren() throws IOException {
		children = new ArrayList();

		children.add(new LabelValueMenuItem("Kernel Version", getFirstLine("/proc/version"), this));
		children.add(new LabelValueMenuItem("Uptime", getFirstLine("/proc/uptime"), this));
		children.add(new StackMenuItem(this, getFileAsList("/proc/meminfo", "Memory")));
		children.add(new StackMenuItem(this, getFileAsList("/var/log/concierge.log", "OSGi Log")));
	}
	
	public List getChildren() {

		if (children == null) {
			try {
				createChildren();
			} catch (IOException e) {
				log.log(LogService.LOG_ERROR, "Unable to populate menu.", e);
				return new ArrayList();
			}
		}

		return children;
	}
	
	public boolean hasChildren() {
		return true;
	}
	
}
