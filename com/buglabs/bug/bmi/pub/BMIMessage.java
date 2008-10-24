package com.buglabs.bug.bmi.pub;

import com.buglabs.util.StringUtil;

/**
 * A message from the BMI system. Format [moduleId] [version] [slot] [event]
 * 
 * @author ken
 * 
 */
public class BMIMessage {
	public static final int EVENT_INSERT = 0;

	public static final int EVENT_REMOVE = 1;

	private String raw;

	private int slot;

	private int event;

	private String moduleId;

	private String version;

	public BMIMessage(String raw) {
		this.raw = raw;
	}

	public BMIMessage(String moduleId, String version, int slot, int event) {
		this.moduleId = moduleId;
		this.version = version;
		this.slot = slot;
		this.event = event;
	}

	public String toString() {
		String eventStr = "";

		switch (event) {
		case EVENT_INSERT:
			eventStr = "add";
			break;
		case EVENT_REMOVE:
			eventStr = "remove";
			break;
		}

		return moduleId + " " + version + " " + slot + " " + eventStr + "\n";
	}

	public int getEvent() {
		return event;
	}

	public String getModuleId() {
		return moduleId;
	}

	public String getRaw() {
		return raw;
	}

	public String getVersion() {
		return version;
	}

	public int getSlot() {
		return slot;
	}

	/**
	 * See http://lurcher/wiki/BMI_-_Runtime_Interface#Message_Definition
	 * 
	 * @return
	 */
	public boolean parse() {
		String[] toks = StringUtil.split(raw, " ");

		if (toks.length != 4) {
			return false;
		}

		this.moduleId = toks[0].trim();
		this.version = toks[1].trim();
		try {
			this.slot = Integer.parseInt(toks[2]);
		} catch (NumberFormatException e) {
			return false;
		}

		String action = toks[3].trim().toUpperCase();

		if (action.equals("ADD")) {
			this.event = BMIMessage.EVENT_INSERT;
		} else if (action.equals("REMOVE")) {
			this.event = BMIMessage.EVENT_REMOVE;
		} else {
			return false;
		}

		return true;
	}
}
