package com.buglabs.app.bugdash2.controller.software;

import com.buglabs.app.bugdash2.Package;

public class PackageUpgradeControllerPBUG extends PackageUpgradeController {
	
	public CommandItem[] getCommands() {
		/*
		 * This process is taken from jconnelly's BUGUpgrade 
		 * 
		 * 1. remove service-tracker
		 * "ipkg --force-depends remove service-tracker"
		 * 
		 * 2. ipkg update
		 * "ipkg update"
		 * 
		 * 3. install service-tracker
		 * "ipkg --force-depends install service-tracker"
		 * 
		 * 4. do actual upgrade
		 * "ipkg --force-overwrite upgrade -t /home/root/"
		 * 
		 * 5. install task-bug
		 * "ipkg --force-overwrite install task-bug -t /home/root/"
		 * 
		 * 6. echo \"-istart com.buglabs.osgi.sewing.jar\" >>  /usr/share/java/init.xargs
		 * This is a workaround until sewing becomes part of BUG rootfs
		 * 
		 * 7. Deleting storage directory and reboot happens after a user gives confirmation
		 * 
		 * UPDATE: 
		 * 2010-01-07 AK 	Added fbprogress to be removed; removed adding sewing to init.xargs
		 * 
		 */
		String cmd = Package.getIpkgCommand(); 
		CommandItem[] commands = {
									new CommandItem(cmd + " --force-depends remove fbprogress service-tracker", 
															"Clean up before starting", 0),
									new CommandItem(cmd + " update", 
															"Update list of available packages", 20),
									new CommandItem(cmd + " --force-depends install service-tracker fbprogress", 
															"Install service tracker", 30),
									new CommandItem("/bin/sh -c yes | ipkg --force-overwrite upgrade -t /home/root/", 
															"Upgrade all installed packages to latest version", 50),
									new CommandItem("/bin/sh -c yes | ipkg --force-overwrite install task-bug -t /home/root/", 
															"Install task-bug", 90),
									new CommandItem("echo \"upgrade done\"", "Done", 100)
								 }; 
		return commands;
	}	

}
