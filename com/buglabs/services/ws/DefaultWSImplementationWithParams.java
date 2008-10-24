package com.buglabs.services.ws;

import java.util.Map;

public class DefaultWSImplementationWithParams extends DefaultWSImplementation
		implements PublicWSProviderWithParams {

	public DefaultWSImplementationWithParams(String publicName) {
		super(publicName);
	}

	public IWSResponse execute(int operation, String input, Map get, Map post) {
		return WSResponse.UnimplementedErrorResponse;
	}

}
