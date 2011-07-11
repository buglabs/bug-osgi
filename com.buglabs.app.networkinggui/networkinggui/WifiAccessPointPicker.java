package networkinggui;

import java.util.List;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.ListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.UIDefaults;
import javax.swing.SwingUtilities;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;

import java.awt.Component;
import java.awt.Color;
import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.TextListener;
import java.awt.event.TextEvent;


import org.osgi.framework.BundleContext;

import com.buglabs.bug.networking.pub.IAccessPoint;
import com.buglabs.bug.networking.pub.IAccessPointSecurity;
import com.buglabs.bug.networking.pub.INetworking;
import com.buglabs.bug.networking.pub.IStateEvent;
import com.buglabs.bug.networking.pub.IStateListener;

public class WifiAccessPointPicker implements FocusListener, ItemListener, ActionListener, TextListener {
	private final String SECURITY_CHOICE_NONE = "None";
	private final String SECURITY_CHOICE_WPA = "WPA";
	private final String SECURITY_CHOICE_WEP = "WEP";
	
	private final String availableSecurityChoices[] = {SECURITY_CHOICE_NONE, SECURITY_CHOICE_WPA, SECURITY_CHOICE_WEP}; 

	private INetworking ks;
	private JPanel panel;
	private JComboBox accessPointChoices;
	private JButton scanButton;
	private JComboBox securityChoices;
	private JTextField ssidField;
	private JPanel passphrasePanel;
	private JTextField passphraseField;
	private JTextField visiblePassphraseField;
	private JButton connectButton;
	private JLabel statusLabel;
	private JProgressBar statusProgressBar;
	
	private PlainDocument passphraseDocument;
	
	private BundleContext context;
	
	private Thread scanThread;
	
	private class AccessPointUIRenderer extends JLabel implements ListCellRenderer {
		public AccessPointUIRenderer() {
	         setOpaque(true);
	    }

	    public Component getListCellRendererComponent(JList list,
	                                                   Object value,
	                                                   int index,
	                                                   boolean isSelected,
	                                                   boolean cellHasFocus) {
	    	if (value != null) {
		    	Color background;
		    	Color foreground;
		    	AccessPointUI ap = (AccessPointUI) value;
		    	StringBuffer displayName = new StringBuffer();
		    	if (ap.isConnected()) {
		    		displayName.append("* ");
		    	}
		    	displayName.append(ap.getName());
		    	setText(displayName.toString());
		    	 
		    	UIDefaults defaults = UIManager.getDefaults(); 
		    	 
		    	if (isSelected) {
		    		background = defaults.getColor("List.selectionBackground");
		    		foreground = defaults.getColor("List.selectionForeground");
		    	} else {
		    		background = defaults.getColor("List.background");
		    		foreground = defaults.getColor("List.foreground");
		    	}
		    	 
		    	setBackground(background);
		    	setForeground(foreground);
	    	}
	    	return this;
	    }
	}
	
	private class AccessPointUI {
		private String name;
		private String ssid;
		private IAccessPointSecurity security;
		private String passphrase;
		private IAccessPoint accessPoint;
		private boolean isConnected;
		private boolean autoConnect;
		
		public AccessPointUI() {
			name = new String();
			security = ks.accessPointSecurityFactory();
			passphrase = new String();
			ssid = new String();
			isConnected = false;
			autoConnect = false;
		}
		
		public AccessPointUI(IAccessPoint accessPoint) {
			this.accessPoint = accessPoint;
			name = accessPoint.getName();
			security = accessPoint.getSecurity();
			passphrase = accessPoint.getPassphrase();
			ssid = accessPoint.getName();
			isConnected = accessPoint.isConnected();
			autoConnect = accessPoint.getAutoConnect();
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			
			sb.append("ssid: '");
			sb.append(ssid);
			sb.append("', ");
			
			sb.append("security: '");
			sb.append(security);
			sb.append("', ");
			
			sb.append("passphrase: '");
			sb.append(passphrase);
			sb.append("'");
			
			sb.append("}");
			return sb.toString();
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getSsid() {
			return ssid;
		}
		
		public void setSsid(String ssid) {
			this.ssid = ssid;
		}
		
		public IAccessPointSecurity getSecurity() {
			return security;
		}
		
		public String getPassphrase() {
			return passphrase;
		}
		
		public void setPassphrase(String passphrase) {
			this.passphrase = passphrase;
		}
		
		public boolean isConnected() {
			return isConnected;
		}
		
		public void setAutoConnect(boolean autoConnect) {
			if (accessPoint != null) {
				accessPoint.setAutoConnect(autoConnect);
			}
		}
		
		public boolean requiresPassphrase() {
			return (security.isWEP() || security.isWPA());
		}
		
		public void connect() {
			if (accessPoint != null) {
				if (accessPoint.requiresPassphrase()) {
					accessPoint.connect(passphrase);
				} else {
					accessPoint.connect();
				}
			}
		}
	}
	
