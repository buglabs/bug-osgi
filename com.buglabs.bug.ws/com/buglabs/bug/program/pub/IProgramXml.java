package com.buglabs.bug.program.pub;

/**
 * This interface defines the xml node and attribute names for the xml
 * representation of the Program entity.
 * 
 * @author ken
 * 
 */
public interface IProgramXml {
	/**
	 * Root node of program element.
	 */
	public static final String NODE_PROGRAM = "program";

	/**
	 * Containment node for program element.
	 */
	public static final String NODE_PROGRAMS = "programs";

	public static final String ATTRIB_ACTIVE = "active";

	public static final String ATTRIB_VERSION = "version";

	public static final String ATTRIB_ID = "id";

	public static final String ATTRIB_TYPE = "type";

	public static final String NODE_TITLE = "title";

	public static final String NODE_AUTHOR = "author";

	public static final String NODE_DATE_MODIFIED = "date_updated";

	public static final String NODE_NOTES = "notes";

	public static final String NODE_SERVICES = "services";

	public static final String NODE_SERVICE = "service";

	public static final String NODE_SERVICES2 = "services2";

	public static final String NODE_PROPERTY = "property";

	public static final String ATTRIB_NAME = "name";

	public static final String ATTRIB_VALUE = "value";

}
