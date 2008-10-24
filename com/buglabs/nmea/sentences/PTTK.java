package com.buglabs.nmea.sentences;

import java.util.ArrayList;
import java.util.List;

import com.buglabs.nmea.SV;
import com.buglabs.nmea.StringUtil;

public class PTTK extends NMEASentence {

	private String msg;
	private int code;
	
	public PTTK() {
		super("$PTTK");
	}
	
	public String getMessage() {
		return msg;
	}
	
	public void setMessage(String msg) {
		this.msg = msg;
	}
	
	public int getErrorCode() {
		return code;
	}
	
	public void setErrorCode(int code) {
		this.code = code;
	}
	
	public Object parse(String sentence) {
		String[] fields = StringUtil.split(sentence, ",");
		
		for(int i = 0; i < fields.length; ++i) {
			switch(i) {
			case 1:
				setMessage(fields[i]);
				break;
			case 2:
				String[] lastfield = StringUtil.split(fields[i], "*");
				if(lastfield.length > 1) {
					setErrorCode(Integer.parseInt(lastfield[0]));
				}
				break;
			default:
				break;
			}
		}

		return this;
	}	
}
