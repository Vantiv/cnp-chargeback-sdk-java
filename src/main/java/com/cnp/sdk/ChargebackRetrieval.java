package com.cnp.sdk;

import com.cnp.sdk.generate.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * ChargebackRetrieval object has methods for performing the different ChargebackRetrieval functionalities supported by the Vantiv API
 * like retrieving chargeback cases based on various filters.
 *
 * @author harshitvora
 *
 */

public class ChargebackRetrieval {

    private Properties config;
    private Communication communication;
    private final String URL_PATH = "/services/chargebacks/";

    public ChargebackRetrieval() {

        communication = new Communication();
        FileInputStream fileInputStream = null;

        try {
            config = new Properties();
            fileInputStream = new FileInputStream((new Configuration()).location());
            config.load(fileInputStream);
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

    /**
     * Construct a ChargebackRetrieval specifying the configuration in code.  This should be used by integrations that
     * have another way to specify their configuration settings (ofbiz, etc)
     *
     * Properties that *must* be set are:
     *
     * 	url (eg https://payments.litle.com/vap/communicator/online)
     *	reportGroup (eg "Default Report Group")
     *	username
     *	merchantId
     *	password
     *	timeout (in seconds)
     *	Optional properties are:
     *	proxyHost
     *	proxyPort
     *	printxml (possible values "true" and "false" - defaults to false)
     *  neuterxml (possible values "true" and "false" - defaults to false)
     */
    public ChargebackRetrieval(Properties config) {
        this.config = config;
        communication = new Communication();
    }

    ////////////////////////////////////////////////////////////////////
    //                    ChargebackRetrieval API:                       //
    ////////////////////////////////////////////////////////////////////


    public ChargebackRetrievalResponse getChargebacksByDate(String yyyy, String mm, String dd) {
        String date = yyyy+"-"+mm+"-"+dd;
        return getRetrievalReposnse("date", date);
    }

    public ChargebackRetrievalResponse getChargebacksByFinancialImpact(String yyyy, String mm, String dd, Boolean impact){
        String date = yyyy+"-"+mm+"-"+dd;
        return getRetrievalReposnse("date", date, "financialOnly", impact.toString());
    }

    public ChargebackRetrievalResponse getActivityByActionable(Boolean actionable){
        return getRetrievalReposnse("actionable", actionable.toString());
    }

    public ChargebackRetrievalResponse getActivityByCaseId(Long caseId) {
        return getRetrievalReposnse(caseId);
    }

    public ChargebackRetrievalResponse getActivityByToken(String token){
        return getRetrievalReposnse("token", token);
    }

    public ChargebackRetrievalResponse getActivityByCardNum(String cardNum, String yy, String mm){
        String expDate = mm+yy;
        return getRetrievalReposnse("cardNumber", cardNum, "expirationDate", expDate);
    }

    public ChargebackRetrievalResponse getActivityByARN(String arn){
        return getRetrievalReposnse("arn", arn);
    }

    private String sendRetrievalRequest(String urlSuffix){
        return communication.getRequest(config, urlSuffix);
    }


    ////////////////////////////////////////////////////////////////////

    private ChargebackRetrievalResponse getRetrievalReposnse(String key, String value){
        String urlSuffix = URL_PATH + "?" + key + "=" + value;
        String response = sendRetrievalRequest(urlSuffix);
        return XMLConverter.generateRetrievalResponse(response);
    }

    private ChargebackRetrievalResponse getRetrievalReposnse(String key1, String value1, String key2, String value2){
        String urlSuffix = URL_PATH + "?" + key1 + "=" + value1 + "&" + key2 + "=" + value2;
        String response = sendRetrievalRequest(urlSuffix);
        return XMLConverter.generateRetrievalResponse(response);
    }

    private ChargebackRetrievalResponse getRetrievalReposnse(Long caseId){
        String urlSuffix = URL_PATH + caseId;
        String response = sendRetrievalRequest(urlSuffix);
        return XMLConverter.generateRetrievalResponse(response);
    }
}
