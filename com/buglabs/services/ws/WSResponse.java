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
package com.buglabs.services.ws;

/**
 * A simple IWSResponse implementation.
 * 
 * @author ken
 * 
 */
public class WSResponse implements IWSResponse {
	private Object content;

	private String errorMessage;

	private int errorCode;

	private String mimeType;

	private boolean error;

	public WSResponse(Object content, String mimeType) {
		this.content = content;
		this.mimeType = mimeType;

		error = false;
	}

	public WSResponse(int errorCode, String errorMessage) {
		error = true;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public Object getContent() {
		return content;
	}

	public boolean isError() {
		return error;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getMimeType() {
		return mimeType;
	}

	public static final IWSResponse UnimplementedErrorResponse = new WSResponse(501, "This service is not implemented.");
}
