package com.cnp.sdk;

import com.cnp.sdk.generate.*;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Chargeback object has methods for performing the different Chargeback functionalities supported by the Vantiv API
 * These functionalities include retrieving chargeback cases based on various filters, updating chargeback cases and
 * managing supporting documents for the chargeback cases.
 *
 * @author harshitvora
 *
 */

public class Chargeback {

    private Properties config;
    private Communication communication;
    private final String URL_PATH = "/services/chargebacks/";

    public Chargeback() {

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

    /**
     * Construct a Chargeback specifying the configuration in code.  This should be used by integrations that
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
    public Chargeback(Properties config) {
        this.config = config;
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

    private ChargebackRetrievalResponse getRetrievalReposnse(String caseId){
        String urlSuffix = URL_PATH + caseId;
        String response = sendRetrievalRequest(urlSuffix);
        return XMLConverter.generateRetrievalResponse(response);
    }

    private String sendRetrievalRequest(String urlSuffix){
        String xml = communication.getRequest(config, urlSuffix);
        return xml;
    }

    public ChargebackRetrievalResponse getChargebacksByDate(String date) throws JAXBException {
        return getRetrievalReposnse("date", date);
    }

    public ChargebackRetrievalResponse getChargebacksByFinancialImpact(String date, Boolean impact){
        return getRetrievalReposnse("date", date, "financialOnly", impact.toString());
    }

    public ChargebackRetrievalResponse getActivityByActionable(Boolean actionable){
        return getRetrievalReposnse("actionable", actionable.toString());
    }

    // ToDo: Make caseId to long?
    public ChargebackRetrievalResponse getActivityByCaseId(String caseId) throws JAXBException {
        return getRetrievalReposnse(caseId);
    }

    public ChargebackRetrievalResponse getActivityByToken(String token){
        return getRetrievalReposnse("token", token);
    }

    public ChargebackRetrievalResponse getActivityByCardNum(String cardNum, String expDate){
        return getRetrievalReposnse("cardNumber", cardNum, "expirationDate", expDate);
    }

    public ChargebackRetrievalResponse getActivityByARN(String arn){
        return getRetrievalReposnse("arn", arn);
    }

    //////////////////////////////////////////////////////////////

    private String sendUpdateRequest(String caseId, String xmlRequest){
        String urlSuffix = URL_PATH + caseId;
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

    //////////////////////////////////////////////////////////////

    //TODO: decide if you want to accept File object or path

    public ChargebackDocumentUploadResponse uploadDocument(String caseId, File document){
        String urlSuffix = URL_PATH + "upload/" + caseId + "/" + document.getName();
        String xml = communication.postDocumentRequest(document, urlSuffix, config);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public File retrieveDocument(String caseId, String documentId){
        String urlSuffix = URL_PATH + "retrieve/" + caseId + "/" + documentId;
        File file = communication.getDocumentRequest(documentId, config, urlSuffix);
        return file;
    }

    public ChargebackDocumentUploadResponse replaceDocument(String caseId, File document){
        String urlSuffix = URL_PATH + "replace/" + caseId + "/" + document.getName();
        String xml = communication.putDocumentRequest(document, urlSuffix, config);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public ChargebackDocumentUploadResponse deleteDocument(String caseId, String documentId){
        String urlSuffix = URL_PATH + "remove/" + caseId + "/" + documentId;
        String xml = communication.deleteDocumentRequest(config, urlSuffix);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public ChargebackDocumentUploadResponse listDocuments(String caseId){
        String urlSuffix = URL_PATH + "list/" + caseId;
        String xml = communication.getRequest(config, urlSuffix);
        return XMLConverter.generateDocumentResponse(xml);
    }

    public static void main(String[] args) throws JAXBException {
        Chargeback r = new Chargeback();
        ChargebackRetrievalResponse re = r.getActivityByCaseId("216002100701");
        System.out.println(re.getChargebackCases().get(0).getCaseId());

//        Chargeback r = new Chargeback();
//        System.out.println(r.addNoteToCase("216002100701", "Test note"));
//        List<ChargebackApiActivity> alist = r.getActivityByCaseId("216002100701").getChargebackCases().get(0).getActivities();
//        for (ChargebackApiActivity a :
//                alist) {
//            System.out.println(a.getActivityType());
//            System.out.println(a.getNotes());
//        }
//        System.out.println(r.representCase("216002100701", 1L,"test"));
    }
}
