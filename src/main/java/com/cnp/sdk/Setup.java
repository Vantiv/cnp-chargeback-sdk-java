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

	private static final Map<String,String> URL_MAP = new HashMap<String,String>() {
		{
			put("sandbox","http://www.testvantivcnp.com/sandbox/new/services/chargebacks/");
			put("prelive","https://payments.vantivprelive.com/vap/services/chargeback/");
			put("postlive","https://payments.vantivpostlive.com/vap/services/chargeback/");
			put("production","https://payments.vantivcnp.com/vap/services/chargeback/");
		}
	};

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

			System.out.println("Please choose a url from the following list (example: 'prelive'):");
			for (Map.Entry entry : URL_MAP.entrySet()) {
				System.out.println("\t" + entry.getKey() + " => " + entry.getValue());
			}
			System.out.println("\tother => You will be asked for all the values");
			lastUserInput = stdin.readLine();
			if(URL_MAP.containsKey(lastUserInput.toLowerCase())) {
				// predefined value
				config.put("url", URL_MAP.get(lastUserInput.toLowerCase()));
				badInput = false;
			}
			else if("other".equalsIgnoreCase(lastUserInput)){
				// user wants to enter custom values
				System.out.println("Please input the URL for online transactions (ex: https://www.testlitle.com/sandbox/communicator/online):");
				config.put("url", stdin.readLine());
				badInput = false;
			}
			else{
				// error condition
				badInput = true;
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

		config.put("printxml", "false");
		config.put("neuterxml", "false");

		config.store(configFile, "");
		System.out.println("The chargeback configuration file has been generated, the file is located at " + file.getAbsolutePath());

		configFile.close();
	}
}
