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
package com.buglabs.module;

public class ModuleProperty implements IModuleProperty {

	private String name;

	private String type;

	private Object value;

	private boolean mutable;

	public ModuleProperty(String name, Object value) {
		this.name = name;
		this.value = value;
		this.type = "String";
		this.mutable = false;
	}

	public ModuleProperty(String name, Object value, String type, boolean mutable) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.mutable = mutable;
	}

	public boolean isMutable() {
		return mutable;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean equals(Object arg0) {
		if (arg0 instanceof ModuleProperty) {
			return this.name.equals(((ModuleProperty) arg0).name);
		}

		return super.equals(arg0);
	}
}
