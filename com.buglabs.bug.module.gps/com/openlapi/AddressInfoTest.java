/*
 * Copyright ThinkTank Maths Limited 2006 - 2008
 *
 * This file is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 */
package com.openlapi;


/**
 *
 */
class AddressInfoTest extends ALAPITest {

	public void runTests() throws LAPIException {
		testUSFields();
		testGBFields();
		testEmptyFields();
		testReAssignFields();
	}

	/**
	 * Behave the same way as the Nokia Reference Implementation for empty strings.
	 * 
	 * @throws LAPIException
	 */
	public void testEmptyFields() throws LAPIException {
		AddressInfo address = new AddressInfo();

		assertion(address.getField(AddressInfo.CITY) == null);

		String blank = new String("");

		address.setField(AddressInfo.CITY, blank);
		assertion(address.getField(AddressInfo.CITY) != null);
		assertion(address.getField(AddressInfo.CITY).equals(blank));
	}

	/**
	 * A full test of all fields for the GB example in JSR-179
	 * 
	 * @throws LAPIException
	 */
	public void testGBFields() throws LAPIException {
		AddressInfo address = new AddressInfo();

		address.setField(AddressInfo.EXTENSION, "The Oaks");
		address.setField(AddressInfo.STREET, "20 Greenford Court");
		address.setField(AddressInfo.POSTAL_CODE, "AB1 9YZ");
		address.setField(AddressInfo.CITY, "Cambridge");
		address.setField(AddressInfo.COUNTY, "Cambridgeshire");
		address.setField(AddressInfo.STATE, "England");
		address.setField(AddressInfo.COUNTRY, "United Kingdom");
		address.setField(AddressInfo.COUNTRY_CODE, "GB");
		address.setField(AddressInfo.URL, "http://britishurl.co.uk");

		assertion(address.getField(AddressInfo.EXTENSION).equals("The Oaks"));
		assertion(address.getField(AddressInfo.STREET).equals(
			"20 Greenford Court"));
		assertion(address.getField(AddressInfo.POSTAL_CODE).equals("AB1 9YZ"));
		assertion(address.getField(AddressInfo.CITY).equals("Cambridge"));
		assertion(address.getField(AddressInfo.COUNTY).equals("Cambridgeshire"));
		assertion(address.getField(AddressInfo.STATE).equals("England"));
		assertion(address.getField(AddressInfo.COUNTRY).equals("United Kingdom"));
		assertion(address.getField(AddressInfo.COUNTRY_CODE).equals("GB"));
	}

	/**
	 * Test that we can reassign fields.
	 * 
	 * @throws LAPIException
	 */
	public void testReAssignFields() throws LAPIException {
		AddressInfo address = new AddressInfo();
		address.setField(AddressInfo.CITY, "Edinburgh");
		assertion(address.getField(AddressInfo.CITY).equals("Edinburgh"));
		address.setField(AddressInfo.CITY, "Belfast");
		assertion(address.getField(AddressInfo.CITY).equals("Belfast"));
	}

	/**
	 * A full test of all fields for the US example in JSR-179
	 * 
	 * @throws LAPIException
	 */
	public void testUSFields() throws LAPIException {
		AddressInfo address = new AddressInfo();

		address.setField(AddressInfo.EXTENSION, "Flat 5");
		address.setField(AddressInfo.STREET, "10 Washington Street");
		address.setField(AddressInfo.POSTAL_CODE, "12345");
		address.setField(AddressInfo.CITY, "Palo Alto");
		address.setField(AddressInfo.COUNTY, "Santa Clara County");
		address.setField(AddressInfo.STATE, "California");
		address.setField(AddressInfo.COUNTRY, "United States of America");
		address.setField(AddressInfo.COUNTRY_CODE, "US");
		address.setField(AddressInfo.URL, "http://www.americanurl.com");
		address.setField(AddressInfo.PHONE_NUMBER, "");

		assertion(address.getField(AddressInfo.EXTENSION).equals("Flat 5"));
		assertion(address.getField(AddressInfo.STREET).equals(
			"10 Washington Street"));
		assertion(address.getField(AddressInfo.POSTAL_CODE).equals("12345"));
		assertion(address.getField(AddressInfo.CITY).equals("Palo Alto"));
		assertion(address.getField(AddressInfo.COUNTY).equals(
			"Santa Clara County"));
		assertion(address.getField(AddressInfo.STATE).equals("California"));
		assertion(address.getField(AddressInfo.COUNTRY).equals(
			"United States of America"));
		assertion(address.getField(AddressInfo.COUNTRY_CODE).equals("US"));
		assertion(address.getField(AddressInfo.URL).equals(
			"http://www.americanurl.com"));
	}

}
