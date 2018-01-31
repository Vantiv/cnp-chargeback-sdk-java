package com.cnp.sdk;

import java.io.File;

public class Configuration {
	
	private static final String CHARGEBACK_SDK_CONFIG = ".cnp_SDK_config.properties";

	public File location() {
		File file = new File(System.getProperty("user.home") + File.separator + CHARGEBACK_SDK_CONFIG);
		if(System.getProperty("java.specification.version").equals("1.4")) {
			if(System.getProperty("CHARGEBACK_CONFIG_DIR") != null) {
				file = new File(System.getProperty("CHARGEBACK_CONFIG_DIR") + File.separator + CHARGEBACK_SDK_CONFIG);
			}
		}
		else {
			if(System.getenv("CHARGEBACK_CONFIG_DIR") != null) {
				if(System.getenv("CHARGEBACK_CONFIG_DIR").equals("classpath:" + CHARGEBACK_SDK_CONFIG)) {
					if (getClass().getClassLoader().getResource(CHARGEBACK_SDK_CONFIG) != null) {
						String filePath = getClass().getClassLoader().getResource(CHARGEBACK_SDK_CONFIG).getPath();
						file = new File(filePath);
					}
				}
				else {
					file = new File(System.getenv("CHARGEBACK_CONFIG_DIR") + File.separator + CHARGEBACK_SDK_CONFIG);
				}
			}
		}
		return file;
	}
}
