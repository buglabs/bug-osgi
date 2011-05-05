/*
 * Sewing: a Simple framework for Embedded-OSGi Web Development
 * Copyright (C) 2009 Bug Labs
 * Email: bballantine@buglabs.net
 * Site: http://www.buglabs.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */

package com.buglabs.osgi.sewing.pub;

import org.osgi.framework.BundleContext;

public interface ISewingService {

	/**
	 * Registers your SewingHttpServlet and initializes the framework for your
	 * application This will register the servlet at the alias you pass in as
	 * well as the resources for images (/alias.images), stylesheets, and
	 * javascripts
	 * 
	 * @param context
	 *            the bundle context for the your web application
	 * @param alias
	 *            the name in the url for your application, must begin with a /,
	 *            i.e. "/MyWebApplication"
	 * @param sewingServlet
	 *            your implementation of SewingHttpServlet
	 */
	public void register(BundleContext context, String alias, SewingHttpServlet sewingServlet);

	/**
	 * Pass the instance of the servlet you sent in to the register request to
	 * unregister the servlet and it's resources
	 * 
	 * @param sewingServlet
	 */
	public void unregister(SewingHttpServlet sewingServlet);
}
