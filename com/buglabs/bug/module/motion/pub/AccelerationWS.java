package com.buglabs.bug.module.motion.pub;

import java.io.IOException;
import java.util.List;

import com.buglabs.bug.accelerometer.pub.AccelerometerSample;
import com.buglabs.bug.accelerometer.pub.IAccelerometerSampleProvider;
import com.buglabs.services.ws.IWSResponse;
import com.buglabs.services.ws.PublicWSDefinition;
import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.services.ws.WSResponse;
import com.buglabs.util.SelfReferenceException;
import com.buglabs.util.XmlNode;

public class AccelerationWS implements PublicWSProvider {
	private IAccelerometerSampleProvider acc;

	public AccelerationWS(IAccelerometerSampleProvider acc) {
		this.acc = acc;
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
		XmlNode root = new XmlNode("Acceleration");
		try {
				XmlNode sample = new XmlNode("sample");
				AccelerometerSample accSample = acc.readSample();
				
				if(accSample != null) {
					//x y z
					sample.addAttribute("x", Float.toString(accSample.getAccelerationX()));
					sample.addAttribute("y", Float.toString(accSample.getAccelerationY()));
					sample.addAttribute("z", Float.toString(accSample.getAccelerationZ()));
					
					root.addChildElement(sample);
				}
		} catch (SelfReferenceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//TODO: Log this
			e.printStackTrace();
		}
		
		return root.toString();
	}

	public String getDescription() {
		return "Returns a sample from the accelerometer";
	}

	public String getPublicName() {
		return "Acceleration";
	}
}
