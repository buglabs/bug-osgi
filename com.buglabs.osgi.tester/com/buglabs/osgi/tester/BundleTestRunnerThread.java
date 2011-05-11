package com.buglabs.osgi.tester;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class BundleTestRunnerThread extends Thread {
	private static final long SETTLE_MILLIS = 5000;
	
	private final BundleContext context;
	
	public BundleTestRunnerThread(BundleContext context) {
		this.context = context;		
	}

	@Override
	public void run()  {	
		TestRunner tr = new TestRunner(new PrintStream(System.out));
		
		try {
			System.out.println("Waiting " + SETTLE_MILLIS + " for OSGi instance to settle...");
			Thread.sleep(SETTLE_MILLIS);
				
			for (ServiceReference sr : Arrays.asList(context.getAllServiceReferences(TestSuite.class.getName(), null))) {
				TestSuite ts = (TestSuite) context.getService(sr);
				
				if (ts != null)
					try {
						runTest(tr, ts);
					} catch (IOException e) {						
						e.printStackTrace();
					}
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {			
		}
		
		//Test execution complete, now forcably shutdown the JVM.
		System.exit(0);
	}

	protected void runTest(TestRunner tr, TestSuite tc) throws IOException {
		System.out.println("Running Test Suite: " + tc.getName());
		TestResult result = tr.doRun(tc);
		
		System.out.print("Test Suite Complete: " + tc.getName());
		System.out.println("  Results ~ Errors: " + result.errorCount() + " Failures: " + result.failureCount());
	}
}
