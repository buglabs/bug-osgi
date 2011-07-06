package com.buglabs.app.bugdash2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.buglabs.util.xml.XmlNode;
import com.buglabs.util.xml.XmlParser;
import com.buglabs.util.simplerestclient.BasicAuthenticationConnectionProvider;
import com.buglabs.util.simplerestclient.HTTPRequest;
import com.buglabs.util.simplerestclient.HTTPResponse;
import com.buglabs.util.simplerestclient.IConnectionProvider;

/**
 * Maintains app search results from BUGnet 
 * @author akweon
 *
 */
public class AppResultManager {
	
	private List results; 
	private int size; 	// total result count 

	private String searchTerm; 
	private String packages; 
	private String tag; 
	private int currentPage; 
	private int pageSize = 10;
	
	public AppResultManager() {
		this.results = new ArrayList(); 
	}
	
	public AppResultManager(String tag, int page) {
		this.results = new ArrayList(); 
		this.searchTerm = ""; 
		this.packages = ""; 			
		this.tag = tag; 
		this.currentPage = page; 
	}
	
	public AppResultManager(String term, String packages, int page) {
		this.results = new ArrayList(); 
		this.searchTerm = term; 
		this.packages = packages;
		this.tag = "";
		this.currentPage = page; 
	}
	
	public List 	getResults() 	{ 	return results; }
	public int 		getSize() 		{ 	return size; 	}
	public int 		getPageSize() 	{ 	return pageSize; }
	public String 	getTag() 		{ 	return tag; }
	
	public void setSearchTerm(String searchTerm) 	{ 	this.searchTerm = searchTerm; }
	public void setPackages(String packages) 		{ 	this.packages = packages; }
	public void setCurrentPage(int currentPage) 	{ 	this.currentPage = currentPage; }
	public void setPageSize(int pageSize) 			{ 	this.pageSize = pageSize; }

	public void query() {
		this.results.clear(); 
		String xml = getFeedContent(); 
		if (!xml.equals("")) {
			XmlNode root, appNode;
			try {
				root = XmlParser.parse(xml);
				this.size = Integer.parseInt(root.getAttribute("size")); 
				List apps = root.getChildren();
				Iterator apps_itr = apps.iterator();
				Iterator itr_module_nodes; 
				
				int index = 0;
				List module_nodes; 
				App app;
				while (apps_itr.hasNext()) {
					appNode = (XmlNode)apps_itr.next();
					app = new App(  Integer.parseInt(appNode.getAttribute("web_id")),
									Integer.parseInt(appNode.getAttribute("version")),
									appNode.getChild("title").getValue(),
									appNode.getChild("username").getValue(),
									appNode.getChild("download_count").getValue(),
									appNode.getChild("description").getValue(),
									appNode.getChild("rating").getValue(),
									appNode.getChild("homepage").getAttribute("url"), 
									appNode.getChild("thumbnail").getAttribute("url"),
									appNode.getChild("icon").getAttribute("url"),
									appNode.getChild("api_version").getValue(),
									appNode.getChild("category").getAttribute("name"),
									appNode.getChild("maturity").getAttribute("name")
								 );
					module_nodes = appNode.getChild("modules").getChildren();
					itr_module_nodes = module_nodes.iterator();
					while (itr_module_nodes.hasNext()) {
						app.addModule(((XmlNode)itr_module_nodes.next()).getAttribute("name"));
					}					
					this.results.add(app); 
					index++; 
				}				
			} catch (IOException e) {
				LogManager.logWarning(e.getMessage());
			}
		}
	}
	
	/*
	 * Checks if valid credentials were provided with the call to programs in the webservice. 
	 * If so, a list of all public apps and private apps that the user has access to is returned. If not, only public apps are returned.
	 */
	private String getFeedContent() {
		String output = "";
		if(WebAdminSettings.isLoggedIn())
		{
			try {
				String feedUrl = App.BUGWS_URL+"/programs?search=" + this.searchTerm + "&packages=" + this.packages + "&tag=" + this.tag + "&current_page=" + this.currentPage;
				LogManager.logDebug("Get feed content: " + feedUrl);
				IConnectionProvider basicAuthConnection = new BasicAuthenticationConnectionProvider(WebAdminSettings.bugnetLogin, WebAdminSettings.bugnetPwd);
				HTTPRequest request = new HTTPRequest(basicAuthConnection); 
				HTTPResponse response = request.get(feedUrl);
				output = response.getString();

			} catch (MalformedURLException e) {
				LogManager.logWarning(e.getMessage());
			} catch (IOException e) {
				LogManager.logWarning(e.getMessage());
			}
		} else {
			try {
				String feedUrl = App.BUGWS_URL+"/programs?search=" + this.searchTerm + "&packages=" + this.packages + "&tag=" + this.tag + "&current_page=" + this.currentPage; 
				LogManager.logDebug("Get feed content: " + feedUrl);
				HTTPRequest request = new HTTPRequest(); 
				HTTPResponse response = request.get(feedUrl);
				output = response.getString();

			} catch (MalformedURLException e) {
				LogManager.logWarning(e.getMessage());
			} catch (IOException e) {
				LogManager.logWarning(e.getMessage());
			}			
		}
		return output; 
	}	
	
	
}
