package com.buglabs.bug.ws;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import com.buglabs.application.ServiceTrackerHelper;
import com.buglabs.application.ServiceTrackerHelper.ManagedInlineRunnable;
import com.buglabs.bug.ws.module.ModuleServlet;
import com.buglabs.bug.ws.module.PackageServlet;
import com.buglabs.bug.ws.program.ConfigAdminServlet;
import com.buglabs.bug.ws.program.ProgramServlet;
import com.buglabs.bug.ws.service.WSHtmlServlet;
import com.buglabs.bug.ws.service.WSServlet;
import com.buglabs.module.IModuleControl;
import com.buglabs.util.LogServiceUtil;
import com.buglabs.util.ServiceFilterGenerator;

/**
 * Bundle activator for BUG web services functionality.
 * 
 * @author kgilmer
 *
 */
public class Activator implements BundleActivator, ManagedInlineRunnable {
	public static final String APP_BUNDLE_PATH = "app.bundle.path";

	public static final String PROGRAM_WS_PATH = "/program";

	public static final String CONFIG_WS_PATH = "/configuration";
	
	public static final String MODULE_WS_PATH = "/module";
	private static final String ROOT_ALIAS = "/";
	private static final String SERVICE_WS_PATH = "/service";
	private static final String SERVICE_HTML_PATH = SERVICE_WS_PATH + ".html";

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private static LogService log;
	
	private Map<String, HttpServlet> servlets;
	
	public static LogService getLog() {
		return log;
	}

	private ServiceTracker st;
	/**
	 * Services required before bundle's functionality will be active.
	 */
	private String[] services = {
			HttpService.class.getName(),
			ConfigurationAdmin.class.getName()
	};
	private HttpService httpService;

	private ConfigurationAdmin configAdmin;

	private ModuleServlet moduleServlet;

	private Hashtable serviceMap;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		log = LogServiceUtil.getLogService(bundleContext);
		st = ServiceTrackerHelper.openServiceTracker(bundleContext, services , this);
		serviceMap = new Hashtable();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		st.close();
		Activator.context = null;
	}

	@Override
	public void run(Map<Object, Object> services) {
		// Determine if object state is valid
		if (servlets != null) {
			log.log(LogService.LOG_ERROR, "Servlets have already been registered but the ServiceTracker was called to register again.");
			return;
		}
			
		// Get a reference to the HTTP service
		httpService = (HttpService) services.get(HttpService.class.getName());
		configAdmin = (ConfigurationAdmin) services.get(ConfigurationAdmin.class.getName());
		
		// Create servlet instances to be hosted by HTTP service
		servlets = new Hashtable<String, HttpServlet>();
		
		try {
			moduleServlet = new ModuleServlet();
			context.addServiceListener(moduleServlet, 
					ServiceFilterGenerator.generateServiceFilter(context, new String[] {IModuleControl.class.getName()}).toString());
			servlets.put(MODULE_WS_PATH, moduleServlet);
		} catch (InvalidSyntaxException e) {
			log.log(LogService.LOG_ERROR, "Failed to construct a valid service filter.", e);
			return;
		}
		servlets.put("/package", new PackageServlet(context));
		servlets.put(PROGRAM_WS_PATH, new ProgramServlet(context));
		servlets.put(CONFIG_WS_PATH, new ConfigAdminServlet(context, configAdmin));
				
		servlets.put(SERVICE_WS_PATH, new WSServlet(context, serviceMap, configAdmin));
		servlets.put(SERVICE_HTML_PATH, new WSHtmlServlet(context, serviceMap, configAdmin));
		
		try {
			httpService.registerResources(ROOT_ALIAS, "static", new StaticResourceContext());
		} catch (NamespaceException e2) {
			log.log(LogService.LOG_ERROR, "Failed to register resource " + ROOT_ALIAS, e2);
		}
		
		// Register servlets
		for (Map.Entry<String, HttpServlet> e : servlets.entrySet())
			try {
				httpService.registerServlet(e.getKey(), e.getValue(), null, null);
			} catch (Exception e1) {
				log.log(LogService.LOG_ERROR, "Failed to register servlet " + e.getKey(), e1);
			} 
	}

	@Override
	public void shutdown() {
		if (moduleServlet != null)
			context.removeServiceListener(moduleServlet);
		
		// Unregister all servlets
		if (httpService != null && servlets != null && servlets.size() > 0)
			for (String alias : servlets.keySet())
				httpService.unregister(alias);
	}
	
	/**
	 * HttpContext for static resource included in bundle.
	 *
	 */
	private class StaticResourceContext implements HttpContext {

		public String getMimeType(String name) {
			return null;
		}

		public URL getResource(String name) {

			return context.getBundle().getResource(name);
		}

		public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
			return true;
		}
	}
}
