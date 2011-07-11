package networkinggui;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;

import java.awt.Component;
import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import org.osgi.framework.BundleContext;

import com.buglabs.bug.networking.pub.IIPv4Settings;
import com.buglabs.bug.networking.pub.IIPv4SettingsMethod;
import com.buglabs.bug.networking.pub.INetworking;
import com.buglabs.bug.networking.pub.ISettingsListener;
import com.buglabs.bug.networking.pub.ISettingsEvent;

public class NetworkSettingsEditor implements ItemListener, ActionListener, FocusListener {
	private JComboBox methodChoices;
	private JComboBox deviceChoices;
	private JTextField addressField;
	private JTextField netmaskField;
	private JTextField gatewayField;
	private JTextField nameserversField;
	private JTextField searchDomainsField;
	private JButton applyButton;
	
	private IIPv4Settings ethernetSettings;
	private IIPv4Settings wifiSettings;
	private IIPv4Settings selectedSettings;
	
	private INetworking ks;
	private BundleContext context;
	
	private JPanel panel;
	
	private final String DEVICE_ETHERNET = "ethernet";
	private final String DEVICE_WIFI = "wifi";
	
	private final String METHOD_MANUAL = "Manual";
	private final String METHOD_AUTO = "Auto";
	private final String METHOD_DISABLE = "Disable";

	private interface SettingsItem {
		public IIPv4Settings getSettings();
		public void setSettings(IIPv4Settings settings);
	}
	
	private class EthernetSettingsItem implements SettingsItem {
		public String toString() {
			return DEVICE_ETHERNET;
		}
		public IIPv4Settings getSettings() {
			return getEthernetSettings();
		}
		
		public void setSettings(IIPv4Settings settings) {
			ks.setEthernetIPv4Settings(settings);
		}
	}

	private class WifiSettingsItem implements SettingsItem {
		public String toString() {
			return DEVICE_WIFI;
		}
		public IIPv4Settings getSettings() {
			return getWifiSettings();
		}
		
		public void setSettings(IIPv4Settings settings) {
			ks.setWifiIPv4Settings(settings);
		}
	}

	private SettingsItem ethernetSettingsItem;
	private SettingsItem wifiSettingsItem;

	private abstract class MethodItem {
		private String label;
		public MethodItem(String label) {
			this.label = label;
		}
		public String toString() {
			return label;
		}
		public abstract void setMethod(IIPv4SettingsMethod method);
	}
	
	private MethodItem autoMethodItem;
	private MethodItem manualMethodItem;
	private MethodItem disabledMethodItem;
	
