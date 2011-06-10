package com.buglabs.bug.program.pub;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author kgilmer
 * 
 */
public interface IUserAppStore {
	public void storeApp(InputStream appInputStream) throws IOException;
}
