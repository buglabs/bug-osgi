package com.buglabs.application;

import java.net.URL;

/**
 * A service that can be utilized to create a Desktop UI like experience for OSGi
 * bundles.  Loosely based on the ConciergeGUI bundle at http://concierge.sourceforge.net
 * 
 * @author kgilmer
 *
 */
public interface IDesktopApp {
	
	/**
	 * Application icon has been clicked.  Applications will typically want to
	 * create or set focus to their primary UIs in here.
	 */
	public void click();
	
	/**
	 * A custom icon can be displayed.  
	 * If null returned a default image will be used.
	 * @return URL to an icon to be displayed
	 */
	public URL getIcon(int width, int height, int depth);
	
	/**
	 * Apps can optionally contribute a flat set of menu items
	 * that are displayed in the App Icon view.
	 * 
	 * IDesktopApp.menuSelected() will be called with the name of 
	 * item when user selects that entry.
	 * 
	 * @return a list of menu items for this application.
	 */
	public String[] getMenuItems();
	
	/**
	 * Call back for when an item is selected by user.
	 * @param item
	 */
	public void menuSelected(String item);
}
