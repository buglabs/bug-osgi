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
	 * @param tagName Name of tag.  RuntimeExeception will be thrown if null value is passed.
	 */
	public XmlNode(String tagName) {
		if (tagName == null)
			throw new RuntimeException("Tag name cannot be null");
		
		this.name = tagName;
		attributes = new HashMap<String, String>();
	}

	/**
	 * Create a node with a String value.
	 * 
	 * @param tagName Name of tag.  RuntimeExeception will be thrown if null value is passed.
	 * @param value Contents of node.  Null is safe to pass.
	 */
	public XmlNode(String tagName, String value) {
		this(tagName);
		
		if (value != null && value.length() > 0) {
			this.value = value;
		}
	}

	/**
	 * Create a node with children.
	 * 
	 * @param tagName Name of tag.  RuntimeExeception will be thrown if null value is passed.
	 * @param children List<XmlNode> of child nodes.
	 */
	public XmlNode(String tagName, List<XmlNode> children) {
		this(tagName);
		this.children = children;
	}

	/**
	 * Create a node with a parent.
	 * 
	 * @param parent Parent XmlNode.
	 * @param tagName Name of tag.  RuntimeExeception will be thrown if null value is passed.
	 */
	public XmlNode(XmlNode parent, String tagName) {
		this(tagName);

		parent.addChild(this);

		this.parentNode = parent;
	}

	/**
	 * Create a node with a parent and children.
	 * 
	 * @param parent Parent XmlNode
	 * @param tagName Name of tag.  RuntimeExeception will be thrown if null value is passed.
	 * @param children List<XmlNode> of child nodes.
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
	 * @param parent Parent XmlNode.
	 * @param tagName Name of tag.  RuntimeExeception will be thrown if null value is passed.
	 * @param value List<XmlNode> of child nodes.
	 */
	public XmlNode(XmlNode parent, String tagName, String value) {
		this(parent, tagName);
		if (value != null && value.length() > 0) {
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
	 * @param name name of attribute
	 * @param value value of attribute
	 * @return instance of self
	 */
	public XmlNode addAttribute(String name, String value) {
		this.getAttributes().put(name, value);

		return this;
	}

	/**
	 * Set the name of the tag.
	 * 
	 * @param tagName Name of tag.
	 * @return instance of node.
	 */
	public XmlNode setName(String tagName) {
		this.name = tagName;
		
		return this;
	}

	/**
	 * @return value of tag.  Can be null.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return true if value is not null
	 */
	public boolean hasValue() {
		return value != null;
	}

	/**
	 * @param value Value of node.  Can only be called on nodes that do not contain children.  
	 * Runtime exception will be generated otherwise.
	 * @return instance of node.
	 */
	public XmlNode setValue(String value) {
		if (value == null) {
			clearValue();
		} else {
			if (hasValue())
				throw new RuntimeException("Cannot set content on a node that has children.");
		}

		this.value = value;
		
		return this;
	}

	/**
	 * Get contents of attribute, or null if attribute does not exist.
	 * 
	 * @param name name of attribute
	 * @return value of attribute or null if does not exist.
	 */
	public String getAttribute(String name) {
		return (String) attributes.get(name);
	}

	/**
	 * @param name attribute name
	 * @param value attribute value
	 * @return instance of XmlNode
	 */
	public XmlNode setAttribute(String name, String value) {
		attributes.put(name, value);

		return this;
	}

	/**
	 * Clear the value of the XML node.
	 * @return instance of XmlNode
	 */
	public XmlNode clearValue() {
		value = null;
		
		return this;
	}

	/**
	 * Add a child XmlNode to the current node.  Runtime exception will be thrown if a node is added to itself.
	 * Runtime exception will also be generated if parent node already contains a value.
	 * 
	 * @param element Child XmlNode
	 * @return instance of XmlNode
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
			count++;
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
		} catch (Exception e) {
			//Intentionally ignore any exception and fall back to toString() on Object.
		}
		return super.toString();
	}

	/**
	 * @param name name of child node
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
	 * @param name name of child node
	 * @deprecated use hasChild()
	 * @return true if a node with the given name exists, false otherwise.
	 */
	public boolean childExists(String name) {
		return hasChild(name);
	}

	/**
	 * @param nodeName name of child node
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
	 * Example: for "<root><leaf1></leaf1><leaf2/></root>" call with "root/leaf1" to
	 * return first occurrence leaf1 node.
	 * 
	 * @param path tree path expressed as node names delimited with '/' character.
	 * @return The node or null if not found.
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
	 * Set the parent node.
	 * @param parent parent node
	 * @return instance of XmlNode
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
	 * @param xmlString input XML as a string
	 * @param isNamespaceAware if namespaces should be parsed
	 * @return An XmlNode that corresponds to the root of the parsed xmlString.
	 * @throws IOException on I/O or XML parse error.
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
	 * @param xmlReader input XML
	 * @param isNamespaceAware if namespaces should be parsed
	 * @return An XmlNode that corresponds to the root of the parsed xmlString.
	 * @throws IOException on I/O or XML parse error.
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
	 * @param isNamespaceAware if namespaces should be parsed
	 * @return An XmlNode that corresponds to the root of the parsed xmlString.
	 * @throws XmlPullParserException on parse error
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

	/**
	 * @param pullParser parser
	 * @param node XmlNode
	 * @return An XmlNode that corresponds to the root of the parsed xmlString.
	 * @throws XmlPullParserException on xml parse error
	 * @throws IOException on I/O error
	 */
	private static XmlNode parse(XmlPullParser pullParser, XmlNode node) throws XmlPullParserException, IOException {
		while (true) {
			switch (parser.next()) {
			case XmlPullParser.START_TAG:
				if (node == null)
					node = new XmlNode(parser.getName());
				else 
					node = new XmlNode(node, parser.getName());
				
				for (int i = 0; i < parser.getAttributeCount(); ++i) {
					node.addAttribute(parser.getAttributeName(i), parser.getAttributeValue(i));
				}
				break;
			case XmlPullParser.TEXT:	
				if (!parser.isWhitespace())
					node.setValue(parser.getText());
				break;
			case XmlPullParser.END_TAG:
				if (node.getParent() != null)
					node = node.getParent();
				break;
			case XmlPullParser.END_DOCUMENT:
				return node;
			default:
				break;
			}
		}
	}
}
