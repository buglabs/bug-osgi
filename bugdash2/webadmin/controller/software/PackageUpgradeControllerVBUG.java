package webadmin.controller.software;

public class PackageUpgradeControllerVBUG extends PackageUpgradeController {
	
	public CommandItem[] getCommands() {
		// place a series of commands you wish to run 
		CommandItem[] commands = {
				new CommandItem("echo \"Getting ready\"", 
										"Getting ready", 0),
				new CommandItem("echo \"upgrade done\"", "Done", 100)										
			 }; 		
		return commands;
	}
}
