package com.cnp.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Setup {

	/* List of environments for the configuration. */
	private enum EnvironmentConfiguration {
		SANDBOX("sandbox", "https://www.testvantivcnp.com/sandbox/new"),
		PRELIVE("prelive", "https://services.vantivprelive.com"),
		POSTLIVE("postlive", "https://services.vantivpostlive.com"),
		PRODUCTION("production", "https://services.vantivcnp.com"),
		OTHER("other", "You will be asked for all the values");

		private final String key;
		private final String url;

		private EnvironmentConfiguration(final String key, final String online) {
			this.key = key;
			this.url = online;
		}

		public final String getKey() {
			return this.key;
		}

		public final String getOnlineUrl() {
			return this.url;
		}

		public static final EnvironmentConfiguration fromValue(final String value) {
			for (final EnvironmentConfiguration environConfig : EnvironmentConfiguration.values()) {
				if (environConfig.getKey().equals(value)) {
					return environConfig;
				}
			}
			return null;
		}
	}

	public static void main(String[] args) throws IOException {
		File file = (new Configuration()).location();
		Properties config = new Properties();
		PrintStream configFile = new PrintStream(file);
		String lastUserInput;

		BufferedReader stdin = new BufferedReader
				(new InputStreamReader(System.in));

		System.out.println("Welcome to Chargeback Java SDK");

		System.out.print("Please input your presenter user name: ");
		config.put("username", stdin.readLine());

		System.out.print("Please input your presenter password: ");
		config.put("password", stdin.readLine());

		System.out.print("Please input your merchantId: ");
		config.put("merchantId", stdin.readLine());

		boolean badInput = false;
		do{
			if(badInput){
				System.out.println("====== Invalid choice entered ======");
			}
			System.out.println("Please choose an environment from the following list (example: 'prelive'):");
			for (final EnvironmentConfiguration environConfig : EnvironmentConfiguration.values()) {
				System.out.println(String.format("\t%s => %s", environConfig.getKey(), environConfig.getOnlineUrl()));
			}
			lastUserInput = stdin.readLine();
			EnvironmentConfiguration environSelected = EnvironmentConfiguration.fromValue(lastUserInput);
			if (environSelected == null) {
				// error condition
				badInput = true;
			} else if (EnvironmentConfiguration.OTHER.equals(environSelected)) {
				// user wants to enter custom values
				System.out.println("Please input the URL for online transactions (ex: https://www.testantivcnp.com/sandbox/communicator/online):");
				config.put("url", stdin.readLine());
				badInput = false;
			} else {
				// standard predefined cases
				config.put("url", environSelected.getOnlineUrl());
				badInput = false;
			}
		} while(badInput);

		System.out.print("\nPlease input the proxy host, if no proxy hit enter: ");
		lastUserInput = stdin.readLine();
		config.put("proxyHost", (lastUserInput == null ? "" : lastUserInput));

		System.out.print("Please input the proxy port, if no proxy hit enter: ");
		lastUserInput = stdin.readLine();
		config.put("proxyPort", (lastUserInput == null ? "" : lastUserInput));

        System.out.print("\nPlease input the timeout: ");
        lastUserInput = stdin.readLine();
        config.put("timeout", (lastUserInput == null ? "" : "10000"));

		config.put("printXml", "false");
		config.put("neuterXml", "false");

		config.store(configFile, "");
		System.out.println("The chargeback configuration file has been generated, the file is located at " + file.getAbsolutePath());

		configFile.close();
	}
}
