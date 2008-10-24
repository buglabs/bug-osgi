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

/**
 * Constants relating to bundles.
 * 
 * @author kgilmer
 * 
 */
public interface BugBundleConstants {
	/**
	 * A designation for core runtime bundles that provide core services.
	 */
	public static final String BUG_BUNDLE_CORE = "Core";

	/**
	 * A module desginated as a Module bundle, providing module services.
	 */
	public static final String BUG_BUNDLE_MODULE = "Module";

	/**
	 * A module designated as an application.
	 */
	public static final String BUG_BUNDLE_APPLICATION = "Application";

	/**
	 * Header to define and describe the type of bundle in the bug context.
	 */
	public static final String BUG_BUNDLE_TYPE_HEADER = "Bug-Bundle-Type";

	/**
	 * The slot ID the module is connected to.
	 */
	public static final String BUG_BUNDLE_SLOT_ID_HEADER = "Bug-Slot-Index";

	/**
	 * This header represents the version of the hardware module.
	 */
	public static final String BUG_BUNDLE_MODULE_VERSION = "Bug-Module-Version";

	/**
	 * The Module ID header as presented by the BMI bus.
	 */
	public static final String BUG_BUNDLE_MODULE_ID = "Bug-Module-Id";	
}
