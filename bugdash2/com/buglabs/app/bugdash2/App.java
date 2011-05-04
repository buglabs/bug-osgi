package com.buglabs.app.bugdash2;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import lib.Utils;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.buglabs.util.simplerestclient.BasicAuthenticationConnectionProvider;
import com.buglabs.util.simplerestclient.HTTPRequest;
import com.buglabs.util.simplerestclient.HTTPResponse;
import com.buglabs.util.simplerestclient.IConnectionProvider;

import com.buglabs.bug.program.pub.IUserAppManager;
import com.buglabs.util.XmlNode;
import com.buglabs.util.XmlParser;

/**
 * Represents BUG apps from BUGnet 
 * @author akweon
 *
 */
public class App {

	public static final 	String BUGWS_URL 		= "http://api.buglabs.net/v3";	
	public static final 	String BUGNET_URL 		= "http://www.buglabs.net";
	public static final		String COMMUNITY_URL 	= "www.buglabs.net"; 
	public static final 	String LOCAL_PATH 		= "/usr/share/java/apps";
	
	private int id; 
	private int version; 
	private String title;
	private String author;
	private String downloads; 
	private String description;
	private String rating;  // make this double? 
	private String url;
	private String medium; 
	private String thumbnail; 
	private String icon; 
	private String apiVersion; 
	private String category, maturity; 
	private Date createdAt; 
	private String createdAtFormatted; 
	private List packages, modules; 
	private List admins; 
	private List collaborators; 
	
	public App(String title) 
	{ 
		this.title = title; 
		this.packages = new ArrayList(); 
		this.modules = new ArrayList(); 
		this.admins = new ArrayList(); 
		this.collaborators = new ArrayList(); 
	}
	
	public App(int id, int version, String title, String author, String downloads,
			String description, String rating, String url, String thumbnail, String icon, 
			String apiVersion, String category, String maturity) 
	{
		this.id = id; 
		this.version = version; 
		this.title = title;
		this.author = author;
		this.downloads = downloads;
		this.description = description;
		this.rating = rating;
		this.url = url;
		this.thumbnail = thumbnail;
		this.icon = icon; 
		this.apiVersion = apiVersion;
		this.category = category; 
		this.maturity = maturity; 
		this.packages = new ArrayList();
		this.modules = new ArrayList(); 
		this.admins = new ArrayList(); 
		this.collaborators = new ArrayList(); 
	}

	public int getId() 				{ 	return id; }
	public int getVersion() 		{ 	return version; }
	public String getTitle() 		{	return title; 	}
	public String getAuthor() 		{ 	return author; 	}
	public String getDownloads() 	{ 	return downloads; }
	public String getDescription() 	{ 	return description; } 
	public String getRating() 		{	return rating; 	}
	public String getUrl() 			{	return url; 	}
	public String getMedium() 		{	return medium; }	
	public String getThumbnail() 	{	return thumbnail; }
	public String getIcon() 		{	return icon; }
	public String getApiVersion() 	{	return apiVersion; 	}
	public String getCategory() 	{	return category; 	}
	public String getMaturity() 	{	return maturity; 	}
	public List   getPackages() 	{ 	return packages; }
	public List	  getModules() 		{	return modules; }
	public Date	  getCreatedAt() 	{ 	return createdAt; }
	public String getCreatedAtFormatted() { return createdAtFormatted; }
	public List   getAdmins() 		{ 	return admins; }
	public List   getCollaborators(){ 	return collaborators; }
	
