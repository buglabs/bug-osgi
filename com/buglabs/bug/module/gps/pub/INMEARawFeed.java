package com.buglabs.bug.module.gps.pub;

import java.io.IOException;
import java.io.InputStream;

public interface INMEARawFeed {
	public InputStream getInputStream() throws IOException;
}
