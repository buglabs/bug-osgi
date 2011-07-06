package com.buglabs.app.bugdash2;

public interface IBatteryInfoProvider {
	
	public String getId() ;

	public double getValue(String path);

}