	public String getURLEncodedTitle() { 
		try {
			return URLEncoder.encode(this.title, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return this.title; 
		} 
	}
	
	public String getDownloadUrl() {
		return BUGWS_URL + "/programs/" + this.getURLEncodedTitle() + "/download"; 
	}
	
	public String displayPackages() {
		String output = ""; 
		for (int i=0; i<this.packages.size(); i++) {
			output += this.packages.get(i) + " ";
		}
		return output; 
	}

	public void addPackage(String p) {
		this.packages.add(p);
	}
	
	public void addModule(String m) {
		this.modules.add(m); 
	}
	
	public void addAdmin(String a) {
		this.admins.add(a);
	}
	
	public void addCollaborator(String c) {
		this.collaborators.add(c);
	}
	
	public void install(String path) throws IOException
	{
		HTTPRequest request;
		HTTPResponse response;
		if (WebAdminSettings.isLoggedIn()) {
			IConnectionProvider basicAuthConnection = new BasicAuthenticationConnectionProvider(WebAdminSettings.bugnetLogin, WebAdminSettings.bugnetPwd);
			request = new HTTPRequest(basicAuthConnection); 
		} else {
			request = new HTTPRequest(); 
		}
		response = request.get(this.getDownloadUrl());
		InputStream is = response.getStream();
		String filename = path + "/" + this.getURLEncodedTitle() + ".jar";
		LogManager.logDebug("Installing: " + filename);
		FileOutputStream fos = new FileOutputStream(filename, false);
		pipe(is, fos);
		BundleContext context  = Activator.getContext();
		ServiceReference sr = context.getServiceReference(IUserAppManager.class.getName());
		IUserAppManager userAppManager = (IUserAppManager) context.getService(sr);
		userAppManager.addApplication(filename);
		userAppManager.run();		
	}
	
	public void lookupByTitle() 
	{
		if (!this.title.equals("")) {
			String url = App.BUGWS_URL+"/programs/" + this.getURLEncodedTitle();
			LogManager.logDebug("App.lookupByTitle: " + url); 
			HTTPRequest request; 
			if (WebAdminSettings.isLoggedIn()) {
				IConnectionProvider basicAuthConnection = new BasicAuthenticationConnectionProvider(WebAdminSettings.bugnetLogin, WebAdminSettings.bugnetPwd);
				request = new HTTPRequest(basicAuthConnection); 
			} else {
				request = new HTTPRequest(); 
			}			
			HTTPResponse response;
			String output = ""; 
			try {
				response = request.get(url);
				output = response.getString();	
			} catch (IOException e) {
				//Ignore error caused by not finding local app on bugnet.
			}
			if (!output.equals("")) {
				try {
					XmlNode root 		= XmlParser.parse(output);
					Iterator related_itr, authors_itr, modules_itr; 
					List related, authors, module_nodes;
					XmlNode author_node; 
					if (root.childExists("title")) {
						this.apiVersion 	= root.getChild("api_version").getValue();
						this.author 		= root.getChild("username").getValue();
						this.description 	= root.getChild("description").getValue(); 
						this.downloads 		= root.getChild("download_count").getValue(); 
						this.id 			= Integer.parseInt(root.getAttribute("id"));
						this.rating 		= root.getChild("rating").getValue(); 
						this.thumbnail 		= root.getChild("thumbnail").getAttribute("url");
						this.medium 		= root.getChild("medium").getAttribute("url");
						this.icon 			= root.getChild("icon").getAttribute("url"); 
						this.url 			= root.getChild("homepage").getAttribute("url"); 
						this.category 		= root.getChild("category").getAttribute("name");
						this.maturity		= root.getChild("maturity").getAttribute("name");
						related = root.getChild("related_programs").getChildren(); 
						related_itr = related.iterator();
						while (related_itr.hasNext()) {
							this.addPackage(((XmlNode)related_itr.next()).getAttribute("packages"));
						}
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
						this.createdAt = df.parse(root.getChild("created_at").getAttribute("value"));
						this.createdAtFormatted = root.getChild("created_at").getAttribute("formatted"); 
						authors = root.getChild("authors").getChildren(); 
						authors_itr = authors.iterator(); 
						while (authors_itr.hasNext()) {
							author_node = (XmlNode)authors_itr.next();
							if (author_node.getAttribute("admin").equals("true"))
								this.addAdmin(author_node.getAttribute("username")); 
							else 
								this.addCollaborator(author_node.getAttribute("username")); 
						}
						module_nodes = root.getChild("modules").getChildren();
						modules_itr = module_nodes.iterator();
						while (modules_itr.hasNext()) {
							this.addModule(((XmlNode)modules_itr.next()).getAttribute("name"));
						}							
					}
					root = null; 
				} catch (IOException e) {
					LogManager.logWarning(e.getMessage());
				} catch (ParseException e) {
					LogManager.logWarning(e.getMessage());
				}
			}
		}
	}
	
	public static AppResultManager lookup(String query, String packages, int page) 
	{
		AppResultManager r = new AppResultManager(query, packages, page);
		r.query(); 
		return r; 
	}
	
	public static AppResultManager lookupByTag(String tag, int page) {
		AppResultManager r = new AppResultManager(tag, page);
		r.query(); 
		return r;		
	}
	
	public static String getLocalPath(String name) {
		return LOCAL_PATH + "/" + name + ".jar"; 
	}

	public static String getModuleIcon(String module) {
		return getModuleIcon(module, "small");
	}
	
	public static String getModuleIcon(String module, String size) {
		return BUGNET_URL + "/images/bug_modules/" + size + "/" + module + ".gif";
	}
	
	public static String getAuthorURL(String login) {
		return BUGNET_URL + "/users/" + login;
	}
	
	public static boolean checkNetworkConnection() {
		boolean connected = false; 
		try {
			// check bugcommunity.com: check if this is reliable
			connected = Utils.checkInternetConnection(Inet4Address.getByName(COMMUNITY_URL), 80, 3000); 
		} catch (UnknownHostException e) {
			LogManager.logWarning(e.getMessage());
		} catch (IOException e) {
			LogManager.logWarning(e.getMessage());
		} catch (Exception e) {
			LogManager.logWarning(e.getMessage());
		}
		return connected; 
	}
	
	// from switch's AppSite
	private int pipe(InputStream in, OutputStream out) throws IOException 
	{
		byte[] buf = new byte[4096];
		int nread;
		int total = 0;
		while ((nread = in.read(buf)) > 0) {
			out.write(buf, 0, nread);
			total += nread;
		}
		buf = null;
		return total;
	}	

	
}
