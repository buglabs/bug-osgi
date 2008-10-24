package com.buglabs.common.tests;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.buglabs.application.AbstractServiceTracker;
import com.buglabs.application.IServiceProvider;
import com.buglabs.application.RunnableWithServices;
import com.buglabs.application.ServiceTrackerHelper;
import com.buglabs.common.tests.pub.ServiceA;
import com.buglabs.common.tests.pub.ServiceB;

import edu.emory.mathcs.backport.java.util.concurrent.Executor;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;

import junit.framework.TestCase;

/**
 * Tests for ServiceTrackers and utility classes based on them.
 * 
 * @author kgilmer
 * 
 */
public class ServiceTrackerTests extends TestCase {

	private BundleContext context;

	protected void setUp() throws Exception {
		context = Activator.getDefault().getContext();
	}

	public void testSingleServiceRegAfter() {
		// Register the tracker

		TestServiceTrackerCustomizer tstc = new TestServiceTrackerCustomizer();
		ServiceTracker st = new ServiceTracker(context, ServiceA.class
				.getName(), tstc);
		st.open();

		// test that service is not available.
		assertFalse(tstc.serviceAvailable);

		// register service
		ServiceRegistration sr = context.registerService(ServiceA.class
				.getName(), new ServiceAImpl(), null);

		assertTrue(tstc.serviceAvailable);

		sr.unregister();

		// test that service is not available.
		assertFalse(tstc.serviceAvailable);
		st.close();
	}
	
	public void testConcurrentServiceTrackerHelpers() {
		int threads = 10;
		int requests = 10;

		Executor ex = Executors.newFixedThreadPool(threads);

		for (int i = 0; i < threads; ++i) {
			ex.execute(new testConcurrentServiceTrackerHelper());
		}
	}

	public void testAbstractServiceTracker() {
		// Register the tracker

		TestAbstractServiceTracker tstc = new TestAbstractServiceTracker(
				context);
		ServiceTracker st = new ServiceTracker(context, ServiceA.class
				.getName(), tstc);
		st.open();

		// test that service is not available.
		assertFalse(tstc.serviceAvailable);

		// register service
		ServiceRegistration sr = context.registerService(ServiceA.class
				.getName(), new ServiceAImpl(), null);

		assertTrue(tstc.serviceAvailable);

		sr.unregister();

		// test that service is not available.
		assertFalse(tstc.serviceAvailable);
		st.close();
	}
	
	public void testManySyncServiceTrackerHelpers() throws InvalidSyntaxException {
		for (int i = 0; i < 1000; ++i) {
			testServiceTrackerHelper();
		}
	}

	
	public void testManySyncServiceTrackerHelpersMulti() throws InvalidSyntaxException {
		for (int i = 0; i < 1000; ++i) {
			testServiceTrackerHelperMutliServices();
		}
	}
	public void testServiceTrackerHelper() throws InvalidSyntaxException {
		// boolean svcAvailable = false;

		// Register the tracker
		TestRunnableWithServices tstc = new TestRunnableWithServices();
		ServiceTracker st = ServiceTrackerHelper.createAndOpen(context,
				ServiceA.class.getName(), tstc);

		// test that service is not available.
		assertFalse(tstc.serviceAvailable);

		// register service
		ServiceRegistration sr = context.registerService(ServiceA.class
				.getName(), new ServiceAImpl(), null);

		assertTrue(tstc.serviceAvailable);

		sr.unregister();

		// test that service is not available.
		assertFalse(tstc.serviceAvailable);
		
		st.close();
	}

	
	public void testServiceTrackerHelperMutliServices() throws InvalidSyntaxException {
		// boolean svcAvailable = false;

		// Register the tracker
		TestRunnableWithServices tstc = new TestRunnableWithServices();
		ServiceTracker st = ServiceTrackerHelper.createAndOpen(context,
				new String[] { ServiceA.class.getName(), ServiceB.class.getName() }, tstc);

		// test that service is not available.
		assertFalse(tstc.serviceAvailable);

		// register service
		ServiceRegistration sr = context.registerService(ServiceA.class
				.getName(), new ServiceAImpl(), null);
		
		// test that all services is not available.
		assertFalse(tstc.serviceAvailable);
		
		// register service
		ServiceRegistration sr2 = context.registerService(ServiceB.class
				.getName(), new ServiceBImpl(), null);

		assertTrue(tstc.serviceAvailable);

		sr.unregister();
		
		// test that all services is not available.
		assertFalse(tstc.serviceAvailable);
		
		sr2.unregister();

		// test that service is not available.
		assertFalse(tstc.serviceAvailable);
		
		st.close();
	}

