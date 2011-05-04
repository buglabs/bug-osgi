package webadmin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import freemarker.template.SimpleScalar;

/**
 * Provides ways to read text files 
 * @author akweon
 *
 */
public class ConfigFile {

	/**
	 * Given a file path, read the content and return each line as a list item 
	 * @param path
	 * @return
	 */
	public static List getContentAsList(String path) {
		return ShellUtil.getList("cat " + path); 
	}
	
	/**
	 * Return lines with the given prefix 
	 * @param path
	 * @param prefix
	 * @return
	 */
	public static List getContentAsList(String path, String prefix) {
		List content = ShellUtil.getList("cat " + path); 
		List result = new ArrayList(); 
		Iterator itr = content.iterator();
		String line; 
		while (itr.hasNext()) {
			line = itr.next().toString(); 
			if (line.startsWith(prefix)) {
				result.add(line); 
			}
		}
		return result; 
	}
	
	public static SimpleScalar getContentAsString(String path) {
		return ShellUtil.getSimpleScalar("cat " + path); 
	}

	
	
}
