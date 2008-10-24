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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.util.XmlNode;
import com.buglabs.util.XmlParser;

public class EventServlet extends HttpServlet {
	private static final long serialVersionUID = -6041541048676568932L;

	private final Map eventMap;

	public EventServlet(Map eventMap) {
		this.eventMap = eventMap;
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BufferedReader br = new BufferedReader(req.getReader());
		StringBuffer reqStr = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			reqStr.append(line);
		}

		XmlNode reqXml = XmlParser.parse(reqStr.toString());

		for (Iterator i = reqXml.getChildren().iterator(); i.hasNext();) {
			XmlNode eventXml = (XmlNode) i.next();
			Subscriber subscriber = new Subscriber(eventXml);

			if (subscriber.isValid()) {
				List subList = (List) eventMap.get(subscriber.getTopic());

				if (subList == null) {
					subList = new ArrayList();
					eventMap.put(subscriber.getTopic(), subList);
				}

				if (!subList.contains(subscriber)) {
					subList.add(subscriber);
				}
			}
		}
	}
}
