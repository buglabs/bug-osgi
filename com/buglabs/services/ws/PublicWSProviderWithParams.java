package com.buglabs.services.ws;

import java.util.Map;

/**
 * An extension of PublicWSProvider that is sent key value pairs for both GET
 * and POST data.  If you implement this, the execute() method defined in
 * PublicWSProvider will never be called.
 * @author dalbert
 *
 */
public interface PublicWSProviderWithParams extends PublicWSProvider {
	/**
	 * Execute a service with get and post parameters passed in.
	 * 
	 * @param operation
	 * 			PublicWSProvider.GET, .PUT, .POST, .DELETE
	 * @param input
	 * 			Request path starting with the public name of the PublicWSProviderWithParams
	 * @param get
	 * 			Key value pairs of the parameters passed in via the query string
	 * @param post
	 * 			Key value pairs passed in via POST data.
	 * @return
	 */
	public IWSResponse execute(int operation, String input, Map get, Map post);
}
