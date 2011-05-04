package webadmin;

import java.util.Iterator;
import java.util.List;

import com.buglabs.util.StringUtil;

/**
 * This class contains a number of static methods to help string formatting in Sewing templates.
 * @author akweon
 *
 */
public class TemplateHelper {

	public static String listToHTML(List list) {
		return listToText(list, "<br />");
	}

	public static String listToText(List list) {
		return listToText(list, "\r\n");
	}
	
	public static String listToText(List list, String delimiter) {
		if (list == null) return ""; 
		String out = "";
		Iterator itr = list.iterator();
		while (itr.hasNext()) {
			out += ((String)itr.next()).trim() + delimiter;
		}
		return out;		
	}
	
	public static String makeJSFriendly(String input) {
		String output = "";
		output = StringUtil.replace(input, "\r\n", "<br />");
		output = StringUtil.replace(output, "\n", "<br />");
		output = StringUtil.replace(output, "'", "\\'");
		
		return output; 
	}
	
	public static String getStatusJSONString(String status, String msg) {
		return "{status:'" + status + "', message:'" + makeJSFriendly(msg) + "'}"; 
	}
	
	public static String getGlobalStatusJSONString(String category, String msg, String detail) {
		return "{category:'" + category + "', message:'" + makeJSFriendly(msg) + "', detail: '" + makeJSFriendly(detail) + "'}";
	}	
	public static String getGlobalStatusJSONString(String category, String msg) {
		return "{category:'" + category + "', message:'" + makeJSFriendly(msg) + "'}";
	}
	
	
}
