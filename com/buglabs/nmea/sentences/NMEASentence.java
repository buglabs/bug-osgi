package com.buglabs.nmea.sentences;

public abstract class NMEASentence {
	public final String type;
	public String checksum;
	
	public NMEASentence(String type) {
		this.type = type;
		checksum = null;
	}
	
	public boolean isValid(String sentence) {
		return sentence.startsWith(type);
	}
	
	public abstract Object parse(String sentence) throws NMEAParserException;

	public String getChecksum() {
		return checksum;
	}
	
	public void setChecksum(String csum) {
		checksum = csum;
	}
}
