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
package com.buglabs.util.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

/**
 * This class represents an XML Node. Any DOM document is a tree of
 * <code>XMLNode</code>s.
 * 
 * This implementation of XmlNode uses the xpp library for parsing and serialization.
 * 
 * @author kgilmer
 * 
 */
public class XmlNode {
	private static XmlSerializer serializer;
	private static XmlPullParserFactory factory;
	private static XmlPullParser parser;
	/**
	 * Name of node.
	 */
	private String name = null;
	/**
	 * Content of node.
	 */
	private String value = null;

	private Map<String, String> attributes;

	private List<XmlNode> children;

	private XmlNode parentNode = null;

	/**
	 * Create an empty node.
	 * 
	 * @param tagName
	 */
	public XmlNode(String tagName) {
		this.name = tagName;
		attributes = new HashMap<String, String>();
	}

	/**
	 * Create a node with a String value.
	 * 
	 * @param tagName
	 * @param value
	 */
	public XmlNode(String tagName, String value) {
		this(tagName);

		if (value.length() > 0) {
			this.value = value;
		}
	}

	/**
	 * Create a node with children.
	 * 
	 * @param tagName
	 * @param children
	 */
	public XmlNode(String tagName, List<XmlNode> children) {
		this(tagName);
		this.children = children;
	}

	/**
	 * Create a node with a parent.
	 * 
	 * @param parent
	 * @param tagName
	 */
	public XmlNode(XmlNode parent, String tagName) {
		this(tagName);

		parent.addChild(this);

		this.parentNode = parent;
	}

	/**
	 * Create a node with a parent and children.
	 * 
	 * @param parent
	 * @param tagName
	 * @param children
	 */
	public XmlNode(XmlNode parent, String tagName, List<XmlNode> children) {
		this(tagName);

		parent.addChild(this);

		this.parentNode = parent;
		this.children = children;
	}

	/**
	 * Create a node with a parent and a String value.
	 * 
	 * @param parent
	 * @param tagName
	 * @param value
	 */
	public XmlNode(XmlNode parent, String tagName, String value) {
		this(parent, tagName);
		if (value.length() > 0) {
			this.value = value;
		}
	}

	/**
	 * @return true if the node has children, false otherwise.
	 */
	public boolean hasChildren() {
		return children != null && children.size() > 0;
	}

	/**
	 * @return Name of this node.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @param value
	 * @return instance of self
	 */
	public XmlNode addAttribute(String name, String value) {
		this.getAttributes().put(name, value);

		return this;
	}

