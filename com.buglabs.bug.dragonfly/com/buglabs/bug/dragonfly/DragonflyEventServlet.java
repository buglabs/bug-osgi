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
package com.buglabs.bug.dragonfly;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.util.xml.XmlNode;
import com.buglabs.util.xml.XmlParser;

/**
 * Servlet to receive subscriptions from SDK clients for module update events.
 * 
 * @author kgilmer
 *
 */
public class DragonflyEventServlet extends HttpServlet {
	private static final long serialVersionUID = -6041541048676568932L;

	private final Map<String, List<DragonflyEventSubscriber>> eventMap;

	/**
	 * @param eventMap map of event subscribers
	 */
	public DragonflyEventServlet(Map<String, List<DragonflyEventSubscriber>> eventMap) {
		this.eventMap = eventMap;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BufferedReader br = new BufferedReader(req.getReader());
		StringBuffer reqStr = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			reqStr.append(line);
		}

		XmlNode reqXml = XmlParser.parse(reqStr.toString());

		for (XmlNode eventXml : reqXml.getChildren()) {
			DragonflyEventSubscriber subscriber = new DragonflyEventSubscriber(eventXml);

			if (subscriber.isValid()) {
				List<DragonflyEventSubscriber> subList = eventMap.get(subscriber.getTopic());

				if (subList == null) {
					subList = new ArrayList<DragonflyEventSubscriber>();
					eventMap.put(subscriber.getTopic(), subList);
				}

				if (!subList.contains(subscriber)) {
					subList.add(subscriber);
				}
			}
		}
	}
}
