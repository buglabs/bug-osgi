package com.buglabs.nmea;

public class Time {
	private String time;
	
	int hour, minutes;
	float seconds;
	
	public Time(String time) {
		this.time = time;
		
		hour = Integer.parseInt(time.substring(0, 2));
		minutes = Integer.parseInt(time.substring(2, 4));
		seconds = Float.parseFloat(time.substring(4));
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public float getSeconds() {
		return seconds;
	}

	public void setSeconds(float seconds) {
		this.seconds = seconds;
	}

	public String getTime() {
		return time;
	}
}
