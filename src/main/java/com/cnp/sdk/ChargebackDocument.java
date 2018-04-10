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

    public ChargebackDocument() {

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

        baseUrl = config.getProperty("url");
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
    public ChargebackDocument(Properties config) {
        this.config = config;
        communication = new Communication();
        baseUrl = config.getProperty("url");
    }

    ////////////////////////////////////////////////////////////////////
    //                    ChargebackDocument API:                       //
    ////////////////////////////////////////////////////////////////////


    public ChargebackDocumentUploadResponse uploadDocument(Long caseId, File document){
        String requestUrl = baseUrl + "upload/" + caseId + "/" + document.getName();
        String xml = communication.httpPostDocumentRequest(document, requestUrl, config);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public File retrieveDocument(Long caseId, String documentId, String filepath){
        String requestUrl = baseUrl + "retrieve/" + caseId + "/" + documentId;
        return communication.httpGetDocumentRequest(filepath, requestUrl, config);
    }

    public ChargebackDocumentUploadResponse replaceDocument(Long caseId, File document){
        String requestUrl = baseUrl + "replace/" + caseId + "/" + document.getName();
        String xml = communication.httpPutDocumentRequest(document, requestUrl, config);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public ChargebackDocumentUploadResponse deleteDocument(Long caseId, String documentId){
        String requestUrl = baseUrl + "remove/" + caseId + "/" + documentId;
        String xml = communication.httpDeleteDocumentRequest(requestUrl, config);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public ChargebackDocumentUploadResponse listDocuments(Long caseId){
        String requestUrl = baseUrl + "list/" + caseId;
        String xml = communication.httpGetRequest(requestUrl, config);
        return XMLConverter.generateDocumentResponse(xml);
    }
    

}
