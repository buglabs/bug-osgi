package webadmin.controller.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webadmin.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.FormFile;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.TemplateModelRoot;

public class fileNavigatorController extends ApplicationController {
	static String curDirectory = "/home/root/";
	private static final String HEADER_KEY = "upDirectory";
	private static final String DEFAULT_MIME_TYPE = "text/plain";
	private File[] files;
	
	public String getTemplateName() { return "system_fileBrowser_navigator.fml"; }

	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		String requestURL = req.getContextPath();
		String tempDirectory = "";
		if(requestURL == null)
		{
			requestURL = "/admin_system/navigator";
		}
		requestURL = getQuery(requestURL);
		requestURL = htmlSpaceCheck(requestURL);
		int branch = getAction(requestURL);
		switch(branch) {
		case 1:
			//remove the ? mark
			requestURL = requestURL.substring(1);
			requestURL = requestURL.concat("/");
			if(requestURL.compareTo("/home/") != 0)
			{
				curDirectory = requestURL;
			}
			break;
		case 2:
			tempDirectory = upDirectory(curDirectory);
			if(tempDirectory.compareTo("/home/") != 0)
			{
				curDirectory = tempDirectory;
			}
			break;
		//do nothing (just reload the present directory... occurs when user creates a file)
		case 3:
			break;
		//delete the file
		case 4:
			requestURL = requestURL.substring(3);
			File target = new File(requestURL);
			target.delete();
			break;
		case 5:
			requestURL = requestURL.substring(3);
			curDirectory = "/home/" + requestURL + "/";
			break;
		}
		
		File file = new File(curDirectory);
		//if the default home folder does not exist, move to the directory of the first user
		if(curDirectory.compareTo("/home/root/") == 0 && !file.exists())
		{
			curDirectory = upDirectory(curDirectory);
			file = new File(curDirectory);
			File[] userNames = file.listFiles();
			for(int i = 0; i < userNames.length; i++)
			{
				if(userNames[i].isDirectory())
				{
					curDirectory += userNames[i].getName();
					curDirectory += "/";
					file = new File(curDirectory);
					break;
				}
			}
		}
		files = file.listFiles(); 
		SimpleList file_list = new SimpleList(); 
		SimpleList dir_list = new SimpleList(); 
		SimpleHash item; 
		for(int i = 0; i < files.length; i++)
		{
			item = new SimpleHash(); 
			item.put("name", files[i].getName()); 
			item.put("path", curDirectory + files[i].getName());
			if (files[i].isDirectory())
				dir_list.add(item);
			else
				file_list.add(item);
		}		

		SimpleHash root = new SimpleHash();
		root.put("file_list", file_list);
		root.put("dir_list", dir_list);
		root.put(HEADER_KEY, curDirectory);
		return root;
	}

	private String htmlSpaceCheck(String requestURL) {
		int index;
		String result = "";
		while((index = requestURL.indexOf("%20")) != -1)
		{
			result += requestURL.substring(0, index) + " ";
			requestURL = requestURL.substring(index + 3);
		}
		if(requestURL.length() > 0) {
			result += requestURL;
		}
		if(result.compareTo("") == 0) {
			return requestURL;
		} else {
			return result;
		}
	}

	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		FormFile file = params.getFile();
		if (file == null)
		{
			return null;
		}
		else
		{
		try {
			FileOutputStream fos = new FileOutputStream(curDirectory + file.getFilename());
			fos.write(file.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		TemplateModelRoot root = get(params, req, resp);
		return root;
		}
	}
	
	/**
	 * Parses out the query of parameter string that comes after the ?
	 * @param string - the url that called the servlet
	 * @return the part of the url that comes after the ? (? included)
	 */
	private String getQuery(String string)
	{
		String result = "";
		int chopLength = string.indexOf("/navigator");
		chopLength += 10;
		result = string.substring(chopLength);
		return result;
	}
	/**
	 * Edits the string holding the current variable to move up one directory.
	 * @param string the current directory
	 * @return - the new directory
	 */
	private String upDirectory(String string)
	{
		String result = "";
		int index = string.lastIndexOf("/");
		string = string.substring(0, index);
		index = string.lastIndexOf("/");
		result = string.substring(0, index);
		result = result.concat("/");
		return result;
	}
		
	/**
	 * Parses the url passed in and determines what action needs to be taken.<br>
	 * 1 - load the files of a given directory in the viewer window<br>
	 * 2 - move up a directory and load those files into the given directory<br>
	 * 3 - reload whatever the current directory is<br>
	 * 4 - delete a specific file from the machine
	 * @param requestURL - the string to be parsed
	 * @return - an int code
	 */
	public int getAction(String requestURL) {
		if(requestURL.startsWith("?/"))
		{
			return 1;
		}
		else if(requestURL.compareTo("?-1") == 0)
		{
			return 2;
		}
		else if(requestURL.compareTo("") == 0)
		{
			return 3;
		}
		else if(requestURL.startsWith("?-2"))
		{
			return 4;
		}
		else if(requestURL.startsWith("?-3"))
		{
			return 5;
		}
		
		return 0;
	}
	/**
	 * Helper method to get the extension of the image file
	 * @param name - the name of the file (or the whole path in this case
	 * @return - the extension
	 */
	private String getExtension(String name) {
		int pos = name.lastIndexOf('.');
		if (pos > 0 && pos < name.length()) {
			return name.substring(pos + 1).toLowerCase();
		}

		return null;
	}
	/**
	 * Return the mimetype of the file.
	 * @param name - the name of the file
	 * @return - the mimetype of the file
	 */
	public String getMimeType(String name) {

		String extension = getExtension(name);

		if (extension == null || extension.length() == 0) {
			return DEFAULT_MIME_TYPE;
		}

		if (extension.equals("jpg") || extension.equals("jpeg")) {
			return "image/jpeg";
		}

		if (extension.equals("gif")) {
			return "image/gif";
		}
		
		if (extension.equals("png"))
			return "image/png";

		if (extension.equals("css")) {
			return "text/css";
		}

		if (extension.equals("txt")) {
			return "text/plain";
		}

		if (extension.equals("wml")) {
			return "text/vnd.wap.wml";
		}

		if (extension.equals("htm") || extension.equals("html")) {
			return "txt/html";
		}

		if (extension.equals("wbmp")) {
			return "image/vnd.wap.wbmp";
		}
		return DEFAULT_MIME_TYPE;
	}
}
