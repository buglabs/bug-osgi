package networkinggui;

import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;
import java.net.URL;
import java.util.Map;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Font;

import com.buglabs.application.IDesktopApp;
import com.buglabs.bug.module.lcd.pub.IModuleDisplay;
import com.buglabs.bug.networking.pub.INetworking;
import com.buglabs.util.LogServiceUtil;
import com.buglabs.application.ServiceTrackerHelper;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;		
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import org.osgi.framework.Bundle;

public class Activator implements BundleActivator, IDesktopApp, WindowListener, ServiceTrackerCustomizer {
	private static LogService logger = null;
	private BundleContext context;
	private ServiceRegistration sr;
    private IModuleDisplay display;
    private WifiAccessPointPicker wifiAccessPointPicker;
    private NetworkSettingsEditor networkSettingsEditor;
    private DeviceActivator deviceActivator;
    private JFrame frame;
    private INetworking networking;
    
    private final String MENU_ITEMS[] = {};

	private static final String [] services = {		
		IModuleDisplay.class.getName(),		
		INetworking.class.getName(),		
	};	
	private ServiceTracker serviceTracker;

	public static void setUIFont (FontUIResource f) {
		
		for (Object key : UIManager.getDefaults().keySet()) { 
	    	Object value = UIManager.get(key);
	    	if (value instanceof javax.swing.plaf.FontUIResource) {
	    		UIManager.put (key, f);
	    	}
	    }

		List lookAndFeelSettings = new LinkedList();
		for (Object key : new HashSet(UIManager.getLookAndFeelDefaults().keySet())) { 
	    	Object value = UIManager.get(key);
	    	if (value instanceof javax.swing.plaf.FontUIResource) {
	    		lookAndFeelSettings.add(key);
	    	}
	    }
		
		for (Object key : lookAndFeelSettings) {
			UIManager.put(key, f);
		}
	}
	
	public Activator() {
		super();
		setUIFont(new FontUIResource("Liberation Sans", Font.BOLD, 12));
	}
	
	public void start(BundleContext ctx) throws Exception {
		this.context = ctx;
		logger = LogServiceUtil.getLogService(context);
		
		Activator.logDebug("starting tracker");
		
		serviceTracker = ServiceTrackerHelper.openServiceTracker(context, services, new ServiceTrackerHelper.ManagedInlineRunnable() {
			
			@Override
			public void run(Map<Object, Object> services) {	
				display = (IModuleDisplay) services.get(IModuleDisplay.class.getName());
				networking = (INetworking) services.get(INetworking.class.getName());			
				// Warning, this method will be called from within the same thread as the OSGi framework.  Long running operations should be avoided here.
				// Implement application here.
								
				wifiAccessPointPicker = new WifiAccessPointPicker(networking, context);
				networkSettingsEditor = new NetworkSettingsEditor(networking, context);
				deviceActivator = new DeviceActivator(networking, context);
				
				registerDesktopApp();
			}
			
			@Override
			public void shutdown() {
				unregisterDesktopApp();
			}
		});
		

	}

	public void stop(BundleContext context) throws Exception {
		if (sr != null) {
			sr.unregister();
			sr = null;
		}
		if (frame != null) {
			frame.dispose();
			frame = null;
		}
	}
	
	public void click() {
		Activator.logDebug("clicked on networking GUI icon");
		showFullGUI();
	}
	
	private void showFullGUI() {
		if (frame != null) {
			frame.dispose();
		}
		JTabbedPane tp = new JTabbedPane();
		
		tp.add("Wifi", wifiAccessPointPicker.getPanel());
		
		tp.add("Config", networkSettingsEditor.getPanel());


		tp.add("Enable", deviceActivator.getPanel());

		JScrollPane scrollPane = new JScrollPane(tp);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(30, Integer.MAX_VALUE));
		
		showFrame(scrollPane);
	}
	
	public URL getIcon(int width, int height, int depth) {
		return context.getBundle().getResource("resources/icon.png");
	}
	
	public String[] getMenuItems() {
		return MENU_ITEMS;
	}

	public void menuSelected(String item) {
	}
	
	private void showFrame(Component component) {
		if (frame != null) {
			frame.dispose();
		}
		frame = new JFrame();
		// frame.setLayout(null);
		frame.add(component);
		frame.addWindowListener(this);
		frame.pack();
		frame.show();
	}
	
	public void windowActivated(WindowEvent arg0) {
	}

	public void windowClosed(WindowEvent arg0) {
	}

	public void windowClosing(WindowEvent arg0) {
		if (frame != null) {
			frame.dispose();
			frame = null;
		}
	}
	
	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}
	
	
	public IDesktopApp getDesktopApp() {
		return this;
	}
	
	
	public void registerDesktopApp() {
		if (sr == null) {
			sr = context.registerService(IDesktopApp.class.getName(), this, null);
		}
	}
	
	public void unregisterDesktopApp() {
		if (sr != null) {
			sr.unregister();
			sr = null;
		}
	}
	
	public BundleContext getContext() {
		return context;
	}
	
	public Bundle getBundle() {
		return context.getBundle();
	}

	public String getName() {
		return "Example";
	}

	public Object addingService(ServiceReference reference) {
		Object svc = context.getService(reference);
		return svc;
	}

	public void modifiedService(ServiceReference reference, Object service) {

	}

	public void removedService(ServiceReference reference, Object service) {
		if (sr != null) {
			sr.unregister();
			sr = null;
		}
	}
	
	/**
	 * @return an instance of the LogService.
	 */
	public static LogService getLogger() {
		return logger;
	}
	
	public static void log(int level, String msg) {
		getLogger().log(level, msg);
	}
	
	public static void logError(String msg) {
		log(LogService.LOG_ERROR, msg);
	}

	public static void logWarning(String msg) {
		log(LogService.LOG_WARNING, msg);
	}
	
	public static void logDebug(String msg) {
		log(LogService.LOG_INFO, msg);
	}
	
	public static void logInfo(String msg) {
		log(LogService.LOG_INFO, msg);
	}
}