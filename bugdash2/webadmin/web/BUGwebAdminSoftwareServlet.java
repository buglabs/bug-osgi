package webadmin.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webadmin.Activator;
import webadmin.WebAdminSettings;
import webadmin.controller.AdminControllerFactory;
import webadmin.controller.bugnet.BUGnetController;
import webadmin.controller.software.PackageNewUpdateController;
import webadmin.controller.software.PackageUpgradeController;

import com.buglabs.osgi.sewing.pub.SewingController;
import com.buglabs.osgi.sewing.pub.SewingHttpServlet;
import com.buglabs.osgi.sewing.pub.util.ControllerMap;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelRoot;

public class BUGwebAdminSoftwareServlet extends SewingHttpServlet {

	private static final long serialVersionUID = 981347371186050050L;
	
	public BUGwebAdminSoftwareServlet() {}

	public ControllerMap getControllerMap() {
		
		ControllerMap controllers = new ControllerMap();
		/* View installed packages */
		controllers.put("index", new index());
		controllers.put("display_installed_packages", AdminControllerFactory.getInstance().getPackageViewerController()); 
		controllers.put("introduce_package_update", new introducePackageUpdate()); 
		
		/* Upgrade packages */
		SewingController package_controller = AdminControllerFactory.getInstance().getUpgradePackagesController();
		
		controllers.put("update_packages", package_controller); 
		controllers.put("read_update_package", 
									AdminControllerFactory.getInstance().getReadPackageUpgradeBufferController(
									(PackageUpgradeController)package_controller));
		
		/* View new updates */
		controllers.put("check_new_updates", new displayNewUpdates()); 
		SewingController update_controller = AdminControllerFactory.getInstance().getPackageNewUpdateController();  
		controllers.put("start_checking_new_updates", update_controller); 
		controllers.put("read_new_updates", AdminControllerFactory.getInstance().getReadPackageNewUpdateBufferController(
											(PackageNewUpdateController)update_controller));
		
		/* Install package */
		controllers.put("install_package", AdminControllerFactory.getInstance().getPackageInstallController()); 
		
		/* Manage BUG apps */ 
		/* 
		- display a placeholder for two iframes 
		- display a list from BUGnet 
		- display a list of installed apps 
		*/
		controllers.put("apps", new displayApps());
		controllers.put("apps_from_bugnet", AdminControllerFactory.getInstance().getAppsBrowserController() ); 
		controllers.put("apps_installed", AdminControllerFactory.getInstance().getAppsManagerController() ); 
		controllers.put("app_viewer", AdminControllerFactory.getInstance().getAppViewerController() ); 
		controllers.put("apps_recommended", AdminControllerFactory.getInstance().getAppsRecommendedController() ); 
		return controllers;
	}
	
	public class index extends SewingController {
		public TemplateModelRoot get(RequestParameters params, 
				HttpServletRequest req, HttpServletResponse resp) {
			SimpleHash root = new SimpleHash();
			root.put("message", new SimpleScalar("index page"));
			return root;
		}
	}
	
	public class introducePackageUpdate extends SewingController {
		
		public String getTemplateName() { return "software_package_upgrade.fml"; }
		public TemplateModelRoot get(RequestParameters params, 
				HttpServletRequest req, HttpServletResponse resp) {
			SimpleHash root = new SimpleHash();
			root.put("message", new SimpleScalar("index page"));
			return root;
		}		
	}
	
	public class displayApps extends SewingController {
		
		public String getTemplateName() { return "software_apps.fml"; }
		public TemplateModelRoot get(RequestParameters params, 
				HttpServletRequest req, HttpServletResponse resp) {
			SimpleHash root = new SimpleHash();
			root.put("logged_in", WebAdminSettings.isLoggedIn());
			root.put("username", WebAdminSettings.bugnetLogin);
			return root; 
		}
		
		public TemplateModelRoot post(RequestParameters params,
	            HttpServletRequest req, HttpServletResponse resp)
		{
			String logout = params.get("btn_logout");
			if(logout != null && WebAdminSettings.isLoggedIn())
			{
				BUGnetController.logout(resp);		
			}
			
			TemplateModelRoot root = get(params, req, resp);
	        return root;
		}
	}
	
	public class displayNewUpdates extends SewingController {

		public String getTemplateName() { return "software_packages_new_updates.fml"; }
		public TemplateModelRoot get(RequestParameters params,
				HttpServletRequest req, HttpServletResponse resp) {
			return null; 
		}


		
	}
	
}