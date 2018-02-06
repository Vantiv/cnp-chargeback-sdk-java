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
                    " If you are not using the .chargeback_SDK_config.properties file," +
                    " please use the " + Chargeback.class.getSimpleName() + "(Properties) constructor." +
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

    public ChargebackDocument(Properties config) {
        this.config = config;
    }

    //TODO: decide if you want to accept File object or path

    public String uploadDocument(String caseId, File document){
        //TODO: fix suffix and test
        String mid = config.getProperty("merchantId");
//        String urlSuffix = "chargebacks/documents/" + mid + "/" + caseId + "/" + document.getName();
        String urlSuffix = "chargebacks/upload/" + caseId + "/" + document.getName();
        String xml = communication.postDocumentRequest(document, urlSuffix, config);
        return xml;
    }

    public File retrieveDocument(String caseId, String documentId){
        String urlSuffix = "chargebacks/retrieve/" + caseId + "/" + documentId;
        File file = communication.getDocumentRequest(documentId, config, urlSuffix);
        return file;
    }

    public String replaceDocument(String caseId, File document){
        String mid = config.getProperty("merchantId");
        String urlSuffix = "chargebacks/replace/" + caseId + "/" + document.getName();
        String xml = communication.putDocumentRequest(document, urlSuffix, config);
        return xml;
    }

    public String deleteDocument(String caseId, String documentId){
        String urlSuffix = "chargebacks/remove/" + caseId + "/" + documentId;
        String xml = communication.deleteDocumentRequest(config, urlSuffix);
        return xml;
    }

    public static void main(String[] args) {
        ChargebackDocument r = new ChargebackDocument();
//        System.out.println(r.retrieveDocument("216004901502", "test_note.pdf"));
        File file = new File("document_test.PNG");
//        System.out.println(r.uploadDocument("216002100701", file));
        System.out.println(r.retrieveDocument("216002100701", "document_test.PNG"));
//        System.out.println(r.replaceDocument("216002100701", file));
//        System.out.println(r.deleteDocument("216002100701", "document_test.PNG"));
    }
}
