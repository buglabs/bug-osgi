package com.buglabs.osgi.tester;

import java.io.File;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

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
 * @author kgilmer
 *
 */
public class Activator implements BundleActivator {


	private static final String JUNIT_REPORT_DIR = "com.buglabs.osgi.tester.report.dir";

	public void start(final BundleContext context) throws Exception {
		File outputDir = context.getDataFile("temp").getParentFile();
		
		if (context.getProperty(JUNIT_REPORT_DIR) != null) {
			outputDir = new File(context.getProperty(JUNIT_REPORT_DIR));
			
			if (outputDir.isFile())
				throw new BundleException("Unable to start tester, " + JUNIT_REPORT_DIR + " is set to an existing file, needs to be a directory.");
			
			if (!outputDir.exists())
				if (!outputDir.mkdirs())
					throw new BundleException("Unable to start tester, unable to create directory " + JUNIT_REPORT_DIR);
		}
			
		System.out.println("Test report output directory: " + outputDir);
		BundleTestRunnerThread thread = new BundleTestRunnerThread(context, outputDir);
		thread.start();
	}

	public void stop(BundleContext context) throws Exception {
		
	}
}