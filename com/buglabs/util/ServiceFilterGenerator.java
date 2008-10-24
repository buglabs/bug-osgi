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

import java.util.List;

import org.osgi.framework.Constants;

/**
 * To be used by generators.
 * 
 * @author Angel Roman
 * 
 */
public class ServiceFilterGenerator {
	public static String generateServiceFilter(List services) {
		if (services.size() == 1) {
			return "(" + Constants.OBJECTCLASS + "=" + ((String) services.get(0)) + ")";
		} else if (services.size() > 1) {
			return "(|" + generateServiceFilter(services.subList(0, 1)) + generateServiceFilter(services.subList(1, services.size())) + ")";
		}

		return "";
	}
}
