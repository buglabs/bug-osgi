/*******************************************************************************
 * Copyright (c) 2008, 2009 Brian Ballantine and Bug Labs, Inc.
 * 
 * MIT License
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package com.buglabs.util.simplerestclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.buglabs.util.Base64;


/**
 * Class for dealing RESTfully with HTTP Requests.
 * 
 * Example Usage:
 * HttpRequest req = new HttpRequest(myConnectionProvider)
 * HttpResponse resp = req.get("http://some.url")
 * System.out.println(resp.getString());
 * 
 * @author Brian
 * 
 * Revisions
 * 09-03-2008 AK added a Map header parameter to "put" and "post" to support http header 
 * 
 * 
 */
public class HTTPRequest {
	
	private static final String METHOD_POST = "POST";
	private static final String HEADER_CONTENT_LENGTH = "Content-Length";
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

	/**
	 * Implementors can configure the http connection before every call is made.
	 * Useful for setting headers that always need to be present in every WS call to a given server.
	 * 
	 * @author kgilmer
	 *
	 */
	public interface HTTPConnectionInitializer {
		/**
		 * @param connection HttpURLConnection
		 */
		void initialize(HttpURLConnection connection);
	}
	
	////////////////////////////////////////////////  HTTP REQUEST METHODS	
	
	private static final String HEADER_TYPE  = HEADER_CONTENT_TYPE;
    private static final String HEADER_PARA  = "Content-Disposition: form-data";
    private static final String CONTENT_TYPE = "multipart/form-data";
    private static final String LINE_ENDING  = "\r\n";
    private static final String BOUNDARY     = "boundary=";
    private static final String PARA_NAME    = "name";
    private static final String FILE_NAME    = "filename";
	private static final int COPY_BUFFER_SIZE = 1024 * 4;
    
    private List<HTTPConnectionInitializer> configurators;
	
	private IConnectionProvider _connectionProvider;
	
	private boolean debugMode = false;
	private boolean throwHTTPErrorResponses = true;
	
	/**
	 * Constructor where client provides connectionProvider.
	 * 	
	 * @param connectionProvider IConnectionProvider
	 */
	public HTTPRequest(IConnectionProvider connectionProvider) {
		this._connectionProvider = connectionProvider;
	}
		
	/**
	 * @param connectionProvider IConnectionProvider
	 * @param debugMode if true debug mode will be enabled, printing method calls and times.
	 */
	public HTTPRequest(IConnectionProvider connectionProvider, boolean debugMode) {
		this(connectionProvider);
		this.debugMode = debugMode;
	}
	
	/**
	 * @param connectionProvider
	 * @param debugMode if true debug mode will be enabled, printing method calls and times.
	 */
	public HTTPRequest(boolean debugMode) {
		this();
		this.debugMode = debugMode;
	}
	
	/**
	 * constructor that uses default connection provider.
	 */
	public HTTPRequest() {
		_connectionProvider = new DefaultConnectionProvider();
	}
	
	/**
	 * @return true if HTTP client will throw HttpExceptions on 4xx HTTP responses.
	 */
	public boolean getThrowsHTTPErrorsAsExceptions() {
		return throwHTTPErrorResponses;
	}
	
	/**
	 * @param value if true, client will throw HttpException if 4xx codes are returned from server
	 */
	public void setThrowsHTTPErrorsAsExceptions(boolean value) {
		this.throwHTTPErrorResponses = value;
	}
	
    /**
     * Do an authenticated HTTP GET from url.
     * 
     * @param url   String URL to connect to
     * @return      HttpURLConnection ready with response data
     * @throws IOException on I/O error
     */
	public HTTPResponse get(String url) throws IOException {
		
		HttpURLConnection conn = getAndConfigureConnection(url);
		conn.setDoInput(true);
		conn.setDoOutput(false);
		
		if (debugMode)
			debugMessage("GET", url, conn);
		
		return connect(conn);
	}

	/**
	 * @param url url of host
	 * @return HttpURLConnection
	 * @throws IOException on I/O error
	 */
	private HttpURLConnection getAndConfigureConnection(String url) throws IOException {
		url = guardUrl(url);
		HttpURLConnection connection = _connectionProvider.getConnection(url);
		
		if (configurators == null)
			return connection;
		
		for (HTTPConnectionInitializer c : configurators)
			c.initialize(connection);
		
		return connection;
	}
	
