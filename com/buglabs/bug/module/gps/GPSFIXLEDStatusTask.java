package com.buglabs.bug.module.gps;

import java.io.IOException;
import java.util.TimerTask;

import com.buglabs.bug.module.gps.pub.IGPSModuleControl;

public class GPSFIXLEDStatusTask extends TimerTask {
	private IGPSModuleControl control;
	
	public GPSFIXLEDStatusTask(IGPSModuleControl control) {
		this.control = control;
	}
	
	public void run() {
		int status = 0;
		int delay = 1;
		try {
			status = control.getStatus();
		
			if((status & 0x01) == 0) {
				control.LEDGreenOn();
				control.LEDRedOff();
				Thread.sleep(delay);
				control.LEDGreenOff();
				control.LEDRedOff();
			} else {
				control.LEDGreenOff();
				control.LEDRedOn();
				Thread.sleep(delay);
				control.LEDGreenOff();
				control.LEDRedOff();
			}
		} catch (IOException e) {
			// TODO: Log this
			System.out.println("FIXLEDStatusTask: Unable to query gps control on slot " +
								control.getSlotId());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
