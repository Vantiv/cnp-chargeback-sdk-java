package com.cnp.sdk;

import com.cnp.sdk.generate.ActivityType;
import com.cnp.sdk.generate.ChargebackUpdateRequest;
import com.cnp.sdk.generate.ChargebackUpdateResponse;
import javax.xml.bind.JAXBException;
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

    private ChargebackUpdateResponse getUpdateResponse(String caseId, ChargebackUpdateRequest request){
        String xmlRequest = XMLConverter.generateUpdateRequest(request);
        String response = sendUpdateRequest(caseId, xmlRequest);
        return XMLConverter.generateUpdateResponse(response);
    }

    public ChargebackUpdateResponse assignCaseToUser(String caseId, String userId, String note) throws JAXBException {
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.ASSIGN_TO_USER);
        request.setAssignedTo(userId);
        request.setNote(note);
        return getUpdateResponse(caseId, request);
    }

    public ChargebackUpdateResponse addNoteToCase(String caseId, String note) throws JAXBException {
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.ADD_NOTE);
        request.setNote(note);
        return getUpdateResponse(caseId, request);
    }

    public ChargebackUpdateResponse assumeLiability(String caseId, String note){
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.MERCHANT_ACCEPTS_LIABILITY);
        request.setNote(note);
        return getUpdateResponse(caseId, request);
    }

    //TODO: figure out data type for representedAmount

    public ChargebackUpdateResponse representCase(String caseId, Long representedAmount, String note){
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.MERCHANT_REPRESENT);
        request.setNote(note);
        request.setRepresentedAmount(representedAmount);
        return getUpdateResponse(caseId, request);
    }

    public ChargebackUpdateResponse respondToRetrievalRequest(String caseId, String note){
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.MERCHANT_RESPOND);
        request.setNote(note);
        return getUpdateResponse(caseId, request);
    }

    public static void main(String[] args) throws JAXBException {
        ChargebackUpdate r = new ChargebackUpdate();
        System.out.println(r.addNoteToCase("216004901502", "Test note"));
    }
}
