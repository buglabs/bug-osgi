package com.buglabs.nmea;

public class SV {
	
	private int PRN;
	private int elevation;
	private int azimuth;
	private int SNR;
	
	public SV(String prn, String elevation, String azimuth, String SNR) {
		try {
			this.PRN = Integer.parseInt(prn);
		} catch (NumberFormatException e) {
			this.PRN = -1;
		}
		
		try {
			this.elevation = Integer.parseInt(elevation);
		} catch (NumberFormatException e) {
			this.elevation = -1;
		}
		
		try {
			this.azimuth = Integer.parseInt(azimuth);
		} catch (NumberFormatException e) {
			this.azimuth = -1;
		}
		
		try {
			//Remove any CRC
			this.SNR = Integer.parseInt(StringUtil.split(SNR, "*")[0]);
		} catch (NumberFormatException e) {
			this.SNR = -1;
		}
	}
	
	public int getAzimuth() {
		return azimuth;
	}
	
	public void setAzimuth(int azimuth) {
		this.azimuth = azimuth;
	}
	
	public int getElevation() {
		return elevation;
	}
	
	public void setElevation(int elevation) {
		this.elevation = elevation;
	}
	
	public int getPRN() {
		return PRN;
	}
	
	public void setPRN(int prn) {
		this.PRN = prn;
	}
	
	public int getSNR() {
		return SNR;
	}
	
	public void setSNR(int snr) {
		this.SNR = snr;
	}
}
