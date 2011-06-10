/* Copyright (c) 2007, 2008 Bug Labs, Inc.
 * All rights reserved.
 *   
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License version  
 * 2 only, as published by the Free Software Foundation.   
 *   
 * This program is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * General Public License version 2 for more details (a copy is  
 * included at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html).   
 *   
 * You should have received a copy of the GNU General Public License  
 * version 2 along with this work; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA   
 *
 */
package com.buglabs.bug.ws.program;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.log.LogService;

import com.buglabs.bug.program.pub.IProgramXml;
import com.buglabs.bug.program.pub.IUserAppManager;
import com.buglabs.bug.ws.Activator;
import com.buglabs.util.BugBundleConstants;
import com.buglabs.util.LogServiceUtil;
import com.buglabs.util.SelfReferenceException;
import com.buglabs.util.StringUtil;
import com.buglabs.util.XmlNode;

public class ProgramServlet extends HttpServlet {

	private static final String HTTP_OK_RESPONSE = "OK";

	private static final String MANIFEST_FILENAME = "MANIFEST.MF";

	private static final String CVS_DIRECTORY_NAME = "CVS";

	private static final String TEXT_XML_MIME_TYPE = "text/xml";

	private static final String TEXT_PLAIN_MIME_TYPE = "text/html";

	private static final String BUNDLE_TEMP_FILENAME = "bundle.jar";

	private static final String APPLICATION_JAVA_ARCHIVE_MIME_TYPE = "application/java-archive";

	private static final long serialVersionUID = -6977609397049223637L;

	public static int BUFFER_SIZE = 10240;

	private final BundleContext context;

	private String incomingBundleDir;

	private LogService log;

	public ProgramServlet(BundleContext context) {
		this.context = context;
		log = Activator.getLog();
		
		this.incomingBundleDir = context.getProperty(Activator.APP_BUNDLE_PATH);
		if (incomingBundleDir == null) {
			throw new RuntimeException("Bundle property " + Activator.APP_BUNDLE_PATH + " is not defined.  " + this.getClass().getName() + " cannot start.");
		}
		
		log.log(LogService.LOG_DEBUG, "BUGapp storage location set to: " + incomingBundleDir);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();

		if (path == null) {
			log.log(LogService.LOG_WARNING, "Error: null program path.");
			resp.sendError(665, "Error: invalid program path.");
			return;
		}

		String[] toks = StringUtil.split(path, "/");

		String jarName = toks[toks.length - 1];

		File bundleDir = new File(incomingBundleDir);

		if (!bundleDir.exists()) {
			log.log(LogService.LOG_ERROR, "Unable to save bundle; can't create file.");
			resp.sendError(0, "Unable to save bundle; can't create file.");
		}

		Bundle existingBundle = findBundleByName(jarName);
		if (existingBundle != null) {
			try {
				uninstallBundle(existingBundle);
			} catch (BundleException e) {
				log.log(LogService.LOG_ERROR, "Unable to uninstall existing bundle.  Aborting install.", e);
			}
		}

		File jarFile = new File(bundleDir.getAbsolutePath() + File.separator + jarName + ".jar");

		FileOutputStream fos = new FileOutputStream(jarFile);
		pipe(req.getInputStream(), fos, req.getContentLength());
		fos.flush();
		fos.close();

		/*appManager.addApplication(jarFile.getAbsolutePath());
		appManager.run();*/
		//TODO: Implement telling knapsack that bundles have changed
		log.log(LogService.LOG_ERROR, "IMPLEMENT ME.");

		resp.getWriter().println(HTTP_OK_RESPONSE);
		req.getInputStream().close();
	}

	/**
	 * Send data from inputstream to outputstream one byte at a time.
	 * 
	 * @param in
	 * @param out
	 * @param length
	 * @throws IOException
	 */
	private static void pipe(InputStream in, OutputStream out, int length) throws IOException {
		for (int i = 0; i < length; ++i) {
			out.write(in.read());
		}

		out.flush();
	}

