package com.cnp.sdk;

import com.cnp.sdk.generate.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

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
    private String baseurl;

    public ChargebackRetrieval() {
        communication = new Communication();
        config = (new Configuration()).getProperties();
        baseurl = config.getProperty("url");
    }

    /**
     * Construct a ChargebackRetrieval specifying the configuration in code.  This should be used by integrations that
     * have another way to specify their configuration settings (ofbiz, etc)
     *
     * Properties that *must* be set are:
     *
     * 	url (eg https://payments.litle.com/vap/communicator/online)
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
        baseurl = config.getProperty("url");
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    ////////////////////////////////////////////////////////////////////
    //                    ChargebackRetrieval API:                    //
    ////////////////////////////////////////////////////////////////////


    public ChargebackRetrievalResponse getChargebacksByDate(String yyyy_mm_dd) {
        return getRetrievalResponse("date", yyyy_mm_dd);
    }

    public ChargebackRetrievalResponse getChargebacksByFinancialImpact(String yyyy_mm_dd, Boolean impact){
        return getRetrievalResponse("date", yyyy_mm_dd, "financialOnly", impact.toString());
    }

    public ChargebackRetrievalResponse getActivityByActionable(Boolean actionable){
        return getRetrievalResponse("actionable", actionable.toString());
    }

    public ChargebackRetrievalResponse getActivityByCaseId(Long caseId) {
        return sendRetrievalRequest(String.valueOf(caseId));
    }

    public ChargebackRetrievalResponse getActivityByToken(String token){
        return getRetrievalResponse("token", token);
    }

    public ChargebackRetrievalResponse getActivityByCardNum(String cardNum, String mm_yy){
        return getRetrievalResponse("cardNumber", cardNum, "expirationDate", mm_yy);
    }

    public ChargebackRetrievalResponse getActivityByARN(String arn){
        return getRetrievalResponse("arn", arn);
    }


    ////////////////////////////////////////////////////////////////////

    private ChargebackRetrievalResponse getRetrievalResponse(String key, String value){
        String urlSuffix = "/?" + key + "=" + value;
        return sendRetrievalRequest(urlSuffix);

    }

    private ChargebackRetrievalResponse getRetrievalResponse(String key1, String value1, String key2, String value2){
        String urlSuffix = "/?" + key1 + "=" + value1 + "&" + key2 + "=" + value2;
        return sendRetrievalRequest(urlSuffix);

    }

    // Use if there are a lot of parameters

    private ChargebackRetrievalResponse getRetrievalResponse(Map<String, String> parameters){
        String urlSuffix = buildUrl(parameters);
        return sendRetrievalRequest(urlSuffix);
    }

    private String buildUrl(Map<String, String> parameters){
        StringBuilder sb = new StringBuilder();
        String prefix = "/?";
        for (Map.Entry entry : parameters.entrySet()) {
            sb.append(prefix);
            prefix = "&";
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    private ChargebackRetrievalResponse sendRetrievalRequest(String urlSuffix){
        String requestUrl = baseurl + urlSuffix;
        return communication.getRetrievalRequest(requestUrl, config);
    }
}
