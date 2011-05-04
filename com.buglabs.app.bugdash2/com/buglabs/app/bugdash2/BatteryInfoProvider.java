package com.buglabs.app.bugdash2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class is originally from SimpleBatteryManager: 
 * http://www.buglabs.net/applications/SimpleBatteryManager
 */
public class BatteryInfoProvider implements IBatteryInfoProvider  {
	 

	public String getId() {
		return null;
	}

	public double getValue(String path)  {
		double result = -1d;
		
		if (path != null && !path.equals("")) {
			try {
				result = Double.parseDouble((getFirstLine(path)));
			} catch (NumberFormatException e) {
				LogManager.logError(this.getClass().getName() + ": " + e.getMessage());
			} catch (IOException e) {
				LogManager.logError(this.getClass().getName() + ": " + e.getMessage());
			}
		}

		return result > 100 ? 100d : result;
	}

	
	private static String getFirstLine(String filename) throws IOException {
		File f = new File(filename);

		BufferedReader br = new BufferedReader(new FileReader(f));

		String line = br.readLine().trim();

		br.close();		
		return line;
	}
	
}