	/**
     * Do an authenticated HTTP GET from url.
     * 
     * @param url   String URL to connect to
     * @param headers Map of <String, String> of headers to process
     * @return      HttpURLConnection ready with response data
     * @throws IOException on I/O error
     */
	public HTTPResponse get(String url, Map<String, String> headers) throws IOException {
		
		HttpURLConnection conn = getAndConfigureConnection(url);
		conn.setDoInput(true);
		conn.setDoOutput(false);
		
		for (Entry<String, String> e : headers.entrySet()) 
			conn.addRequestProperty(e.getKey(), e.getValue());
		
		if (debugMode)
			debugMessage("GET", url, conn);
		
		return connect(conn);
	}
	
	
    /**
     * Do an HTTP POST to url.
     * 
     * @param url   String URL to connect to
     * @param data  String data to post 
     * @return      HttpURLConnection ready with response data
     * @throws IOException on I/O error
     */
	public HTTPResponse post(String url, String data) throws IOException {
		return post(url, data, null);
	}

	/**
	 * Do an HTTP POST to url w/ extra http headers.
	 * 
	 * @param url url of host
	 * @param data data to pass as body of message
	 * @param headers HTTP Headers
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	public HTTPResponse post(String url, String data, Map<String, String> headers) throws IOException {
		
		HttpURLConnection conn = getAndConfigureConnection(url);
		
		if (headers != null) 
			for (Entry<String, String> e : headers.entrySet())
				conn.setRequestProperty(e.getKey(), e.getValue());
			
		if (debugMode)
			debugMessage(METHOD_POST, url + " data: " + data, conn);
		
		conn.setDoOutput(true);
		OutputStreamWriter osr = new OutputStreamWriter(conn.getOutputStream());
		osr.write(data);
		osr.flush();
		osr.close();
		return connect(conn);
	}
	
    /**
     * Do an HTTP POST to url.
     * 
     * @param url       String URL to connect to
     * @param stream    InputStream data to post 
     * @return          HttpURLConnection ready with response data
     * @throws IOException on I/O error
     */
	public HTTPResponse post(String url, InputStream stream) throws IOException {
		byte[] buff = streamToByteArray(stream);
		String data = Base64.encodeBytes(buff);
		return post(url, data);
	}	
	
	
	/**
	 * Posts a Map of key, value pair properties, like a web form.
	 * 
	 * @param url url of host
	 * @param properties map of properties to pass in post
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	public HTTPResponse post(String url, Map<String, String> properties) throws IOException {
		String data = propertyString(properties);
		HashMap<String, String> headers = new HashMap<String, String>(); 
		headers.put(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);		
		return post(url, data, headers);
	}
	
	/**
	 * Posts a Map of key, value pair properties, like a web form.
	 * 
	 * @param url url of host
	 * @param properties map of properties to pass in post
	 * @param headers HTTP headers to pass in post
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	public HTTPResponse post(String url, Map<String, String> properties
			, Map<String, String> headers) throws IOException {
		String data = propertyString(properties);		 
		headers.put(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);		
		return post(url, data, headers);
	}

	
	/**
	 * Post byte data to a url.
	 * 
	 * @param url url of host
	 * @param data message body as byte array
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	public HTTPResponse post(String url, byte[] data) throws IOException {
		
		HttpURLConnection conn = getAndConfigureConnection(url);
		conn.setRequestProperty(HEADER_CONTENT_LENGTH, String.valueOf(data.length));
		conn.setRequestMethod(METHOD_POST);
		conn.setDoOutput(true);
		
		if (debugMode)
			debugMessage(METHOD_POST, url, conn);
		
		OutputStream os = conn.getOutputStream();
		os.write(data);
		return connect(conn);
	}
	
	/**
	 * Does a multipart post which is different than a regular post
	 * mostly use this one if you're posting files.
	 * 
	 * @param url url of host
	 * @param parameters Key-Value pairs in map.  Keys are always string.  Values can be string or IFormFile
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	public HTTPResponse postMultipart(String url, Map<String, Object> parameters) throws IOException {
		
		HttpURLConnection conn = getAndConfigureConnection(url);
		conn.setRequestMethod(METHOD_POST);
		String boundary = createMultipartBoundary();
		conn.setRequestProperty(HEADER_TYPE, CONTENT_TYPE + "; " + BOUNDARY + boundary);
        conn.setDoOutput(true);		
        
        if (debugMode)
			debugMessage(METHOD_POST, url, conn);
		
		// write things out to connection
        OutputStream os = conn.getOutputStream();
        
		// add parameters
        Object [] elems = parameters.keySet().toArray();
        StringBuffer buf; // lil helper
        IFormFile file;
	    for (int i = 0; i < elems.length; i++) {
	    	String key = (String) elems[i];
	    	Object obj = parameters.get(key);
	    	//System.out.println("--" + key);

	    	buf = new StringBuffer();
	    	if (obj instanceof IFormFile) {
	    		file = (IFormFile) obj;
	    		buf.append("--" + boundary + LINE_ENDING);
	    		buf.append(HEADER_PARA);
	    		buf.append("; " + PARA_NAME + "=\"" + key + "\"");
	    		buf.append("; " + FILE_NAME + "=\"" + file.getFilename() + "\"" + LINE_ENDING);
	    		buf.append(HEADER_TYPE + ": " + file.getContentType() + ";");
	    		buf.append(LINE_ENDING);
	    		buf.append(LINE_ENDING);
	    		os.write(buf.toString().getBytes());
		    	os.write(file.getBytes());	    		
	    	} else if (obj != null) {
		    	buf.append("--" + boundary + LINE_ENDING);
		    	buf.append(HEADER_PARA);
		    	buf.append("; " + PARA_NAME + "=\"" + key + "\"");
		    	buf.append(LINE_ENDING);
		    	buf.append(LINE_ENDING);
		    	buf.append(obj.toString());
		    	os.write(buf.toString().getBytes());
	    	}
	    	os.write(LINE_ENDING.getBytes());
	    }
	    os.write(("--" + boundary + "--" + LINE_ENDING).getBytes());	
		return connect(conn);
	}
	
	
	/**
	 * Do an HTTP PUT to url.
	 * 
	 * @param url  String URL to connect to
	 * @param data String data to post 
	 * @return     HttpURLConnection ready with response data
	 * @throws IOException on I/O error
	 */
	public HTTPResponse put(String url, String data) throws IOException {
		return put(url, data, null);
	}

