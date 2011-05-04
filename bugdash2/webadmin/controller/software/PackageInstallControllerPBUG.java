package webadmin.controller.software;

public class PackageInstallControllerPBUG extends PackageInstallController {

	public String getConfigFile() {
		return "/etc/ipkg.conf";
	}
	
	public String getConfigPath() {
		return "/etc/ipkg";
	}	

	public String getDestPath() {
		return "/home/root";
	}
	
	

}
