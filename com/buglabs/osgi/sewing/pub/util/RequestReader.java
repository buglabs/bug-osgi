package com.buglabs.osgi.sewing.pub.util;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.log.LogService;

import com.buglabs.osgi.sewing.LogManager;

/**
 * A static class to abstract the reading of the body of a request object
 * 
 * 
 * @author bballantine
 * 
 */
public class RequestReader {

	private static final int MAX_BYTES = 3 * 1024 * 1024; // megabytes
	private static final int MAX_TRIES = 10;
	private static final int MILLISECONDS_WAIT = 10;
	private static final String CONTENT_LENGTH = "Content-Length";

	public static String read(HttpServletRequest req) throws IOException {
		StringBuffer sbuf = new StringBuffer();

		int contentLength = MAX_BYTES;
		try {
			contentLength = Integer.parseInt(req.getHeader(CONTENT_LENGTH));
		} catch (NumberFormatException e) {
			LogManager.log(LogService.LOG_DEBUG, "Unable to get content length of multipart body.", e);
		}

		InputStream istream = req.getInputStream();
		int r, tries = 0, numread = 0;
		while (tries < MAX_TRIES && numread < contentLength) {
			if ((r = istream.read()) >= 0) {
				sbuf.append((char) r);
				++numread;
				tries = 0;
			} else {
				try {
					Thread.sleep(MILLISECONDS_WAIT);
				} catch (InterruptedException e) {
				}
				++tries;
			}
		}

		return sbuf.toString();
	}

}
