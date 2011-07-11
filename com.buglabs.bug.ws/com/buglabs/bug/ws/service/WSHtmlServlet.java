package com.buglabs.bug.ws.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;

import com.buglabs.services.ws.PublicWSDefinition;
import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.services.ws.PublicWSProvider2;

/**
 * This is a very simple servlet that provides an HTML view of the services
 * availible on a BUG
 * 
 * This is registered in PublicWSAdminImpl along with WSServlet, which handles
 * the actual xml services
 * 
 * @author brian
 * 
 */
public class WSHtmlServlet extends AbstractWSServlet {

	private static final long serialVersionUID = -6855836987056987821L;

	public WSHtmlServlet(BundleContext context, Hashtable servicesMap, ConfigurationAdmin configAdmin) {
		super(context, servicesMap, configAdmin);
	}

	protected void executeHttpMethod(HttpServletRequest req, HttpServletResponse resp, int reqMethod) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		writer.print("<html>\n" + "<head>\n" + "  <link rel=\"shortcut icon\" href=\"favicon.ico\">\n" + "  <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">\n"
				+ "  <title>BUG webservices</title>\n" + "</head>\n" + "<body>\n" + "  <div class=\"wrapper\">\n"
				+ "    <img class=\"logo\" src=\"img/logo.gif\" alt=\"bugbeta\"/>\n" + "  	 <div class=\"content\">\n" + "      <img src=\"img/art.gif\" style=\"float:left;\">\n"
				+ "	   <div class=\"words\">\n" + "        <h1>Services On Your BUG</h1>\n" + "          <p class=\"txt\">\n" + getServicesListAsHtml() + "          </p>\n"
				+ "          <p><a href=\"/\">back to services main page</a></p>" + "        </div>\n" + "      </div>\n" + "	</div>\n" + "</body>\n" + "</html>");

	}

	private String getServicesListAsHtml() {
		Map servicesMap = getServiceMap();
		String buffer = "<ul>";

		for (Iterator i = servicesMap.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			Object serviceObj = servicesMap.get(key);

			buffer += "<li>";
			if (serviceObj instanceof PublicWSProvider) {
				PublicWSProvider pubService = (PublicWSProvider) serviceObj;

				boolean isServiceEnabled = false;
				try {
					isServiceEnabled = isServiceEnabled(getConfigurationAdmin(), pubService.getPublicName());
				} catch (IOException e) {
				}
				// service is disabled, don't a line item for it
				if (!isServiceEnabled)
					continue;

				buffer += "<a href=\"service/" + pubService.getPublicName() + "\"" + ">" + pubService.getPublicName() + "</a><br/>";
				buffer += pubService.getDescription() + "<br/>";
				buffer += "Request Methods:" + "<br/>";

				PublicWSDefinition def = pubService.discover(PublicWSProvider2.GET);
				if (def != null) {
					buffer += getRequestInfo(pubService, def, "GET");
				}

				def = pubService.discover(PublicWSProvider2.POST);
				if (def != null) {
					buffer += getRequestInfo(pubService, def, "POST");
				}

				def = pubService.discover(PublicWSProvider2.DELETE);
				if (def != null) {
					buffer += getRequestInfo(pubService, def, "DELETE");
				}

				def = pubService.discover(PublicWSProvider2.PUT);
				if (def != null) {
					buffer += getRequestInfo(pubService, def, "PUT");
				}

			} else {
				buffer += key;
			}
			buffer += "</li>";
		}

		buffer += "</ul>";
		return buffer;
	}

	private String getRequestInfo(PublicWSProvider pubService, PublicWSDefinition def, String reqType) {
		String out = "<ul><li>" + reqType + "<br/>" + "returns - " + def.getReturnType() + "<br/>";
		if (def.getParameters() != null)
			out += "parameters -" + def.getParameters().toString();
		out += "</li></ul>";
		return out;
	}
}
