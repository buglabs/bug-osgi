package com.buglabs.common.tests.osgi;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

import com.buglabs.common.tests.Activator;
import com.buglabs.common.tests.osgi.pub.ServiceA;
import com.buglabs.util.LogServiceUtil;
import com.buglabs.util.OSGiServiceLoader;

import junit.framework.TestCase;

/**
 * Test OSGi functionality provided by com.buglabs.common.
 * @author kgilmer
 *
 */
public class OSGiTestCommon extends TestCase {

	/**
	 * Test to see if can get a reference to LogService using LogServiceUtil.
	 */
	public void testGetLogService() {
		BundleContext context = Activator.getDefault().getContext();
		
		assertNotNull(context);
		
		LogService ls = LogServiceUtil.getLogService(context);
		
		assertNotNull(ls);
	}
	
	/**
	 * Test the OSGiServiceLoader class
	 * @throws Exception
	 */
	public void testOSGiServiceLoader() throws Exception {
		BundleContext context = Activator.getDefault().getContext();
		
		assertNotNull(context);
		
		final List<Object> services = new ArrayList<Object>();
		
		//Test base case
		OSGiServiceLoader.loadServices(context, ServiceA.class.getName(), null, new OSGiServiceLoader.IServiceLoader() {
			
			@Override
			public void load(Object service) throws Exception {
				services.add(service);
			}
		});
		
		assertTrue(services.size() == 0);
		
		ServiceRegistration sr = context.registerService(ServiceA.class.getName(), new ServiceAImpl(), null);
		
		assertNotNull(sr);
		
		//Test one registration
		OSGiServiceLoader.loadServices(context, ServiceA.class.getName(), null, new OSGiServiceLoader.IServiceLoader() {
			
			@Override
			public void load(Object service) throws Exception {
				services.add(service);
			}
		});
		
		assertTrue(services.size() == 1);
		
		sr.unregister();
		services.clear();
		//Test after deregistration
		OSGiServiceLoader.loadServices(context, ServiceA.class.getName(), null, new OSGiServiceLoader.IServiceLoader() {
			
			@Override
			public void load(Object service) throws Exception {
				services.add(service);
			}
		});
		
		assertTrue(services.size() == 0);
	}
	
	private class ServiceAImpl implements ServiceA {

		@Override
		public int methodA() {
			
			return 0;
		}
		
	}
}
