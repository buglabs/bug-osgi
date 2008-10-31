package com.buglabs.bug.module.lcd;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.buglabs.bug.jni.common.FCNTL_H;
import com.buglabs.bug.jni.lcd.LCDControl;
import com.buglabs.bug.module.lcd.pub.LCDModlet;
import com.buglabs.bug.module.pub.IModlet;
import com.buglabs.bug.module.pub.IModletFactory;

public class Activator implements BundleActivator, IModletFactory {

	private static final String DEV_NODE_CONTROL = "/dev/bmi_lcd_control";
	private BundleContext context;
	private ServiceRegistration sr;
	private LCDControl lcdcontrol;
	private static Activator instance;
	
	public Activator() {
		instance = this;
	}
	
	public void start(BundleContext context) throws Exception {
		this.context = context;
		sr = context.registerService(IModletFactory.class.getName(), this, null);
		String devnode_control = DEV_NODE_CONTROL;

		lcdcontrol = new LCDControl();
		if(lcdcontrol.open(devnode_control, FCNTL_H.O_RDWR) < 0) {
			throw new RuntimeException("Unable to open control device:" + devnode_control);
		}
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		lcdcontrol.close();
	}

	public static Activator getInstance() {
		synchronized (instance) {
			return instance;
		}
	}
	
	public LCDControl getLCDControl() { 
		return lcdcontrol;
	}

	public IModlet createModlet(BundleContext context, int slotId) {		
		return new LCDModlet(context, slotId, getModuleId());
	}
	public String getModuleId() {
		return (String) context.getBundle().getHeaders().get("Bug-Module-Id");
	}

	public String getName() {		
		return (String) context.getBundle().getHeaders().get("Bundle-SymbolicName");
	}

	public String getVersion() {		
		return (String) context.getBundle().getHeaders().get("Bundle-Version");
	}

	public BundleContext getBundleContext() {
		return context;
	}

}
