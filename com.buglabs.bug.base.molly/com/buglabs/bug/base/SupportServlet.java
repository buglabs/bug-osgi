package com.buglabs.bug.base;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.support.ISupportInfoFormatter;
import com.buglabs.support.SupportInfo;

/**
 * Servlet for /support web service on BUG
 * 
 * @author bballantine
 *
 */
public class SupportServlet extends HttpServlet {
	private static final long serialVersionUID = 932235542410276130L;

	private SupportInfo info;
	private ISupportInfoFormatter formatter;
	
	public SupportServlet(SupportInfo info, ISupportInfoFormatter formatter) {
		this.info = info;
		this.formatter = formatter;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType(formatter.getContentType());
		resp.getWriter().print(info.getInfo(formatter));
	}
}