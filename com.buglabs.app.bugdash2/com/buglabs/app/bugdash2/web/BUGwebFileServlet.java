package com.buglabs.app.bugdash2.web;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.osgi.sewing.pub.SewingHttpServlet;
import com.buglabs.osgi.sewing.pub.util.ControllerMap;

public class BUGwebFileServlet extends SewingHttpServlet {

	private static final long serialVersionUID = 4188094934485024898L;

	public ControllerMap getControllerMap() {
		return null;
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String imagePath = req.getQueryString();
		imagePath = htmlCheckSpace(imagePath);
		if(imagePath.startsWith("-1"))
		{
			resp.setContentType("application/x-download"); 
			//resp.setHeader("Content-Disposition", "attachment; filename=\"" + "myfile" + "\""); // not implemented // TODO: add file name 
			String filePath = imagePath.substring(2);
			File requested = new File(filePath);
			int length = 0;
			OutputStream out;
			try {
				out = resp.getOutputStream();
				byte[] bbuf = new byte[512];
		        DataInputStream in = new DataInputStream(new FileInputStream(requested));
		        while ((in != null) && ((length = in.read(bbuf)) != -1))
		        {
		            out.write(bbuf,0,length);
		        }
		        in.close();
		        out.flush();
		        out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			//System.out.println("pathname parsed out: " + imagePath);
			//before we create the image, check to see if the file is an image (solely based on the extension)
			String type = getExtension(imagePath);
			File imageFile = new File(imagePath);
			resp.setContentType(type);
			try{
				FileInputStream in = new FileInputStream(imageFile); 
				ServletOutputStream out = resp.getOutputStream();
				byte[] bbuf = new byte[1024];
				int count = 0; 
				while ((count = in.read(bbuf)) >= 0) 
				{ 
					out.write(bbuf, 0, count); 
				}
				in.close(); 
				out.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String htmlCheckSpace(String imagePath) {
		int index;
		String result = "";
		while((index = imagePath.indexOf("%20")) != -1)
		{
			result += imagePath.substring(0, index) + " ";
			imagePath = imagePath.substring(index + 3);
		}
		if(imagePath.length() > 0) {
			result += imagePath;
		}
		if(result.compareTo("") == 0) {
			return imagePath;
		} else {
			return result;
		}
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
}
