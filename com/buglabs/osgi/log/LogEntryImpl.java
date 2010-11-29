/*******************************************************************************
 * Copyright (c) 2010 Bug Labs, Inc.
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

package com.buglabs.osgi.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;

/**
 * A log entry implementation for the LogReaderService.
 * 
 * @author kgilmer
 *
 */
class LogEntryImpl implements LogEntry {
	private final Bundle bundle;
	private final int level;
	private final String message;
	private final ServiceReference sr;
	private final long time;
	private final Throwable exception;
	private SimpleDateFormat formatter;
	private final String dateFormat;

	protected LogEntryImpl(Bundle bundle, int level, String message, ServiceReference sr, long time, Throwable exception, String dateFormat) {
		this.bundle = bundle;
		this.level = level;
		this.message = message;
		this.sr = sr;
		this.time = time;
		this.exception = exception;
		this.dateFormat = dateFormat;
	}

	
	public Bundle getBundle() {
		return bundle;
	}

	
	public Throwable getException() {
		return exception;
	}

	
	public int getLevel() {
		return level;
	}

	
	public String getMessage() {
		return message;
	}

	
	public ServiceReference getServiceReference() {
		return sr;
	}

	
	public long getTime() {
		return time;
	}

	/**
	 * Print a human-readable form of the log entry to a PrintStream.
	 * @param ps
	 */
	protected void print(PrintStream ps) {
		ps.println(formatTime(time) + " " + getLevelLabel(level) + ": " + message);
			if (exception != null) {
				exception.printStackTrace(ps);
			} 
	}
	
	
	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		print(ps);
		
		try {
			baos.close();
			return baos.toString();
		} catch (IOException e) {
			return super.toString();
		}
	}

	/**
	 * @param t
	 * @return A human-readable date string.
	 */
	private String formatTime(long t) {
		if (formatter == null) {
			formatter = new SimpleDateFormat(dateFormat);
		}
		
		return formatter.format(new Date(t));
	}

	/**
	 * @param level
	 * @return A human-readable log level string.
	 */
	private String getLevelLabel(int level) {
		switch (level) {
		case 1:
			return "ERROR  ";
		case 2:
			return "WARNING";
		case 3:
			return "INFO   ";
		case 4:
			return "DEBUG  ";
		}

		return "UNKNOWN";
	}

}