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
package com.buglabs.menu;

import java.util.Comparator;
import java.util.List;

/**
 * Implements common functionality for a menu node.
 * @author kgilmer
 *
 */
public abstract class AbstractMenuNode implements IMenuNode, Comparable {
	private static final long serialVersionUID = 244185876040942855L;

	private String name;

	private IMenuNode parent;

	public AbstractMenuNode(String name) {
		this.name = name;
	}

	public AbstractMenuNode(String name, IMenuNode parent) {
		this.name = name;
		this.parent = parent;
	}

	/* 
	 * If you extend AbstractMenuNode and wish to have children nodes this method needs to be overridden.
	 * 
	 * (non-Javadoc)
	 * @see com.buglabs.menu.IMenuNode#getChildren()
	 */
	public List getChildren() {
		return null;
	}

	public boolean sortChildren() {
		return true;
	}
	
	public Comparator getComparator() {
		return null;
	}
	
	public IMenuNode getParent() {
		return parent;
	}

	public IMenuNode setParent(IMenuNode parent) {
		this.parent = parent;
		return parent;
	}

	public boolean hasChildren() {
		List c = getChildren();

		if (c == null) {
			return false;
		}

		return getChildren().size() > 0;
	}

	public boolean isBusy() {
		return false;
	}
	
	public String getBusyName() {
		return null;
	}
	
	public IMenuNode getFirstParentOfType(String type) {
		IMenuNode node = this;

		while ((node = node.getParent()) != null) {
			if (node.getClass().getName().equals(type)) {
				return node;
			}
		}

		return null;
	}

	public void execute() throws Exception {
		// by default no action is taken.
	}

	public IMenuNode getNextSibling() {
		int me = getSiblingIndex();
		
		if (me > -1) {
			List sibs = this.getParent().getChildren();
			if (sibs.size() >= (me + 2)) {
				return (IMenuNode) sibs.get(me + 1);
			}
		}

		return null;
	}

	public IMenuNode getPreviousSibling() {
		int me = getSiblingIndex();

		if (me > 0) {
			return (IMenuNode) this.getParent().getChildren().get(me - 1);
		}

		return null;
	}

	private int getSiblingIndex() {
		if (this.getParent() == null) {
			//node has no parent and as such no siblings.
			return -1;
		} else if (this.getParent().getChildren() == null) {
			//there is a no forward association between parent and child.
			throw new RuntimeException("There is a no forward association between parent and child.");
		}
		List sibs = this.getParent().getChildren();
		return sibs.indexOf(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public boolean equals(Object arg0) {
		if (arg0 instanceof IMenuNode) {
			if (((IMenuNode) arg0).getName().equals(this.getName())) {
				return true;
			}
			return false;
		}

		return super.equals(arg0);
	}
	
	public int compareTo(Object arg0) {
		if (arg0 instanceof IMenuNode) {
			return this.getName().compareTo(((IMenuNode)arg0).getName());
		}
		
		return this.getName().compareTo(arg0.toString());
	}
	
	/*
	 * It is not smart to use this in a node's constructor.  If you do that and a set of
	 * submenus has not been registered yet, you will end up with an incomplete path.
	 * (non-Javadoc)
	 * @see com.buglabs.menu.IMenuNode#getPath()
	 */
	public String getPath() {
		StringBuffer sb = new StringBuffer();
		
		IMenuNode n = this;
		sb.append(this.getName());
		n = this.getParent();
		
		while (n != null) {
			String s = sb.toString();
			sb = new StringBuffer(n.getName() + ".");
			sb.append(s);
			n = n.getParent();
		}
		
		sb.deleteCharAt(0);
		return sb.toString();
	}
	
	public boolean isError() {
		return false;
	}
	
	public String getErrorName() {
		return null;
	}
}
