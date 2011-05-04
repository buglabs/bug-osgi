package com.buglabs.app.bugdash2.controller.system;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.App;
import com.buglabs.app.bugdash2.LogManager;
import com.buglabs.app.bugdash2.Package;
import com.buglabs.app.bugdash2.ShellUtil;
import com.buglabs.app.bugdash2.TemplateHelper;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import com.buglabs.util.StringUtil;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelRoot;

/**
 * 
 * @author Mike Angerville
 * 
 * UPDATES:
 * 	2010-08-10 AK moved html snippets to template; modify package install to use Package#install
 *
 */
public class DateTimeController extends ApplicationController {
	
	private static SimpleDateFormat date_format = new SimpleDateFormat("MM-dd-yyyy HH:mm");
	private boolean installed = false;
	private boolean setTZ = false;
	
	public DateTimeController() { }
	
	public String getTemplateName() { return "system_display_datetime.fml"; }

	public TemplateModelRoot get(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		Calendar 	calendar	= Calendar.getInstance();
		SimpleHash 	root 		= new SimpleHash();
		SimpleList	zone_list 	= new SimpleList(); 
		
		SimpleScalar output = Package.info("tzdata"); 
		String tzDataInfo = (output != null) ? output.toString().trim() : "";
		File zoneinfoDir = new File("/usr/share/zoneinfo");
		boolean zoneinfoDirExists = zoneinfoDir.exists();
		if( !tzDataInfo.equals("") && tzDataInfo.indexOf("ERROR") == -1 && zoneinfoDirExists) {
			installed = true; setTZ = true;
		}
		else {
			setTZ = false; installed = false;
		}
		
		if(installed) {
			String zoneinfoDirectory = "/usr/share/zoneinfo/";

			String localtimeLink = ShellUtil.getSimpleScalar("ls -haltr /etc/localtime").toString().trim();
			String[] linkParts = StringUtil.split(localtimeLink, "-> ");
			String currentTZdir = "";
			if(linkParts.length > 1)
			{
				currentTZdir = StringUtil.replace(linkParts[1], zoneinfoDirectory, "");
			}
			String currentTZ = StringUtil.replace(currentTZdir, "/", " - ");
			currentTZ = StringUtil.replace(currentTZ, "_", " ");
			
			String[] zidItems = zoneinfoDir.list();
			Arrays.sort(zidItems);
			ArrayList timezoneDirs = new ArrayList();
			for(int i = 0; i < zidItems.length; i++)
			{
				File temp = new File(zoneinfoDirectory + zidItems[i]);
				if(temp.isDirectory())
				{
					timezoneDirs.add(temp);
				}
			}
		
			ArrayList timezones = new ArrayList();
			ListIterator tzdIterator = timezoneDirs.listIterator();
			while(tzdIterator.hasNext())
			{
				File temp = (File) tzdIterator.next();
				File[] tempArr = temp.listFiles();
				Arrays.sort(tempArr);
				for(int i = 0; i < tempArr.length; i++)
				{
					String timezone = temp.getName() + " - " + tempArr[i].getName();
					timezones.add(timezone);
				}
			} 
		
			ListIterator tzIterator = timezones.listIterator();
			while(tzIterator.hasNext())
			{
				String timezone = (String) tzIterator.next();
				timezone = StringUtil.replace(timezone, "_", " ");
				String[] tzParts = StringUtil.split(timezone, "-");
				tzParts[0] = tzParts[0].trim();
				tzParts[1] = tzParts[1].trim();
				tzParts[0] = StringUtil.replace(tzParts[0], " ", "_");
				tzParts[1] = StringUtil.replace(tzParts[1], " ", "_");
				SimpleHash timezoneHash = new SimpleHash();
				if(timezone.equals(currentTZ))
				{
					timezoneHash.put("selected", "selected");
				}
				else
					timezoneHash.put("selected", "");
				timezoneHash.put("value", tzParts[0] + "/" + tzParts[1]);
				timezoneHash.put("name", timezone);
				zone_list.add(timezoneHash);
			}
		}
		root.put("ipkg_installed", installed+"");
		root.put("zone_list", zone_list);
		root.put("current_datetime", date_format.format(calendar.getTime())); 
		return root; 
	}

	public TemplateModelRoot post(RequestParameters params, 
			HttpServletRequest req, HttpServletResponse resp) {
		String user_input 		= params.get("inp_datetime");
		String js_submit_status = "{category: '', message: '', detail: ''}";
		SimpleScalar result 	= new SimpleScalar();
		Date new_date;
		String zoneinfoDirectory = "/usr/share/zoneinfo/";

		String ipkg_name = "tzdata";
		boolean time_set = false; 

		if(params.get("chb_install_tzdata") != null && !installed && !setTZ) {
			if (App.checkNetworkConnection()) {
				result = Package.downloadExtra(); 	
				result = Package.update();
				result = Package.install(ipkg_name);
				try {
					if (result.getAsString().indexOf("error") > -1) {
						js_submit_status = "{category: 'error', message: 'There was a problem while installing " + ipkg_name + "', detail: '" + TemplateHelper.makeJSFriendly(result.getAsString()) + "'}";
					} else {
						js_submit_status = "{category: 'info', message: 'The package " + ipkg_name + " is installed successfully', detail: '" + TemplateHelper.makeJSFriendly(result.getAsString()) + "'}";
					}
				} catch (TemplateModelException e) {
					LogManager.logDebug(this.getClass().getName() + ": " + e.getMessage());
				}
			} else {
				result = new SimpleScalar("No network connection");
				js_submit_status = "{category: 'error', message: 'No network connection'}";
			}
		} else if(setTZ && installed) {
			//possibly check for null/existence first?
			String timezone = params.get("ddl_timezone");
			ShellUtil.getSimpleScalar( "ln -sf " + zoneinfoDirectory + timezone + " /etc/localtime" );
			time_set = true; 
			try {
				new_date = date_format.parse(user_input);
				SimpleDateFormat system_format = new SimpleDateFormat("MMddHHmmyyyy");
				String cmd = "date " + system_format.format(new_date); 
				result = ShellUtil.getSimpleScalar(cmd); 			
				js_submit_status = "{category: 'info', message: 'Date and time updated successfully'}";
			} catch (ParseException e) {
				js_submit_status = "{category: 'error', message: 'Please check your date', detail: '" + TemplateHelper.makeJSFriendly(e.getMessage()) + "'}";
				LogManager.logWarning(e.getMessage());
			}
		}
		TemplateModelRoot root = get(params, req, resp);
		root.put("time_set", new SimpleScalar(time_set+"")); 
		root.put("js_submit_status", new SimpleScalar(js_submit_status));
		return root;
	}
	

}
