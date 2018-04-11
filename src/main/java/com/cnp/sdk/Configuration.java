package com.cnp.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	private static final String CHARGEBACK_SDK_CONFIG = ".chargeback_SDK_config.properties";

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

	public Properties getProperties(){
	    Properties properties;
        FileInputStream fileInputStream = null;
        try {
            properties = new Properties();
            fileInputStream = new FileInputStream((new Configuration()).location());
            properties.load(fileInputStream);
            return properties;
        } catch (FileNotFoundException e) {
            throw new ChargebackException("Configuration file not found." +
                    " If you are not using the .chargeback_SDK_config.properties file," +
                    " please use the " + ChargebackRetrieval.class.getSimpleName() + "(Properties) constructor." +
                    " If you are using .chargeback_SDK_config.properties, you can generate one using java -jar cnp-chargeback-sdk-java-x.xx.jar", e);
        } catch (IOException e) {
            throw new ChargebackException("Configuration file could not be loaded.  Check to see if the user running this has permission to access the file", e);
        } finally {
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new ChargebackException("Configuration FileInputStream could not be closed.", e);
                }
            }
        }
    }
}