	/**
	 * Set the name of the tag.
	 * 
	 * @param tagName
	 */
	public XmlNode setName(String tagName) {
		this.name = tagName;
		
		return this;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

	public boolean hasValue() {
		return value != null;
	}

	public XmlNode setValue(String text) {
		if (text == null) {
			clearValue();
		} else {
			if (hasValue())
				throw new RuntimeException("Cannot set content on a node that has children.");
		}

		this.value = text;
		
		return this;
	}

	/**
	 * Get contents of attribute, or null if attribute does not exist.
	 * 
	 * @param name
	 * @return
	 */
	public String getAttribute(String name) {
		return (String) attributes.get(name);
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public XmlNode setAttribute(String name, String value) {
		attributes.put(name, value);

		return this;
	}

	/**
	 * Clear the value of the XML node.
	 */
	public XmlNode clearValue() {
		value = null;
		
		return this;
	}

	/**
	 * Equivalent to addChildElement except that unchecked exception is thrown
	 * on self referencing call.
	 * 
	 * @param element
	 * @return
	 */
	public XmlNode addChild(XmlNode element) {
		if (element == this) {
			throw new RuntimeException("Cannot add node to itself.");
		}

		if (this.value != null) {
			throw new RuntimeException("Cannot add child elements to a node that has content.");
		}

		getChildren().add(element);
		element.setParent(this);
		
		return element;
	}

	/**
	 * @return children of node, or empty list if no children exist.
	 */
	public List<XmlNode> getChildren() {
		if (children == null) {
			children = new ArrayList<XmlNode>();
		}

		return children;
	}

	/**
	 * @return Map of attribute names and values as Strings.
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	/**
	 * @return depth of this node in the DOM
	 */
	public int getDepth() {
		if (parentNode == null) 
			return 0;
		
		int count = 0;
		
		XmlNode p = this;
		while ((p = p.getParent()) != null) {
			count ++;
		}
		
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		try {
			return serialize(this);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.toString();
	}

	/**
	 * @param name
	 * @return true if a node with the given name exists, false otherwise.
	 */
	public boolean hasChild(String name) {
		if (!hasChildren()) {
			return false;
		}

		if (getChild(name) != null) {
			return true;
		}

		return false;
	}
	
	/**
	 * @param name
	 * @return true if a node with the given name exists, false otherwise.
	 */
	public boolean childExists(String name) {
		return hasChild(name);
	}

	/**
	 * @param nodeName
	 * @return node with given name if exists or null otherwise.
	 */
	public XmlNode getChild(String nodeName) {
		if (children == null) {
			return null;
		}

		for (Iterator<XmlNode> i = getChildren().iterator(); i.hasNext();) {
			XmlNode child = i.next();
			if (child.name.equals(nodeName)) {
				return child;
			}
		}

		return null;
	}

	/**
	 * Retrieve a node from this element using xpath-like notation.
	 * 
	 * Ex. for <root><leaf1><leaf1><leaf2></root> call with "root/leaf1" to
	 * return first occurrence leaf1 node.
	 * 
	 * @param path
	 * @return
	 */
	public XmlNode getFirstElement(String path) {
		String[] elems = path.split("/");
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
	public XmlNode setParent(XmlNode parent) {
		if (parentNode != null) {
			parentNode.getChildren().remove(this);
		}

		parentNode = parent;
		
		return this;
	}

	/**
	 * Serialize an XmlNode into a String.
	 * 
	 * @param node XmlNode
	 * @return String of XML
	 * @throws XmlPullParserException on parse errors
	 * @throws IOException on I/O errors
	 */
	public static String serialize(XmlNode node) throws XmlPullParserException, IOException {
		if (serializer == null) {
			if (factory == null) {
				factory = XmlPullParserFactory.newInstance(
						"org.xmlpull.mxp1.MXParser,org.xmlpull.mxp1_serializer.MXSerializer", null);
			}
			serializer = factory.newSerializer();
			serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe", true);
		}
		
		Writer writer = new StringWriter();
		serializer.setOutput(writer);		
		
		walkTree(node, serializer);
		
		return writer.toString();
	}
	
	/**
	 * @param node XmlNode
	 * @param serializer XmlSerializer
	 * @throws IOException on IO error
	 */
	private static void walkTree(XmlNode node, XmlSerializer serializer) 
		throws IOException {
		
		serializer.startTag(null, node.getName());
		
		for (String attrName : node.getAttributes().keySet()) {
			serializer.attribute(null, attrName, node.getAttribute(attrName));
		}
		
		if (node.hasValue()) {
			serializer.text(node.getValue());
		} else if (node.hasChildren()) {
			for (XmlNode c : node.children)
				walkTree(c, serializer);
		}
		
		serializer.endTag(null, node.getName());
	}

	/**
	 * Parse an xml string into an XmlNode.
	 * 
	 * @param xmlString String of XML
	 * @return XmlNode
	 * @throws IOException on I/O errors
	 */
	public static XmlNode parse(String xmlString) throws IOException {
		try {
			parser = getParser(true);
			parser.setInput(new StringReader(xmlString));
			return parse(parser, null);
		} catch (XmlPullParserException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * @param xmlString
	 * @param isNamespaceAware
	 * @return
	 * @throws IOException
	 */
	public static XmlNode parse(String xmlString, boolean isNamespaceAware) throws IOException {
		try {
			parser = getParser(isNamespaceAware);
			parser.setInput(new StringReader(xmlString));
			return parse(parser, null);
		} catch (XmlPullParserException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Parse an xml string into an XmlNode.
	 * 
	 * @param xmlReader Reader of XML document
	 * @return XmlNode
	 * @throws IOException on I/O errors
	 */
	public static XmlNode parse(Reader xmlReader) throws IOException {
		try {
			parser = getParser(true);
			parser.setInput(xmlReader);
			
			return parse(parser, null);
		} catch (XmlPullParserException e) {
			throw new IOException(e);
		}	
	}	
	
	/**
	 * @param xmlReader
	 * @param isNamespaceAware
	 * @return
	 * @throws IOException
	 */
	public static XmlNode parse(Reader xmlReader, boolean isNamespaceAware) throws IOException {
		try {
			parser = getParser(isNamespaceAware);
			parser.setInput(xmlReader);
			
			return parse(parser, null);
		} catch (XmlPullParserException e) {
			throw new IOException(e);
		}	
	}

	/**
	 * @return
	 * @throws XmlPullParserException
	 */
	private static XmlPullParser getParser(boolean isNamespaceAware) throws XmlPullParserException {
		if (parser == null) {
			if (factory == null) {
				factory = XmlPullParserFactory.newInstance(
						"org.xmlpull.mxp1.MXParser,org.xmlpull.mxp1_serializer.MXSerializer", null);
			}
			factory.setNamespaceAware(isNamespaceAware);
			parser = factory.newPullParser();
		}
		
		parser = factory.newPullParser();
		
		return parser;
	}

	private static XmlNode parse(XmlPullParser parser2, XmlNode e) throws XmlPullParserException, IOException {
		while (true) {
			switch (parser.next()) {
			case XmlPullParser.START_TAG:
				if (e == null)
					e = new XmlNode(parser.getName());
				else 
					e = new XmlNode(e, parser.getName());
				
				for (int i = 0; i < parser.getAttributeCount(); ++i) {
					e.addAttribute(parser.getAttributeName(i), parser.getAttributeValue(i));
				}
				break;
			case XmlPullParser.TEXT:	
				if (!parser.isWhitespace())
					e.setValue(parser.getText());
				break;
			case XmlPullParser.END_TAG:
				if (e.getParent() != null)
					e = e.getParent();
				break;
			case XmlPullParser.END_DOCUMENT:
				return e;
			default:
				break;
			}
		}
	}
}
