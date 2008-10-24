package com.buglabs.nmea;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.buglabs.nmea.sentences.GGA;
import com.buglabs.nmea.sentences.GSV;
import com.buglabs.nmea.sentences.NMEAParserException;
import com.buglabs.nmea.sentences.NMEASentence;
import com.buglabs.nmea.sentences.PTTK;
import com.buglabs.nmea.sentences.RMC;

public class NMEAParser {
	private static List sentences;
	
	public NMEAParser() {
		sentences = new ArrayList();
		sentences.add(new GSV());
		sentences.add(new RMC());
		sentences.add(new GGA());
		sentences.add(new PTTK());
	}
	
	public Object parse(String sen) throws NMEAParserException {
		Iterator iter = sentences.iterator();
		
		while(iter.hasNext()) {
			NMEASentence sentence = (NMEASentence) iter.next();
			if(sentence.isValid(sen)) {
				return sentence.parse(sen);
			}
		}
		
		return null;
	}
}
