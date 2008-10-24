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
package com.buglabs.bug.event;

import com.buglabs.util.XmlNode;

public class Subscriber {

	private String url;

	private String topic;

	public Subscriber(XmlNode node) {
		url = node.getAttribute("url");
		topic = node.getAttribute("topic");
	}

	public boolean isValid() {
		if (url == null || topic == null) {
			return false;
		}

		return true;
	}

	public String getTopic() {
		return topic;
	}

	public String getUrl() {
		return url;
	}

	public boolean equals(Object arg0) {
		if (arg0 instanceof Subscriber) {
			Subscriber othersub = (Subscriber) arg0;
			if (url.equals(othersub.getUrl()) && topic.equals(othersub.getTopic())) {
				return true;
			}

			return false;
		}
		return super.equals(arg0);
	}

}
