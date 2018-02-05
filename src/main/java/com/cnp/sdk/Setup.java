package com.cnp.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Properties;

public class Setup {

	private static final HashMap<String,String> URL_MAP = new HashMap<String,String>() {
		{
			put("sandbox","https://www.testlitle.com/sandbox/communicator/online");
			put("cert","https://prelive.litle.com/vap/communicator/online");
			put("precert","https://postlive.litle.com/vap/communicator/online");
			put("production","https://payments.litle.com/vap/communicator/online");
		}
	};

	public static void main(String[] args) throws IOException {
		File file = (new Configuration()).location();
		Properties config = new Properties();
		PrintStream configFile = new PrintStream(file);
		String lastUserInput;

		BufferedReader stdin = new BufferedReader
				(new InputStreamReader(System.in));

		System.out.println("Welcome to Chargeback Java_SDK");
		System.out.print("Please input your presenter user name: ");
		config.put("username", stdin.readLine());
		System.out.print("Please input your presenter password: ");
		config.put("password", stdin.readLine());
		System.out.print("Please input your merchantId: ");
		config.put("merchantId", stdin.readLine());
		boolean badInput = false;
		do{
			if( badInput ){
				System.out.println("====== Invalid choice entered ======");
			}
			System.out.println("Please choose an environment from the following list (example: 'prelive'):");
			System.out.println("\tsandbox => www.testlitle.com");
			System.out.println("\tcert => cert.litle.com");
			System.out.println("\tprecert => precert.litle.com");
			System.out.println("\tproduction => payments.litle.com");
			System.out.println("\tother => You will be asked for all the values");
			lastUserInput = stdin.readLine();
			if(
					lastUserInput.compareToIgnoreCase("cert") == 0 ||
							lastUserInput.compareToIgnoreCase("sandbox") == 0 ||
							lastUserInput.compareToIgnoreCase("precert") == 0 ||
							lastUserInput.compareToIgnoreCase("production") == 0
					) {
				// standard predefined cases
				config.put("url", URL_MAP.get(lastUserInput.toLowerCase()));
				badInput = false;
			} else if(lastUserInput.compareToIgnoreCase("other") == 0){
				// user wants to enter custom values
				System.out.println("Please input the URL for online transactions (ex: https://www.testlitle.com/sandbox/communicator/online):");
				config.put("url", stdin.readLine());
				badInput = false;
			} else{
				// error condition
				badInput = true;
			}
		} while( badInput );

		System.out.print("\nPlease input the proxy host, if no proxy hit enter: ");
		lastUserInput = stdin.readLine();
		config.put("proxyHost", (lastUserInput == null ? "" : lastUserInput));
		System.out.print("Please input the proxy port, if no proxy hit enter: ");
		lastUserInput = stdin.readLine();
		config.put("proxyPort", (lastUserInput == null ? "" : lastUserInput));

		config.put("reportGroup", "Default Report Group");
		config.put("printxml", "false");
		config.put("neuterxml", "false");

		config.store(configFile, "");
		System.out.println("The chargeback configuration file has been generated, the file is located at " + file.getAbsolutePath());

		configFile.close();
	}
}
