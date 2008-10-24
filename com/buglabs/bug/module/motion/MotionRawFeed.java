package com.buglabs.bug.module.motion;

import java.io.InputStream;

import com.buglabs.bug.module.motion.pub.IMotionRawFeed;
import com.buglabs.util.LogServiceUtil;
import com.buglabs.util.StreamMultiplexer;

public class MotionRawFeed extends StreamMultiplexer implements IMotionRawFeed {

	public MotionRawFeed(InputStream is) {
		super(is, 1);
		setName("MotionRawFeed");
		setLogService(LogServiceUtil.getLogService(Activator.getInstance().getBundleContext()));
	}
}
