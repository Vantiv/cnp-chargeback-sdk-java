package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackRetrievalResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class ChargebackRetrieval {

    private Properties config;
    private Communication communication;

    public ChargebackRetrieval() {

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
     *	version (eg 8.10)
     *	timeout (in seconds)
     *	Optional properties are:
     *	proxyHost
     *	proxyPort
     *	printxml (possible values "true" and "false" - defaults to false)
     * TODO: comments
     * @param config
     */
    public ChargebackRetrieval(Properties config) {
        this.config = config;
    }

    private String sendRetrievalRequest(String key, String value){
        String urlSuffix = "chargebacks/?" + key + "=" + value;
        String xml = communication.getRequest(config, urlSuffix);
        return xml;
    }

    private String sendRetrievalRequest(String key1, String value1, String key2, String value2){
        String urlSuffix = "chargebacks/?" + key1 + "=" + value1 + "&" + key2 + "=" + value2;
        String xml = communication.getRequest(config, urlSuffix);
        return xml;
    }

    private String sendRetrievalRequest(String caseId){
        String urlSuffix = "chargebacks/" + caseId;
        String xml = communication.getRequest(config, urlSuffix);
        return xml;
    }

    public ChargebackRetrievalResponse getChargebacksByDate(String date) throws JAXBException {
        String response = sendRetrievalRequest("date", date);
        return XMLConverter.generateRetrievalResponse(response);
    }

    public ChargebackRetrievalResponse getChargebacksByFinancialImpact(String date, Boolean impact){
        String response = sendRetrievalRequest("date", date, "financialOnly", impact.toString());
        return XMLConverter.generateRetrievalResponse(response);
    }

    public ChargebackRetrievalResponse getActivityByActionable(Boolean actionable){
        String response = sendRetrievalRequest("actionable", actionable.toString());
        return XMLConverter.generateRetrievalResponse(response);
    }

    public ChargebackRetrievalResponse getActivityByCaseId(String caseId) throws JAXBException {
        String response = sendRetrievalRequest(caseId);
        return XMLConverter.generateRetrievalResponse(response);
    }

    public ChargebackRetrievalResponse getActivityByToken(String token){
        String response = sendRetrievalRequest("token", token);
        return XMLConverter.generateRetrievalResponse(response);
    }

    public ChargebackRetrievalResponse getActivityByCardNum(String cardNum, String expDate){
        String response = sendRetrievalRequest("cardNumber", cardNum, "expirationDate", expDate);
        return XMLConverter.generateRetrievalResponse(response);
    }

    public ChargebackRetrievalResponse getActivityByARN(String arn){
        String response = sendRetrievalRequest("arn", arn);
        return XMLConverter.generateRetrievalResponse(response);
    }

    //TODO: methods should return response object

    public static void main(String[] args) throws JAXBException {
        ChargebackRetrieval r = new ChargebackRetrieval();
        ChargebackRetrievalResponse re = r.getChargebacksByDate("2018-01-31");
        System.out.println(re.getChargebackCases().get(0).getActivities().get(0).getNotes());

//        System.out.println(r.getChargebacksByFinancialImpact("2018-01-30", true));
    }
}
