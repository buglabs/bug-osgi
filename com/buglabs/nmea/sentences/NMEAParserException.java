package com.buglabs.nmea.sentences;

public class NMEAParserException extends RuntimeException {

	public NMEAParserException(String msg, Throwable e) {
		super(msg, e);
	}
}
