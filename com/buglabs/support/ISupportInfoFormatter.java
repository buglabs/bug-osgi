package com.buglabs.support;

import java.util.Map;

public interface ISupportInfoFormatter {

	public abstract String getContentType();

	public abstract String buildResponse(String description, String kernelVersion, String rootfsVersion, Map jvmProperties, Map bundleVersions);
}
