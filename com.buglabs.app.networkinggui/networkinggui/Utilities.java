package networkinggui;

import java.io.IOException;

public class Utilities {
	static void keyboardUp() {
		try {
			Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "matchbox-keyboard --show"});
		} catch (final IOException e) {
			System.err.println("Failed to toggle keyboard: " + e);
		}
	}
	
	static void keyboardDown() {
    	try {
    		Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "matchbox-keyboard --hide"});
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}
}
