package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackDocumentUploadResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * ChargebackDocument object has methods for performing the different ChargebackDocument functionalities supported by the Vantiv API
 * like managing supporting documents for the chargeback cases.
 *
 * @author harshitvora
 *
 */

public class ChargebackDocument {
    private Properties config;
    private Communication communication;
    private String baseUrl;
    private final String SERVICE_ROUTE = "/services/chargebacks";

    public ChargebackDocument() {
        communication = new Communication();
        config = (new Configuration()).getProperties();
        baseUrl = config.getProperty("url") + SERVICE_ROUTE;
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
    public ChargebackDocument(Properties config) {
        this.config = config;
        communication = new Communication(config);
        baseUrl = config.getProperty("url") + SERVICE_ROUTE;
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    ////////////////////////////////////////////////////////////////////
    //                    ChargebackDocument API:                       //
    ////////////////////////////////////////////////////////////////////


    public ChargebackDocumentUploadResponse uploadDocument(Long caseId, File document){
        String requestUrl = baseUrl + "/upload/" + caseId + "/" + document.getName();
        return communication.httpPostDocumentRequest(document, requestUrl);
    }

    public File retrieveDocument(Long caseId, String documentId, String filepath){
        String requestUrl = baseUrl + "/retrieve/" + caseId + "/" + documentId;
        return communication.httpGetDocumentRequest(filepath, requestUrl);
    }

    public ChargebackDocumentUploadResponse replaceDocument(Long caseId, String documentId, File document){
        String requestUrl = baseUrl + "/replace/" + caseId + "/" + documentId;
        return communication.httpPutDocumentRequest(document, requestUrl);
    }

    public ChargebackDocumentUploadResponse deleteDocument(Long caseId, String documentId){
        String requestUrl = baseUrl + "/delete/" + caseId + "/" + documentId;
        return communication.httpDeleteDocumentRequest(requestUrl);
    }

    public ChargebackDocumentUploadResponse listDocuments(Long caseId){
        String requestUrl = baseUrl + "/list/" + caseId;
        return communication.httpGetDocumentListRequest(requestUrl);
    }

}
