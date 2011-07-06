package com.buglabs.app.bugdash2;


/**
 * Represents configuration property entry 
 * @author iocanto
 *
 */
public class ConfigPropEntry {

	private String key;	
	private String value;
	
	public ConfigPropEntry(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
