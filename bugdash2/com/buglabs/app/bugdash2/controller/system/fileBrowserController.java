package com.buglabs.app.bugdash2.controller.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;


import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.FormFile;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelRoot;

public class fileBrowserController extends ApplicationController {
	private String destination = "/home/root/fileserver/";
	private static final String RESULT_KEY 	= "result";
	private static final String DESTINATION_KEY = "dest";
	private static final String USERS_KEY = "userlist";
	BundleContext contex;
	public String getTemplateName() { return "system_fileBrowser.fml"; }
	
	public TemplateModelRoot get(RequestParameters params, 
			HttpServletRequest req, HttpServletResponse resp) {
		String users = getUsers();
		SimpleHash root = new SimpleHash();
		root.put(USERS_KEY, users);
		return root;
	}

	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		String output = "";
		SimpleHash root = new SimpleHash();
		File file;
		FormFile formfile;
		int branch = 0;
		String users = getUsers();
		root.put(USERS_KEY, users);
		//Parameters from the html form.
		String uploadFile = params.get("add");
		String removeDirectory = params.get("removeDir");
		String createDirectory = params.get("dir");
		if(uploadFile != null)
		{
			branch = 1;
		}
		else if(createDirectory != null)
		{
			branch = 3;
		}
		else if(removeDirectory == null)
		{
			branch = 2;
		}
		//get the code that determines which form was submitted
		switch(branch) {
		//creating a directory
		case 3:
			String path = params.get("hiddenDir");
			String dirName = params.get("dir_name");
			//dirName = spaceCheck(dirName);
			file = new File(path + dirName);
			boolean created = file.mkdir();
			if(created)
			{
				System.out.println("Directory Successfully Created");
			}
			else
			{
				System.out.println("Directory Creation Failed!");
			}
			return root;
		//removing a directory
		case 2:
			String path1 = params.get("hiddenDir");
			file = new File(path1);
			removeDirectory(file);
			return root;
		//uploading a file
		case 1:
			formfile = params.getFile();
			if (formfile == null)
			{
				root.put(RESULT_KEY, "Upload didn't work, Try again.");
				return root;
			}
			else
			{
				destination = params.get("hiddenDir");
			try {
				FileOutputStream fos = new FileOutputStream(destination + formfile.getFilename());
				fos.write(formfile.getBytes());
				fos.close();
				output = "Successfully Uploaded!";
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			root.put(RESULT_KEY, output);
			root.put(DESTINATION_KEY, destination);
			return root;
		}
		}
		return root;
	}
	
	/*private String spaceCheck(String dirName) {
		int index;
		String result = "";
		while((index = dirName.indexOf(" ")) != -1)
		{
			result = dirName.substring(0, index) + " ";
			dirName = dirName.substring(index + 1);
		}
		if(dirName.length() > 0) {
			result += dirName;
		}
		return result;
	}*/

	/**
	 * Returns a code determining which of the form buttons was clicked.
	 * @param uploadFile - non-null if the user selected to upload a file
	 * @param removeDirectory - non-null if the user selected to remove a directory
	 * @param createDirectory - non-null if the user selected to create a directory
	 * @return - an int code: 1 - upload <br> 2 - remove a directory <br> 3 - create a directory
	 */
	public int getFormSubmitted(String uploadFile, String removeDirectory,
			String createDirectory) {
		if(uploadFile != null && uploadFile.compareTo("null") != 0)
		{
			System.out.println("uploading a file");
			return 1;
		}
		else if(removeDirectory != null && removeDirectory.compareTo("null") != 0)
		{
			System.out.println("removing a directory");
			return 2;
		}
		else if(createDirectory != null && createDirectory.compareTo("null") != 0)
		{
			System.out.println("uploading a file");
			return 3;
		}
		return 0;
	}
	/**
	 * Generates the HTML code that allows switching of users
	 * @return - the string containing the html code.
	 */
	private String getUsers() {
		String code = "";
		File file = new File("/home/");
		File[] files = file.listFiles();
		for(int i = 0; i < files.length; i++)
		{
			if(files[i].isDirectory())
			{
				code += "<a href='/admin_system/navigator?-3" + files[i].getName() + "' target='nav'>" + files[i].getName() + "</a> | ";
			}
		}
		return code;
	}
	/**
	 * Helper function that removes all files and directories inside of a directory so that it can be completely deleted
	 * @param filename - the file (directory) that is to be deleted.
	 */
	private void removeDirectory(File filename)
	{
		File[] subFiles = filename.listFiles();
		if(subFiles != null)
		{
			for(int i = 0; i < subFiles.length; i++)
			{
				if(subFiles[i].isDirectory())
					removeDirectory(subFiles[i]);
				else
					subFiles[i].delete();
			}
		}
		filename.delete();
	}
}