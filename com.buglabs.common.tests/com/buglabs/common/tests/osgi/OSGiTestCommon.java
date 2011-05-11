package com.buglabs.common.tests.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import com.buglabs.common.tests.Activator;
import com.buglabs.util.LogServiceUtil;

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
}
