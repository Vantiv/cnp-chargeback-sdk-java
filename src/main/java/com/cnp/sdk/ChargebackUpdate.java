package com.cnp.sdk;

import com.cnp.sdk.generate.ActivityType;
import com.cnp.sdk.generate.ChargebackUpdateRequest;
import com.cnp.sdk.generate.ChargebackUpdateResponse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * ChargebackUpdate object has methods for performing the different ChargebackUpdate functionalities supported by the Vantiv API
 * like updating chargeback cases.
 *
 * @author harshitvora
 *
 */

public class ChargebackUpdate {
    private Properties config;
    private Communication communication;
    private String baseurl;

    public ChargebackUpdate() {
        communication = new Communication();
        config = (new Configuration()).getProperties();
        baseurl = config.getProperty("url");
    }

    /**
     * Construct a ChargebackUpdate specifying the configuration in code.  This should be used by integrations that
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
    public ChargebackUpdate(Properties config) {
        this.config = config;
        communication = new Communication();
        baseurl = config.getProperty("url");
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
    }

    ////////////////////////////////////////////////////////////////////
    //                    ChargebackUpdate API:                       //
    ////////////////////////////////////////////////////////////////////


    public ChargebackUpdateResponse assignCaseToUser(Long caseId, String userId, String note) {
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.ASSIGN_TO_USER);
        request.setAssignedTo(userId);
        request.setNote(note);
        return getUpdateResponse(caseId, request);
    }

    public ChargebackUpdateResponse addNoteToCase(Long caseId, String note) {
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.ADD_NOTE);
        request.setNote(note);
        return getUpdateResponse(caseId, request);
    }

    public ChargebackUpdateResponse assumeLiability(Long caseId, String note){
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.MERCHANT_ACCEPTS_LIABILITY);
        request.setNote(note);
        return getUpdateResponse(caseId, request);
    }

    public ChargebackUpdateResponse representCase(Long caseId, Long representedAmount, String note){
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.MERCHANT_REPRESENT);
        request.setNote(note);
        request.setRepresentedAmount(representedAmount);
        return getUpdateResponse(caseId, request);
    }

    public ChargebackUpdateResponse representCase(Long caseId, String note){
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.MERCHANT_REPRESENT);
        request.setNote(note);
        return getUpdateResponse(caseId, request);
    }

    public ChargebackUpdateResponse respondToRetrievalRequest(Long caseId, String note){
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.MERCHANT_RESPOND);
        request.setNote(note);
        return getUpdateResponse(caseId, request);
    }

    public ChargebackUpdateResponse requestArbitration(Long caseId, String note){
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.MERCHANT_REQUESTS_ARBITRATION);
        request.setNote(note);
        return getUpdateResponse(caseId, request);
    }


    ////////////////////////////////////////////////////////////////////

    private ChargebackUpdateResponse getUpdateResponse(Long caseId, ChargebackUpdateRequest request){
        String xmlRequest = XMLConverter.generateUpdateRequest(request);
        String requestUrl = baseurl + caseId;
        return communication.httpPutUpdateRequest(xmlRequest, requestUrl, config);
    }
}