	private class NoneAccessPoint extends AccessPointUI {
		@Override
		public String getName() {
			return "[None]"; 
		}
		
		@Override
		public void connect() {
			Activator.logDebug("Connecting to [None]");
			// TODO: We should disable the connect button when [None] is selected.
		}
	}
	
	private class OtherAccessPoint extends AccessPointUI {
		@Override
		public String getName() {
			return "Other..."; 
		}
		@Override
		public void connect() {
			Activator.logDebug("Connecting to Other...");
			ks.connectToAccessPoint(getSsid(), getSecurity(), getPassphrase());
		}
	} 
	
	private List<AccessPointUI> accessPoints;
	private AccessPointUI selectedAccessPoint;
	
	private final NoneAccessPoint noneAccessPoint;
	private final OtherAccessPoint otherAccessPoint;
	
	public WifiAccessPointPicker(INetworking ks, BundleContext context) {
		super();
		this.ks = ks;
		this.context = context;

		passphraseDocument = new PlainDocument();
		
		accessPoints = new LinkedList<AccessPointUI>(); 
		noneAccessPoint = new NoneAccessPoint();
		otherAccessPoint = new OtherAccessPoint();
		accessPointChoices = new JComboBox();
		accessPointChoices.addItemListener(this);
		accessPointChoices.setRenderer(new AccessPointUIRenderer());
		
		securityChoices = new JComboBox();
		for (int i = 0; i < availableSecurityChoices.length; i++) {
			securityChoices.addItem(availableSecurityChoices[i]);
		}
		securityChoices.addItemListener(this);
		
		ssidField = new JTextField();
		ssidField.addFocusListener(this);
		// ssidField.addTextListener(this);
		
		passphraseField = new JPasswordField();
		passphraseField.setDocument(passphraseDocument);
		passphraseField.addFocusListener(this);
		visiblePassphraseField = new JTextField();
		visiblePassphraseField.setDocument(passphraseDocument);
		visiblePassphraseField.addFocusListener(this);
				
		connectButton = new JButton("Connect");
		connectButton.addActionListener(this);
		// connectButton.setBackground(new Color(0xC7, 0xF6, 0x6F));
		// connectButton.setBorder(BorderFactory.createLineBorder(new Color(0x65, 0x9A, 0x00), 1));
		
		scanButton = new JButton("Look for Networks");
		scanButton.addActionListener(this);
		// scanButton.setBackground(new Color(0xC7, 0xF6, 0x6F));
		// scanButton.setBorder(BorderFactory.createLineBorder(new Color(0x65, 0x9A, 0x00), 1));
		
		statusLabel = new JLabel();
		statusLabel.setHorizontalAlignment(JLabel.CENTER);
		
		statusProgressBar = new JProgressBar();
		statusProgressBar.setIndeterminate(true);
		
		IStateListener stateListener = new IStateListener() {
			public void stateChanged(final IStateEvent se) {
				final Boolean isWifi = new Boolean(se.getDeviceType().isWifi());
				final Boolean isAssociation = new Boolean(se.getState().isAssociation());
				final Boolean isReady = new Boolean(se.getState().isReady());
				final Boolean isFailure = new Boolean(se.getState().isFailure());
				final Boolean isDisconnect = new Boolean(se.getState().isDisconnect());
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						// Activator.logDebug("STATE CHANGE " + se.getState().toString() + " on " + (se.getDeviceType().isEthernet() ? "ethernet" : "wifi"));
						if (isWifi) {
							if (isAssociation) {
								statusConnecting();
							} else if (isReady) {
								statusConnected();
								selectedAccessPoint.setAutoConnect(true);
							} else if (isFailure) {
								statusFailedToConnect();
							} else if (isDisconnect) {
								statusDisconnected();
							}
						}
					}
				});
			}
		};
		context.registerService(IStateListener.class.getName(), stateListener, null);
	}
	
	private JComboBox getAccessPointChoices() {
		return accessPointChoices;
	}
	
	private JComboBox getSecurityChoices() {
		return securityChoices;
	}
	
	private JTextField getPassphraseField() {
		return passphraseField;
	}
	
	private JTextField getVisiblePassphraseField() {
		return visiblePassphraseField;
	}
	
	private JTextField getSSIDField() {
		return ssidField;
	}
	
	private void createPanel() {
		panel = new JPanel();
		panel.setLayout(new GridLayout(5, 2));
	
		panel.add(scanButton);
		panel.add(getAccessPointChoices());
		JLabel ssidLabel = new JLabel("SSID");
		ssidLabel.setHorizontalAlignment(JLabel.RIGHT);
		panel.add(ssidLabel);
		panel.add(getSSIDField());
		JLabel securityLabel = new JLabel("Security");
		securityLabel.setHorizontalAlignment(JLabel.RIGHT);
		panel.add(securityLabel);
		panel.add(getSecurityChoices());
		
		JPanel passphraseControlPanel = new JPanel();
		passphraseControlPanel.setLayout(new GridLayout(1, 2));
		passphraseControlPanel.add(new JLabel("Passphrase"));
		JCheckBox showPassphrase = new JCheckBox("Show");
		passphraseControlPanel.add(showPassphrase);
		showPassphrase.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					passphrasePanel.remove(getPassphraseField());
					passphrasePanel.add(getVisiblePassphraseField());
				} else {
					passphrasePanel.remove(getVisiblePassphraseField());
					passphrasePanel.add(getPassphraseField());
				}
				passphrasePanel.revalidate();
				passphrasePanel.repaint();
			}
		});
		
		panel.add(passphraseControlPanel);
		passphrasePanel = new JPanel(new GridLayout(1, 1));
		passphrasePanel.add(getPassphraseField());
		panel.add(passphrasePanel);
		panel.add(connectButton);

		JPanel statusPanel = new JPanel(new GridLayout(2, 1));
		statusPanel.add(statusProgressBar);
		statusPanel.add(statusLabel);
		panel.add(statusPanel);
	}
	
	private void statusDisconnected() {
		statusProgressBar.setIndeterminate(false);
		statusProgressBar.setForeground(Color.BLACK);
		statusProgressBar.setValue(0);
		statusLabel.setText("Disconnected");
	}
	
	private void statusConnecting() {
		try {
			// statusProgressBar.setIndeterminate(true);
			statusProgressBar.setForeground(Color.YELLOW);
			statusProgressBar.setValue(50);
			statusLabel.setText("Connecting");
		} catch (Exception e) {
			Activator.logDebug("exception in statusConnecting");
		}
	}

	private void statusConnected() {
		try {
			statusProgressBar.setIndeterminate(false);
			statusProgressBar.setForeground(Color.GREEN);
			statusProgressBar.setValue(100);
			statusLabel.setText("Connected");
		} catch (Exception e) {
			Activator.logDebug("exception in statusConnected");
		}
	}

	private void statusFailedToConnect() {
		statusProgressBar.setIndeterminate(false);
		statusProgressBar.setForeground(Color.RED);
		statusProgressBar.setValue(100);
		statusLabel.setText("Failed");
	}
	
	private void connect() {
		Activator.logDebug("handling connect action");
		connectButton.setEnabled(false);
		Thread connectionThread = new Thread() {
			public void run() {
				Activator.logDebug("trying to connect");
				try {
					selectedAccessPoint.connect();
				} catch (Exception e) {
					Activator.logError("Exception while attempting to connect");
					Activator.logError(e.getMessage());
					for (StackTraceElement ste : e.getStackTrace()) {
						Activator.logError(ste.toString());
					}
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							statusFailedToConnect();
						}
					});
				}
				Activator.logDebug("finished trying to connected");
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						connectButton.setEnabled(true);
					}
				});
			}
		};
		connectionThread.start();
	}
	
	private Thread scan() {
		scanButton.setEnabled(false);
		scanThread = new Thread() {
			public void run() {
				Activator.logDebug("scanning for access points");
				try {
					ks.scanAccessPoints();
					ks.scanAccessPoints();
				} catch (Exception e) {
					Activator.logError("Exception while scanning for access points.");
					Activator.logError(e.getMessage());
					for (StackTraceElement ste : e.getStackTrace()) {
						Activator.logError(ste.toString());
					}
				}
				Activator.logDebug("scanned for access points");
				updateAccessPoints();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						scanButton.setEnabled(true);
					}
				});
			}
		};
		scanThread.start();
		return scanThread;
	}
	
	public JPanel getPanel() {
		if (panel == null) {
			createPanel();
		}
		new Thread() {
			public void run() {
				try {
					scan().join();
				} catch (InterruptedException e) {
					Activator.logDebug("scan was interrupted");
				}
				final AccessPointUI connectedAccessPoint = getConnectedAccessPoint(); 
				if (connectedAccessPoint == noneAccessPoint) {
					Activator.logDebug("no connected access point, using [None]");
				} else {
					Activator.logDebug("connected access point right here is " + connectedAccessPoint.getName());
				}
				selectedAccessPoint = connectedAccessPoint;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						updateInterface();
						if (connectedAccessPoint != noneAccessPoint) {
							statusConnected();
						} else {
							statusDisconnected();
						}
					}
				});
			}
		}.start();
		
		return panel;
	}
	
    public void focusGained(FocusEvent arg0) {
		Utilities.keyboardUp();
    }

    public void focusLost(FocusEvent arg0) {
    	Utilities.keyboardDown();
    	if (arg0.getSource() == passphraseField || arg0.getSource() == visiblePassphraseField) {
    		try {
    			selectedAccessPoint.setPassphrase(passphraseDocument.getText(0, passphraseDocument.getLength()));
    		} catch (BadLocationException e) {
    			Activator.logDebug("Error while setting the password. This should not happen.");
    		}
    	} else if (arg0.getSource() == ssidField) {
    		selectedAccessPoint.setSsid(ssidField.getText());
    	} 
    }
    
    public void setSecurityFromString(String securityString, AccessPointUI ap) {
    	if (securityString.compareTo(SECURITY_CHOICE_NONE) == 0) {
    		ap.getSecurity().setNone();
    	} else if (securityString.compareTo(SECURITY_CHOICE_WEP) == 0) {
    		ap.getSecurity().setWEP();
    	} else if (securityString.compareTo(SECURITY_CHOICE_WPA) == 0) {
    		ap.getSecurity().setWPA();
    	} else {
    		Activator.logError("Unknown security type: '" + securityString + "'");
    	}
    }
    
    public String securityToString(IAccessPointSecurity security) {
    	if (security.isWEP()) {
    		return SECURITY_CHOICE_WEP;
    	} else if (security.isWPA()) {
    		return SECURITY_CHOICE_WPA;
    	}
    	return SECURITY_CHOICE_NONE;
    }
    
    public void itemStateChanged(ItemEvent e) {
    	if (e.getStateChange() == ItemEvent.SELECTED) {
	    	if (e.getSource() == accessPointChoices) {
	    		AccessPointUI newlySelectedAccessPoint = (AccessPointUI) accessPointChoices.getSelectedItem();
	    		if (newlySelectedAccessPoint != null) {
	    			selectedAccessPoint = newlySelectedAccessPoint;
	    		} else {
	    			Activator.logDebug("newlySelectedAccessPoint was null, setting selectedAccessPoint to none");
	    		}
	    		Activator.logDebug("Selected AP is " + (selectedAccessPoint == null ? "NOTHING" : selectedAccessPoint.getName()));
	        	updateSSIDField();
	        	updateSecurityChoices();
	        	updatePassphraseField();
	    	} else if (e.getSource() == securityChoices) {
	    		setSecurityFromString((String) securityChoices.getSelectedItem(), selectedAccessPoint);
	        	updatePassphraseField();
	    	}
    	}
    }
    
    public void actionPerformed(ActionEvent e) {
    	Activator.logDebug("button pressed");
    	if (e.getSource() == connectButton) {
    		Activator.logDebug("connectButton pressed");
    		connect();
    	} else if (e.getSource() == scanButton) {
    		try {
    			scan().join();
    		} catch (InterruptedException ex) {
    			Activator.logDebug("interrupted while scanning");
    		}
    		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					updateInterface();
				}
			});
    	}
    }
    
    public void textValueChanged(TextEvent e) {
    	JTextField source = (JTextField) e.getSource();
    	String value = source.getText(); 
    	if (source == ssidField && isSSIDFieldEnabled()) {
    		Activator.logDebug("setting SSID ...");
    		selectedAccessPoint.setSsid(value);
    	} else if (source == passphraseField && isPassphraseFieldEnabled()) {
    		Activator.logDebug("setting passphrase ...");
    		selectedAccessPoint.setPassphrase(value);
    	}
    }
    
    private AccessPointUI getConnectedAccessPoint() {
    	Activator.logDebug("getting connected access point");
    	for (AccessPointUI ap :getAccessPoints()) {
    		Activator.logDebug("trying " + ap.getName());
    		if (ap.isConnected()) {
    			Activator.logDebug("connected to " + ap.getName());
    			return ap;
    		} else {
    			Activator.logDebug("NOT connected to " + ap.getName());
    		}
		}
    	Activator.logWarning("could not find connected AP");
    	return noneAccessPoint;
    }

    private void updateAccessPoints() {
    	Activator.logDebug("entering: updateAccessPoints()");
    	synchronized (accessPoints) {
			accessPoints.clear();
			
			List<IAccessPoint> backendAccessPoints = ks.getAccessPoints();
			
			accessPoints.add(noneAccessPoint);
			accessPoints.add(otherAccessPoint);
			
			for (IAccessPoint ap : backendAccessPoints) {
				Activator.logDebug("Adding access point");
				AccessPointUI apUI = new AccessPointUI(ap);
				accessPoints.add(apUI);
				Activator.logDebug("Added access point " + apUI.getName());
			}
    	}
		Activator.logDebug("leaving: updateAccessPoints()");
    }
    
    private List<AccessPointUI> getAccessPoints() {
    	synchronized (accessPoints) {
    		return accessPoints;
    	}
    }
    
    private AccessPointUI getAccessPointByName(String accessPointName) {
    	for (AccessPointUI ap : getAccessPoints()) {
    		if (accessPointName.compareTo(ap.getName()) == 0) {
    			return ap;
    		}
    	}
    	return noneAccessPoint;
    }
    
    private void updateAccessPointChoices() {
    	synchronized (accessPointChoices) {
	    	Activator.logDebug("removing all access point choices");
	    	accessPointChoices.removeItemListener(this);
	    	// Store the currently selected access point name, because removing all items
	    	// selects a new access point.
	    	String selectedAccessPointName = noneAccessPoint.getName();
	    	if (selectedAccessPoint != null) {
	    		selectedAccessPointName = selectedAccessPoint.getName();
	    	}
			
	    	accessPointChoices.removeAllItems();
	    	for (AccessPointUI apUI : getAccessPoints()) {
	    		Activator.logDebug("Adding access point " + apUI.getName());
	    		accessPointChoices.addItem(apUI);
   			}

			selectedAccessPoint = getAccessPointByName(selectedAccessPointName);
			if (selectedAccessPoint != null) {
				Activator.logDebug("selecting " + selectedAccessPoint.getName() + " from accessPointChoices.");
				accessPointChoices.setSelectedItem(selectedAccessPoint);
			} else {
				Activator.logDebug("no selected access point to set");
			}
			accessPointChoices.addItemListener(this);
    	}
    }
    
    private void updateInterface() {
    	updateAccessPointChoices();
    	updateSSIDField();
    	updateSecurityChoices();
    	updatePassphraseField();
    }
    
    private boolean isSSIDFieldEnabled() {
    	return selectedAccessPoint == otherAccessPoint;
    }
    
    private void updateSSIDField() {
    	if (isSSIDFieldEnabled()) {
    		ssidField.setText(otherAccessPoint.getSsid());
    	} else {
    		ssidField.setText("");
    	}
    	
    	ssidField.setEnabled(isSSIDFieldEnabled());
    	ssidField.setVisible(isSSIDFieldEnabled());
    }

    private boolean isSecurityChoicesEnabled() {
    	return selectedAccessPoint == otherAccessPoint;
    }
    
    private boolean isSecurityChoicesVisible() {
    	return (selectedAccessPoint != noneAccessPoint && (selectedAccessPoint.requiresPassphrase() || selectedAccessPoint == otherAccessPoint)); 
    }
    
    private void updateSecurityChoices() {
    	String securityString = securityToString(selectedAccessPoint.getSecurity());
    	securityChoices.setSelectedItem(securityString);
    	securityChoices.setEnabled(isSecurityChoicesEnabled());
    	securityChoices.setVisible(isSecurityChoicesVisible());
    }

    private boolean isPassphraseFieldEnabled() {
    	return (selectedAccessPoint != noneAccessPoint && selectedAccessPoint.requiresPassphrase());
    }
    
    private boolean isPassphraseFieldVisible() {
    	return (selectedAccessPoint != noneAccessPoint && selectedAccessPoint.requiresPassphrase());
    }
    
    private void updatePassphraseField() {
    	if (isPassphraseFieldEnabled()) {
    		passphraseField.setText(selectedAccessPoint.getPassphrase());
    	} else {
    		passphraseField.setText("");
    	}
    	
    	passphraseField.setEnabled(isPassphraseFieldEnabled());
    	passphraseField.setVisible(isPassphraseFieldVisible());
    	visiblePassphraseField.setEnabled(isPassphraseFieldEnabled());
    	visiblePassphraseField.setVisible(isPassphraseFieldVisible());
    }
}
