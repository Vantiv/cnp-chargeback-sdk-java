package com.cnp.sdk;

import com.cnp.sdk.generate.ActivityType;
import com.cnp.sdk.generate.ChargebackRetrievalResponse;
import com.cnp.sdk.generate.ChargebackUpdateRequest;
import com.cnp.sdk.generate.ChargebackUpdateResponse;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Chargeback {

    private Properties config;
    private Communication communication;

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
     *	version (eg 8.10)
     *	timeout (in seconds)
     *	Optional properties are:
     *	proxyHost
     *	proxyPort
     *	printxml (possible values "true" and "false" - defaults to false)
     * TODO: comments
     * @param config
     */
    public Chargeback(Properties config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////

    private ChargebackRetrievalResponse getRetrievalReposnse(String key, String value){
        String urlSuffix = "chargebacks/?" + key + "=" + value;
        String response = sendRetrievalRequest(urlSuffix);
        return XMLConverter.generateRetrievalResponse(response);
    }

    private ChargebackRetrievalResponse getRetrievalReposnse(String key1, String value1, String key2, String value2){
        String urlSuffix = "chargebacks/?" + key1 + "=" + value1 + "&" + key2 + "=" + value2;
        String response = sendRetrievalRequest(urlSuffix);
        return XMLConverter.generateRetrievalResponse(response);
    }

    private ChargebackRetrievalResponse getRetrievalReposnse(String caseId){
        String urlSuffix = "chargebacks/" + caseId;
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
//        Chargeback r = new Chargeback();
//        ChargebackRetrievalResponse re = r.getChargebacksByDate("2018-01-31");
//        System.out.println(re.getChargebackCases().get(0).getActivities().get(0).getNotes());


        Chargeback r = new Chargeback();
        System.out.println(r.addNoteToCase("216002100701", "Test note"));
    }
}
