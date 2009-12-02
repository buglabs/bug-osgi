package com.buglabs.osgi.sewing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.osgi.sewing.pub.SewingHttpServlet;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

public class RedirectInfo {
	private String controller_name;
	private int request_type = SewingHttpServlet.GET;
	private HttpServletRequest request_object;
	private HttpServletResponse response_object;
	private RequestParameters parameters;
	private String url = null;

	public RedirectInfo(String url) {
		this.url = url;
	}

	public RedirectInfo(int requestType, String controllerName, RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		controller_name = controllerName;
		request_type = requestType;
		request_object = req;
		response_object = resp;
		parameters = params;
		url = null;
	}

	public String getControllerName() {
		return controller_name;
	}

	public int getRequestType() {
		return request_type;
	}

	public HttpServletRequest getRequestObject() {
		return request_object;
	}

	public HttpServletResponse getResponseObject() {
		return response_object;
	}

	public RequestParameters getParams() {
		return parameters;
	}

	public String getUrl() {
		return url;
	}

	public String getRedirectHtml() {
		String urlStr = url;
		if (urlStr == null)
			urlStr = "";
		return "<meta http-equiv=\"Refresh\"" + "content=\"0; url=" + urlStr + "\">";
	}
}
