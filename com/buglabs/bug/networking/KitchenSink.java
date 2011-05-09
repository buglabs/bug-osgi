/*******************************************************************************
 * Copyright (c) 2011 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package com.buglabs.bug.networking;


import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Vector;

import org.freedesktop.DBus;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import net.connman.Manager;
import net.connman.Struct1;
import net.connman.Service;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.buglabs.bug.networking.pub.IAccessPoint;
import com.buglabs.bug.networking.pub.IAccessPointSecurity;
import com.buglabs.bug.networking.pub.IAccessPointsEvent;
import com.buglabs.bug.networking.pub.IAccessPointsListener;
import com.buglabs.bug.networking.pub.IActivationEvent;
import com.buglabs.bug.networking.pub.IActivationListener;
import com.buglabs.bug.networking.pub.IConnectionState;
import com.buglabs.bug.networking.pub.IIPv4Settings;
import com.buglabs.bug.networking.pub.IIPv4SettingsMethod;
import com.buglabs.bug.networking.pub.INetworking;
import com.buglabs.bug.networking.pub.INetworkingDevice;
import com.buglabs.bug.networking.pub.ISettingsEvent;
import com.buglabs.bug.networking.pub.ISettingsListener;
import com.buglabs.bug.networking.pub.IStateEvent;
import com.buglabs.bug.networking.pub.IStateListener;

public class KitchenSink implements INetworking {
	private static KitchenSink instance;
	private DBusConnection bus;
	private Manager manager;
	private BundleContext context;

	private List<Service> allServices = new LinkedList<Service>();
	List<IAccessPoint> accessPoints = new LinkedList<IAccessPoint>();
	
	private INetworkingDevice ethernetDevice;
	private INetworkingDevice wifiDevice;
	
	private ManagerObserver managerObserver;
	private ServiceObserver serviceObserver;

	private List<IStateListener> stateListeners;
	private List<IActivationListener> activationListeners;
	private List<ISettingsListener> settingsListeners;
	
	private String SECURITY_STRING_NONE = "none";
	private String SECURITY_STRING_WEP= "wep";
	private String SECURITY_STRING_WPA = "wpa";
	
	private class ManagerObserver implements DBusSigHandler<Manager.PropertyChanged> {
		private ServiceTracker activationListenerTracker;
		private boolean ethernetEnabled;
		private boolean wifiEnabled;
		
		private ServiceTracker accessPointsListenerTracker;

		private String ENABLED_TECHNOLOGIES = "EnabledTechnologies";
		private String ENABLED_TECHNOLOGIES_WIFI = "wifi";
		private String ENABLED_TECHNOLOGIES_ETHERNET = "ethernet";
		private String SERVICES = "Services";

		public ManagerObserver() {
			ethernetEnabled = getEthernetDevice().isEnabled();
			wifiEnabled = getWifiDevice().isEnabled();

			activationListenerTracker = new ServiceTracker(context, IActivationListener.class.getName(), null);
			activationListenerTracker.open();
			
			accessPointsListenerTracker = new ServiceTracker(context, IAccessPointsListener.class.getName(), null);
			accessPointsListenerTracker.open();
		}
		
		public void handle(final Manager.PropertyChanged pc) {
			if (pc.a.compareTo(ENABLED_TECHNOLOGIES) == 0) {
				Activator.logDebug("enabled technologies changed");
				boolean isEthernetEnabled = false;
				boolean isWifiEnabled = false;
				for (String enabledTechnology : (Vector<String>) pc.b.getValue()) {
					if (enabledTechnology.compareTo(ENABLED_TECHNOLOGIES_ETHERNET) == 0) {
						isEthernetEnabled = true;
					} else if (enabledTechnology.compareTo(ENABLED_TECHNOLOGIES_WIFI) == 0) {
						isWifiEnabled = true;
					}
				}
				if (ethernetEnabled != isEthernetEnabled) {
					IActivationEvent activationEvent = new ActivationEvent(DeviceType.Ethernet(), isEthernetEnabled);
					Activator.logDebug("looking for ethernet activation listeners");
					Object als [] = activationListenerTracker.getServices();
					if (als != null) {
						for (Object al : als) {
							Activator.logDebug("found an ethernet activation listener");
							((IActivationListener) al).activationChanged(activationEvent);
						}
					}
					ethernetEnabled = isEthernetEnabled;
				}
				
				if (wifiEnabled != isWifiEnabled) {
					IActivationEvent activationEvent = new ActivationEvent(DeviceType.Wifi(), isWifiEnabled);
					Object als [] = activationListenerTracker.getServices();
					if (als != null) {
						for (Object al : als) {
							((IActivationListener) al).activationChanged(activationEvent);
						}
					}
					wifiEnabled = isWifiEnabled;
				}
			}
		}
	}
	
	private class ServiceObserver implements DBusSigHandler<Service.PropertyChanged> {
		private ServiceTracker stateListenerTracker;
		private ServiceTracker settingsListenerTracker;
		private final String PROPERTY_STATE = "State";
		private final String PROPERTY_IPV4= "IPv4";
		private final String PROPERTY_NAMESERVERS = "Nameservers";
		private final String PROPERTY_SEARCHDOMAINS = "Domains";
		
		private final String STATE_ASSOCIATION = "association";
		private final String STATE_CONFIGURATION = "configuration";
		private final String STATE_READY = "ready";
		private final String STATE_ONLINE = "online";
		private final String STATE_DISCONNECT = "disconnect";
		private final String STATE_FAILURE = "failure";
		private final String STATE_IDLE = "idle";
		
		private final HashMap<String, ConnectionState.State> stringToState = new HashMap<String, ConnectionState.State>() {{
			put(STATE_ASSOCIATION, ConnectionState.State.ASSOCIATION);
			put(STATE_CONFIGURATION, ConnectionState.State.CONFIGURATION);
			put(STATE_READY, ConnectionState.State.READY);
			put(STATE_ONLINE, ConnectionState.State.ONLINE);
			put(STATE_DISCONNECT, ConnectionState.State.DISCONNECT);
			put(STATE_FAILURE, ConnectionState.State.FAILURE);
			put(STATE_IDLE, ConnectionState.State.IDLE);
		}};
		
		public ServiceObserver() {
			settingsListenerTracker = new ServiceTracker(context, ISettingsListener.class.getName(), null);
			settingsListenerTracker.open();
			
			stateListenerTracker = new ServiceTracker(context, IStateListener.class.getName(), null);
			stateListenerTracker.open();
		}
		public void handle(final Service.PropertyChanged pc) {
			Activator.logDebug("Service property changed:[" + pc.getPath() + "] " + pc.a + ":" + pc.b.getValue());
			String serviceNamePathParts[] = pc.getPath().split("/");
			String serviceName = serviceNamePathParts[serviceNamePathParts.length - 1];
			if (pc.a.compareTo(PROPERTY_STATE) == 0) {
				String stateName = (String) pc.b.getValue();
				DeviceType stateDeviceType = null; 
				if (serviceName.startsWith("wifi")) {
					stateDeviceType = DeviceType.Wifi();
				} else if (serviceName.startsWith("ethernet")) {
					stateDeviceType = DeviceType.Ethernet();
				}
				
				if (stateDeviceType != null) {
					IConnectionState state = new ConnectionState(stringToState.get(stateName));
					if (state != null) {
						IStateEvent stateEvent = new StateEvent(state, stateDeviceType); 
						Object sls[] =  stateListenerTracker.getServices();
						if (sls != null) {
							for (Object sl : sls) {
								((IStateListener) sl).stateChanged(stateEvent);
							}
						}
					}
				}
			} else if (pc.a.compareTo(PROPERTY_IPV4) == 0 ||
					pc.a.compareTo(PROPERTY_NAMESERVERS) == 0 ||
					pc.a.compareTo(PROPERTY_SEARCHDOMAINS) == 0) {
				DeviceType deviceType = null;
				if (serviceName.startsWith("wifi")) {
					deviceType = DeviceType.Wifi();
				} else if (serviceName.startsWith("cable")) {
					deviceType = DeviceType.Ethernet();
				}
				if (deviceType != null) {
					ISettingsEvent settingsEvent = new SettingsEvent(deviceType);
					Object sls[] = settingsListenerTracker.getServices();
					if (sls != null) {
						for (Object sl : settingsListenerTracker.getServices()) {
							((ISettingsListener) sl).settingsChanged(settingsEvent);
						}
					}
				}
			}
		}
	}
	
	private KitchenSink(DBusConnection bus, BundleContext context) {
		this.bus = bus;
		this.context = context;
		
		stateListeners = new LinkedList<IStateListener>();
		activationListeners = new LinkedList<IActivationListener>();
		settingsListeners = new LinkedList<ISettingsListener>();
		
		try {
			manager = bus.getRemoteObject("net.connman", "/", Manager.class);
		} catch (Exception e) {
			// TODO: Handle this exception.
			Activator.logError("trouble creating manager");
		}
		
		managerObserver = new ManagerObserver();
		serviceObserver = new ServiceObserver();
	}
	
	public static void createInstance(DBusConnection bus, BundleContext context) {
		if (instance == null) {
			instance = new KitchenSink(bus, context);
		}
	}
	
	public static KitchenSink getInstance() {
		return instance;
	}

	public void scanAccessPoints() {
		manager.RequestScan("wifi");
	}
	
	private void resetAccessPoints() {
		synchronized(accessPoints) {
			accessPoints = new LinkedList<IAccessPoint>();
		}
	}
	
	public List<IAccessPoint> getAccessPoints() {
		synchronized(accessPoints) {
			if (accessPoints.isEmpty()) {
				for (Service service : getServices()) {
					try {
						String type = (String) service.GetProperties().get("Type").getValue();
						if (type != null && (type.compareTo("wifi") == 0)) {
							service.GetProperties();
							Activator.logDebug("adding service: " + service);
							accessPoints.add(new AccessPoint((Service) service));
						}
					} catch (Exception e) {
						// TODO: Add better exception handling here.
						Activator.logDebug("service no longer exists: " + service);
					}
				}
			}
		}
		return accessPoints;
	}
	
	public void connectToAccessPoint(String ssid, IAccessPointSecurity security, String passphrase) {
		/*
		 * From the Connman DBus documentation
		 * (http://moblin.org/documentation/moblin-sdk/coding-tutorials/developers-introduction-connman)
		 * here's what we need to establish a connection:
		 * 
		 * {
		 *  'Type':'wifi',
		 *  'Mode':'managed',
		 *  'SSID':'ssid',
		 *  'Security':'WEP',
		 *  'Passphrase':'secret'
		 *}
		 */
		Activator.logDebug("Connecting to hidden AP ...");
		HashMap<String, Variant> serviceDict = new HashMap<String, Variant>();
		serviceDict.put("Type", new Variant<String>("wifi"));
		serviceDict.put("Mode", new Variant<String>("managed"));
		serviceDict.put("SSID", new Variant<String>(ssid));
		serviceDict.put("Security", new Variant<String>(securityStringFromSecurity(security)));
		serviceDict.put("Passphrase", new Variant<String>(passphrase));
		Activator.logDebug("Connecting to AP '" + ssid +"' with security '" + securityStringFromSecurity(security) + "' and passphrase '" + passphrase + "'");
		manager.ConnectService(serviceDict);
	}
	
	public INetworkingDevice getEthernetDevice() {
		if (ethernetDevice == null) {
			ethernetDevice = new NetworkDevice(manager, Technology.ETHERNET_TECHNOLOGY);
		}
		return ethernetDevice;
	}

	public INetworkingDevice getWifiDevice() {
		if (wifiDevice == null) {
			wifiDevice = new NetworkDevice(manager, Technology.WIFI_TECHNOLOGY);
		}
		return wifiDevice;
	}
	
	public IIPv4Settings getEthernetIPv4Settings() {
		return getConnectedServiceSettings(Technology.ETHERNET_TECHNOLOGY, "eth0");
	}
	
	public IIPv4Settings getWifiIPv4Settings() {
		return getConnectedServiceSettings(Technology.WIFI_TECHNOLOGY, "wlan0");
	}
	
	public void setEthernetIPv4Settings(IIPv4Settings settings) {
		applyIPv4Settings(settings, Technology.ETHERNET_TECHNOLOGY);
	}
	
	public void setWifiIPv4Settings(IIPv4Settings settings) {
		applyIPv4Settings(settings, Technology.WIFI_TECHNOLOGY);
	}
	
	public IAccessPointSecurity accessPointSecurityFactory() {
		return new AccessPointSecurity();
	}
	
	public IIPv4SettingsMethod IPv4SettingsMethodFactory() {
		return new IPv4SettingsMethod();
	}

	public void setup() {
		addObservers();
	}
	
	public void teardown() {
		removeObservers();
	}
	
	public void addStateListener(IStateListener l) {
		stateListeners.add(l);
	}
	
	public void removeStateListener(IStateListener l) {
		stateListeners.remove(l);
	}
	
	public void addActivationListener(IActivationListener l) {
		activationListeners.add(l);
	}
	
	public void removeActivationListener(IActivationListener l) {
		activationListeners.remove(l);
	}
	
	public void addSettingsListener(ISettingsListener l) {
		settingsListeners.add(l);
	}
	
	public void removeSettingsListener(ISettingsListener l) {
		settingsListeners.remove(l);
	}
	
	private void addObservers() {
		addManagerObserver();
		addServiceObserver();
	}
	
	private void removeObservers() {
		removeManagerObserver();
		removeServiceObserver();
	}
	
	private void addManagerObserver() {
		try {
			bus.addSigHandler(Manager.PropertyChanged.class, managerObserver);
		} catch (DBusException e) {
			// TODO Throw a better exception.
		}
	}
	
	private void addServiceObserver() {
		try {
			bus.addSigHandler(Service.PropertyChanged.class, serviceObserver);
		} catch (DBusException e) {
			// TODO Throw a better exception.
		}
	}
	
	private void removeManagerObserver() {
		try {
			Activator.logDebug("removing manager observer");
			bus.removeSigHandler(Manager.PropertyChanged.class, managerObserver);
			Activator.logDebug("removed manager observer");
		} catch (DBusException e) {
			Activator.logError("exception removing manager observer");
			// TODO Throw a better exception.
		}
	}
	
	private void removeServiceObserver() {
		try {
			Activator.logDebug("removing service observer");
			bus.removeSigHandler(Service.PropertyChanged.class, serviceObserver);
			Activator.logDebug("removed service observer");
		} catch (DBusException e) {
			Activator.logError("exception removing service observer");
			// TODO Throw a better exception.
		}
	}
	
	private IIPv4Settings getConnectedServiceSettings(Technology technology, String interfaceName) {
		Activator.logDebug("getting connected service settings for " + interfaceName);
		Service connectedService = getConnectedService(technology, interfaceName);
		IIPv4Settings connectedServiceSettings;
		if (connectedService != null) {
			connectedServiceSettings = new IPv4Settings(connectedService); 
		} else {
			connectedServiceSettings = new IPv4UnconnectedSettings();
		}
		return connectedServiceSettings;
	}
	
	private Service getConnectedService(Technology technology, String interfaceName) {
		Service connectedService = null;
		Activator.logDebug("Trying to get connected interface.");
		for (Service service : getServices()) {
			try {
				
				String serviceTechnology = (String) service.GetProperties().get("Type").getValue();
				String serviceState = (String) service.GetProperties().get("State").getValue();
				Map m = (Map) service.GetProperties().get("Ethernet").getValue();
				Variant v = (Variant) m.get("Interface");
				String serviceInterfaceName = (String) v.getValue();
				Activator.logDebug("Interface: " + serviceInterfaceName + ", State: " + serviceState);
				if (serviceTechnology.compareTo(technology.typeString()) == 0 &&
						serviceInterfaceName.compareTo(interfaceName) == 0 &&
						(serviceState.compareTo("ready") == 0|| serviceState.compareTo("online") == 0)
						) {
					connectedService = service;
					break;
				}
			} catch (Exception e) {
				// TODO: enventually we need to take care of all of these GetProperties exceptions in one place.
			}
		}
		return connectedService;
	}
	
	private void resetServices() {
		synchronized (allServices) {
			allServices = new LinkedList<Service>();
		}
	}
	
	private void resetServices(Vector servicePaths) {
		synchronized (allServices) {
			allServices = new LinkedList<Service>();
			for (Object s : servicePaths) {
				allServices.add(getServiceByPath(s.toString()));
			}
		}
	}
	
	private List<Service> getServices() {
		Variant servicePaths = manager.GetProperties().get("Services");
		synchronized(allServices) {
			if (allServices.isEmpty()) {
				for (Object path : (Vector) servicePaths.getValue()) {
					try {
						Service service = (Service) bus.getRemoteObject("net.connman", path.toString() , Service.class);
						// Do this is case GetProperties doesn't exist on this service.
						service.GetProperties();
						allServices.add(service);
					} catch (Exception e) {
						// TODO: Add better exception handling here.
						Activator.logError("Problem examining " + path + " for inclusion in wifi services");
					}
				}
			}
		}
		return new LinkedList<Service>(allServices);
	}
	
	private Service getServiceByPath(String path) {
		Service service = null;
		try {
			service = (Service) bus.getRemoteObject("net.connman", path, Service.class);
		} catch (Exception e) {
			// TODO: Add better exception handling here.
			Activator.logError("Problem getting service " + path);
		}
		return service;
	}
	
	private String securityStringFromSecurity(IAccessPointSecurity security) {
		String securityString = "";
		if (security.isNone()) {
			securityString = SECURITY_STRING_NONE;
		} else if (security.isWEP()) {
			securityString = SECURITY_STRING_WEP;
		} else if (security.isWPA()) {
			securityString = SECURITY_STRING_WPA;
		} else {
			// TODO: We should probably throw an exception here.
		}
		return securityString;
	}
	
	private void applyIPv4Settings(IIPv4Settings settings, Technology technology) {
		if (technology == Technology.ETHERNET_TECHNOLOGY) {
			applyIPv4Settings(settings, technology, "eth0");
		} else if (technology == Technology.WIFI_TECHNOLOGY) {
			applyIPv4Settings(settings, technology, "wlan0");
		}
		
	}
	
	private void applyIPv4Settings(IIPv4Settings settings, Technology technology, String interfaceName) {
		Service service = getConnectedService(technology, interfaceName);
		IPv4Settings.applySettings(settings, service);
	}
}
