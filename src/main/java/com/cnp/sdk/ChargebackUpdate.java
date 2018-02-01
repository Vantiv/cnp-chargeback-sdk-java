package com.cnp.sdk;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ChargebackUpdate {

    private Properties config;
    private Communication communication;

    public ChargebackUpdate() {

        communication = new Communication();
        FileInputStream fileInputStream = null;

        try {
            config = new Properties();
            fileInputStream = new FileInputStream((new Configuration()).location());
            config.load(fileInputStream);
        } catch (FileNotFoundException e) {
            throw new ChargebackException("Configuration file not found." +
                    " If you are not using the .litle_SDK_config.properties file," +
                    " please use the " + ChargebackRetrieval.class.getSimpleName() + "(Properties) constructor." +
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

    public ChargebackUpdate(Properties config) {
        this.config = config;
    }

    private String sendUpdateRequest(String caseId, String xmlRequest){
        String urlSuffix = "chargebacks/" + caseId;
        String xml = communication.putRequest(config, urlSuffix, xmlRequest);
        return xml;
    }

    public String assignCaseToUser(String caseId, String userId, String note){
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackUpdateRequest xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "<activityType>ASSIGN_TO_USER</activityType>\n" +
                "<assignTo>"+userId+"</assignTo>\n" +
                "<note>"+note+"</note>\n" +
                "</chargebackUpdateRequest>";
        return sendUpdateRequest(caseId, xmlRequest);
    }

    public String addNoteToCase(String caseId, String note){
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackUpdateRequest xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "<activityType>ADD_NOTE</activityType>\n" +
                "<note>"+note+"</note>\n" +
                "</chargebackUpdateRequest>";
        return sendUpdateRequest(caseId, xmlRequest);
    }

    public String assumeLiability(String caseId, String note){
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackUpdateRequest xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "<activityType>MERCHANT_ACCEPTS_LIABILITY</activityType>\n" +
                "<note>"+note+"</note>\n" +
                "</chargebackUpdateRequest>";
        return sendUpdateRequest(caseId, xmlRequest);
    }

    public String representCase(String caseId, double representedAmount, String note){
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackUpdateRequest xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "<activityType>MERCHANT_REPRESENT</activityType>\n" +
                "<note>"+note+"</note>\n" +
                "<representedAmount>"+representedAmount+"</representedAmount>\n" +
                "</chargebackUpdateRequest>";
        return sendUpdateRequest(caseId, xmlRequest);
    }

    public String respondToRetrievalRequest(String caseId, String note){
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackUpdateRequest xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "<activityType>MERCHANT_RESPOND</activityType>\n" +
                "<note>"+note+"</note>\n" +
                "</chargebackUpdateRequest>";
        return sendUpdateRequest(caseId, xmlRequest);
    }

    public static void main(String[] args) {
        ChargebackUpdate r = new ChargebackUpdate();
        System.out.println(r.addNoteToCase("216004901502", "Test note"));

    }
}
