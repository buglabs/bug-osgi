package com.buglabs.util.shell;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.util.shell.pub.IShellService;

/**
 * This activator registers an instance of IShellService in the service registry.
 * 
 * @author kgilmer
 *
 */
public class Activator implements BundleActivator {

	private static BundleContext context;

	/**
	 * @return BundleContext
	 */
	static BundleContext getContext() {
		return context;
	}

	private ServiceRegistration sr;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		sr = context.registerService(IShellService.class.getName(), new ShellService(), null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		sr.unregister();
		Activator.context = null;
	}

}