	/**
	 * Send data from inputstream to outputstream one byte at a time.
	 * 
	 * @param in
	 * @param out
	 * @param length
	 * @throws IOException
	 */
	private static void pipe(InputStream in, OutputStream out) throws IOException {
		byte buf[] = new byte[1024 * 8];
		int read;

		while ((read = in.read(buf)) > 0) {
			out.write(buf, 0, read);
		}

		out.flush();
	}

	private void uninstallBundle(Bundle bundle) throws BundleException {
		if (bundle != null && bundle.getState() != Bundle.UNINSTALLED) {
			bundle.stop();
			bundle.uninstall();
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		String response = null;

		if (path == null) {
			// Get a list of description of all programs.
			resp.setContentType(TEXT_XML_MIME_TYPE);
			response = getAllBundles();
			resp.getWriter().println(response);
		} else {
			String id = path.substring(1).trim();

			// User should not be able to access System Bundle
			if (Long.parseLong(id) == 0) {
				resp.setContentType(TEXT_PLAIN_MIME_TYPE);
				resp.sendError(503);
				return;

			}

			if (!isNumber(id)) {
				resp.setContentType(TEXT_PLAIN_MIME_TYPE);
				resp.sendError(0, "Invalid program id: " + id);
				return;
			}

			Bundle b = findBundleById(id);

			if (b == null) {
				resp.setContentType(TEXT_PLAIN_MIME_TYPE);
				resp.sendError(0, "No program with id: " + id);
				return;
			}

			File jarfile = null;

			try {
				URI uri = new URI(b.getLocation());
				jarfile = new File(uri);
			} catch (URISyntaxException e) {
				log.log(LogService.LOG_ERROR, "Unable to get bundle location, invalid URL.", e);
			}

			if (jarfile != null && jarfile.exists() && jarfile.isFile()) {
				resp.setContentType(APPLICATION_JAVA_ARCHIVE_MIME_TYPE);
				FileInputStream fis = new FileInputStream(jarfile);
				pipe(fis, resp.getOutputStream());
			} else if (jarfile != null && jarfile.exists() && jarfile.isDirectory()) {
				resp.setContentType(APPLICATION_JAVA_ARCHIVE_MIME_TYPE);
				File bundleFile = new File(incomingBundleDir + File.separator + BUNDLE_TEMP_FILENAME);
				jarfile = createJar(jarfile, bundleFile);

				FileInputStream fis = new FileInputStream(jarfile);
				pipe(fis, resp.getOutputStream());
			} else {
				resp.setContentType(TEXT_PLAIN_MIME_TYPE);
				resp.sendError(0, "No program with id: " + id);
			}
		}
	}

	/**
	 * Implementation of doDelete that uninstalls supplied bundle
	 */
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo().replace('+', ' ');

		if (path == null) {
			resp.sendError(665, "Error: invalid program path.");
			return;
		}

		String[] toks = StringUtil.split(path, "/");

		try {
			Bundle b = findBundleByName(toks[toks.length - 1]);

			//TODO: Implement telling knapsack that bundles have changed
			log.log(LogService.LOG_ERROR, "IMPLEMENT ME.");
			//if (b != null && b.getState() != Bundle.UNINSTALLED) {
			if (b != null) {				
				uninstallBundle(b);
			}
		} catch (BundleException e) {
			resp.sendError(0, "Error: Unable to remove bundle." + e.getMessage());
		}
	}

	private boolean isNumber(String id) {

		try {
			Long.parseLong(id);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	/**
	 * Geenerate a Jar file from a directory.
	 * 
	 * @param jarDir
	 * @param bundleFile
	 * @return
	 * @throws IOException
	 */
	private File createJar(File jarDir, File bundleFile) throws IOException {
		List files = new ArrayList();
		getAllChildFiles(jarDir, files);
		createJarArchive(bundleFile, (File[]) files.toArray(new File[files.size()]), jarDir);

		return bundleFile;
	}

	/**
	 * Recursively return set of all child files.
	 * 
	 * @param root
	 * @param files
	 */
	private void getAllChildFiles(File root, List files) {

		File[] children = root.listFiles(new FilenameFilter() {

			public boolean accept(File arg0, String arg1) {
				if (arg1.startsWith(".")) {
					return false;
				}

				if (arg1.equals(CVS_DIRECTORY_NAME)) {
					return false;
				}

				if (arg1.equals(MANIFEST_FILENAME)) {
					return false;
				}

				return true;
			}

		});

		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				if (children[i].isFile()) {
					files.add(children[i].getAbsoluteFile());
				} else {
					getAllChildFiles(children[i], files);
				}
			}
		}
	}

