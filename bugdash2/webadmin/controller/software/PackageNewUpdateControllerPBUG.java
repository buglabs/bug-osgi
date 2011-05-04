package webadmin.controller.software;

public class PackageNewUpdateControllerPBUG extends PackageNewUpdateController {

	public String getCommand() {
		return webadmin.Package.UPDATE_AND_UPGRADE_COMMAND;
	}

}
