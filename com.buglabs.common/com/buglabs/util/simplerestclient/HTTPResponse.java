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
package com.buglabs.util.simplerestclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Class for wrapping the response connection. This is returned when calling
 * get/post/put/delete/head on an HttpRequest object Use this object to check
 * status code and get data from request
 * 
 * @author Brian
 * 
 *         Revisions 09-04-2008 AK added getHeaderField(String key)
 * 
 */
public class HTTPResponse {

	/////// we use the following response codes
	/**
	 * 200 general success.
	 */
	public static final int HTTP_CODE_OK = HttpURLConnection.HTTP_OK; // 200 general success
	/**
	 * 201 resource created.
	 */
	public static final int HTTP_CODE_CREATED = HttpURLConnection.HTTP_CREATED; // 201 resource created
	/**
	 * 400 general error.
	 */
	public static final int HTTP_CODE_BAD_REQUEST = HttpURLConnection.HTTP_BAD_REQUEST; // 400 general error
	/**
	 * 401 not authorized.
	 */
	public static final int HTTP_CODE_NOT_AUTHORIZED = HttpURLConnection.HTTP_UNAUTHORIZED; // 401 not authorized
	/**
	 * 404 not found.
	 */
	public static final int HTTP_CODE_NOT_FOUND = HttpURLConnection.HTTP_NOT_FOUND; // 404 not found

	/**
	 * 415 unsupported media type.
	 */
	public static final int HTTP_CODE_UNSUPPORTED_TYPE = HttpURLConnection.HTTP_UNSUPPORTED_TYPE; // 415 unsupported media type
	/**
	 * 500 internal/application error.
	 */
	public static final int HTTP_CODE_INTERNAL_ERROR = HttpURLConnection.HTTP_INTERNAL_ERROR; // 500 internal/application error

	private static final String DEFAULT_ERROR_MESSAGE = "There was an HTTP connection error.  The server responded with status code ";
	private HttpURLConnection connection;

	/**
	 * Constructor must take in an HttpURLConnection.
	 * @param connection HttpURLConnection
	 */
	public HTTPResponse(HttpURLConnection connection) {
		this.connection = connection;
	}

	/**
	 * Check the response status in http header throw error if an error status
	 * is returned.
	 * 
	 * @throws IOException on I/O error
	 */
	public void checkStatus() throws IOException {
		checkStatus(null);
	}

	/**
	 * Get an input stream from the connection.
	 * @return response as a stream
	 * @throws IOException on I/O error
	 */
	public InputStream getStream() throws IOException {
		InputStream is = null;
		try {
			is = connection.getInputStream();
		} catch (IOException e) {
			throwHTTPException(e);
		}
		return is;
	}

	/**
	 * Get a string from the connection.
	 * 
	 * @return String body of HTTP Response
	 * @throws IOException on I/O error
	 */
	public String getString() throws IOException {
		InputStream is = getStream();
		return inputStreamToString(is);
	}

	/**
	 * Get response code from request.
	 * 
	 * @param connection
	 * @return response code or -1 if no response was provided in response.
	 */
	public int getResponseCode() {
		try {
			return connection.getResponseCode();
		} catch (IOException e) {		
			return -1;
		}				
	}

	/**
	 * Gets a header value from the http response.
	 * 
	 * @param key header key
	 * @return value of header or null if no header by the key name exists.
	 */
	public String getHeaderField(String key) {
		return connection.getHeaderField(key);
	}

	/**
	 * Get error message out of connection.
	 * 
	 * @return error from response or empty string if no error.
	 * @throws IOException on I/O error
	 */
	public String getErrorMessage() throws IOException {
		return inputStreamToString(connection.getErrorStream());		
	}

	/////////////////////////////////////////////////////////// Helpful Methods Club

	/**
	 * Check the status of the current connection and throw an error if we find
	 * an http error code. Pass in a default error message.
	 * 
	 * @param errorMessage error message
	 * @throws IOException error exception if response is an error.
	 */
	private void checkStatus(String errorMessage) throws IOException {
		// Get Response code out
		int response = getResponseCode();
		// only set message from response body if it's an HTTP error
		if (response >= HTTP_CODE_BAD_REQUEST) {
			// gobble up the error so as not to hold us back getting an error message set
			try {
				errorMessage = getErrorMessage();
			} catch (IOException e) {
				if (errorMessage == null)
					errorMessage = DEFAULT_ERROR_MESSAGE + response + ".";
			}
			throw new HTTPException(response, errorMessage);
		}
	}

	/**
	 * Helper function deals w/ all http errors.
	 * 	
	 * @param exception input exception
	 * @throws IOException exception relating to HTTP response.
	 */
	private void throwHTTPException(IOException exception) throws IOException {
		// Turn exception into HTTPException if possible
		if (connection != null) {
			checkStatus(exception.getMessage());
		}
		throw exception;
	}

	/**
	 * Convert input stream to string.
	 * 
	 * @param is input stream
	 * @return error as string or empty string if stream is empty.
	 * @throws IOException on I/O error
	 */
	private static String inputStreamToString(InputStream is) throws IOException {
		if (is == null)
			return "";
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuilder sb = new StringBuilder();
		
		while ((line = rd.readLine()) != null) {
			sb.append(line);
			sb.append('\n');
		}
		rd.close();
		
		return sb.toString();
	}
}
