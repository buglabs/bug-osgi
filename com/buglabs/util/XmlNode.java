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
package com.buglabs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This class represents an XML Node.  Any DOM document is a tree of type XMLNode.
 * 
 * @author kgilmer
 *
 */
public class XmlNode {
	private String tagName;

	private String text;

	private Map attributes;

	private List childElements;

	private XmlNode parentNode;

	public XmlNode(String tagName) {
		this.tagName = tagName;
		attributes = new HashMap();
	}

	public XmlNode(String tagName, String value) {
		this(tagName);
		this.text = value;
	}

	public XmlNode(XmlNode parent, String tagName) {
		this(tagName);

		try {
			parent.addChildElement(this);
		} catch (SelfReferenceException e) {
			// this should never happen...new object creation.
			e.printStackTrace();
		}

		this.parentNode = parent;
	}

	public XmlNode(XmlNode parent, String tagName, String value) {
		this(parent, tagName);
		this.text = value;
	}

	/**
	 * @return Name of this node.
	 */
	public String getName() {
		return tagName;
	}

	/**
	 * Add an attribute to the current node.
	 * 
	 * @param name
	 * @param value
	 */
	public void addAttribute(String name, String value) {
		this.getAttributes().put(name, value);
	}

	public void setName(String tagName) {
		this.tagName = tagName;
	}

	public String getValue() {
		return text;
	}

	public void setValue(String text) {
		this.text = text;
	}

	public String getAttribute(String name) {
		return (String) attributes.get(name);
	}

	public void setAttribute(String name, String value) {
		attributes.put(name, value);
	}

	public XmlNode addChildElement(XmlNode element) throws SelfReferenceException {
		if (element == this) {
			throw new SelfReferenceException(element);
		}

		getChildren().add(element);
		return element;
	}

	public List getChildren() {
		if (childElements == null) {
			childElements = new ArrayList();
		}

		return childElements;
	}

	public Map getAttributes() {
		return attributes;
	}

	public String toString() {
		return printXml("");
	}

	private String printXml(String indent) {
		String s = indent + "<" + this.tagName;

		if (attributes != null) {
			for (Iterator i = attributes.keySet().iterator(); i.hasNext();) {
				String attrib = (String) i.next();

				// Only display attributes with non-null values.
				if (attributes.get(attrib) != null) {
					String value = attributes.get(attrib).toString();

					s += " " + attrib + "=\"" + value + "\"";
				}

			}
		}

		if ((this.childElements == null || this.childElements.size() == 0) && text == null) {
			s += "/>\n";

			return s;
		}

		if (childElements != null && childElements.size() > 0) {
			s += ">\n";
			for (Iterator i = getChildren().iterator(); i.hasNext();) {
				XmlNode n = (XmlNode) i.next();
				s += n.printXml(indent + "  ");
			}
			s += indent;
		} else if (text != null) {
			s += ">" + text;
		}

		s += "</" + tagName + ">\n";

		return s;
	}

	public boolean childExists(String name) {
		// TODO optimize

		if (getChild(name) != null) {
			return true;
		}

		return false;
	}

	public XmlNode getChild(String nodeName) {
		// TODO Optimize

		for (Iterator i = getChildren().iterator(); i.hasNext();) {
			XmlNode child = (XmlNode) i.next();
			if (child.tagName.equals(nodeName)) {
				return child;
			}
		}

		return null;
	}

	/**
	 * Retrieve a node from this element using xpath-like notation.
	 * 
	 * @param path
	 * @return
	 */
	public XmlNode getFirstElement(String path) {
		String[] elems = StringUtil.split(path, "/");
		XmlNode root = this;
		for (int i = 0; i < elems.length; ++i) {
			root = (XmlNode) root.getChild(elems[i]);

			if (root == null) {
				break;
			}
		}

		return root;
	}

	/**
	 * @return The parent node or <code>null</code> if root node in DOM.
	 */
	public XmlNode getParent() {
		return parentNode;
	}

	/**
	 * @param parent
	 * @throws SelfReferenceException
	 */
	public void setParent(XmlNode parent) throws SelfReferenceException {
		if (parentNode != null) {
			parentNode.getChildren().remove(this);
		}

		if (parent != null) {
			parent.addChildElement(this);
		}

		parentNode = parent;
	}
}
