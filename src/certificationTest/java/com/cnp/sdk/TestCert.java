package com.cnp.sdk;

import com.cnp.sdk.generate.*;
import org.junit.Before;
import org.junit.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class TestCert {

    private ChargebackRetrieval cbkRetrieval;
    private ChargebackUpdate cbkUpdate;

    @Before
    public void setup() throws IOException {
        Properties config = new Properties();
        FileInputStream fileInputStream = new FileInputStream((new Configuration()).location());
        config.load(fileInputStream);
        config.setProperty("url", "https://prelive.litle.com/vap/services/chargebacks/");
        config.setProperty("proxyHost", "");
        config.setProperty("proxyPort", "");
        cbkRetrieval = new ChargebackRetrieval(config);
        cbkUpdate = new ChargebackUpdate(config);
    }

    @Test
    public void test1(){
        ChargebackRetrievalResponse response = cbkRetrieval.getChargebacksByDate("2013-01-01");
        List<ChargebackApiCase> cases = response.getChargebackCases();

        Collections.sort(cases, new Comparator<ChargebackApiCase>() {
            @Override
            public int compare(ChargebackApiCase o1, ChargebackApiCase o2) {
                return o1.getAcquirerReferenceNumber().compareTo(o2.getAcquirerReferenceNumber());
            }
        });

        assertEquals(11, cases.size());

        testChargebackCase(cases.get(0), "1111111111", "Generic", "FIRST_CHARGBACK");
        testChargebackCase(cases.get(1), "2222222222", "Generic", "FIRST_CHARGBACK");
        testChargebackCase(cases.get(2), "3333333333", "Generic", "FIRST_CHARGBACK");
        testChargebackCase(cases.get(3), "4444444444", "Generic", "FIRST_CHARGBACK");
        testChargebackCase(cases.get(4), "5555555550", "Visa Collaboration", "PRE_ARB_CHARGBACK");
        testChargebackCase(cases.get(5), "5555555551", "Visa Collaboration", "PRE_ARB_CHARGBACK");
        testChargebackCase(cases.get(6), "5555555552", "Visa Collaboration", "PRE_ARB_CHARGBACK");
        testChargebackCase(cases.get(7), "6666666660", "Visa Collaboration", "ARBITRATION_CHARGEBACK");
        testChargebackCase(cases.get(8), "7777777770", "Visa Allocation", "ISSUER_DECLINE_PRESAB");
        testChargebackCase(cases.get(9), "7777777771", "Visa Allocation", "ISSUER_DECLINE_PRESAB");
        testChargebackCase(cases.get(10), "7777777772", "Visa Allocation", "ISSUER_DECLINE_PRESAB");
    }

    @Test
    public void test2(){
        Long caseId = getCaseIdForArn("1111111111");

        ChargebackUpdateResponse updateResponse = cbkUpdate.addNoteToCase(caseId, "Cert test2");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.ADD_NOTE.toString(), activity.getActivityType());
        assertEquals("Cert test2", activity.getNotes());
    }

    @Test
    public void test3_1(){
        Long caseId = getCaseIdForArn("2222222222");

        ChargebackUpdateResponse updateResponse = cbkUpdate.representCase(caseId, "Cert test3_1");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.MERCHANT_REPRESENT.toString(), activity.getActivityType());
        assertEquals("Cert test3_1", activity.getNotes());

    }

    @Test
    public void test3_2(){
        Long caseId = getCaseIdForArn("3333333333");

        ChargebackUpdateResponse updateResponse = cbkUpdate.representCase(caseId, 10027L,"Cert test3_2");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.MERCHANT_REPRESENT.toString(), activity.getActivityType());
        assertEquals(new Long(10027L), activity.getSettlementAmount());
        assertEquals("Cert test3_2", activity.getNotes());

    }

    @Test
    public void test4_and_5_1(){
        //test4
        Long caseId = getCaseIdForArn("4444444444");

        ChargebackUpdateResponse updateResponse = cbkUpdate.assumeLiability(caseId, "Cert test4");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.MERCHANT_ACCEPTS_LIABILITY.toString(), activity.getActivityType());
        assertEquals("Cert test4", activity.getNotes());

        //test5_1
        try{
            updateResponse = cbkUpdate.assumeLiability(caseId, "Cert test5_1");
            fail("Expected Exception");
        } catch(ChargebackException e){
            assertEquals("400 : Bad Request", e.getMessage());
        }
    }

    @Test
    public void test5_2(){
        try{
            ChargebackRetrievalResponse retrievalResponse1 = cbkRetrieval.getActivityByCaseId(1234L);
            fail("Expected Exception");
        } catch(ChargebackException e){
            assertEquals("404 : Not Found", e.getMessage());
        }
    }

    @Test
    public void test6_1(){
        Long caseId = getCaseIdForArn("5555555550");

        ChargebackUpdateResponse updateResponse = cbkUpdate.representCase(caseId, "Cert test6_1");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.MERCHANT_REPRESENT.toString(), activity.getActivityType());
        assertEquals("Cert test6_1", activity.getNotes());
    }

    @Test
    public void test6_2(){
        Long caseId = getCaseIdForArn("5555555551");

        ChargebackUpdateResponse updateResponse = cbkUpdate.representCase(caseId, 10051L,"Cert test6_2");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.MERCHANT_REPRESENT.toString(), activity.getActivityType());
        assertEquals(new Long(10051L), activity.getSettlementAmount());
        assertEquals("Cert test6_2", activity.getNotes());
    }

    @Test
    public void test7(){
        //test4
        Long caseId = getCaseIdForArn("5555555552");

        ChargebackUpdateResponse updateResponse = cbkUpdate.assumeLiability(caseId, "Cert test7");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.MERCHANT_ACCEPTS_LIABILITY.toString(), activity.getActivityType());
        assertEquals("Cert test7", activity.getNotes());
    }

    @Test
    public void test8(){
        //test4
        Long caseId = getCaseIdForArn("6666666660");

        ChargebackUpdateResponse updateResponse = cbkUpdate.assumeLiability(caseId, "Cert test8");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.MERCHANT_ACCEPTS_LIABILITY.toString(), activity.getActivityType());
        assertEquals("Cert test8", activity.getNotes());
    }

    @Test
    public void test9_1(){
        Long caseId = getCaseIdForArn("7777777770");

        ChargebackUpdateResponse updateResponse = cbkUpdate.representCase(caseId, "Cert test9_1");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.MERCHANT_REPRESENT.toString(), activity.getActivityType());
        assertEquals("Cert test9_1", activity.getNotes());
    }

    @Test
    public void test9_2(){
        Long caseId = getCaseIdForArn("7777777771");

        ChargebackUpdateResponse updateResponse = cbkUpdate.representCase(caseId, 10071L,"Cert test9_2");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.MERCHANT_REPRESENT.toString(), activity.getActivityType());
        assertEquals(new Long(10071L), activity.getSettlementAmount());
        assertEquals("Cert test9_2", activity.getNotes());
    }

    @Test
    public void test10(){
        //test4
        Long caseId = getCaseIdForArn("7777777772");

        ChargebackUpdateResponse updateResponse = cbkUpdate.assumeLiability(caseId, "Cert test10");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ActivityType.MERCHANT_ACCEPTS_LIABILITY.toString(), activity.getActivityType());
        assertEquals("Cert test10", activity.getNotes());
    }

    private void testChargebackCase(ChargebackApiCase cbkcase, String arn, String cbkflow, String casecycle){
        assertEquals(arn, cbkcase.getAcquirerReferenceNumber());
//        assertEquals(cbkflow, cbkcase.get());
        assertEquals(casecycle, cbkcase.getCycle());
    }

    private Long getCaseIdForArn(String arn){
        ChargebackRetrievalResponse retrievalResponse = cbkRetrieval.getActivityByARN(arn);
        return retrievalResponse.getChargebackCases().get(0).getCaseId();
    }

    private ChargebackApiActivity getLastActivity(Long caseId){
        ChargebackRetrievalResponse retrievalResponse = cbkRetrieval.getActivityByCaseId(caseId);
        ChargebackApiCase caseAfter = retrievalResponse.getChargebackCases().get(0);
        List<ChargebackApiActivity> activitiesAfter = caseAfter.getActivities();
        return activitiesAfter.get(activitiesAfter.size() -1);
    }
}