	/**
	 * Do an HTTP PUT to url with extra headers.
	 * 
	 * @param url url of host
	 * @param data data as a string for content body
	 * @param headers HTTP headers
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	public HTTPResponse put(String url, String data, Map<String, String> headers) throws IOException {
		
		HttpURLConnection connection = getAndConfigureConnection(url);
		
		if (headers != null) 
			for (Entry<String, String> e : headers.entrySet())
				connection.setRequestProperty(e.getKey(), e.getValue());
		
		if (debugMode)
			debugMessage("PUT", url, connection);
		
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		OutputStreamWriter osr = new OutputStreamWriter(connection.getOutputStream());
		osr.write(data);
		osr.flush();
		osr.close();
		return connect(connection);		
	}
	
	/**
     * Do an HTTP PUT to url.
     * 
     * @param url       String URL to connect to
     * @param stream    InputStream data to put 
     * @return          HttpURLConnection ready with response data
     * @throws IOException on I/O error
     */	
	public HTTPResponse put(String url, InputStream stream) throws IOException {
		byte[] buff = streamToByteArray(stream);
		String data = Base64.encodeBytes(buff);
		return put(url, data);		
	}	
	
	/**
	 * Do an HTTP DELETE to url.
	 * 
	 * @param url url of host
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	public HTTPResponse delete(String url) throws IOException {
		
		HttpURLConnection connection = getAndConfigureConnection(url);
		connection.setDoInput(true);
		connection.setRequestMethod("DELETE");
		
		if (debugMode)
			debugMessage("DELETE", url, connection);
		
		return connect(connection);
	}	
	
	/**
	 * Do an HTTP DELETE to url.
	 * 
	 * @param url url of host
	 * @param headers HTTP headers as <String, String> Map
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	public HTTPResponse delete(String url, Map<String, String> headers) throws IOException {
		HttpURLConnection connection = getAndConfigureConnection(url);
		
		if (headers != null) 
			for (Entry<String, String> e : headers.entrySet())
				connection.setRequestProperty(e.getKey(), e.getValue());
		
		if (debugMode)
			debugMessage("DELETE", url, connection);
		
		connection.setDoInput(true);
		connection.setRequestMethod("DELETE");
		return connect(connection);
	}

	/**
	 * Puts a Map of key, value pair properties, like a web form.
	 * 
	 * @param url url of host
	 * @param properties map of properties for request body
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	public HTTPResponse put(String url, Map<String, String> properties) throws IOException {
		String data = propertyString(properties);
		HashMap<String, String> headers = new HashMap<String, String>(); 
		headers.put(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
		return put(url, data, headers);
	}
	
	/**
	 * Puts a Map of key, value pair properties, like a web form.
	 * 
	 * @param url url of host
	 * @param properties properties in request body
	 * @param headers HTTP headers for request
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	public HTTPResponse put(String url, Map<String, String> properties, Map<String, String> headers) throws IOException {
		String data = propertyString(properties);	 
		headers.put(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
		return put(url, data, headers);
	}

    /**
     * Do an HTTP HEAD to url.
     * 
     * @param url       String URL to connect to 
     * @return          HttpURLConnection ready with response data
     * @throws	IOException on I/O error
     */ 
	public HTTPResponse head(String url) throws IOException {
		HttpURLConnection connection = getAndConfigureConnection(url);
		connection.setDoOutput(true);
		connection.setRequestMethod("HEAD");
		
		if (debugMode)
			debugMessage("HEAD", url, connection);
		
		return connect(connection);
	}
	
	
	////////////////////////////////////////////////////////////// THESE HELP

    
	/**
	 * Connect to server, check the status, and return the new HTTPResponse.
	 * 
	 * @param connection HttpURLConnection
	 * @return HTTPResponse
	 * @throws IOException on I/O error
	 */
	private HTTPResponse connect(HttpURLConnection connection) throws IOException {
		long timestamp = 0;
		if (debugMode)
			timestamp = System.currentTimeMillis();
		
		HTTPResponse response = new HTTPResponse(connection);
		
		if (throwHTTPErrorResponses)
			response.checkStatus();
		
		if (debugMode)
			debugMessage(timestamp, connection.getURL().toString());
			
		return response;
	}
	

