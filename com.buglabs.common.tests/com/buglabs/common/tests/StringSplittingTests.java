package com.buglabs.common.tests;


import com.buglabs.util.StringUtil;

import junit.framework.TestCase;

/**
 * Tests for custom string splitting function as CDC/Foundation does not include String.split();
 * @author ken
 *
 */
public class StringSplittingTests extends TestCase {
	public void testSplit() {
		String s = "a dog runs fast";

		String[] w = StringUtil.split(s, " ");

		assertTrue(w.length == 4);

		assertTrue(w[3].equals("fast"));
	}

	public void testNoSplit() {
		String s = "adogrunsfast";

		String[] w = StringUtil.split(s, " ");

		assertTrue(w.length == 1);

		assertTrue(w[0].equals("adogrunsfast"));
	}

	public void testAllSplit() {
		String s = ";;;;";

		String[] w = StringUtil.split(s, ";");

		assertTrue(w.length == 4);
	}
	
	public void testXPathSplit() {
		String expr = "/programs/program";
		
		String[] split = StringUtil.split(expr, "/");
				
		assertEquals(3, split.length);
		
		assertEquals("", split[0]);
		assertEquals("programs", split[1]);
		assertEquals("program", split[2]);
		
	}
	
	public void testFileURISplit() {
		String expr = "file:/temp/angel";
		
		String split[] = StringUtil.split(expr, "file:");
		
		assertEquals(2, split.length);
		
		assertEquals("", split[0]);
		assertEquals("/temp/angel", split[1]);
	}
}
