package webadmin.controller.software;

public class PackageViewerControllerPBUG extends PackageViewerController {

	public String getIpkgStatus() {
		return "/usr/lib/ipkg/status"; 
	}

}