	public NetworkSettingsEditor(INetworking ks, BundleContext context) {
		this.ks = ks;
		this.context = context;
		deviceChoices = new JComboBox();
		methodChoices = new JComboBox();
		ethernetSettings = ks.getEthernetIPv4Settings();
		wifiSettings = ks.getWifiIPv4Settings();
		selectedSettings = ethernetSettings;
		
		addressField = new JTextField();
		netmaskField = new JTextField();
		gatewayField = new JTextField();
		nameserversField = new JTextField();
		searchDomainsField = new JTextField();
		
		ethernetSettingsItem = new EthernetSettingsItem();
		wifiSettingsItem = new WifiSettingsItem();
		
		autoMethodItem = new MethodItem(METHOD_AUTO) {
			public void setMethod(IIPv4SettingsMethod method) {
				method.setAuto();
			}
		};
		
		manualMethodItem = new MethodItem(METHOD_MANUAL) {
			public void setMethod(IIPv4SettingsMethod method) {
				method.setManual();
			}
		};
		
		disabledMethodItem = new MethodItem(METHOD_DISABLE) {
			public void setMethod(IIPv4SettingsMethod method) {
				method.setDisabled();
			}
		};
		
		ISettingsListener settingsListener = new ISettingsListener() {
			public void settingsChanged(final ISettingsEvent se) {
				final boolean isEthernet = se.getDeviceType().isEthernet();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Activator.logDebug("Settings changed on " + (isEthernet ? "ethernet" : "wifi"));
						getNewSettings();
						populateUI();
						updateUI();
					}
				});
			}
		};
		
		context.registerService(ISettingsListener.class.getName(), settingsListener, null);
	}
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (e.getSource() == deviceChoices) {
				selectedSettings = ((SettingsItem) deviceChoices.getSelectedItem()).getSettings();
				Activator.logDebug("selected device " + deviceChoices.getSelectedItem());
				Activator.logDebug("selected settings: " + selectedSettings);
				populateUI();
			}
	    	updateUI();
		}
    }
	
    public void focusGained(FocusEvent arg0) {
		Utilities.keyboardUp();
    }

    public void focusLost(FocusEvent arg0) {
    	Utilities.keyboardDown();
    }
	
	public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == applyButton) {
    		applySettings();
    	}
    	updateUI();
    }

	public JPanel getPanel() {
		if (panel == null) {
			createPanel();
			initializeUI();
		}
		selectedSettings = ethernetSettings;
		populateUI();
		updateUI();
		return panel;
	}
	
	private void createPanel() {
		panel = new JPanel();
		panel.setLayout(new GridLayout(7, 2));
	
		panel.add(getDeviceChoices());
		panel.add(getMethodChoices());
		panel.add(new JLabel("Address"));
		panel.add(addressField);
		panel.add(new JLabel("Mask"));
		panel.add(netmaskField);
		panel.add(new JLabel("Gateway"));
		panel.add(gatewayField);
		panel.add(new JLabel("Nameservers"));
		panel.add(nameserversField);
		panel.add(new JLabel("Search Domains"));
		panel.add(searchDomainsField);
		panel.add(applyButton = new JButton("Apply"));
		
		for (JTextField tf : new ArrayList<JTextField>() {{ 
			add(addressField);
			add(netmaskField);
			add(gatewayField);
			add(nameserversField);
			add(searchDomainsField);
			}} ) {
			tf.addFocusListener(this);
		}
		applyButton.addActionListener(this);
	}
	
	private void initializeUI() {
		deviceChoices.addItem(ethernetSettingsItem);
		deviceChoices.addItem(wifiSettingsItem);
		deviceChoices.addItemListener(this);
		
		methodChoices.addItem(manualMethodItem);
		methodChoices.addItem(autoMethodItem);
		methodChoices.addItem(disabledMethodItem);

		methodChoices.addItemListener(this);
	}
	
	private void populateUI() {
		new Thread() {
			public void run () {
				final MethodItem methodItem = methodToMethodItem(selectedSettings.getMethod());
				final String address = selectedSettings.getAddress();
				final String netmask = selectedSettings.getNetmask();
				final String gateway = selectedSettings.getGateway();
				final StringBuffer nameserversStringBuffer = new StringBuffer();
				for (String nameserver : selectedSettings.getNameservers()) {
					nameserversStringBuffer.append(nameserver);
					nameserversStringBuffer.append(",");
				}
				
				if (nameserversStringBuffer.length() > 0) {
					nameserversStringBuffer.deleteCharAt(nameserversStringBuffer.length() - 1);
				}
				
				final StringBuffer searchDomainStringBuffer = new StringBuffer();
				for (String searchDomain : selectedSettings.getSearchDomains()) {
					searchDomainStringBuffer.append(searchDomain);
					searchDomainStringBuffer.append(",");
				}
				
				if (searchDomainStringBuffer.length() > 0) {
					searchDomainStringBuffer.deleteCharAt(searchDomainStringBuffer.length() - 1);
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						methodChoices.setSelectedItem(methodItem);
						addressField.setText(address);
						netmaskField.setText(netmask);
						gatewayField.setText(gateway);
		
						nameserversField.setText(nameserversStringBuffer.toString());

						searchDomainsField.setText(searchDomainStringBuffer.toString());
					}
				});
			}
		}.start();
	}
	
	private void updateUI() {
		boolean enableFields = (methodChoices.getSelectedItem() == manualMethodItem);
		Activator.logDebug("Enable Fields:" + enableFields);
		for (Component tf : new ArrayList<JTextField>() {{ 
			add(addressField);
			add(netmaskField);
			add(gatewayField);
			add(nameserversField);
			add(searchDomainsField);
			}} ) {
			tf.setEnabled(enableFields);
		}
	}
	
	private JComboBox getDeviceChoices() {
		return deviceChoices;
	}
	
	private JComboBox getMethodChoices() {
		return methodChoices;
	}
	
	private void applySettings() {
		((MethodItem) methodChoices.getSelectedItem()).setMethod(selectedSettings.getMethod());
		if (selectedSettings.getMethod().isAuto()) {
			Activator.logDebug("Auto");
		} else if (selectedSettings.getMethod().isDisabled()) {
			Activator.logDebug("Disable");
		} else if (selectedSettings.getMethod().isManual()) {
			Activator.logDebug("Manual");
		} else {
			Activator.logDebug("Unknown");
		}
		
		selectedSettings.setAddress(addressField.getText());

		selectedSettings.setNetmask(netmaskField.getText());

		selectedSettings.setGateway(gatewayField.getText());

		String nameservers[] = nameserversField.getText().replaceAll(" ", "").split(",");
		
		selectedSettings.setNameservers(Arrays.asList(nameservers));

		String searchDomains[] = searchDomainsField.getText().replaceAll(" ", "").split(",");
		
		selectedSettings.setSearchDomains(Arrays.asList(searchDomains));
		Activator.logDebug("applying");
		
		((SettingsItem) deviceChoices.getSelectedItem()).setSettings(selectedSettings);
	}
	
	private MethodItem methodToMethodItem(IIPv4SettingsMethod method) {
		if (method.isAuto()) {
			return autoMethodItem;
		} else if (method.isDisabled()) {
			return disabledMethodItem;
		} else if (method.isManual()) {
			return manualMethodItem;
		}
		return null;
	}
	
	private void getNewSettings() {
		boolean ethernetSelected = true;
		if (selectedSettings == wifiSettings) {
			ethernetSelected = false;
		}
		ethernetSettings = ks.getEthernetIPv4Settings();
		wifiSettings = ks.getWifiIPv4Settings();
		if (ethernetSelected) {
			selectedSettings = ethernetSettings;
		} else {
			selectedSettings = wifiSettings;
		}
	}
	
	private IIPv4Settings getEthernetSettings() {
		return ethernetSettings;
	}
	
	private IIPv4Settings getWifiSettings() {
		return wifiSettings;
	}
}
