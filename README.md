Vantiv eCommerce Java Chargeback SDK
=====================

About Vantiv eCommerce
------------
[Vantiv eCommerce](https://developer.vantiv.com/community/ecommerce) powers the payment processing engines for leading companies that sell directly to consumers through  internet retail, direct response marketing (TV, radio and telephone), and online services. Vantiv eCommerce is the leading authority in card-not-present (CNP) commerce, transaction processing and merchant services.


About this SDK
--------------
The Vantiv eCommerce Java Chargeback SDK is a Java implementation of the [Vantiv eCommerce](https://developer.vantiv.com/community/ecommerce) Chargeback API. This SDK was created to make it as easy as possible to manage your chargebacks using Vantiv eCommerce API. This SDK utilizes the HTTPS protocol to securely connect to Vantiv eCommerce. Using the SDK requires coordination with the Vantiv eCommerce team in order to be provided with credentials for accessing our systems.

Setup
-----

1. Add JCenter repository to your Maven or Gradle build:
	1. For Maven, please read instructions at: https://bintray.com/bintray/jcenter
	2. For Gradle, add `jcenter()` to your `repositories { ... }`
2. Add the dependency
    1. For Maven:
        ```xml
            <dependency>
                <groupId>com.cnp</groupId>
                <artifactId>cnp-chargeback-sdk-java</artifactId>
                <version>2.1.0</version>
            </dependency>
        ```

    2. For Gradle:
        ```groovy

            compile(group: 'com.cnp', name: 'cnp-chargeback-sdk-java', version: '2.1.0')

        ```
        
3. Create your configuration file with one of the following
    * Run `java -jar /path/to/cnp-chargeback-sdk-java.jar` and answer the questions, or
    * Add a file `.chargeback_SDK_config.properties` to your home directory with the correct properties in it
4. Use it:

```java

import com.cnp.sdk.*;
import com.cnp.sdk.generated.*

public class SampleCnpTxn {

import com.cnp.sdk.*;
import com.cnp.sdk.generated.*;
public class SampleCnpTxn {


	public static void main(String[] args) {

		// Visa $10 Sale
		Sale sale = new Sale();
		sale.setReportGroup("Planets");
		sale.setOrderId("12344");
		sale.setAmount(1000L);
		sale.setOrderSource(OrderSourceType.ECOMMERCE);
		CardType card = new CardType();
		card.setType(MethodOfPaymentTypeEnum.VI);
		card.setNumber("4100000000000002");
		card.setExpDate("1210");
		sale.setCard(card);
		
		// Peform the transaction on the Vantiv eCommerce Platform
		SaleResponse response = new CnpOnline().sale(sale);

		// display result
		System.out.println("Message: " + response.getMessage());
		System.out.println("Vantiv eCommerce Transaction ID: " + response.getCnpTxnId());
	}
}
```
