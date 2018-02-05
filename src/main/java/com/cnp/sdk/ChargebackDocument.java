package com.cnp.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ChargebackDocument {

    private Properties config;
    private Communication communication;

    public ChargebackDocument() {

        communication = new Communication();
        FileInputStream fileInputStream = null;

        try {
            config = new Properties();
            fileInputStream = new FileInputStream((new Configuration()).location());
            config.load(fileInputStream);
        } catch (FileNotFoundException e) {
            throw new ChargebackException("Configuration file not found." +
                    " If you are not using the .litle_SDK_config.properties file," +
                    " please use the " + Chargeback.class.getSimpleName() + "(Properties) constructor." +
                    " If you are using .litle_SDK_config.properties, you can generate one using java -jar cnp-chargeback-sdk-java-x.xx.jar", e);
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

    public ChargebackDocument(Properties config) {
        this.config = config;
    }

    public String uploadDocument(String caseId, String document){
        //TODO: fix suffix and test
        File file = new File(document);
        String mid = config.getProperty("merchantId");
        String urlSuffix = "chargebacks/documents/" + mid + "/" + caseId + "/" + file.getName();
        String xml = communication.postDocumentRequest(file, urlSuffix, config);
        return xml;
    }

    public String retrieveDocument(String caseId, String document){
        String urlSuffix = "chargebacks/documents/" + config.getProperty("merchantId") + "/" + caseId + "/" + document;
        String xml = communication.getDocumentRequest(config, urlSuffix);
        return xml;
    }

    public String replaceDocument(String caseId, String document){
        File file = new File(document);
        String mid = config.getProperty("merchantId");
        String urlSuffix = "chargebacks/documents/" + mid + "/" + caseId + "/" + file.getName();
        String xml = communication.putDocumentRequest(file, urlSuffix, config);
        return xml;
    }

    public String deleteDocument(String caseId, String document){
        String urlSuffix = "chargebacks/documents/" + config.getProperty("merchantId") + "/" + caseId + "/" + document;
        String xml = communication.deleteDocumentRequest(config, urlSuffix);
        return xml;
    }

    public static void main(String[] args) {
        ChargebackDocument r = new ChargebackDocument();
//        System.out.println(r.retrieveDocument("216004901502", "test_note.pdf"));
        System.out.println(r.uploadDocument("216004901502", "document_test.PNG"));
    }
}
