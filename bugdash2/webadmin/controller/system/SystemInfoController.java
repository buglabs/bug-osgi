package webadmin.controller.system;

import webadmin.ShellUtil;
import webadmin.controller.ApplicationController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;


public class SystemInfoController extends ApplicationController {

	private static final String[][] COMMANDS = {{"kernel", 		"cat /proc/version"},
												{"build", 		"cat /etc/buildinfo"},
												{"drives", 		"df -h"},
												{"meminfo", 	"cat /proc/meminfo"},
												{"cpuinfo", 	"cat /proc/cpuinfo"},
												{"partitions",	"cat /proc/partitions"}};	
	
	public String getTemplateName() {
		return "system_display_system_info.fml";
	}

	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {

		TemplateModelRoot root = new SimpleHash();
		SimpleScalar result; 
		for (int i = 0; i < COMMANDS.length; i++) {
			result = ShellUtil.getSimpleScalar(COMMANDS[i][1]); 
			root.put(COMMANDS[i][0], result);
		}		
		return root; 
	}


	
}