	// #############################################
	
	private class testConcurrentServiceTrackerHelper implements Runnable {

		public void run() {
			// boolean svcAvailable = false;

			// Register the tracker
			TestRunnableWithServices tstc = new TestRunnableWithServices();
			ServiceTracker st;
			try {
				st = ServiceTrackerHelper.createAndOpen(context,
						new String[] { ServiceA.class.getName(), ServiceB.class.getName() }, tstc);
			} catch (InvalidSyntaxException e) {
				throw new RuntimeException(e);
			}
			// register service
			ServiceRegistration sr = context.registerService(ServiceA.class
					.getName(), new ServiceAImpl(), null);
			// register service
			ServiceRegistration sr2 = context.registerService(ServiceB.class
					.getName(), new ServiceBImpl(), null);

			// Since there are concurrent instances of this method running, we cannot rely on a service not being available.
			
			assertTrue(tstc.serviceAvailable);

			sr.unregister();
			sr2.unregister();
			
			st.close();
		}
		
	}

	private class TestRunnableWithServices implements RunnableWithServices {
		boolean serviceAvailable = false;

		public void allServicesAvailable(IServiceProvider serviceProvider) {
			Object o = serviceProvider.getService(ServiceA.class);

			if (o != null && o instanceof ServiceA) {
				serviceAvailable = true;
			}
		}

		public void serviceUnavailable(IServiceProvider serviceProvider, ServiceReference sr, Object service) {
			serviceAvailable = false;
		}

	}

	private class TestServiceTrackerCustomizer implements
			ServiceTrackerCustomizer {
		boolean serviceAvailable = false;

		public Object addingService(ServiceReference arg0) {
			Object srv = context.getService(arg0);

			if (srv instanceof ServiceA) {
				serviceAvailable = true;
			}

			return srv;
		}

		public void modifiedService(ServiceReference arg0, Object arg1) {
			// TODO Auto-generated method stub

		}

		public void removedService(ServiceReference arg0, Object arg1) {
			Object srv = context.getService(arg0);

			if (srv instanceof ServiceA) {
				serviceAvailable = false;
			}
		}

	}

	private class TestAbstractServiceTracker extends AbstractServiceTracker {
		public TestAbstractServiceTracker(BundleContext context) {
			super(context);
			getServices().add(ServiceA.class.getName());
		}

		boolean serviceAvailable = false;

		public void doStart() {
			Object o = this.getService(ServiceA.class);
			if (o != null && o instanceof ServiceA) {
				serviceAvailable = true;
			}
		}

		public List getServices() {
			if (services == null) {
				services = new ArrayList();
				services.add(ServiceA.class.getName());
			}

			return services;
		}

		public void doStop() {			
		}
		
		public void removedService(ServiceReference reference, Object service) {
			Object srv = context.getService(reference);

			if (srv instanceof ServiceA) {
				serviceAvailable = false;
			}
		}
	}
	
	// ######################

	private class ServiceAImpl implements ServiceA {

		public int methodA() {
			// TODO Auto-generated method stub
			return 0;
		}

	}
	
	private class ServiceBImpl implements ServiceB {

		public int methodB() {
			// TODO Auto-generated method stub
			return 0;
		}
	}

}
