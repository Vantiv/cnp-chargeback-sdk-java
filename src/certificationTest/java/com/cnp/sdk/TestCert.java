package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackApiActivity;
import com.cnp.sdk.generate.ChargebackApiCase;
import com.cnp.sdk.generate.ChargebackRetrievalResponse;
import com.cnp.sdk.generate.ChargebackUpdateResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class TestCert {

    private ChargebackRetrieval cbkRetrieval;
    private ChargebackUpdate cbkUpdate;
    // test responses
    final String CYCLE_FIRST_CHARGEBACK = "First Chargeback";
    final String CYCLE_PRE_ARB_CHARGBACK = "Pre-Arbitration";
    final String CYCLE_ARBITRATION_CHARGEBACK = "VISA Pre-Arbitration/Arbitration";
    final String CYCLE_ISSUER_DECLINE_PRESAB = "Issuer Declined Pre-Arbitration";
    final String ACTIVITY_MERCHANT_REPRESENT = "Merchant Represent";
    final String ACTIVITY_MERCHANT_ACCEPTS_LIABILITY = "Merchant Accepts Liability";
    final String ACTIVITY_ADD_NOTE = "Add Note";

    @Before
    public void setup(){
        Properties config = (new Configuration()).getProperties();
        config.setProperty("url", "https://services.vantivprelive.com/services/chargebacks/");
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

//        testChargebackCase(cases.get(0), "1111111111", CYCLE_FIRST_CHARGEBACK);
        testChargebackCase(cases.get(1), "2222222222", CYCLE_FIRST_CHARGEBACK);
        testChargebackCase(cases.get(2), "3333333333", CYCLE_FIRST_CHARGEBACK);
        testChargebackCase(cases.get(3), "4444444444", CYCLE_FIRST_CHARGEBACK);
        testChargebackCase(cases.get(4), "5555555550", CYCLE_PRE_ARB_CHARGBACK);
        testChargebackCase(cases.get(5), "5555555551", CYCLE_PRE_ARB_CHARGBACK);
        testChargebackCase(cases.get(6), "5555555552", CYCLE_PRE_ARB_CHARGBACK);
        testChargebackCase(cases.get(7), "6666666660", CYCLE_ARBITRATION_CHARGEBACK);
        testChargebackCase(cases.get(8), "7777777770", CYCLE_ISSUER_DECLINE_PRESAB);
        testChargebackCase(cases.get(9), "7777777771", CYCLE_ISSUER_DECLINE_PRESAB);
        testChargebackCase(cases.get(10), "7777777772", CYCLE_ISSUER_DECLINE_PRESAB);
    }

    @Test
    public void test2(){
        Long caseId = getCaseIdForArn("1111111111");

        cbkUpdate.addNoteToCase(caseId, "Cert test2");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_ADD_NOTE, activity.getActivityType());
        assertEquals("Cert test2", activity.getNotes());
    }

    @Test
    public void test3_1(){
        Long caseId = getCaseIdForArn("2222222222");

        cbkUpdate.representCase(caseId, "Cert test3_1");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_MERCHANT_REPRESENT, activity.getActivityType());
        assertEquals("Cert test3_1", activity.getNotes());

    }

    @Test
    public void test3_2(){
        Long caseId = getCaseIdForArn("3333333333");

        cbkUpdate.representCase(caseId, 10027L,"Cert test3_2");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_MERCHANT_REPRESENT, activity.getActivityType());
        assertEquals(new Long(10027L), activity.getSettlementAmount());
        assertEquals("Cert test3_2", activity.getNotes());

    }

    @Test
    public void test4_and_5_1(){
        //test4
        Long caseId = getCaseIdForArn("4444444444");

        cbkUpdate.assumeLiability(caseId, "Cert test4");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_MERCHANT_ACCEPTS_LIABILITY, activity.getActivityType());
        assertEquals("Cert test4", activity.getNotes());

        //test5_1
        try{
            cbkUpdate.assumeLiability(caseId, "Cert test5_1");
            fail("Expected Exception");
        } catch(ChargebackException e){
            assertEquals("400 : Bad Request - Cannot perform activity <Merchant Accepts Liability> for case <" + caseId + "> in queue <Merchant Assumed>", e.getMessage());
        }
    }

    @Test
    public void test5_2(){
        try{
            cbkRetrieval.getChargebackByCaseId(1234L);
            fail("Expected Exception");
        } catch(ChargebackException e){
            assertEquals("404 : Not Found - Could not find requested object.", e.getMessage());
        }
    }

    @Test
    public void test6_1(){
        Long caseId = getCaseIdForArn("5555555550");

        cbkUpdate.representCase(caseId, "Cert test6_1");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_MERCHANT_REPRESENT, activity.getActivityType());
        assertEquals("Cert test6_1", activity.getNotes());
    }

    @Test
    public void test6_2(){
        Long caseId = getCaseIdForArn("5555555551");

        cbkUpdate.representCase(caseId, 10051L,"Cert test6_2");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_MERCHANT_REPRESENT, activity.getActivityType());
        assertEquals(new Long(10051L), activity.getSettlementAmount());
        assertEquals("Cert test6_2", activity.getNotes());
    }

    @Test
    public void test7(){
        //test4
        Long caseId = getCaseIdForArn("5555555552");

        cbkUpdate.assumeLiability(caseId, "Cert test7");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_MERCHANT_ACCEPTS_LIABILITY, activity.getActivityType());
        assertEquals("Cert test7", activity.getNotes());
    }

    @Test
    public void test8(){
        //test4
        Long caseId = getCaseIdForArn("6666666660");

        cbkUpdate.assumeLiability(caseId, "Cert test8");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_MERCHANT_ACCEPTS_LIABILITY, activity.getActivityType());
        assertEquals("Cert test8", activity.getNotes());
    }

    @Test
    public void test9_1(){
        Long caseId = getCaseIdForArn("7777777770");

        cbkUpdate.representCase(caseId, "Cert test9_1");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_MERCHANT_REPRESENT, activity.getActivityType());
        assertEquals("Cert test9_1", activity.getNotes());
    }

    @Test
    public void test9_2(){
        Long caseId = getCaseIdForArn("7777777771");

        cbkUpdate.representCase(caseId, 10071L,"Cert test9_2");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_MERCHANT_REPRESENT, activity.getActivityType());
        assertEquals(new Long(10071L), activity.getSettlementAmount());
        assertEquals("Cert test9_2", activity.getNotes());
    }

    @Test
    public void test10(){
        //test4
        Long caseId = getCaseIdForArn("7777777772");

        cbkUpdate.assumeLiability(caseId, "Cert test10");

        ChargebackApiActivity activity = getLastActivity(caseId);

        assertEquals(ACTIVITY_MERCHANT_ACCEPTS_LIABILITY, activity.getActivityType());
        assertEquals("Cert test10", activity.getNotes());
    }

    private void testChargebackCase(ChargebackApiCase cbkcase, String arn, String casecycle){
        assertEquals(arn, cbkcase.getAcquirerReferenceNumber());
        assertEquals(casecycle, cbkcase.getCycle());
    }

    private Long getCaseIdForArn(String arn){
        ChargebackRetrievalResponse retrievalResponse = cbkRetrieval.getChargebacksByARN(arn);
        return retrievalResponse.getChargebackCases().get(0).getCaseId();
    }

    private ChargebackApiActivity getLastActivity(Long caseId){
        ChargebackRetrievalResponse retrievalResponse = cbkRetrieval.getChargebackByCaseId(caseId);
        ChargebackApiCase caseAfter = retrievalResponse.getChargebackCases().get(0);
        List<ChargebackApiActivity> activitiesAfter = caseAfter.getActivities();
        return activitiesAfter.get(0);
    }
}