	/**
	 * Code from forum board for creating a Jar.
	 * 
	 * @param archiveFile
	 * @param tobeJared
	 * @param rootDir
	 * @throws IOException
	 */
	protected void createJarArchive(File archiveFile, File[] tobeJared, File rootDir) throws IOException {

		byte buffer[] = new byte[BUFFER_SIZE];
		// Open archive file
		FileOutputStream stream = new FileOutputStream(archiveFile);
		JarOutputStream out = new JarOutputStream(stream, new Manifest(new FileInputStream(rootDir.getAbsolutePath() + File.separator + "META-INF" + File.separator
				+ MANIFEST_FILENAME)));

		for (int i = 0; i < tobeJared.length; i++) {
			if (tobeJared[i] == null || !tobeJared[i].exists() || tobeJared[i].isDirectory())
				continue; // Just in case...

			String relPath = getRelPath(rootDir.getAbsolutePath(), tobeJared[i].getAbsolutePath());

			// Add archive entry
			JarEntry jarAdd = new JarEntry(relPath);
			jarAdd.setTime(tobeJared[i].lastModified());
			out.putNextEntry(jarAdd);
			// Write file to archive
			FileInputStream in = new FileInputStream(tobeJared[i]);
			while (true) {
				int nRead = in.read(buffer, 0, buffer.length);
				if (nRead <= 0)
					break;
				out.write(buffer, 0, nRead);
			}
			in.close();
		}

		out.close();
		stream.close();

	}

	/**
	 * Return the difference of two paths.
	 * 
	 * @param rootPath
	 * @param subPath
	 * @return
	 */
	private String getRelPath(String rootPath, String subPath) {

		return subPath.substring(rootPath.length() + 1);
	}

	private XmlNode getBundleXmlNode(Bundle b) {
		XmlNode pnode = new XmlNode(IProgramXml.NODE_PROGRAM);

		int state = b.getState();

		String strState = "false";
		if (state == Bundle.ACTIVE) {
			strState = "true";
		}
		pnode.addAttribute(IProgramXml.ATTRIB_ACTIVE, strState);
		pnode.addAttribute(IProgramXml.ATTRIB_VERSION, (String) b.getHeaders().get("Bundle-Version"));
		pnode.addAttribute(IProgramXml.ATTRIB_ID, "" + b.getBundleId());

		pnode.addAttribute(IProgramXml.ATTRIB_TYPE, getBugBundleType(b));
		new XmlNode(pnode, IProgramXml.NODE_TITLE, (String) b.getHeaders().get("Bundle-Name"));
		new XmlNode(pnode, IProgramXml.NODE_AUTHOR, (String) b.getHeaders().get("Bundle-Vendor"));
		// We don't have date created in the Bundle interface so for now use
		// modified date. TODO Fix this.
		new XmlNode(pnode, "date_created", "1-1-2007");
		new XmlNode(pnode, IProgramXml.NODE_DATE_MODIFIED, "1-1-2007");
		new XmlNode(pnode, IProgramXml.NODE_NOTES);
		XmlNode sNode = new XmlNode(pnode, IProgramXml.NODE_SERVICES);
		XmlNode servicePropsNode = new XmlNode(pnode, IProgramXml.NODE_SERVICES2);

		ServiceReference[] sr = b.getRegisteredServices();

		if (sr != null) {
			for (int i = 0; i < sr.length; ++i) {
				String[] s = (String[]) sr[i].getProperty("objectClass");
				new XmlNode(sNode, IProgramXml.NODE_SERVICE, s[0]);
				// the following mass of code adds a services2 node to the xml
				// that includes the service properties
				XmlNode propertiesNode = new XmlNode(servicePropsNode, IProgramXml.NODE_SERVICE);
				propertiesNode.addAttribute(IProgramXml.ATTRIB_NAME, s[0]);
				String[] properties = sr[i].getPropertyKeys();
				for (int j = 0; j < properties.length; j++) {
					if (properties[j] == "service.id" || properties[j] == "objectClass")
						continue;
					XmlNode propNode = new XmlNode(propertiesNode, IProgramXml.NODE_PROPERTY);
					propNode.addAttribute(IProgramXml.ATTRIB_NAME, properties[j]);
					propNode.addAttribute(IProgramXml.ATTRIB_VALUE, "" + sr[i].getProperty(properties[j]));
				}
			}
		}

		return pnode;
	}

