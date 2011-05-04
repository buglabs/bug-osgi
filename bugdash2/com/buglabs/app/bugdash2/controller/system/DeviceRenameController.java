package com.buglabs.app.bugdash2.controller.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.app.bugdash2.ShellUtil;
import com.buglabs.app.bugdash2.controller.ApplicationController;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import com.buglabs.util.StringUtil;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

/**
 * @author Jeff Sinckler
 * 
 * UPDATE:
 * 	2010-07-30 AK modified POST so that it can output in json format 
 * 
 */
public class DeviceRenameController extends ApplicationController {
	private String 	myTemplate;
	
	public String getTemplateName() { return this.myTemplate; }
	
	public TemplateModelRoot get(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		this.myTemplate = "system_rename_device.fml";
		SimpleHash root = new SimpleHash(); 
		root.put("hostname", ShellUtil.getSimpleScalar("cat /etc/hostname")); //gets the hostname from /etc/hostname and puts it in the hash
		return root;
	}

	/**
	 * Obtains the user-inputted name and updates the device's name appropriately.
	 * @author Jeff Sinckler
	 */
	public TemplateModelRoot post(RequestParameters params, HttpServletRequest req, HttpServletResponse resp) {
		boolean use_json = params.get("format") != null && params.get("format").equals("json"); 
		this.myTemplate = (use_json) ? "message.fml" : "system_rename_device.fml"; 
        boolean errorDetected = false;
		
		//get the name that was submitted in the form
        String newName = params.get("inp_bugname");
        //get the original hostname of the BUG
        String oldName = ShellUtil.getSimpleScalar("cat /etc/hostname").toString();
        //removes an extra newline that comes from cat output
        oldName = oldName.substring(0, oldName.length() - 1);
        
        //get the hostname file that we need into a simplescalar using the shellutil
        String hostnameFile = ShellUtil.getSimpleScalar("cat /etc/hostname").toString();
        //removes an extra newline that comes from cat output
        hostnameFile = hostnameFile.substring(0, hostnameFile.length() - 1);
        //replace the old hostname file with the new hostbname from user input
        //and write the new file
        String newHostnameFile = StringUtil.replace(hostnameFile, oldName, newName);
        ShellUtil.getSimpleScalar("echo \"" + newHostnameFile + "\" > /etc/hostname").toString();
        //hostnameCheck used to ensure that the UNIX write process didn't wrongly interfere with anything
        String hostnameCheck = ShellUtil.getSimpleScalar("cat /etc/hostname").toString();
        //removes an extra newline that comes from cat output
        hostnameCheck = hostnameCheck.substring(0, hostnameCheck.length() - 1);
                
        //get the hosts file for modification
        //String hostsFile = ShellUtil.getSimpleScalar("cat /etc/hosts").toString();
        String hostsFile = ShellUtil.getSimpleScalar("cat /etc/hosts").toString();
        //get the index of the second new line
        int newLineIndex1 = hostsFile.indexOf("\n");
        int newLineIndex2 = hostsFile.substring(newLineIndex1+1).indexOf("\n");
        newLineIndex1 += newLineIndex2 + 1;
        String hostsFile2;
        if(newLineIndex1 != (hostsFile.length() - 1)) {
        	hostsFile2 = hostsFile.substring(newLineIndex1+1);
        } else {
        	hostsFile2 = "";
        }
        //is there another new line AND another occurance of 127.0.0.1?
        if(hostsFile2.indexOf("\n") != -1 && hostsFile2.indexOf("127.0.0.1") != -1 && hostsFile.lastIndexOf(oldName) != -1)
        {
        	//then we need to modify!
        	//get the last index of the old hostname.
        	int index = hostsFile.lastIndexOf(oldName);
        	//replace that instance with the new name
        	hostsFile = hostsFile.substring(0, index) + newName + hostsFile.substring(index + oldName.length(), hostsFile.length());
        	String cmd = "echo \"" + hostsFile.substring(0, hostsFile.length() - 1) + "\" > /etc/hosts";
            ShellUtil.getSimpleScalar(cmd);
        } else {
        	//we need to append!
        	//form the line to add
        	String addon = "127.0.0.1\t" + newName + "\n";
        	//run the command that will add it to the end.
        	String cmd = "echo \"" + addon + "\" >> /etc/hosts";
        	ShellUtil.getSimpleScalar(cmd);
        }
        
        //get the hosts file again after being written to. Used to error check
        //String hostsCheck = ShellUtil.getSimpleScalar("cat /etc/hosts").toString();
        
        //check the hosts file to ensure that it was properly updated
        /*if(!checkHosts(oldName, newName, hostsFile.toLowerCase(), hostsCheck))
        {
        	errorDetected = true;
        }
        
        //check to see if the hostname file has been correctly changed
        //if they are not equal...
        if(hostnameCheck.compareTo(newName) != 0)
        {
            //something went wrong, so revert and abort.
            errorDetected = true;
           
        }*/
       
    	TemplateModelRoot root; 
    	if (use_json)
    		root = new SimpleHash(); 
    	else 
    		root = get(params, req, resp);
    	
        if(errorDetected == false)
        {
            //make sure we set output to blank when we call the get method
            root.put("message", new SimpleScalar("Bug successfully renamed!"));
            return root;
        }
        //error situation: revert both files back to the original
        else
        {	
        	ShellUtil.getSimpleScalar("echo \"" + hostsFile + "\" > /etc/hosts").toString();
            ShellUtil.getSimpleScalar("echo \"" + hostnameFile + "\" > /etc/hostname").toString();
            root.put("message", new SimpleScalar("An error occurred, reverting names back to the original."));
            return root;
        }
    } 
	
	/**
	 * Used to ensure that the hosts file was properly changed.
	 * Checks that the new hostname is contained in the new hosts file, and checks that
	 * the original hosts file and the new hosts file are indeed different.
	 * @author Michael Angerville
	 */
	/*private boolean checkHosts(String oldName, String newName, String oldHostsFile, String newHostsFile)
	{
		//gets position of original name in original hosts file
		int oldIndex = oldHostsFile.indexOf(oldName.toLowerCase());
		//if not found, compare two hosts files, covers case of blank hosts files
		if(oldIndex == -1)
		{
			if(oldHostsFile.compareTo(newHostsFile) == 0)
			{
				return true;
			}
		}
		
		//gets position of new name in new hosts files
		int newIndex = newHostsFile.indexOf(newName);
		if(newIndex == -1)
			return false;

		//ensures hosts file was changed, flags an error if it wasn't changed, but the oldName and newName are different
		//this shows the case of the user attempting to change it, but the change failed
		if(oldHostsFile.compareTo(newHostsFile) == 0)
		{
				if(oldName.compareTo(newName) != 0)
				{
					return false;
				}
		}
		return true;
	}*/
}