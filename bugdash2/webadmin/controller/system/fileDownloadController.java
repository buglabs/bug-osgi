package webadmin.controller.system;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webadmin.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.TemplateModelRoot;

public class fileDownloadController extends ApplicationController {

	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		resp.setContentType("application/x-download");
		String filePath = req.getContextPath().substring("/download?".length());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
	
}
