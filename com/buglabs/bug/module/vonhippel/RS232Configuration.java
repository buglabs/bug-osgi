package com.buglabs.bug.module.vonhippel;

public class RS232Configuration {
	
	private int baudrate;
	private int bitsperchar;
	private int stopbits;
	private String parity;
	private boolean autocts;
	private boolean autorts;
	private boolean blocking;
	
	public RS232Configuration(int baudrate){
		this.baudrate = baudrate;
		this.bitsperchar = 8;
		this.stopbits = 1;
		this.parity = "none";
		this.autocts = false;
		this.autorts = false;
		this.blocking = false;	
	}
	public RS232Configuration(){
		this.baudrate = 9600;
		this.bitsperchar = 8;
		this.stopbits = 1;
		this.parity = "none";
		this.autocts = false;
		this.autorts = false;
		this.blocking = false;
	}

	public int getBaudrate() {
		return baudrate;
	}

	public void setBaudrate(int baudrate) {
		this.baudrate = baudrate;
	}

	public int getBitsperchar() {
		return bitsperchar;
	}

	public void setBitsperchar(int bitsperchar) {
		this.bitsperchar = bitsperchar;
	}

	public int getStopbits() {
		return stopbits;
	}

	public void setStopbits(int stopbits) {
		this.stopbits = stopbits;
	}

	public String getParity() {
		return parity;
	}

	public void setParity(String parity) {
		this.parity = parity;
	}

	public boolean isAutocts() {
		return autocts;
	}

	public void setAutocts(boolean autocts) {
		this.autocts = autocts;
	}

	public boolean isAutorts() {
		return autorts;
	}

	public void setAutorts(boolean autorts) {
		this.autorts = autorts;
	}

	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}
	

	
	
	
}
