package com.buglabs.osgi.tester;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * A test runner for OSGi-contexted JUnit tests.
 * 
 * To expose tests to this runner, register the tests as such:
 * 
 * <code>BundleContext.registerService(TestSuite.class.getName(), new TestSuite(<Test Case Name>.class), null); </code>
 * 
 * In runtime, two console commands become available: tlist and trun
 * 
 * <code>tlist</code> is used to list available test cases that the tester can see.
 * <code>trun</code> is used to execute all or a specific test case.
 * 
 * If the OSGi bundle property <code>com.buglabs.osgi.tester.autorun</code> is set, all tests will be executed on <code>Activator.start()</code>.
 * 
 * @author kgilmer
 *
 */
public class Activator implements BundleActivator {


	public void start(final BundleContext context) throws Exception {
		BundleTestRunnerThread thread = new BundleTestRunnerThread(context);
		thread.start();
	}

	public void stop(BundleContext context) throws Exception {
		
	}
}