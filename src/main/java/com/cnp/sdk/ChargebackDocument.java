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
    private final String URL_PATH = "/services/chargebacks/";

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
    }

    ////////////////////////////////////////////////////////////////////
    
    //TODO: decide if you want to accept File object or path

    public ChargebackDocumentUploadResponse uploadDocument(Long caseId, File document){
        String urlSuffix = URL_PATH + "upload/" + caseId + "/" + document.getName();
        String xml = communication.postDocumentRequest(document, urlSuffix, config);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public ChargebackDocumentUploadResponse retrieveDocument(Long caseId, String documentId, String filepath){
        String urlSuffix = URL_PATH + "retrieve/" + caseId + "/" + documentId;
        String xml = communication.getDocumentRequest(filepath, config, urlSuffix);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public ChargebackDocumentUploadResponse replaceDocument(Long caseId, File document){
        String urlSuffix = URL_PATH + "replace/" + caseId + "/" + document.getName();
        String xml = communication.putDocumentRequest(document, urlSuffix, config);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public ChargebackDocumentUploadResponse deleteDocument(Long caseId, String documentId){
        String urlSuffix = URL_PATH + "remove/" + caseId + "/" + documentId;
        String xml = communication.deleteDocumentRequest(config, urlSuffix);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public ChargebackDocumentUploadResponse listDocuments(Long caseId){
        String urlSuffix = URL_PATH + "list/" + caseId;
        String xml = communication.getRequest(config, urlSuffix);
        return XMLConverter.generateDocumentResponse(xml);
    }
    

}