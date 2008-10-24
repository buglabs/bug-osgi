package com.buglabs.util;

/**
 * Representation of a StreamMultiplexerEvent
 * 
 * @author Angel Roman
 */
public class StreamMultiplexerEvent {
	/**
	 * A new input stream has been requested.
	 */
	public static final int EVENT_STREAM_ADDED = 0;
	
	/**
	 * An input stream has been closed.
	 */
	public static final int EVENT_STREAM_REMOVED = 1;

	public StreamMultiplexerEvent(int type, int size) {
		numberOfStreams = size;
		this.type = type;
	}
	
	/**
	 * The event type. Possible values are:
	 * @see EVENT_STREAM_ADDED
	 * @see EVENT_STREAM_REMOVED
	 */
	public int type;
	
	/**
	 * The number of current active input streams.
	 */
	public int numberOfStreams;
}
