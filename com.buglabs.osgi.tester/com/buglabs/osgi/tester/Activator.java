package com.buglabs.osgi.tester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.osgi.shell.ICommand;
import com.buglabs.osgi.shell.IShellCommandProvider;

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
public class Activator implements BundleActivator, IShellCommandProvider {

	private ServiceRegistration sr;
	private ICommand runCmd;

	public void start(final BundleContext context) throws Exception {
		runCmd = new RunTestCaseCommand();
		sr = context.registerService(IShellCommandProvider.class.getName(), this, null);
		
		//Auto run if it's been specified.
		if (context.getProperty("com.buglabs.osgi.tester.autorun") != null && context.getProperty("com.buglabs.osgi.tester.autorun").toUpperCase().equals("TRUE")) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
						runCmd.initialize(new ArrayList(), System.out, System.err, context);
						runCmd.execute();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			
		}
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
	}

	@Override
	public List getCommands() {
		return Arrays.asList(new ICommand[] {runCmd, new ListTestsCommand()});
	}
}