    /**
     * Create a byte array from the contents of an input stream.
     * 
     * @param in    InputStream to turn into a byte array 
     * @return      byte array (byte[]) w/ contents of input stream
     * @throws IOException on I/O error
     */ 
	public static byte[] streamToByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int read = 0;
		byte[] buff = new byte[COPY_BUFFER_SIZE];
		
		while ((read = in.read(buff)) > 0) {
			os.write(buff, 0, read);
		}
		
		return os.toByteArray();
	}	

	/**
	 *  turns a map into a key=value property string.
	 *  
	 * @param props
	 * @return
	 * @throws IOException
	 */
	public static String propertyString(Map<String, String> props) throws IOException {
		String propstr = new String();
		String key;
		for (Iterator<String> i = props.keySet().iterator(); i.hasNext();) {
			key = i.next();
			propstr = propstr + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode((String) props.get(key), "UTF-8");
			if (i.hasNext()) {
				propstr = propstr + "&";
			}
		}
		return propstr;
	}
	
	/**
	 * helper to create multipart form boundary
	 * 
	 * @return
	 */
	private static String createMultipartBoundary() {
		StringBuffer buf = new StringBuffer();
        buf.append("---------------------------");
        
        for (int i=0; i < 15; i++) {
        	double rand = Math.random() * 35;
        	if (rand < 10) {
        		buf.append((int)rand);
        	} else {
        		int ascii = 87 + (int)rand;
        		char symbol = (char)ascii;
        		buf.append(symbol);
        	}
        }
        return buf.toString();
	}
	
	/**
	 * Add a initializer that will be called for each http operation before the call is made.
	 * @param c
	 */
	public void addConfigurator(HTTPConnectionInitializer c) {
		if (configurators == null)
			configurators = new ArrayList<HTTPRequest.HTTPConnectionInitializer>();
		
		if (!configurators.contains(c))
			configurators.add(c);
	}
	
	/**
	 * Remove a initializer.
	 * @param c
	 */
	public void removeConfigurator(HTTPConnectionInitializer c) {
		if (configurators == null)
			return;
		
		configurators.remove(c);
		
		if (configurators.size() == 0)
			configurators = null;
	}	
	
	/**
	 * Print debug messages
	 * @param httpMethod
	 * @param url
	 * @param conn 
	 */
	private void debugMessage(String httpMethod, String url, HttpURLConnection conn) {
		System.out.println("HTTPRequest DEBUG " + System.currentTimeMillis() + ": [" + httpMethod + "] " + url + " ~ " + conn.getRequestProperties());
	}
	
	/**
	 * Print debug messages with ws time info
	 * @param time
	 * @param url
	 */
	private void debugMessage(long time, String url) {
		System.out.println("HTTPRequest DEBUG time (" + (System.currentTimeMillis() - time) + " ms): " + url);
	}
	
	/**
	 * Check for null and handle protocol-less type.
	 * @param url
	 */
	private String guardUrl(String url) {
		if (url == null)
			throw new RuntimeException("URL passed in was null.");

		//If no protocol defined in url, assume HTTP.
		if (!url.toLowerCase().trim().startsWith("http"))
			return "http://" + url;
		
		return url;
	}

}
