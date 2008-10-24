package com.buglabs.bug.module.vonhippel.pub;

import java.util.List;

import com.buglabs.bug.jni.vonhippel.VonHippel;
import com.buglabs.services.ws.IWSResponse;
import com.buglabs.services.ws.PublicWSDefinition;
import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.services.ws.WSResponse;
import com.buglabs.util.XmlNode;

public class VonHippelWS implements PublicWSProvider {
	

	private VonHippel vhioctl;

	public VonHippelWS(VonHippel vhioctl){
		this.vhioctl = vhioctl;
	
	}
	public PublicWSDefinition discover(int operation) {
		if (operation == PublicWSProvider.GET) {
			return new PublicWSDefinition() {

				public List getParameters() {
					return null;
				}

				public String getReturnType() {
					return "text/xml";
				}
			};
		}

		return null;
	}

	public IWSResponse execute(int operation, String input) {
		if (operation == PublicWSProvider.GET) {
			return new WSResponse(getAccelerationXml(), "text/xml");
		}
		return null;
	}
	
	private Object getAccelerationXml() {
		XmlNode root = new XmlNode("Status");
		
		//do some magic here to read the state of the VH module and display it
		return root.toString();
	}

	public String getDescription() {
		return "Returns status of pins on Von Hippel module";
	}

	public String getPublicName() {
		return "VonHippel";
	}
}
