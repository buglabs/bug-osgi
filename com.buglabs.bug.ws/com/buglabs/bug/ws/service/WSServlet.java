package com.buglabs.bug.ws.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;

import com.buglabs.services.ws.IWSResponse;
import com.buglabs.services.ws.PublicWSDefinition;
import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.services.ws.PublicWSProvider2;
import com.buglabs.services.ws.PublicWSProviderWithParams;
import com.buglabs.util.StringUtil;
import com.buglabs.util.XmlNode;

/**
 * This class used to do it all, but now it extends AbstractWSServlet so other
 * Service view classes can share functionality. Some of this classes previous
 * functionality has been pushed into it's abstract parent.
 * 
 * 
 */
public class WSServlet extends AbstractWSServlet {

	private static final long serialVersionUID = 2559949124867457077L;

	public WSServlet(BundleContext context, Hashtable serviceMap, ConfigurationAdmin configAdmin) {
		super(context, serviceMap, configAdmin);
	}

	protected void executeHttpMethod(HttpServletRequest req, HttpServletResponse resp, int method) throws IOException, ServletException {
		if (req.getPathInfo() == null) {
			returnServices(resp, getServiceMap());
			return;
		}

		String urlPath = req.getPathInfo().substring(1);
		String serviceName;
		int end = urlPath.indexOf("/");
		if (end < 0) {
			serviceName = urlPath;
		} else {
			serviceName = urlPath.substring(0, end);
		}

		PublicWSProvider service = (PublicWSProvider) getServiceMap().get(serviceName.toUpperCase());

		// Service is not found, return 404 error
		if (service == null) {
			resp.sendError(404, serviceName + " was not found");
			return;
		}

		// check if service is disabled, if so send appropriate message to
		// the user
		if (!isServiceEnabled(getConfigurationAdmin(), service.getPublicName())) {
			resp.sendError(503, service.getPublicName() + " has been disabled by the user.");
			return;
		}

		IWSResponse response;

		if (service instanceof PublicWSProviderWithParams) {
			Map post;
			Map get;
			if (req.getMethod().equals("GET")) {
				get = req.getParameterMap();
				post = new HashMap();
			} else {
				get = new HashMap();
				String query = req.getQueryString();
				if (query != null) {
					String[] params = StringUtil.split(query, "&");
					for (int i = 0; i < params.length; i++) {
						String[] pair = StringUtil.split(params[i], "=");
						if (pair.length > 1) {
							get.put(pair[0], pair[1]);
						} else if (pair.length > 0) {
							get.put(pair[0], null);
						}
					}
				}
				post = req.getParameterMap();
			}
			response = ((PublicWSProviderWithParams) service).execute(method, urlPath, get, post);
		} else {
			response = service.execute(method, urlPath);
		}

		if (response == null) {
			// TODO make this error message more helpful.
			resp.sendError(0, "Service failed. (NULL response)");
		} else if (response.isError()) {
			resp.sendError(response.getErrorCode(), response.getErrorMessage());
		} else {
			if (response.getMimeType() == null) {
				resp.setContentType("text/plain");
			} else {
				resp.setContentType(response.getMimeType());
			}

			Object data = response.getContent();

			if (data instanceof InputStream) {
				pipe((InputStream) data, resp.getOutputStream());
				((InputStream) data).close();
				System.gc();
			} else {
				resp.getWriter().print(response.getContent());
			}
		}
	}

	private void returnServices(HttpServletResponse resp, Map serviceMap2) throws IOException {
		resp.setContentType("text/xml");
		resp.getWriter().print(mapToXml("service", serviceMap2).toString());
	}

	private XmlNode mapToXml(String entityName, Map serviceMap2) throws IOException {
		// Collection container is plural.
		// TODO: make sure 's' is the right pluralizer.
		XmlNode root = new XmlNode(entityName + "s");

		for (Iterator i = serviceMap2.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			Object v = serviceMap2.get(key);

			XmlNode element = null;

			if (v instanceof PublicWSProvider) {
				PublicWSProvider pubService = (PublicWSProvider) v;

				// service is disabled, don't create an element for it
				if (!isServiceEnabled(getConfigurationAdmin(), pubService.getPublicName())) {
					continue;
				}

				element = new XmlNode(root, entityName);

				element.addAttribute("name", pubService.getPublicName());
				element.addAttribute("description", pubService.getDescription());

				PublicWSDefinition def = pubService.discover(PublicWSProvider2.GET);
				if (def != null) {
					XmlNode defNode = new XmlNode(element, "get");
					defNode.setAttribute("returns", def.getReturnType());
					if (def.getParameters() != null) {
						defNode.setAttribute("parameters", def.getParameters().toString());
					} else {
						defNode.setAttribute("parameters", "");
					}
				}

				def = pubService.discover(PublicWSProvider2.POST);
				if (def != null) {
					XmlNode defNode = new XmlNode(element, "post");
					defNode.setAttribute("returns", def.getReturnType());
					if (def.getParameters() != null) {
						defNode.setAttribute("parameters", def.getParameters().toString());
					} else {
						defNode.setAttribute("parameters", "");
					}
				}

				def = pubService.discover(PublicWSProvider2.DELETE);
				if (def != null) {
					XmlNode defNode = new XmlNode(element, "delete");
					defNode.setAttribute("returns", def.getReturnType());
					if (def.getParameters() != null) {
						defNode.setAttribute("parameters", def.getParameters().toString());
					} else {
						defNode.setAttribute("parameters", "");
					}
				}

				def = pubService.discover(PublicWSProvider2.PUT);
				if (def != null) {
					XmlNode defNode = new XmlNode(element, "put");
					defNode.setAttribute("returns", def.getReturnType());
					if (def.getParameters() != null) {
						defNode.setAttribute("parameters", def.getParameters().toString());
					} else {
						defNode.setAttribute("parameters", "");
					}
				}
			} else {
				element = new XmlNode(root, entityName);
				element.addAttribute("name", key);
			}
		}

		return root;
	}

	private static void pipe(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[500000];
		int nread;
		int total = 0;

		synchronized (in) {
			while ((nread = in.read(buf, 0, buf.length)) >= 0) {
				out.write(buf, 0, nread);
				total += nread;
			}
		}
		out.flush();
		buf = null;
	}

}
