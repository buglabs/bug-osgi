package com.buglabs.app.bugdash2.controller.system;


public class LogControllerPBUG extends LogController {
	
	public String getOSGiLogPath() {
		return "/var/log/felix.log";
	}
	
	public String getMessagesPath() {
		return "/var/log/messages";
	}
	
}
