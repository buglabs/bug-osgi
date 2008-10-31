package com.buglabs.bug.module.gps;

import java.io.IOException;
import java.util.TimerTask;

import org.osgi.service.log.LogService;

import com.buglabs.bug.module.gps.pub.IGPSModuleControl;

public class GPSFIXLEDStatusTask extends TimerTask {
	private IGPSModuleControl control;
	private final LogService log;
	
	public GPSFIXLEDStatusTask(IGPSModuleControl control, LogService log) {
		this.control = control;
		this.log = log;
	}
	
	public void run() {
		int status = 0;
		int delay = 1;
		try {
			status = control.getStatus();
		
			if((status & 0x01) == 0) {
				control.setLEDGreen(true);
				control.setLEDRed(false);
				Thread.sleep(delay);
				control.setLEDGreen(false);
				control.setLEDGreen(false);
			} else {
				control.setLEDGreen(false);
				control.setLEDRed(true);
				Thread.sleep(delay);
				control.setLEDGreen(false);
				control.setLEDRed(false);
			}
		} catch (IOException e) {
			log.log(LogService.LOG_ERROR,"FIXLEDStatusTask: Unable to query gps control on slot " +
								control.getSlotId(), e);
		} catch (InterruptedException e) {
			//Ignore interruption
		}
	}
}
