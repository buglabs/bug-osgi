package com.buglabs.util;

public interface IStreamMultiplexerListener {
	/**
	 * Called when the size of opened inputstreams has changed. 
	 * 
	 * @param size the number of input streams
	 */
	public void streamNotification(StreamMultiplexerEvent event);
}
	