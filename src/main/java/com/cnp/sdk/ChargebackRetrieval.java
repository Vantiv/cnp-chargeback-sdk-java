package com.cnp.sdk;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private String getRetrievalResponse(String key, String value){
        String urlSuffix = "chargebacks/?" + key + "=" + value;
        String xml = communication.getRequest(config, urlSuffix);
        return xml;
    }

    private String getRetrievalResponse(String key1, String value1, String key2, String value2){
        String urlSuffix = "chargebacks/?" + key1 + "=" + value1 + "&" + key2 + "=" + value2;
        String xml = communication.getRequest(config, urlSuffix);
        return xml;
    }

    private String getRetrievalResponse(String caseId){
        String urlSuffix = "chargebacks/" + caseId;
        String xml = communication.getRequest(config, urlSuffix);
        return xml;
    }

    public String getChargebacksByDate(String date){
        return getRetrievalResponse("date", date);
    }

    public String getChargebacksByFinancialImpact(String date, Boolean impact){
        return getRetrievalResponse("date", date, "financialOnly", impact.toString());
    }

    public String getActivityByActionable(Boolean actionable){
        return getRetrievalResponse("actionable", actionable.toString());
    }

    public String getActivityByCaseId(String caseId){
        return getRetrievalResponse(caseId);
    }

    public String getActivityByToken(String token){
        return getRetrievalResponse("token", token);
    }

    public String getActivityByCardNum(String cardNum, String expDate){
        return getRetrievalResponse("cardNumber", cardNum, "expirationDate", expDate);
    }

    public String getActivityByARN(String arn){
        return getRetrievalResponse("arn", arn);
    }

    //TODO: methods should return response object

    public static void main(String[] args) {
        ChargebackRetrieval r = new ChargebackRetrieval();
//        System.out.println(r.getChargebacksByDate("2018-01-30"));
        System.out.println(r.getChargebacksByFinancialImpact("2018-01-30", true));
    }
}
