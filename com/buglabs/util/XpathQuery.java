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
package com.buglabs.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Reduced XPath query engine against simplified Xml DOM.
 * 
 * @author ken
 * 
 */
public class XpathQuery {
	/**
	 * Return a list of nodes.
	 */
	public static final int NODE_LIST = 1;

	public static final int NODE = 2;

	/**
	 * Convienence method. Calls evaluate with NODE_LIST.
	 * 
	 * @param expression
	 * @param node
	 * @return
	 */
	public static List getNodes(String expression, XmlNode node) {
		return (List) evaluate(expression, node, NODE_LIST);
	}

	/**
	 * Convienence method. Calls evaluate with NODE.
	 * 
	 * @param expression
	 * @param node
	 * @return
	 */
	public static XmlNode getNode(String expression, XmlNode node) {
		return (XmlNode) evaluate(expression, node, NODE);
	}

	/**
	 * Execute a XPath query against a node.
	 * 
	 * @param expression
	 * @param element
	 * @param returnType
	 *            The type of data expected to be returned.
	 * @return
	 */
	public static Object evaluate(String expression, XmlNode element, int returnType) {
		List elems;

		switch (returnType) {
		case NODE:
			elems = new ArrayList();
			findElements(element, StringUtil.split(expression, "/"), 1, elems, true);

			if (elems.size() > 1) {
				throw new RuntimeException("Search for first returned multiple matches!");
			}

			if (elems.size() == 1) {
				return elems.get(0);
			}

			return null;
		case NODE_LIST:
			elems = new ArrayList();

			if (expression.startsWith("//")) {
				findAllElements(element, StringUtil.split(expression, "/"), 2, elems);
			} else {
				findElements(element, StringUtil.split(expression, "/"), 1, elems, false);
			}

			return elems;
		default:
			return null;
		}
	}

	/**
	 * Search through a node tree, applying xpath string to each node.
	 * 
	 * @param node
	 * @param xpath
	 * @param xpathIndex
	 *            xpath string split on '/'. This value is index of the array.
	 * @param matches
	 *            List of matched XmlNodes.
	 * @param firstOnly
	 *            If true, return first match only, else search entire tree.
	 */
	private static void findElements(XmlNode node, String[] xpath, int xpathIndex, List matches, boolean firstOnly) {
		if (nodeMatch(node, xpath[xpathIndex])) {
			if (xpathIndex + 1 == xpath.length) {
				matches.add(node);
			} else {
				for (Iterator i = node.getChildren().iterator(); i.hasNext() && (!searchComplete(firstOnly, matches));) {
					XmlNode child = (XmlNode) i.next();
					findElements(child, xpath, xpathIndex + 1, matches, firstOnly);
				}
			}
		}
	}

	/**
	 * Apply full XPath query to each tree in node.
	 * 
	 * @param node
	 * @param xpath
	 * @param xpathIndex
	 * @param matches
	 */
	private static void findAllElements(XmlNode node, String[] xpath, int xpathIndex, List matches) {
		// First see if this node matches, if so add it to the list.
		if (deepNodeMatch(node, xpath[xpathIndex])) {
			matches.add(node);
		}

		// Second iterate through the children.
		for (Iterator i = node.getChildren().iterator(); i.hasNext();) {
			XmlNode child = (XmlNode) i.next();
			findAllElements(child, xpath, xpathIndex, matches);
		}

	}

	/**
	 * check if search is first node only and node has been found.
	 * 
	 * @param firstOnly
	 * @param matches
	 * @return
	 */
	private static boolean searchComplete(boolean firstOnly, List matches) {
		if (!firstOnly) {
			return false;
		}

		if (matches.size() > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Evaluate if given node matches XPath term.
	 * 
	 * @param node
	 * @param exprStr
	 * @return
	 */
	private static boolean nodeMatch(XmlNode node, String exprStr) {
		AttribExpression ae = new AttribExpression(exprStr);

		if (!ae.exists) {
			return tagNameMatch(node, exprStr);
		}

		if (ae.getTagName().equalsIgnoreCase(node.getName())) {
			if (node.getAttributes().containsKey(ae.getName())) {
				String attrVal = node.getAttribute(ae.getName());

				switch (ae.getOperator()) {
				case AttribExpression.EQUAL_OPERATOR:
					if (attrVal.equals(ae.getValue())) {
						return true;
					}
					return false;

				case AttribExpression.NOT_EQUAL_OPERATOR:
					if (attrVal.equals(ae.getValue())) {
						return true;
					}
					return false;
				default:
					throw new RuntimeException("Invalid operator for attribute.");
				}
			}
		}

		return false;
	}

	/**
	 * Evaluate simple xpath term that matches tag name only.
	 * 
	 * @param node
	 * @param exprStr
	 * @return
	 */
	private static boolean tagNameMatch(XmlNode node, String exprStr) {
		if (node.getName().equalsIgnoreCase(exprStr)) {
			return true;
		}

		return false;
	}

	/**
	 * Perform deep search, applying full xpath expression to current node.
	 * 
	 * @param node
	 * @param exprStr
	 * @return
	 */
	private static boolean deepNodeMatch(XmlNode node, String exprStr) {

		List l = new ArrayList();

		findElements(node, StringUtil.split(exprStr, "/"), 0, l, true);

		if (l.size() > 0) {
			return true;
		}

		return false;
	}
}
