package com.buglabs.osgi.tester;

import java.io.IOException;
import java.io.PrintStream;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.buglabs.osgi.shell.ICommand;
import com.buglabs.osgi.shell.pub.AbstractCommand;
import com.buglabs.util.OSGiServiceLoader;
import com.buglabs.util.OSGiServiceLoader.IServiceLoader;

public class RunTestCaseCommand extends AbstractCommand implements ICommand {
	private int testCount = 0;
	
	private int errors = 0;
	private int runs = 0;
	private int failures = 0;

	@Override
	public void execute() throws Exception {
		errors = 0;
		runs = 0;
		failures = 0;
		
		if (arguments.size() == 0) {
			OSGiServiceLoader.loadServices(context, TestSuite.class.getName(), null, new IServiceLoader() {

				public void load(Object service) throws Exception {
					TestRunner tr = new TestRunner(new PrintStream(RunTestCaseCommand.this.err));
					TestSuite tc = (TestSuite) service;

					runTest(tr, tc);
					testCount++;
				}

			});
		} else {
			final int execIndex = Integer.parseInt((String) arguments.get(0));
			OSGiServiceLoader.loadServices(context, TestSuite.class.getName(), null, new IServiceLoader() {
				int count = 1;
				
				public void load(Object service) throws Exception {
					TestRunner tr = new TestRunner(new PrintStream(RunTestCaseCommand.this.err));
					TestSuite tc = (TestSuite) service;

					if (count == execIndex) {
						runTest(tr, tc);
						testCount++;
					}
					count++;
				}
			});
		}
		
		println("\nTest execution complete.\nExecuted " + testCount + " test suites.  Runs: " + runs + "  Errors: " + errors + "  Failures: " + failures);
	}

	protected void runTest(TestRunner tr, TestSuite tc) throws IOException {
		println("Running Test Suite: " + tc.getName());
		TestResult result = tr.doRun(tc);
		
		errors += result.errorCount();
		runs += result.runCount();
		failures += result.failureCount();
		
		println("Test Suite Complete: " + tc.getName());
	}

	@Override
	public String getName() {
		return "trun";
	}

	@Override
	public String getUsage() {
		return " [Test Index]";
	}

	@Override
	public String getDescription() {
		return "Execute one or many JUnit tests.  Call test.list to find indexes if needed.";
	}
}
