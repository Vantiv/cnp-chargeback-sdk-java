Vantiv eCommerce Java Chargeback SDK
=====================

About Vantiv eCommerce
------------
[Vantiv eCommerce](https://developer.vantiv.com/community/ecommerce) powers the payment processing engines for leading companies that sell directly to consumers through  internet retail, direct response marketing (TV, radio and telephone), and online services. Vantiv eCommerce is the leading authority in card-not-present (CNP) commerce, transaction processing and merchant services.


About this SDK
--------------
The Vantiv eCommerce Java Chargeback SDK is a Java implementation of the [Vantiv eCommerce](https://developer.vantiv.com/community/ecommerce) Chargeback API. This SDK was created to make it as easy as possible to manage your chargebacks using Vantiv eCommerce API. This SDK utilizes the HTTPS protocol to securely connect to Vantiv eCommerce. Using the SDK requires coordination with the Vantiv eCommerce team in order to be provided with credentials for accessing our systems.

Each Java SDK release supports all of the functionality present in the associated Vantiv eCommerce Chargeback API version (e.g., SDK v2.1.0 supports Vantiv eCommerce Chargeback API v2.1). Please see the Chargeback API reference guide to get more details on what the Vantiv eCommerce chargeback engine supports.

This SDK was implemented to support the Java programming language and was created by Vantiv eCommerce. Its intended use is for online and batch transaction processing utilizing your account on the Vantiv eCommerce payments engine.

See LICENSE file for details on using this software.

Please contact [Vantiv eCommerce](https://developer.vantiv.com/community/ecommerce) to receive valid merchant credentials in order to run tests successfully or if you require assistance in any way.  We are reachable at sdksupport@Vantiv.com

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
import com.cnp.sdk.generated.*;
public class ChargebackSample {


	public static void main(String[] args) {

        	// Get chargeback case by Acquirer Reference Number
        	ChargebackRetrieval cbkRetrieval = new ChargebackRetrieval();
        	ChargebackRetrievalResponse response = cbkRetrieval.getChargebacksByARN("000000000");
        	ChargebackApiCase case1 = response.getChargebackCases().get(0);
        	Long caseId = case1.getCaseId();

        	// Add note to chargeback case
        	ChargebackUpdate cbkUpdate = new ChargebackUpdate();
        	cbkUpdate.addNoteToCase(123L, "Sample chargeback");

        	// Retrieve supporting document for chargeback case
        	ChargebackDocument cbkDocument = new ChargebackDocument();
        	File file = cbkDocument.retrieveDocument(caseId, "test.tiff", "documents/test.tiff");
    	}

}
```