	private String getBugBundleType(Bundle b) {
		String type = "";

		String temp = (String) b.getHeaders().get(BugBundleConstants.BUG_BUNDLE_TYPE_HEADER);
		if (temp != null) {
			type = temp;
		}

		return type;
	}

	private Bundle findBundleById(String id) {
		Bundle[] bundles = context.getBundles();

		for (int i = 0; i < bundles.length; ++i) {
			if (bundles[i].getBundleId() == Long.parseLong(id)) {
				return bundles[i];
			}
		}

		return null;
	}

	private Bundle findBundleByName(String name) {
		Bundle[] bundles = context.getBundles();

		for (int i = 0; i < bundles.length; ++i) {
			Dictionary headers = bundles[i].getHeaders();
			if (headers != null) {
				String symbolicName = (String) headers.get("Bundle-Name");
				if (symbolicName != null) {
					if (symbolicName.equals(name)) {
						return bundles[i];
					}
				}
			}
		}

		return null;
	}

	private String getAllBundles() throws SelfReferenceException {
		Bundle[] bundles = context.getBundles();

		XmlNode root = new XmlNode(IProgramXml.NODE_PROGRAMS);

		for (int i = 0; i < bundles.length; ++i) {
			Bundle b = bundles[i];
			root.addChildElement(getBundleXmlNode(b));
		}

		return root.toString();
	}

	public static String getRelativePath(File file, File relativeTo) throws IOException {
		file = new File(file + File.separator + "89243jmsjigs45u9w43545lkhj7").getParentFile();
		relativeTo = new File(relativeTo + File.separator + "984mvcxbsfgqoykj30487df556").getParentFile();
		File origFile = file;
		File origRelativeTo = relativeTo;
		ArrayList filePathStack = new ArrayList();
		ArrayList relativeToPathStack = new ArrayList();
		// build the path stack info to compare it afterwards
		file = file.getCanonicalFile();
		while (file != null) {
			filePathStack.add(0, file);
			file = file.getParentFile();
		}
		relativeTo = relativeTo.getCanonicalFile();
		while (relativeTo != null) {
			relativeToPathStack.add(0, relativeTo);
			relativeTo = relativeTo.getParentFile();
		}
		// compare as long it goes
		int count = 0;
		file = (File) filePathStack.get(count);
		relativeTo = (File) relativeToPathStack.get(count);
		while ((count < filePathStack.size() - 1) && (count != relativeToPathStack.size() - 1) && file.equals(relativeTo)) {
			count++;
			file = (File) filePathStack.get(count);
			relativeTo = (File) relativeToPathStack.get(count);
		}
		if (file.equals(relativeTo))
			count++;
		// up as far as necessary
		StringBuffer relString = new StringBuffer();
		for (int i = count; i != relativeToPathStack.size(); i++) {
			relString.append(".." + File.separator);
		}
		// now back down to the file
		for (int i = count; i < filePathStack.size() - 1; i++) {
			relString.append(((File) filePathStack.get(i)).getName() + File.separator);
		}
		relString.append(((File) filePathStack.get(filePathStack.size() - 1)).getName());
		// just to test
		File relFile = new File(origRelativeTo.getAbsolutePath() + File.separator + relString.toString());
		if (!relFile.getCanonicalFile().equals(origFile.getCanonicalFile())) {
			throw new IOException("Failed to find relative path.");
		}
		return relString.toString();
	}
}
