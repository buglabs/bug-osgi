package networkinggui;

import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;


import org.osgi.framework.BundleContext;

import com.buglabs.bug.networking.pub.INetworking;

public class DeviceActivator implements ItemListener {
	private INetworking ks;
	private BundleContext context;
	private JCheckBox ethernetEnable;
	private JCheckBox wifiEnable;
	private JLabel ethernetLabel;
	private JLabel wifiLabel;
		
	public DeviceActivator(INetworking ks, BundleContext context) {
		this.ks = ks;
		this.context = context;
		ethernetEnable = new JCheckBox();
		ethernetEnable.addItemListener(this);
		wifiEnable = new JCheckBox();
		wifiEnable.addItemListener(this);
		
		ethernetLabel = new JLabel("ethernet");
		wifiLabel = new JLabel("wifi");
		
	}

	public JPanel getPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));
		panel.add(ethernetLabel);
		panel.add(ethernetEnable);
		panel.add(wifiLabel);
		panel.add(wifiEnable);
		
		populateUI();
		return panel;
	}
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == ethernetEnable) {
			if (ks.getEthernetDevice().isEnabled() != ethernetEnable.isSelected()) {
				Activator.logDebug(ethernetEnable.isSelected() ? "enabling" : "disabling" + " ethernet");
				ks.getEthernetDevice().setEnabled(ethernetEnable.isSelected());
			}
		} else if (e.getSource() == wifiEnable) {
			if (ks.getWifiDevice().isEnabled() != wifiEnable.isSelected()) {
				ks.getWifiDevice().setEnabled(wifiEnable.isSelected());
			}
		}
	}
	
	private void populateUI() {
		new Thread() {
			public void run() {
				final Boolean ethernetItemEnabled = ks.getEthernetDevice().isAvailable();
				final Boolean ethernetChecked = ks.getEthernetDevice().isEnabled() && ks.getEthernetDevice().isAvailable();
				final Boolean wifiItemEnabled = ks.getWifiDevice().isAvailable();
				final Boolean wifiChecked = ks.getWifiDevice().isEnabled() && ks.getWifiDevice().isAvailable();
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						ethernetEnable.setEnabled(ethernetItemEnabled);
						ethernetEnable.setSelected(ethernetChecked);
						
						wifiEnable.setEnabled(wifiItemEnabled);
						wifiEnable.setSelected(wifiChecked);
					}
				});
			}
		}.start();
		
		
		
	}
}
