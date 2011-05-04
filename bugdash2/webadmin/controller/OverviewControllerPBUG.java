package webadmin.controller;

public class OverviewControllerPBUG extends OverviewController {

	public String getBuildInfoCommand() {
		return "cat /etc/buildinfo";
	}

	public String getDiskUsageCommand() {
		return "df -h";
	}
	
	public String getKernelInfoCommand() {
		return "cat /proc/version";
	}

	public String getBatteryInfoPath() {
		return "/sys/class/power_supply/bq27500-0/capacity";
	}

	public String getHostName() {
		return "cat /etc/hostname";
	}	

}
