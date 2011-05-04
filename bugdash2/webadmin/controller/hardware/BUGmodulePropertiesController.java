package webadmin.controller.hardware;

import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webadmin.Activator;
import webadmin.controller.ApplicationController;

import com.buglabs.module.IModuleControl;
import com.buglabs.module.IModuleProperty;
import com.buglabs.module.MutableModuleProperty;
import com.buglabs.osgi.sewing.pub.util.RequestParameters;
import com.buglabs.util.StringUtil;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.TemplateModelRoot;

public class BUGmodulePropertiesController extends ApplicationController {
	
	public String getTemplateName() { return "hardware_bugmodule_properties.fml"; }
	
	public TemplateModelRoot get(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		
		int slot = Integer.valueOf(params.get("slot")).intValue(); // retrieve slot
		IModuleControl mc = Activator.getModule(slot); // retrieve module
		
		SimpleHash root = new SimpleHash();
		root.put("slot", slot);
		root.put("module_name", mc.getModuleName());
		root.put("properties_list", getPropertiesList(mc));
		return root;
	}
	
	public TemplateModelRoot post(RequestParameters params,
			HttpServletRequest req, HttpServletResponse resp) {
		
		int slot = Integer.valueOf(params.get("slot")).intValue(); // retrieve slot
		IModuleControl mc = Activator.getModule(slot); // retrieve module
		
		String[] names = StringUtil.split(params.get("name"), ",");
		if (names == null) System.out.println("names[] == null");
		else for (int i=0; i<names.length; i++) System.out.println("names["+i+"]:"+names[i]);
		
		String[] types = StringUtil.split(params.get("type"), ",");
		if (types == null) System.out.println("types[] == null");
		else for (int i=0; i<types.length; i++) System.out.println("types["+i+"]:"+types[i]);
		
		if (names != null) {
			for (int i=0 ; i<names.length; i++) {
				
				System.out.println("setting property...");
				System.out.println("i:" + i + " | name:" + names[i] + " | value:" + params.get(names[i]) + " | type:" + types[i]);
				
				mc.setModuleProperty( new MutableModuleProperty( names[i], params.get(names[i])) );
				System.out.println("property set");
			}
		}
		
		SimpleHash root = new SimpleHash();
		root.put("slot", slot);
		root.put("module_name", mc.getModuleName());
		root.put("properties_list", getPropertiesList(mc));
		return root;
	}
	
	private SimpleList getPropertiesList(IModuleControl mc) {
		SimpleList properties_list = new SimpleList();
		
		for (Iterator i = mc.getModuleProperties().iterator(); i.hasNext();) {
			IModuleProperty prop = (IModuleProperty) i.next();
			SimpleHash propHash = new SimpleHash();
			propHash.put("name", prop.getName());
			if (prop.getValue() != null) {
				propHash.put("value", prop.getValue().toString());
			}
			else {
				propHash.put("value", "null");
			}
			propHash.put("type", prop.getType());
			propHash.put("isMutable", prop.isMutable());
			
			properties_list.add(propHash);
		}
		
		return properties_list;
	}
	
}
